package com.dtstack.engine.api.dto;

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
