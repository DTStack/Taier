package com.dtstack.engine.api.param;

import com.dtstack.engine.api.domain.BaseEntity;

/**
 * Date: 2020/7/29
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class SecurityLogParam extends BaseEntity {

    private String sign;
    private String appTag;

    private Long tenantId;
    private Long startTime;
    private Long endTime;
    private String operator;
    private Integer currentPage;
    private Integer pageSize;
    private String oprtation;
    private String operationObject;
    private Boolean isRoot;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppTag() {
        return appTag;
    }

    public void setAppTag(String appTag) {
        this.appTag = appTag;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOprtation() {
        return oprtation;
    }

    public void setOprtation(String oprtation) {
        this.oprtation = oprtation;
    }

    public String getOperationObject() {
        return operationObject;
    }

    public void setOperationObject(String operationObject) {
        this.operationObject = operationObject;
    }

    public Boolean getRoot() {
        return isRoot;
    }

    public void setRoot(Boolean root) {
        isRoot = root;
    }


}
