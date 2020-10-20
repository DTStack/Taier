package com.dtstack.engine.api.domain.po;

import java.util.Date;
import java.util.List;

/**
 * @author 青涯
 */
public class ClusterAlertPO {

    private Integer id;

    private Integer clusterId;

    private Integer alertId;

    private Integer isDefault;


    private Date gmtCreated;

    private Date gmtModified;

    private String alertGateName;

    private String alertGateSource;

    private Integer alertGateType;

    private Integer isDeleted;

    //for query
    private List<Integer> alertGateTypes;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getAlertId() {
        return alertId;
    }

    public void setAlertId(Integer alertId) {
        this.alertId = alertId;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
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

    public String getAlertGateName() {
        return alertGateName;
    }

    public void setAlertGateName(String alertGateName) {
        this.alertGateName = alertGateName;
    }

    public String getAlertGateSource() {
        return alertGateSource;
    }

    public void setAlertGateSource(String alertGateSource) {
        this.alertGateSource = alertGateSource;
    }

    public Integer getAlertGateType() {
        return alertGateType;
    }

    public void setAlertGateType(Integer alertGateType) {
        this.alertGateType = alertGateType;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<Integer> getAlertGateTypes() {
        return alertGateTypes;
    }

    public void setAlertGateTypes(List<Integer> alertGateTypes) {
        this.alertGateTypes = alertGateTypes;
    }
}
