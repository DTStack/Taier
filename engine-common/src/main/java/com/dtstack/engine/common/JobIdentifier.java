package com.dtstack.engine.common;

import com.dtstack.engine.common.enums.EDeployMode;

import java.io.Serializable;

/**
 * Company: www.dtstack.com
 * @author yuebai
 */

public class JobIdentifier implements Serializable {

    private String engineJobId;

    private String applicationId;

    private String taskId;

    private Long tenantId;

    private String engineType;

    /**
     * 默认 perjob
     */
    private Integer deployMode = EDeployMode.PERJOB.getType();

    private Long userId;

    private String pluginInfo;

    private Long timeout;

    private JobIdentifier() {

    }

    public JobIdentifier(String engineJobId, String applicationId, String taskId, Long tenantId, String engineType, Integer deployMode, Long userId, String pluginInfo) {
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.taskId = taskId;
        this.tenantId = tenantId;
        this.engineType = engineType;
        this.deployMode = deployMode;
        this.userId = userId;
        this.pluginInfo = pluginInfo;
    }

    private JobIdentifier(String engineJobId, String applicationId, String taskId) {
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.taskId = taskId;
    }

    public static JobIdentifier createInstance(String engineJobId, String applicationId, String taskId) {
        return new JobIdentifier(engineJobId, applicationId, taskId);
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getTaskId() {
        return taskId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getEngineType() {
        return engineType;
    }

    public Integer getDeployMode() {
        return deployMode;
    }

    public Long getUserId() {
        return userId;
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
                ", timeout=" + timeout +
                '}';
    }
}
