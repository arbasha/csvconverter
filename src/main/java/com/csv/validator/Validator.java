package com.csv.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.csv.converter.util.Constants;
import com.csv.converter.util.Keys;
import com.csv.model.Deal;
import com.csv.model.FileInfo;
import com.csv.model.InvalidDeal;
import com.csv.model.ValidDeal;
import com.univocity.parsers.common.processor.RowListProcessor;

public final class Validator {

	private Validator() {
		// To Avoid Instantiation
	}

	public static boolean isHeaderValid(String[] headersFromCSV) {
		if (headersFromCSV == null) {
			return false;
		}
		return Arrays.equals(headersFromCSV, Constants.HEADERS_WE_PARSE);

	}

	private static Converter<?, ?> getConverter(String csvColumnName, Class<?> className) {
		for (Field field : className.getDeclaredFields()) {
			Convert convert = field.getAnnotation(Convert.class);
			if (convert != null && convert.csvColumnName().equals(csvColumnName)) {
				try {
					return convert.validateAndConvertClass().newInstance();
				} catch (Exception ex) {
					return null;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Map<Keys, Object> getParsedData(RowListProcessor rowListProcessor) {
		String[] headersFromCSV = rowListProcessor.getHeaders();
		List<String[]> rows = rowListProcessor.getRows();

		Map<Keys, Object> map = new HashMap<>();

		List<ValidDeal> validDeals = new ArrayList<>();
		List<InvalidDeal> inValidDeals = new ArrayList<>();
		FileInfo fileInfo = new FileInfo();

		map.put(Keys.VALID_DEALS, validDeals);
		map.put(Keys.INVALID_DEALS, inValidDeals);
		map.put(Keys.FILE_INFO, fileInfo);

		for (String[] row : rows) {

			Long dealId = null;
			String fromISOCode = null;
			String toISOCode = null;
			Date dealTimeStamp = null;
			Double dealAmount = null;

			for (int i = 0; i < row.length; i++) {
				String value = row[i];
				String headerName = headersFromCSV[i];
				@SuppressWarnings("rawtypes")
				Converter conveter = getConverter(headerName, Deal.class);
				switch (headerName) {
				case Constants.COLUMN_DEAL_UNIQUE_ID:
					if (conveter != null) {
						dealId = (Long) conveter.convert(value);
					}
					break;
				case Constants.COLUMN_FROM_CURRENCY_ISO_CODE:
					if (conveter != null) {
						fromISOCode = (String) conveter.convert(value);
					}
					break;
				case Constants.COLUMN_TO_CURRENCY_ISO_CODE:
					if (conveter != null) {
						toISOCode = (String) conveter.convert(value);
					}
					break;
				case Constants.COLUMN_DEAL_TIMESTAMP:
					if (conveter != null) {
						dealTimeStamp = (Date) conveter.convert(value);
					}
					break;
				case Constants.COLUMN_DEAL_AMOUNT:
					if (conveter != null) {
						dealAmount = (Double) conveter.convert(value);
					}
					break;
				}
			}

			// Build error flags, if flag is non zero then we have issues, also
			// assigning individual flags (binary bit) to each error, at later
			// point we can
			// figure what were the errors we saw in a file and also query them
			// accordingly using bit-and operation
			long flags = 0l;
			flags = flags | (dealId == null ? Constants.INVALID_DEAL_ID : 0l);
			flags = flags | (fromISOCode == null ? Constants.INVALID_FROM_ISO_CODE : 0l);
			flags = flags | (toISOCode == null ? Constants.INVALID_TO_ISO_CODE : 0l);
			flags = flags | (dealTimeStamp == null ? Constants.INVALID_DEAL_TIMESTAMP : 0l);
			flags = flags | (dealAmount == null ? Constants.INVALID_DEAL_AMOUNT : 0l);

			fileInfo.setFlags(fileInfo.getFlags() | flags);

			Deal deal = null;
			if (ObjectUtils.allNotNull(dealId, fromISOCode, toISOCode, dealTimeStamp, dealAmount)) {
				deal = new ValidDeal();
				validDeals.add((ValidDeal) deal);

			} else {
				deal = new InvalidDeal();
				inValidDeals.add((InvalidDeal) deal);
			}
			deal.setDealAmount(dealAmount);
			deal.setDealId(dealId);
			deal.setDealTime(dealTimeStamp);
			deal.setOrderingCurrencyCode(fromISOCode);
			deal.setReceivingCurrencyCode(toISOCode);
			deal.setFile(fileInfo);
		}
		return map;

	}
}
