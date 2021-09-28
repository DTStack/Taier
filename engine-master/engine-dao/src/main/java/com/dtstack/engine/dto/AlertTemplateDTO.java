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

package com.dtstack.engine.dto;

import java.util.Date;

public class AlertTemplateDTO {
	
	private Long id;
	
	private String alertTemplateName;
	
	private Integer alertTemplateType;
	
	private Integer alertTemplateStatus;
	
	private String alertTemplate;
	
    private Integer isDeleted;

    private Date gmtCreated;

    private Date gmtModified;
    
    private String alertGateSource;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAlertTemplateName() {
		return alertTemplateName;
	}

	public void setAlertTemplateName(String alertTemplateName) {
		this.alertTemplateName = alertTemplateName;
	}

	public Integer getAlertTemplateType() {
		return alertTemplateType;
	}

	public void setAlertTemplateType(Integer alertTemplateType) {
		this.alertTemplateType = alertTemplateType;
	}

	public Integer getAlertTemplateStatus() {
		return alertTemplateStatus;
	}

	public void setAlertTemplateStatus(Integer alertTemplateStatus) {
		this.alertTemplateStatus = alertTemplateStatus;
	}

	public String getAlertTemplate() {
		return alertTemplate;
	}

	public void setAlertTemplate(String alertTemplate) {
		this.alertTemplate = alertTemplate;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Date getGmtCreated() {
		return gmtCreated;
	}

	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public String getAlertGateSource() {
		return alertGateSource;
	}

	public void setAlertGateSource(String alertGateSource) {
		this.alertGateSource = alertGateSource;
	}

}
