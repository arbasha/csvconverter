package com.csv.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class ISOConverter implements Converter<String, String> {

	@Override
	public String convert(String input) {
		if (isValid(input)) {
			return input.toUpperCase();
		}
		return null;
	}

	@Override
	public boolean isValid(String input) {
		if (!NumberUtils.isCreatable(input) && StringUtils.isAlpha(input)
				&& (input.length() >= 2 && input.length() <= 3)) {
			return true;
		}
		return false;
	}

}
