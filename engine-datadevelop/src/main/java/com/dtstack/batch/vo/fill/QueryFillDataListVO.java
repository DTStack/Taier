package com.dtstack.batch.vo.fill;

import com.dtstack.batch.vo.base.PageVO;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 1:40 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryFillDataListVO extends PageVO {

    /**
     * 补数据名称
     */
    private String jobName;

    /**
     * 操作人用户id
     */
    private Long userId;

    /**
     * 业务日期
     */
    private String bizDay;

    /**
     * 补数据运行 格式yyyy-MM-dd
     */
    private String runDay;

    /**
     * 租户id
     */
    private Long tenantId;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBizDay() {
        return bizDay;
    }

    public void setBizDay(String bizDay) {
        this.bizDay = bizDay;
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
}
