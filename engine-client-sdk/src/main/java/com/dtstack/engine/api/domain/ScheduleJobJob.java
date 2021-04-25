package com.dtstack.engine.api.domain;


import io.swagger.annotations.ApiModel;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class ScheduleJobJob extends AppTenantEntity {

    private String jobKey;

    private String parentJobKey;

    private Integer parentAppType;


    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getParentJobKey() {
        return parentJobKey;
    }

    public void setParentJobKey(String parentJobKey) {
        this.parentJobKey = parentJobKey;
    }

    public Integer getParentAppType() {
        return parentAppType;
    }

    public void setParentAppType(Integer parentAppType) {
        this.parentAppType = parentAppType;
    }
}
