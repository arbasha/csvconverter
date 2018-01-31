package com.csv.validator;

import org.apache.commons.lang3.math.NumberUtils;

public class DoubleConverter implements Converter<String, Double> {

	@Override
	public Double convert(String input) {
		if (isValid(input)) {
			return Double.parseDouble(input);
		}
		return null;
	}

	@Override
	public boolean isValid(String input) {
		return NumberUtils.isParsable(input);
	}

}
