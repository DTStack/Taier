package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.pager.PageQuery;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/1/13 10:11 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertRecordJoinDTO {

    private Long recordId;
    private List<Long> recordIds;
    private Long tenantId;
    private Long projectId;
    private Long userId;
    private Integer appType;
    private Integer readStatus;

    private Integer currentPage;
    private Integer pageSize;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getCurrentPage() {
        if (currentPage ==null || currentPage<=0) {
            currentPage = 1;
        }

        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        if (pageSize ==null || pageSize<=0) {
            pageSize = 10;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    public List<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }
}
