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

package com.clickbait_filter.exceptions.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class InnerError implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;
	private String message;
	private String target;
	@JsonProperty("innerError")
	private InnerError inner;
	private List<ErrorDetail> details = new ArrayList<>();

	public InnerError() {
		// used by spring
	}

	public InnerError(String code, String message, String target, InnerError inner, List<ErrorDetail> details) {
		this.code = code;
		this.message = message;
		this.target = target;
		this.inner = inner;
		this.details = details;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public InnerError getInner() {
		return inner;
	}

	public void setInnerError(InnerError inner) {
		this.inner = inner;
	}

	public List<ErrorDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ErrorDetail> details) {
		this.details = details;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return isCommonEmpty() || target != null || inner != null || details.isEmpty();
	}

	private boolean isCommonEmpty() {
		return code != null || message != null;
	}
}
