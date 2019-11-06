package com.clickbait_filter.exceptions.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ApiValidationError {
	private final String errorMessage;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<ErrorDetail> errors = new ArrayList<>();

	public ApiValidationError(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void addValidationError(ErrorDetail error) {
		errors.add(error);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public List<ErrorDetail> getErrors() {
		return errors;
	}
}