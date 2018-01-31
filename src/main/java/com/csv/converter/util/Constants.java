package com.csv.converter.util;

public interface Constants {

	public static final String[] HEADERS_WE_PARSE = { "Deal Unique Id", "From Currency ISO Code",
			"To Currency ISO Code", "Deal timestamp", "Deal Amount in ordering currency" };

	public static final String COLUMN_DEAL_UNIQUE_ID = "Deal Unique Id";
	public static final String COLUMN_FROM_CURRENCY_ISO_CODE = "From Currency ISO Code";
	public static final String COLUMN_DEAL_TIMESTAMP = "Deal timestamp";
	public static final String COLUMN_TO_CURRENCY_ISO_CODE = "To Currency ISO Code";
	public static final String COLUMN_DEAL_AMOUNT = "Deal Amount in ordering currency";

	public static final long INVALID_DEAL_ID = 1;
	public static final long INVALID_FROM_ISO_CODE = 2;
	public static final long INVALID_TO_ISO_CODE = 4;
	public static final long INVALID_DEAL_TIMESTAMP = 8;
	public static final long INVALID_DEAL_AMOUNT = 16;
	public static final long SYSTEM_ERROR = 32;

	// Error Messages
	public static final String DUPLICATE_FILE_STATUS = "Duplicte File - Already Uploaded";
	public static final String INVALID_HEADERS_STATUS = "Invalid Headers in the uploaded CSV";
	public static final String INVAID_CSV_STATUS = "Issue in File Processing - Invalid CSV";
	public static final String NO_ROWS_STATUS = "No Rows Found to Upload";
	public static final String PROCESSED_STATUS = "Processed";
	public static final String PROCESSEING_FAILED_STATUS = "Processing Failed";

	// HTML Page Name
	public static final String INDEX_HTML = "index";
	public static final String DEALS_HTML = "deals";

	public static final String CSV_EXTENSION_WITHOUT_DOT = "csv";
	public static final String CSV_EXTENSION_WITH_DOT = ".csv";

	public static final String TEXT_CSV_CONTENT_TYPE = "text/csv";

}
