package com.clickbait_filter.exceptions;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.clickbait_filter.exceptions.model.ErrorDetail;
import com.clickbait_filter.exceptions.model.ApiValidationError;

/**
 * The Class ValidationErrorBuilder.
 * <p>
 * ValidationErrorBuilder is used to build a validation error.
 */
public class ValidationErrorBuilder {

	/**
	 * Instantiates a new validation error builder.
	 */
	private ValidationErrorBuilder() {
	}

	/**
	 * Builds validation error from binding errors.
	 *
	 * @param errors
	 *            The errors
	 * @return The api validation error as object, containing all errors.
	 */
	public static ApiValidationError fromBindingErrors(Errors errors) {
		ApiValidationError error = new ApiValidationError("Validation failed. " + errors.getErrorCount() + " error(s)");
		for (ObjectError objectError : errors.getAllErrors()) {
			FieldError fieldError = (FieldError) objectError;
			String message = String.format("%s %s", fieldError.getField(), fieldError.getDefaultMessage());
			ErrorDetail errorDetail = new ErrorDetail(fieldError.getObjectName(), fieldError.getCode(),
					fieldError.getField(), message);
			error.addValidationError(errorDetail);
		}
		return error;
	}
}