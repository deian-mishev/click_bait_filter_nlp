package com.clickbait_filter.exceptions;

public final class ErrorCodes {
	public static final String INTERNAL_ERROR = "InternalError";
	public static final String CBS_RESPONSE_ERROR = "CbsResponseError";
	public static final String BAD_ARGUMENT = "BadArgument";
	public static final String MALFORMED_VALUE = "MalformedValue";
	public static final String SERVICE_UNAVAILABLE = "ServiceUnavailable";
	public static final String EXTERNAL_SERVICE_ERROR = "ExternalServiceError";
	public static final String DATABASE_ERROR = "DatabaseError";
	public static final String RESOURCE_NOT_FOUND = "ResourceNotFound";
	public static final String UNAUTHORIZED = "Unauthorized";
	public static final String UNCAUGHT_EXCEPTION = "UncaughtException";
	
	public static final String EXTERNAL_SYSTEM_ERROR="I/O request to external system failed. %s.";

	private ErrorCodes() {
	}
}