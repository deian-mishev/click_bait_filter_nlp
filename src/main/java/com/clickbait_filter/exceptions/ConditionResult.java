package com.clickbait_filter.exceptions;

import java.io.Serializable;

public class ConditionResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean conditionFullfiled;
	private String conditionMessage;
	private String conditionName;

	public ConditionResult(String conditionName) {
		this.conditionFullfiled = true;
		this.conditionMessage = "";
		this.conditionName = conditionName;
	}

	public ConditionResult(String conditionName, boolean conditionFullfiled, String conditionMessage) {
		this.conditionName = conditionName;
		this.conditionFullfiled = conditionFullfiled;
		this.conditionMessage = conditionMessage;
	}

	public boolean isConditionFullfiled() {
		return this.conditionFullfiled;
	}

	public void setConditionFullfiled(boolean conditionFullfiled) {
		this.conditionFullfiled = conditionFullfiled;
	}

	public String getConditionMessage() {
		return this.conditionMessage;
	}

	public void setConditionMessage(String conditionMessage) {
		this.conditionMessage = conditionMessage;
	}

	public String getConditionName() {
		return this.conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
}