package com.clickbait_filter.exceptions;

public class ExternalErrorResponseException extends Exception {

	private static final long serialVersionUID = 1L;

	private final EvaluateConditionsResult conditionsResult;
	private final String fromStatus;
	private final String toStatus;
	private final Integer entityId;

	public ExternalErrorResponseException(String message) {
		super(message);
		conditionsResult = null;
		fromStatus = null;
		toStatus = null;
		entityId = null;
	}

	public ExternalErrorResponseException(Throwable cause) {
		super(cause);
		conditionsResult = null;
		fromStatus = null;
		toStatus = null;
		entityId = null;
	}

	public ExternalErrorResponseException(String message, Throwable cause) {
		super(message, cause);
		conditionsResult = null;
		fromStatus = null;
		toStatus = null;
		entityId = null;
	}

	public ExternalErrorResponseException(String message, EvaluateConditionsResult conditionsResult, String fromStatus,
			String toStatus, Integer entityId) {
		super(message);
		this.conditionsResult = conditionsResult;
		this.fromStatus = fromStatus;
		this.toStatus = toStatus;
		this.entityId = entityId;
	}

	public EvaluateConditionsResult getEvaluateConditionsResult() {
		return conditionsResult;
	}

	public String getFromStatus() {
		return fromStatus;
	}

	public String getToStatus() {
		return toStatus;
	}

	public Integer getEntityId() {
		return entityId;
	}
}