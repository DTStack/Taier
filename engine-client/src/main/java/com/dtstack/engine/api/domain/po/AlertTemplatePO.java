package com.dtstack.engine.api.domain.po;

import java.util.Date;


/**
 * <p>
 *     告警模板
 *     <sql>
 *         CREATE TABLE dt_alert_template (
 *            id bigint(11) PRIMARY KEY AUTO_INCREMENT,
 *            alert_template_name VARCHAR(32),
 *            alert_template_type SMALLINT(2),
 *            alert_template_status SMALLINT(2),
 *            alert_template VARCHAR(4096),
 *            is_deleted SMALLINT(2),
 *            gmt_created timestamp,
 *            gmt_modified timestamp
 *            );
 *     </sql>
 * </p>
 */
public class AlertTemplatePO {

    private Long id;

    private String alertTemplateName;

    private int alertTemplateType;

    private String alertTemplate;

    private int alertTemplateStatus;
    
    private String alertGateSource;

    private int isDeleted;

    private Date gmtCreated;

    private Date gmtModified;

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

    public int getAlertTemplateType() {
        return alertTemplateType;
    }

    public void setAlertTemplateType(int alertTemplateType) {
        this.alertTemplateType = alertTemplateType;
    }

    public String getAlertTemplate() {
        return alertTemplate;
    }

    public void setAlertTemplate(String alertTemplate) {
        this.alertTemplate = alertTemplate;
    }

    public int getAlertTemplateStatus() {
        return alertTemplateStatus;
    }

    public void setAlertTemplateStatus(int alertTemplateStatus) {
        this.alertTemplateStatus = alertTemplateStatus;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
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
