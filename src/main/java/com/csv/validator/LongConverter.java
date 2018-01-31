package com.csv.validator;

import org.apache.commons.lang3.math.NumberUtils;

public class LongConverter implements Converter<String, Long> {

	@Override
	public Long convert(String input) {
		if (isValid(input)) {
			return Long.parseLong(input);
		}
		return null;
	}

	@Override
	public boolean isValid(String input) {
		return NumberUtils.isParsable(input);
	}

}
