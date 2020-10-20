package com.dtstack.engine.api.domain.po;


import java.util.Date;

/**
 * @author 青涯
 */
public class AlertGatePO {

    private Long id;
    
    /**
     * 引入租户id,同时支持官方通道和自定义通道
     */
    private Long tenantId;

    private String alertGateName;

    /**
     * <p>
     *     告警通道类型,见{}
     * </p>
     */
    private Integer alertGateType;

    private String alertGateJson;

    private String alertGateCode;

    private Integer alertGateStatus;

    /**
     * 通道标识，业务唯一键
     * isDelete=0时，仅存在一个alertGateSource
     * isDelete=1时，可能存在多个alertGateSource
     */
    private String alertGateSource;
    
    private String filePath;

    private Integer isDeleted;

    private Date gmtCreated;

    private Date gmtModified;

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

    public Integer getAlertGateType() {
        return alertGateType;
    }

    public void setAlertGateType(Integer alertGateType) {
        this.alertGateType = alertGateType;
    }

    public String getAlertGateJson() {
        return alertGateJson;
    }

    public void setAlertGateJson(String alertGateJson) {
        this.alertGateJson = alertGateJson;
    }

    public String getAlertGateCode() {
        return alertGateCode;
    }

    public void setAlertGateCode(String alertGateCode) {
        this.alertGateCode = alertGateCode;
    }

    public Integer getAlertGateStatus() {
        return alertGateStatus;
    }

    public void setAlertGateStatus(Integer alertGateStatus) {
        this.alertGateStatus = alertGateStatus;
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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

    @Override
    public String toString() {
        return "AlertGatePO{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", alertGateName='" + alertGateName + '\'' +
                ", alertGateType=" + alertGateType +
                ", alertGateJson='" + alertGateJson + '\'' +
                ", alertGateCode='" + alertGateCode + '\'' +
                ", alertGateStatus=" + alertGateStatus +
                ", alertGateSource='" + alertGateSource + '\'' +
                ", filePath='" + filePath + '\'' +
                ", isDeleted=" + isDeleted +
                ", gmtCreated=" + gmtCreated +
                ", gmtModified=" + gmtModified +
                '}';
    }
}
