package com.dtstack.taier.scheduler.dto.fill;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 1:40 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryFillDataListDTO {

    /**
     * 补数据名称
     */
    private String jobName;

    /**
     * 操作人用户id
     */
    private Long operatorId;

    /**
     * 补数据运行时间 格式yyyy-MM-dd
     */
    private String runDay;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 当前页码
     */
    private Integer currentPage;

    /**
     * 当前页数
     */
    private Integer pageSize;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getRunDay() {
        return runDay;
    }

    public void setRunDay(String runDay) {
        this.runDay = runDay;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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
}
