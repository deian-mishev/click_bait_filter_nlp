package com.clickbait_filter.exceptions;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.clickbait_filter.exceptions.model.ApiError;
import com.clickbait_filter.exceptions.model.InnerError;
import com.clickbait_filter.exceptions.model.ApiValidationError;
import com.clickbait_filter.exceptions.model.ErrorDetail;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public interface ErrorUtils {

	public static ApiError buildError(String code, String message) {
		return buildError(code, message, null, Arrays.asList());
	}

	public static ApiError buildError(String code, String message, String target) {
		return buildError(code, message, target, Arrays.asList());
	}

	public static ApiError buildError(String code, String message, String target, List<?> subErrors) {
		ApiError error = new ApiError();
		InnerError inner = new InnerError();
		inner.setCode(code);
		inner.setMessage(message);
		inner.setTarget(target);

		handleInnerErrors(subErrors, inner);

		error.setError(inner);

		return error;
	}

	/**
	 * Creates the validation error.
	 *
	 * @param ex The exception
	 * @return The response entity containing JSON Error
	 */
	public static ApiError buildError(WebExchangeBindException ex) {
		String error = "Field Validation Error Encountered!";
		ApiValidationError validationError = ValidationErrorBuilder.fromBindingErrors(ex.getBindingResult());
		List<ApiValidationError> subErrors = Arrays.asList(validationError);

		return buildError(ErrorCodes.BAD_ARGUMENT, error, null, subErrors);
	}

	public static ApiError buildError(ResourceAccessException ex) {
		String error = "";
		if (ex.getCause() instanceof HttpHostConnectException) {
			String hostname = ((HttpHostConnectException) ex.getCause()).getHost().toURI();
			error = String.format(ErrorCodes.EXTERNAL_SYSTEM_ERROR, hostname);
		} else if (ex.getCause() instanceof SocketTimeoutException) {
			String message = ((SocketTimeoutException) ex.getCause()).getMessage();
			error = String.format(ErrorCodes.EXTERNAL_SYSTEM_ERROR, message);
		}

		return buildError(ErrorCodes.SERVICE_UNAVAILABLE, error);
	}

	public static void handleInnerErrors(List<?> subErrors, InnerError inner) {
		if (subErrors != null && !subErrors.isEmpty()) {
			Object first = subErrors.get(0);
			InnerError secondInner = new InnerError();
			for (Object sub : subErrors) {
				processError(inner, secondInner, first, sub);
			}
			if (!secondInner.isEmpty()) {
				inner.setInnerError(secondInner);
			}
		}
	}

	/**
	 * Builds the response entity.
	 *
	 * @param apiError The web error
	 * @return The response entity containing JSON Error
	 */
	public static ResponseEntity<ApiError> buildResponseEntity(ApiError apiError, HttpStatus status) {
		return new ResponseEntity<>(apiError, status);
	}

	/**
	 * Creates the validation error.
	 *
	 * @param ex The exception
	 * @return The response entity containing JSON Error
	 */
	public static ResponseEntity<ApiError> buildResponseEntity(WebExchangeBindException ex) {
		return buildResponseEntity(buildError(ex), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle cbs service error response exception.
	 * <p>
	 * This method handles ExternalErrorResponseException which is thrown from Cbs
	 * Service instance.
	 * <p>
	 * The status code of the response is BAD_REQUEST.
	 *
	 * @param ex The exception
	 * @return The response entity containing JSON Error
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static ResponseEntity<ApiError> buildResponseEntity(ExternalErrorResponseException ex) throws IOException {
		try {
			HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
			String errorText = ex.getMessage();
			ApiError error = buildError(ErrorCodes.EXTERNAL_SERVICE_ERROR, ex.getMessage());
			if (ex.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException cause = (HttpClientErrorException) ex.getCause();
				errorText = String.format("Error in making external request to %s!", ex.getMessage());
				error = constructExternalError(cause.getResponseBodyAsString());
				if (cause.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
					httpStatus = HttpStatus.UNAUTHORIZED;
				}
			}
			error.getError().setMessage(errorText);
			return buildResponseEntity(error, httpStatus);
		} catch (NullPointerException e) {
			LoggerFactory.getLogger(ErrorUtils.class).error(e.getMessage(), e);
			String error = "Error in making external request!";
			return buildResponseEntity(buildError(ErrorCodes.EXTERNAL_SERVICE_ERROR, error), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public static ApiError constructExternalError(String response) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		ApiError error;
		try {
			error = objectMapper.readValue(response, ApiError.class);
			if (error.getError().getInner() != null && !error.getError().getInner().isEmpty()) {
				error.getError().getInner().setMessage(error.getError().getMessage());
			} else if (error.getError().getDetails().isEmpty() && !StringUtils.isEmpty(error.getError().getMessage())) {
				ErrorDetail detail = new ErrorDetail();
				detail.setCode(error.getError().getCode());
				detail.setMessage(error.getError().getMessage());
				error.getError().getDetails().add(detail);
			}
		} catch (MismatchedInputException e) {
			LoggerFactory.getLogger(ErrorUtils.class).error(e.getMessage(), e);

			JsonNode json = objectMapper.readTree(response);
			error = new ApiError();
			InnerError inner = new InnerError();
			inner.setCode(ErrorCodes.BAD_ARGUMENT);

			InnerError secondInner = new InnerError();
			ErrorDetail detail = new ErrorDetail();
			detail.setCode("BadRequest");
			detail.setMessage(json.get("message").asText());
			secondInner.setDetails(Arrays.asList(detail));

			inner.setInnerError(secondInner);
			error.setError(inner);
		}
		return error;
	}

	/**
	 * Handle resource access errors.
	 * <p>
	 * This method handles ResourceAccessException.
	 * <p>
	 * The status code of the response is SERVICE_UNAVAILABLE.
	 *
	 * @param ex The Exception
	 * @return The response entity containing JSON Error
	 */
	public static ResponseEntity<ApiError> buildResponseEntity(ResourceAccessException ex) {
		String error = "";
		if (ex.getCause() instanceof HttpHostConnectException) {
			String hostname = ((HttpHostConnectException) ex.getCause()).getHost().toURI();
			error = String.format(ErrorCodes.EXTERNAL_SYSTEM_ERROR, hostname);
		} else if (ex.getCause() instanceof SocketTimeoutException) {
			String message = ((SocketTimeoutException) ex.getCause()).getMessage();
			error = String.format(ErrorCodes.EXTERNAL_SYSTEM_ERROR, message);
		}
		return buildResponseEntity(buildError(ErrorCodes.SERVICE_UNAVAILABLE, error), HttpStatus.SERVICE_UNAVAILABLE);
	}

	/**
	 * Builds the response entity.
	 *
	 * @param apiError The web error
	 * @return The response entity containing JSON Error
	 */
	public static ResponseEntity<EvaluateConditionsResult> buildResponseEntity(
			EvaluateConditionsResult conditionsResult, HttpStatus status) {
		return new ResponseEntity<>(conditionsResult, status);
	}

	public static void processError(InnerError inner, InnerError secondInner, Object first, Object sub) {
		String simpleClassName = first.getClass().getSimpleName();
		if ("ApiValidationError".equals(simpleClassName)) {
			ApiValidationError subError = (ApiValidationError) sub;
			for (int y = 0; y < subError.getErrors().size(); y++) {
				inner.getDetails().add(subError.getErrors().get(y));
			}
		} else if ("ErrorDetail".equals(simpleClassName)) {
			inner.getDetails().add((ErrorDetail) sub);
		} else if ("String".equals(simpleClassName)) {
			ErrorDetail detail = new ErrorDetail();
			detail.setMessage((String) sub);
			secondInner.getDetails().add(detail);
		} else if ("ConditionResult".equals(simpleClassName)) {
			ConditionResult conditionResult = (ConditionResult) sub;
			ErrorDetail conditionMessage = new ErrorDetail();
			conditionMessage.setMessage(conditionResult.getConditionMessage());
			inner.getDetails().add(conditionMessage);
		} else if ("ConstraintViolation".equals(simpleClassName) || "ConstraintViolationImpl".equals(simpleClassName)) {
			ConstraintViolation<?> constraintViolation = (ConstraintViolation<?>) sub;
			ErrorDetail constraintMessage = new ErrorDetail(constraintViolation.getMessageTemplate(),
					constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
			inner.getDetails().add(constraintMessage);
		}
	}
}
