package com.csv.validator;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateConverter implements Converter<String, Date> {

	private final static Logger logger = LoggerFactory.getLogger(Validator.class);

	private static final String[] DATE_PATTERNS = { "dd/MM/yyyy HH:mm:ss", "yyyy.MM.dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss",
			"dd-MMM-yyyy", "yyyy-MM-dd", "dd/MM/yyyy"
			/*
			 * "yyyy.MM.dd G 'at' HH:mm:ss z", "EEE, MMM d, ''yy", "h:mm a",
			 * "hh 'o''clock' a, zzzz", "K:mm a, z",
			 * "yyyyy.MMMMM.dd GGG hh:mm aaa", "EEE, d MMM yyyy HH:mm:ss Z",
			 * "yyMMddHHmmssZ", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
			 */ };

	@Override
	public Date convert(String input) {
		if (isValid(input)) {
			try {
				return DateUtils.parseDate(input, DATE_PATTERNS);
			} catch (ParseException ex) {
				logger.error("Date Parse Exception", ex);
			}
		}
		return null;
	}

	@Override
	public boolean isValid(String input) {
		return input != null;
	}

}
