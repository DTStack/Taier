package com.dtstack.engine.api.dto;


import java.util.Date;

/**
 * <p>
 *     告警通道数据对象
 * </p>
 * @author 青涯
 */
public class AlertGateDTO {

    private Long id;

	private Long tenantId;

    private String alertGateName;

    private int alertGateType;

    private String alertGateJson;

    private int alertGateStatus;

    private String alertGateCode;

    private int isDeleted;

    private Date gmtCreated;

    private Date gmtModified;
    
    private String alertGateSource;
    
    private String filePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTenantId() {
    	return tenantId;
    }
    
    public void setTenantId(Long tenantId) {
    	this.tenantId = tenantId;
    }

    public String getAlertGateName() {
        return alertGateName;
    }

    public void setAlertGateName(String alertGateName) {
        this.alertGateName = alertGateName;
    }

    public int getAlertGateType() {
        return alertGateType;
    }

    public void setAlertGateType(int alertGateType) {
        this.alertGateType = alertGateType;
    }

    public String getAlertGateJson() {
        return alertGateJson;
    }

    public void setAlertGateJson(String alertGateJson) {
        this.alertGateJson = alertGateJson;
    }

    public int getAlertGateStatus() {
        return alertGateStatus;
    }

    public void setAlertGateStatus(int alertGateStatus) {
        this.alertGateStatus = alertGateStatus;
    }

    public String getAlertGateCode() {
        return alertGateCode;
    }

    public void setAlertGateCode(String alertGateCode) {
        this.alertGateCode = alertGateCode;
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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
