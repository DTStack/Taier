package com.dtstack.engine.common;

import java.io.Serializable;

/**
 * Reason:
 * Date: 2018/11/5
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobIdentifier implements Serializable {

    private String engineJobId;

    private String applicationId;

    private String taskId;

    private Long tenantId;

    private String engineType;

    private Integer deployMode;

    private Long userId;

    private String pluginInfo;

    public JobIdentifier(String engineJobId, String applicationId, String taskId, Long tenantId, String engineType, Integer deployMode, Long userId,String pluginInfo) {
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.taskId = taskId;
        this.tenantId = tenantId;
        this.engineType = engineType;
        this.deployMode = deployMode;
        this.userId = userId;
        this.pluginInfo = pluginInfo;
    }

    public JobIdentifier(String engineJobId, String applicationId, String taskId){
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.taskId = taskId;
    }

    public static JobIdentifier createInstance(String jobId, String applicationId, String taskId){
        return new JobIdentifier(jobId, applicationId, taskId);
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Integer getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(Integer deployMode) {
        this.deployMode = deployMode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "JobIdentifier{" +
                "engineJobId='" + engineJobId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", tenantId=" + tenantId +
                ", engineType=" + engineType +
                ", deployMode=" + deployMode +
                ", userId=" + userId +
                '}';
    }
}
