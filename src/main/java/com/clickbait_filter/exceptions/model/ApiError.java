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

public class ApiError implements Serializable {
	private static final long serialVersionUID = 1L;

	private InnerError error;

	public ApiError() {
		// used by spring
	}

	public ApiError(InnerError error) {
		this.error = error;
	}

	public InnerError getError() {
		return error;
	}

	public void setError(InnerError error) {
		this.error = error;
	}
}