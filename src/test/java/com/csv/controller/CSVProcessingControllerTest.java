package com.csv.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.csv.converter.util.Constants;
import com.csv.model.Deal;
import com.csv.model.FileInfo;
import com.csv.model.ValidDeal;
import com.csv.service.DealMetricsService;
import com.csv.service.DealService;
import com.csv.service.FileInfoService;

public class CSVProcessingControllerTest {

	@InjectMocks
	private CSVProcessingController csvController;

	@Mock
	private DealService dealService;

	@Mock
	private FileInfoService fileService;

	@Mock
	private DealMetricsService dealMetricService;

	@Mock
	private MultipartHttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FileInfo fileInfo;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testUpload() throws Exception {
		// Get test csv file
		mockFile("testdata", false);
		LinkedList<FileInfo> fileInfos = csvController.upload(request, response);
		FileInfo fileInfo = fileInfos.getLast();
		assertNotNull(fileInfos);
		assertNotNull(fileInfo);
		assertEquals("testdata", fileInfo.getFileName());
		assertEquals("Processed", fileInfo.getStatus());
		assertEquals(2, fileInfo.getImportedDealCount());
		assertEquals(2, fileInfo.getInvalidDealCount());

	}

	@Test
	public void testSkipProcessingOfSameFile() throws Exception {
		// Get test csv file
		mockFile("testdata", true);
		LinkedList<FileInfo> fileInfos = csvController.upload(request, response);
		FileInfo fileInfo = fileInfos.getLast();
		assertNotNull(fileInfos);
		assertNotNull(fileInfo);
		assertEquals("testdata", fileInfo.getFileName());
		assertEquals("Duplicte File - Already Uploaded", fileInfo.getStatus());
		assertEquals(0, fileInfo.getImportedDealCount());
		assertEquals(0, fileInfo.getInvalidDealCount());
	}

	@Test
	public void testProcessingOfSameFileWithSystemError() throws Exception {
		// Get test csv file
		mockFile("testdata", true);
		// Mock System Error Flag
		Mockito.when(fileInfo.getFlags()).thenReturn(Constants.SYSTEM_ERROR);
		LinkedList<FileInfo> fileInfos = csvController.upload(request, response);
		FileInfo fileInfo = fileInfos.getLast();
		assertNotNull(fileInfos);
		assertNotNull(fileInfo);
		assertEquals("testdata", fileInfo.getFileName());
		assertEquals("Processed", fileInfo.getStatus());
		assertEquals(2, fileInfo.getImportedDealCount());
		assertEquals(2, fileInfo.getInvalidDealCount());
	}

	@Test
	public void testCsvWithInvalidHeaders() throws Exception {
		// Get test csv file
		mockFile("invalid_headers", false);
		LinkedList<FileInfo> fileInfos = csvController.upload(request, response);
		FileInfo fileInfo = fileInfos.getLast();
		assertNotNull(fileInfos);
		assertNotNull(fileInfo);
		assertEquals("invalid_headers", fileInfo.getFileName());
		assertEquals("Invalid Headers in the uploaded CSV", fileInfo.getStatus());
		assertEquals(0, fileInfo.getImportedDealCount());
		assertEquals(0, fileInfo.getInvalidDealCount());
	}

	@Test
	public void testCsvWithEmptyData() throws Exception {
		// Get test csv file
		mockFile("empty", false);
		LinkedList<FileInfo> fileInfos = csvController.upload(request, response);
		FileInfo fileInfo = fileInfos.getLast();
		assertNotNull(fileInfos);
		assertNotNull(fileInfo);
		assertEquals("empty", fileInfo.getFileName());
		assertEquals("No Rows Found to Upload", fileInfo.getStatus());
	}

	@Test(expected = Exception.class)
	public void testInvalidCSV() throws Exception {
		// Get test csv file
		mockFile("invalid_csv", false);
		LinkedList<FileInfo> fileInfos = csvController.upload(request, response);
		FileInfo fileInfo = fileInfos.getLast();
		assertNotNull(fileInfos);
		assertNotNull(fileInfo);
		assertEquals("invalid_csv", fileInfo.getFileName());
		assertEquals("Issue in File Processing - Invalid CSV", fileInfo.getStatus());
	}

	@Test
	public void testSettingOfSystemErrorFlag() throws Exception {
		// Get test csv file
		mockFile("testdata", true);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws SQLException {

				throw new SQLException();
			}
		}).when(dealService).saveDeals(Mockito.anyListOf(ValidDeal.class));
		csvController.upload(request, response);
	}

	@Test
	public void testIndexHomePage() throws Exception {
		ModelAndView view = csvController.home();
		assertNotNull(view);
		assertEquals("index", view.getViewName());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetDeals() throws Exception {
		ExtendedModelMap model = new ExtendedModelMap();
		String fileName = "valid.csv";
		Mockito.when(fileService.findByFileName(fileName)).thenReturn(fileInfo);
		List<Deal> testDeals = new ArrayList<>();
		testDeals.add(new ValidDeal("1", 100l, "USD", "AUD", 100d, new Date(System.currentTimeMillis()), fileInfo));
		Mockito.when(fileInfo.getDeals()).thenReturn(testDeals);

		String pageToShow = csvController.getDeals(fileName, (Model) model);
		assertNotNull(model.get("search"));
		assertEquals(1, ((List<Deal>) model.get("search")).size());
		assertEquals("valid.csv", fileName);
		assertEquals("deals", pageToShow);
	}

	@Test
	public void testGetDealsWithNoFileName() throws Exception {
		ExtendedModelMap model = new ExtendedModelMap();
		String fileName = "";
		String pageToShow = csvController.getDeals(fileName, (Model) model);
		assertEquals("index", pageToShow);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetDealsWithInvalidFileName() throws Exception {
		ExtendedModelMap model = new ExtendedModelMap();
		String fileName = "invalid";
		Mockito.when(fileService.findByFileName(fileName)).thenReturn(null);
		String pageToShow = csvController.getDeals(fileName, (Model) model);
		assertNotNull(model.get("search"));
		assertEquals(0, ((List<Deal>) model.get("search")).size());
		assertEquals("invalid", fileName);
		assertEquals("deals", pageToShow);
	}

	@Test
	public void testGetFile() throws Exception {
		// Get test csv file
		ResponseEntity<?> respEntity = csvController.getFile(Mockito.mock(HttpServletRequest.class));
		assertNotNull(respEntity);
		assertNotNull(respEntity.getBody());
		assertNotNull(respEntity.getHeaders());
		assertNotNull(respEntity.getHeaders().get("content-disposition"));
		assertEquals("text/csv", respEntity.getHeaders().get("Content-Type").get(0));
	}

	private void mockFile(String fileName, boolean mockFilePresenceInDB) throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName + ".csv");

		MultipartFile mpf = Mockito.mock(MultipartFile.class);
		Iterator<String> fileNames = Arrays.asList(fileName).iterator();
		Mockito.when(request.getFileNames()).thenReturn(fileNames);
		Mockito.when(request.getFile(fileName)).thenReturn(mpf);
		Mockito.when(mpf.getOriginalFilename()).thenReturn(fileName);
		if (mockFilePresenceInDB) {
			Mockito.when(fileService.findByFileName(fileName)).thenReturn(fileInfo);
		} else {
			Mockito.when(fileService.findByFileName(fileName)).thenReturn(null);
		}
		Mockito.when(mpf.getBytes()).thenReturn(IOUtils.toByteArray(inputStream));
	}

}
