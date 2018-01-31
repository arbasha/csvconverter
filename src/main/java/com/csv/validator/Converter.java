package com.csv.validator;

public interface Converter<I, O> {

	O convert(I input);

	boolean isValid(I input);
}
