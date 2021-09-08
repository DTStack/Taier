package com.dtstack.engine.domain;


import com.dtstack.engine.common.annotation.Unique;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class ScheduleFillDataJob extends AppTenantEntity {

    @Unique
    private String jobName;

    private String runDay;

    private String fromDay;

    private String toDay;

    /**
     * 发起操作的用户
     */
    @ApiModelProperty(notes = "发起操作的用户")
    private Long createUserId;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getRunDay() {
        return runDay;
    }

    public void setRunDay(String runDay) {
        this.runDay = runDay;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getFromDay() {
        return fromDay;
    }

    public void setFromDay(String fromDay) {
        this.fromDay = fromDay;
    }

    public String getToDay() {
        return toDay;
    }

    public void setToDay(String toDay) {
        this.toDay = toDay;
    }

}
