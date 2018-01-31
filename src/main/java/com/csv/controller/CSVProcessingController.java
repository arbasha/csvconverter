package com.csv.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.csv.converter.util.Constants;
import com.csv.converter.util.Keys;
import com.csv.converter.util.Utils;
import com.csv.model.Deal;
import com.csv.model.DealMetrics;
import com.csv.model.FileInfo;
import com.csv.model.InvalidDeal;
import com.csv.model.ValidDeal;
import com.csv.service.DealMetricsService;
import com.csv.service.DealService;
import com.csv.service.FileInfoService;
import com.csv.validator.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.univocity.parsers.common.processor.RowListProcessor;

@Controller
public class CSVProcessingController {

	private final static Logger logger = LoggerFactory.getLogger(CSVProcessingController.class);

	@Autowired
	private DealService dealService;

	@Autowired
	private FileInfoService fileService;

	@Autowired
	private DealMetricsService dealMetricService;

	private LinkedList<FileInfo> files = new LinkedList<>();

	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody LinkedList<FileInfo> upload(MultipartHttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("Inside upload method");
		// Start Timer
		Instant start = Instant.now();

		// build an iterator
		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf = null;

		// get each file, TODO: we can run multiple file asynchronously
		while (itr.hasNext()) {

			// get next MultipartFile
			mpf = request.getFile(itr.next());
			String fileName = mpf.getOriginalFilename();
			// if files > 10 remove the first from the list
			if (files.size() >= 10)
				files.pop();

			// Exit if file already uploaded and no system error (as we can re
			// process the same file again)
			FileInfo fileFromDB = null;
			if ((fileFromDB = getDuplicateFile(fileName)) != null) {
				addFilesForDisplay(fileName, Constants.DUPLICATE_FILE_STATUS, fileFromDB.getImportedDealCount(),
						fileFromDB.getInvalidDealCount(), start);
				return files;
			}

			File file = Utils.convertMultiPartToIoFile(mpf);

			try (Reader reader = new FileReader(file);) {
				RowListProcessor rowListProcessor = new RowListProcessor();
				Utils.parseCSV(reader, rowListProcessor);
				String[] headersFromCSV = rowListProcessor.getHeaders();

				// Basic Validations
				// Valid CSV or Headers
				if (!Validator.isHeaderValid(headersFromCSV)) {
					addFilesForDisplay(fileName, Constants.INVALID_HEADERS_STATUS, 0, 0, start);
					return files;
				}

				List<String[]> rows = rowListProcessor.getRows();
				if (CollectionUtils.isEmpty(rows)) {
					addFilesForDisplay(fileName, Constants.NO_ROWS_STATUS, 0, 0, start);
					return files;
				}

				Map<Keys, Object> parseDataMap = Validator.getParsedData(rowListProcessor);
				List<ValidDeal> validDeals = (List<ValidDeal>) parseDataMap.get(Keys.VALID_DEALS);
				List<InvalidDeal> inValidDeals = (List<InvalidDeal>) parseDataMap.get(Keys.INVALID_DEALS);

				// save the file first, as we are doing bulk update, Cascade
				// wont work
				FileInfo fileInfo = (FileInfo) parseDataMap.get(Keys.FILE_INFO);
				fileInfo.setFileName(fileName);

				FileInfo savedFile = fileService.saveFileInfo(fileInfo);
				validDeals.forEach(validDeal -> validDeal.setFile(savedFile));
				inValidDeals.forEach(inValidDeal -> inValidDeal.setFile(savedFile));

				Instant dbProcessingTime = Instant.now();

				try {
					// TODO: If invalid count is high, we can run insert valid and invalid data parallel
					if (CollectionUtils.isNotEmpty(validDeals)) {
						dealService.saveDeals(validDeals);
					}

					if (CollectionUtils.isNotEmpty(inValidDeals)) {
						dealService.saveDeals(inValidDeals);
					}

					// Run as async
					updateMetrics(validDeals);
					addFilesForDisplay(fileName, Constants.PROCESSED_STATUS, validDeals.size(), inValidDeals.size(),
							dbProcessingTime);

				} catch (Exception ex) {
					logger.error("UnExpected Exception", ex);
					// Log system error flag, we may re-process this flag again
					savedFile.setFlags((savedFile.getFlags() | Constants.SYSTEM_ERROR));
					// Save the file with system error flag
					fileService.saveFileInfo(savedFile);
					addFilesForDisplay(fileName, Constants.PROCESSEING_FAILED_STATUS, validDeals.size(),
							inValidDeals.size(), start);
				}

				// delete uploaded file from server, TODO: move this to
				// async-block
				FileUtils.deleteQuietly(file);
			} catch (Exception ex) {
				logger.error("Issue in File Processing", ex);
				addFilesForDisplay(fileName, Constants.INVAID_CSV_STATUS, 0, 0, start);
				throw ex;
			}

		}
		logger.info("Exiting - upload method");
		return files;

	}

	private FileInfo getDuplicateFile(String fileName) {
		FileInfo fileFromDB = fileService.findByFileName(fileName);
		if (fileFromDB != null && !((fileFromDB.getFlags() & Constants.SYSTEM_ERROR) == Constants.SYSTEM_ERROR)) {
			return fileFromDB;
		}
		return null;
	}

	private void addFilesForDisplay(String fileName, String status, long importedDealCount, long invalidDealCount,
			Instant startTime) {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName(fileName);
		fileInfo.setImportedDealCount(importedDealCount);
		fileInfo.setInvalidDealCount(invalidDealCount);
		fileInfo.setStatus(status);
		fileInfo.setProcessingTime(Duration.between(startTime, Instant.now()).getSeconds() + " second(s)");
		files.add(fileInfo);
	}

	/**
	 * @param validDeals
	 */
	@Async("metriceUpdateTaskExecutor")
	public CompletableFuture<List<DealMetrics>> updateMetrics(List<ValidDeal> validDeals) {
		logger.info("Inside - updateMetrics method");
		try {
			Map<String, DealMetrics> isoDealCountMap = new HashMap<>();

			validDeals.forEach(validDeal -> {
				DealMetrics metric = null;
				if ((metric = isoDealCountMap.get(validDeal.getOrderingCurrencyCode())) != null) {
					metric.setDealCount(metric.getDealCount() + 1);
					isoDealCountMap.put(validDeal.getOrderingCurrencyCode(), metric);
				} else {
					metric = new DealMetrics();
					metric.setOrderingCurrencyCode(validDeal.getOrderingCurrencyCode());
					metric.setDealCount(1l);
					isoDealCountMap.put(validDeal.getOrderingCurrencyCode(), metric);
				}
			});

			List<DealMetrics> dbMetrics = null;
			ArrayList<String> queryData = new ArrayList<>(isoDealCountMap.keySet());
			if ((dbMetrics = dealMetricService.findAll(queryData)) != null) {
				for (DealMetrics dbMetric : dbMetrics) {
					DealMetrics curr = isoDealCountMap.get(dbMetric.getOrderingCurrencyCode());
					curr.setDealCount(curr.getDealCount() + dbMetric.getDealCount());
				}
			}

			ArrayList<DealMetrics> list = new ArrayList<>();
			for (DealMetrics metricToSave : isoDealCountMap.values()) {
				list.add(metricToSave);
			}
			// Save the metrics back to DB
			return CompletableFuture.completedFuture(dealMetricService.saveDealMetrics(list));
		} catch (Exception ex) {
			// Metric should not mess up actual processing
			logger.error("Deal Metric Update Issue");
		} finally {
			logger.info("Exiting - updateMetrics method");
		}
		return null;
	}

	@RequestMapping(value = { "/", "/upload" }, method = RequestMethod.GET)
	public ModelAndView home() {
		files = new LinkedList<FileInfo>();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		return modelAndView;
	}

	@RequestMapping(value = "/fileinfo", method = RequestMethod.GET)
	public String getDeals(@RequestParam(value = "search", required = false) String fileName, Model model)
			throws JsonProcessingException {

		if (fileName == null || "".equals(fileName.trim())) {
			return Constants.INDEX_HTML;
		}

		if (!Constants.CSV_EXTENSION_WITHOUT_DOT.equalsIgnoreCase(FilenameUtils.getExtension(fileName))) {
			fileName = fileName + Constants.CSV_EXTENSION_WITH_DOT;
		}

		List<Deal> searchResults = null;
		FileInfo file = fileService.findByFileName(fileName);
		if (file == null || file.getDeals() == null) {
			searchResults = new ArrayList<>();
		} else {
			searchResults = file.getDeals();
		}

		// TODO showing top 1000 for now, implement pagination
		List<Deal> finalResults = searchResults.size() == 0 ? searchResults
				: searchResults.subList(0, searchResults.size() > 1000 ? 1000 : searchResults.size());

		model.addAttribute("search", finalResults);
		model.addAttribute("fileName", fileName);
		return Constants.DEALS_HTML;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/testdata", produces = { "text/csv" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity getFile(HttpServletRequest request)
			throws IOException, ParseException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		ResponseEntity respEntity = null;

		String fileName = "test_data_" + System.currentTimeMillis() + ".csv";
		File testFile = new File(System.getProperty("java.io.tmpdir") + fileName);
		testFile.createNewFile();
		try (Writer writer = Files.newBufferedWriter(Paths.get(testFile.getPath()));) {
			CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);

			csvWriter.writeNext(Constants.HEADERS_WE_PARSE);

			RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
			SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
			for (long i = 1; i <= 100000; i++) {
				csvWriter.writeNext(new String[] { String.valueOf(i), generator.generate(2).toUpperCase(),
						generator.generate(2).toUpperCase(), formater.format(new java.util.Date()),
						String.valueOf(RandomUtils.nextInt(1, 1000)) });
			}
			csvWriter.flush();
			csvWriter.close();
		}

		InputStream inputStream = new FileInputStream(testFile);
		byte[] out = org.apache.commons.io.IOUtils.toByteArray(inputStream);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		responseHeaders.add(HttpHeaders.CONTENT_TYPE, Constants.TEXT_CSV_CONTENT_TYPE);

		respEntity = new ResponseEntity(out, responseHeaders, HttpStatus.OK);

		// Delte the file
		FileUtils.deleteQuietly(testFile);
		return respEntity;
	}
}