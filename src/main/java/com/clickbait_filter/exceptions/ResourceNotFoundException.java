package com.clickbait_filter.exceptions;

public class ResourceNotFoundException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String target;

	public ResourceNotFoundException(String message, String target) {
		super(message);
		this.target = target;
	}

	public String getTarget() {
		return target;
	}
}