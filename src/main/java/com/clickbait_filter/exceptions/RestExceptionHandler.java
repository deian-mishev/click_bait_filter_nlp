/*
 * Copyright 2017-2018 Software Group BG AD. All rights reserved.
 *
 * Licensed under the Software Group proprietary License (the "License");
 * You may use, modify and distribute this code only under the terms of the License.
 * You should have received a copy of the License together with this code. If not, please write to: support@softwaregroup.com
 * Any Open Source Software that may be delivered to you embedded in or in association with this code is provided
 * pursuant to the open source license applicable to the respective Open Source Software.
 *
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.clickbait_filter.exceptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ServerWebInputException;

import com.clickbait_filter.exceptions.model.ApiError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * The Class RestExceptionHandler.
 * <p>
 * This class's methods handle the throwing of Exceptions throughout the
 * application.
 * <p>
 * Each exception gets handled by the appropriate method and a custom JSON is
 * generated and returned as a response to the client's request.
 * <p>
 * The class is annotated with HIGHEST_PRECEDENCE order, which means Spring is
 * firstly going to try handling the errors with one of RestExceptionHandler's
 * methods.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {

	/**
	 * Handle general errors.
	 * <p>
	 * This method handles errors, that are not handled by any other
	 * RestExceptionHandler method.
	 * <p>
	 * The status code of the response is INTERNAL_SERVER_ERROR.
	 *
	 * @param ex
	 *            The Exception
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneralErrors(Exception ex) {
		if (ex instanceof NullPointerException && "The mapper returned a null value.".equals(ex.getMessage())) {
			return ErrorUtils.buildResponseEntity(ErrorUtils.buildError(ErrorCodes.BAD_ARGUMENT, "Type mismatch."),
					HttpStatus.BAD_REQUEST);
		}
		String error = ex.getMessage();
		return ErrorUtils.buildResponseEntity(ErrorUtils.buildError(ErrorCodes.INTERNAL_ERROR, error),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle external request exception.
	 * <p>
	 * This method handles errors, that are thrown when making external request.
	 * E.g. status code of response is different from 200 OK.
	 * <p>
	 * The status code of the response is BAD_REQUEST.
	 *
	 * @param ex
	 *            The Exception
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler(RestClientException.class)
	public ResponseEntity<ApiError> handleRestClientException(RestClientException ex) {
		String error = "Request to external system failed";
		return ErrorUtils.buildResponseEntity(ErrorUtils.buildError(ErrorCodes.EXTERNAL_SERVICE_ERROR, error),
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle resource access errors.
	 * <p>
	 * This method handles ResourceAccessException.
	 * <p>
	 * The status code of the response is SERVICE_UNAVAILABLE.
	 *
	 * @param ex
	 *            The Exception
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<ApiError> handleResourceAccessErrors(ResourceAccessException ex) {
		return ErrorUtils.buildResponseEntity(ex);
	}

	/**
	 * Handle http message not readable exception.
	 * <p>
	 * This method handles HttpMessageNotReadableException. It might be a result of
	 * Malformed JSON request etc.
	 * <p>
	 * The status code of the response is BAD_REQUEST.
	 *
	 * @param ex
	 *            The HttpMessageNotReadableException
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		String error = "Malformed JSON request";
		return ErrorUtils.buildResponseEntity(ErrorUtils.buildError(ErrorCodes.MALFORMED_VALUE, error),
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle method argument not valid exception.
	 * <p>
	 * This method handles WebExchangeBindException. The exception is a result of a
	 * Validation error on some POJO class.
	 * <p>
	 * The status code of the response is BAD_REQUEST.
	 *
	 * @param exception
	 *            The exception
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<ApiError> handleWebExchangeBindException(WebExchangeBindException ex) {
		return ErrorUtils.buildResponseEntity(ex);
	}

	/**
	 * Handle loan web exceptions.
	 * <p>
	 * This method is intended to handle general Loan Api errors. It handles
	 * JpaObjectRetrievalFailureException, IllegalArgumentException.
	 * <p>
	 * The status code of the response is BAD_REQUEST.
	 *
	 * @param ex
	 *            The exception
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler({ JpaObjectRetrievalFailureException.class, IllegalArgumentException.class })
	public ResponseEntity<ApiError> handleLoanApiExceptions(Exception ex) {
		String error = ex.getMessage();
		return ErrorUtils.buildResponseEntity(ErrorUtils.buildError(ErrorCodes.BAD_ARGUMENT, error),
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle resource not found exception.
	 * <p>
	 * This method handles ResourceNotFoundException.
	 * <p>
	 * The status code of the response is NOT_FOUND.
	 *
	 * @param ex
	 *            The exception
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return ErrorUtils.buildResponseEntity(
				ErrorUtils.buildError(ErrorCodes.RESOURCE_NOT_FOUND, ex.getMessage(), ex.getTarget()),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * Handle cbs service error response exception.
	 * <p>
	 * This method handles ExternalErrorResponseException which is thrown from Cbs
	 * Service instance.
	 * <p>
	 * The status code of the response is BAD_REQUEST.
	 *
	 * @param ex
	 *            The exception
	 * @return The response entity containing JSON Error
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@ExceptionHandler(ExternalErrorResponseException.class)
	public ResponseEntity<ApiError> handleExternalErrorResponseException(ExternalErrorResponseException ex)
			throws IOException {
		return ErrorUtils.buildResponseEntity(ex);
	}

	/**
	 * Handle hibernate global exception.
	 * <p>
	 * This method handles DataAccessException which is thrown by Hibernate.
	 * <p>
	 * The status code of the response is INTERNAL_SERVER_ERROR.
	 *
	 * @param ex
	 *            The exception
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ApiError> handleHibernateGlobalException(DataAccessException ex) {
		String error = "Database exception occured!";
		return ErrorUtils.buildResponseEntity(ErrorUtils.buildError(ErrorCodes.DATABASE_ERROR, error),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle ServerWebInputException.
	 * <p>
	 * This method handles errors thrown when some validation on controller method
	 * fails
	 *
	 * @param ex
	 *            The ServerWebInputException
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler(ServerWebInputException.class)
	public ResponseEntity<ApiError> handleServerWebInputException(ServerWebInputException ex) {
		String error = ex.getReason().split(":")[0];
		return ErrorUtils.buildResponseEntity(ErrorUtils.buildError(ErrorCodes.BAD_ARGUMENT, error),
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle method argument not valid exception.
	 * <p>
	 * This method handles ConstraintViolationException. The exception is a result
	 * of a Validation error on some POJO class.
	 * <p>
	 * The status code of the response is BAD_REQUEST.
	 *
	 * @param exception
	 *            The exception
	 * @return The response entity containing JSON Error
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
		String error = "Request Validation Error Encountered!";
		List<ConstraintViolation<?>> constraintViolations = new ArrayList<>();
		for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
			if (constraintViolation.getPropertyPath().toString().indexOf(".arg") < 0) {
				constraintViolations.add(constraintViolation);
			}
		}
		return ErrorUtils.buildResponseEntity(
				ErrorUtils.buildError(ErrorCodes.BAD_ARGUMENT, error, "", constraintViolations),
				HttpStatus.BAD_REQUEST);
	}
}