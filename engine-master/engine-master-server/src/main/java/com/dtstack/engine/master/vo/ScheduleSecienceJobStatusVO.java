/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.vo;

import io.swagger.annotations.ApiModel;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
@ApiModel
public class ScheduleSecienceJobStatusVO {
	
	private Long totalLabCount;
	
	private Long totalNotebookCount;
	
	private Long deployLabCount;
	
	private Long deployNotebookCount;
	
	private Long failLabCount;
	
	private Long failNotebookCount;
	
	private Long successLabCount;
	
	private Long successNotebookCount;

	public Long getTotalLabCount() {
		return totalLabCount;
	}

	public void setTotalLabCount(Long totalLabCount) {
		this.totalLabCount = totalLabCount;
	}

	public Long getTotalNotebookCount() {
		return totalNotebookCount;
	}

	public void setTotalNotebookCount(Long totalNotebookCount) {
		this.totalNotebookCount = totalNotebookCount;
	}

	public Long getDeployLabCount() {
		return deployLabCount;
	}

	public void setDeployLabCount(Long deployLabCount) {
		this.deployLabCount = deployLabCount;
	}

	public Long getDeployNotebookCount() {
		return deployNotebookCount;
	}

	public void setDeployNotebookCount(Long deployNotebookCount) {
		this.deployNotebookCount = deployNotebookCount;
	}

	public Long getFailLabCount() {
		return failLabCount;
	}

	public void setFailLabCount(Long failLabCount) {
		this.failLabCount = failLabCount;
	}

	public Long getFailNotebookCount() {
		return failNotebookCount;
	}

	public void setFailNotebookCount(Long failNotebookCount) {
		this.failNotebookCount = failNotebookCount;
	}

	public Long getSuccessLabCount() {
		return successLabCount;
	}

	public void setSuccessLabCount(Long successLabCount) {
		this.successLabCount = successLabCount;
	}

	public Long getSuccessNotebookCount() {
		return successNotebookCount;
	}

	public void setSuccessNotebookCount(Long successNotebookCount) {
		this.successNotebookCount = successNotebookCount;
	}

}
