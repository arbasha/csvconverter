package com.csv.converter.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public final class Utils {

	private Utils() {
		// To Avoid Instantiation
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static File convertMultiPartToIoFile(MultipartFile file) throws IOException {
		// Create a temp file in temp folder we might not get permission issues,
		// not sure how it will behave in linux servers
		File convFile = new File(System.getProperty("java.io.tmpdir") + file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	/**
	 * @param date
	 * @return
	 */
	public static Timestamp convertDateToSqlTime(Date date) {
		if (date == null) {
			return null;
		}
		return new java.sql.Timestamp(date.getTime());
	}

	/**
	 * @param reader
	 * @param rowListProcessor
	 */
	public static void parseCSV(Reader reader, RowListProcessor rowListProcessor) {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setIgnoreLeadingWhitespaces(true);
		parserSettings.setIgnoreTrailingWhitespaces(true);
		parserSettings.setProcessor(rowListProcessor);
		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(reader);
	}

}
