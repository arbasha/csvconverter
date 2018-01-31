package com.csv.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ ElementType.FIELD })
public @interface Convert {

	@SuppressWarnings("rawtypes")
	Class<? extends Converter> validateAndConvertClass();

	String csvColumnName() default "";
}
