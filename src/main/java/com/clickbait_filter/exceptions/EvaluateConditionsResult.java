
package com.clickbait_filter.exceptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EvaluateConditionsResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean conditionsFullfiled;
	private List<ConditionResult> conditionResults;

	public EvaluateConditionsResult() {
		conditionsFullfiled = true;
		conditionResults = new ArrayList<>();
	}

	public boolean isConditionsFullfiled() {
		return this.conditionsFullfiled;
	}

	public void setConditionsFullfiled(boolean conditionsFullfiled) {
		this.conditionsFullfiled = conditionsFullfiled;
	}

	public List<ConditionResult> getConditionResults() {
		return this.conditionResults;
	}

	public void setConditionResults(List<ConditionResult> conditionResults) {
		this.conditionResults = conditionResults;
	}

	public void addConditionResult(ConditionResult conditionResult) {
		if (conditionResults == null) {
			conditionResults = new ArrayList<>();
		}
		conditionResults.add(conditionResult);
	}
}