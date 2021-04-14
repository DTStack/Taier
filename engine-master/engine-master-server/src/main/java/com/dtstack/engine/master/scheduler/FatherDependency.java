package com.dtstack.engine.master.scheduler;

/**
 * @Auther: dazhi
 * @Date: 2021/3/15 6:59 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FatherDependency {

    private String jobKey;

    private Integer appType;

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}
