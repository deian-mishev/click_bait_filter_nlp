package com.clickbait_filter.exceptions.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ErrorDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private String objectName;
	private String code;
	private String target;
	private String message;

	public ErrorDetail() {
	}

	public ErrorDetail(String code, String target, String message) {
		this.code = code;
		this.target = target;
		this.message = message;
	}

	public ErrorDetail(String objectName, String code, String target, String message) {
		this(code, target, message);
		this.objectName = objectName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
