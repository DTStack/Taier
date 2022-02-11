/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taiga.pluginapi;

import com.dtstack.taiga.pluginapi.constrant.ConfigConstant;
import com.dtstack.taiga.pluginapi.enums.ComputeType;
import com.dtstack.taiga.pluginapi.enums.EJobType;
import com.dtstack.taiga.pluginapi.enums.EQueueSourceType;
import com.dtstack.taiga.pluginapi.exception.PluginDefineException;
import com.dtstack.taiga.pluginapi.pojo.JobResult;
import com.dtstack.taiga.pluginapi.pojo.ParamAction;
import com.dtstack.taiga.pluginapi.util.MathUtil;
import com.dtstack.taiga.pluginapi.util.PublicUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class JobClient implements Serializable {

    protected long priority = 0;

    /**
     * 默认的优先级，值越小，优先级越高
     */
    private static final int DEFAULT_PRIORITY_LEVEL_VALUE = 10;

    /**
     * 用户填写的优先级占的比重
     */
    private static final int PRIORITY_LEVEL_WEIGHT = 100000;

    private JobClientCallBack jobClientCallBack;

    private List<JarFileInfo> attachJarInfos = Lists.newArrayList();

    private JarFileInfo coreJarInfo;

    private Properties confProperties;

    private String sql;

    private String taskParams;

    private String jobName;

    private String jobId;

    private String engineTaskId;

    private String applicationId;

    private EJobType jobType;

    private ComputeType computeType;

    private JobResult jobResult;

    /**
     * externalPath 不为null则为从保存点恢复
     */
    private String externalPath;

    /**
     * 提交MR执行的时候附带的执行参数
     */
    private String classArgs;


    private String groupName;

    private long generateTime;

    private int maxRetryNum;

    private volatile long lackingCount;

    private Long tenantId;

    private Integer queueSourceType;

    private Long submitCacheTime;

    private Boolean isForceCancel;

    /**
     * 重试超时时间
     */
    private long submitExpiredTime;

    /**
     * 重试间隔时间
     */
    private Long retryIntervalTime;

    /**
     * 任务运行版本
     */
    private String componentVersion;
    /**
     * 0正常调度 1补数据 2临时运行
     */
    private Integer type;

    private Integer deployMode;

    private String pluginInfo;

    private Long userId;

    private Integer taskType;


    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public JobClient() {

    }

    public JobClient(ParamAction paramAction) throws Exception {
        this.sql = paramAction.getSqlText();
        this.taskParams = paramAction.getTaskParams();
        this.jobName = paramAction.getName();
        this.jobId = paramAction.getJobId();
        this.engineTaskId = paramAction.getEngineTaskId();
        this.applicationId = paramAction.getApplicationId();
        this.computeType = ComputeType.getType(paramAction.getComputeType());
        this.externalPath = paramAction.getExternalPath();
        this.classArgs = paramAction.getExeArgs();
        this.generateTime = paramAction.getGenerateTime();
        this.lackingCount = paramAction.getLackingCount();
        this.tenantId = paramAction.getTenantId();
        this.queueSourceType = EQueueSourceType.NORMAL.getCode();
        this.submitExpiredTime = paramAction.getSubmitExpiredTime();
        this.retryIntervalTime = paramAction.getRetryIntervalTime();
        this.componentVersion = paramAction.getComponentVersion();
        this.taskType = paramAction.getTaskType();

        this.maxRetryNum = paramAction.getMaxRetryNum() == null ? 0 : paramAction.getMaxRetryNum();
        if (taskParams != null) {
            this.confProperties = PublicUtil.stringToProperties(taskParams);
        }
        if (paramAction.getPriority() <= 0) {
            String valStr = confProperties == null ? null : confProperties.getProperty(ConfigConstant.CUSTOMER_PRIORITY_VAL);
            int priorityLevel = valStr == null ? DEFAULT_PRIORITY_LEVEL_VALUE : MathUtil.getIntegerVal(valStr);
            //设置priority值, 值越小，优先级越高
            this.priority = paramAction.getGenerateTime() + (long) priorityLevel * PRIORITY_LEVEL_WEIGHT;
        } else {
            priority = paramAction.getPriority();
        }
        this.groupName = paramAction.getGroupName();
        if (StringUtils.isBlank(groupName)) {
            groupName = ConfigConstant.DEFAULT_GROUP_NAME;
        }
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public ParamAction getParamAction() {
        ParamAction action = new ParamAction();
        action.setSqlText(sql);
        action.setTaskParams(taskParams);
        action.setName(jobName);
        action.setJobId(jobId);
        action.setEngineTaskId(engineTaskId);
        action.setComputeType(computeType.getType());
        action.setExternalPath(externalPath);
        action.setExeArgs(classArgs);
        action.setGroupName(groupName);
        action.setGenerateTime(generateTime);
        action.setPriority(priority);
        action.setApplicationId(applicationId);
        action.setMaxRetryNum(maxRetryNum);
        action.setLackingCount(lackingCount);
        action.setTenantId(tenantId);
        action.setRetryIntervalTime(retryIntervalTime);
        action.setSubmitExpiredTime(submitExpiredTime);
        action.setComponentVersion(componentVersion);
        action.setTaskType(taskType);
        return action;
    }


    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public Integer getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(Integer deployMode) {
        this.deployMode = deployMode;
    }

    public Boolean getForceCancel() {
        return isForceCancel;
    }

    public void setForceCancel(Boolean forceCancel) {
        isForceCancel = forceCancel;
    }

    public Integer getQueueSourceType() {
        return queueSourceType;
    }

    public void setQueueSourceType(Integer queueSourceType) {
        this.queueSourceType = queueSourceType;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getEngineTaskId() {
        return engineTaskId;
    }

    public void setEngineTaskId(String engineTaskId) {
        this.engineTaskId = engineTaskId;
    }


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public EJobType getJobType() {
        return jobType;
    }

    public void setJobType(EJobType jobType) {
        this.jobType = jobType;
    }

    public JobResult getJobResult() {
        return jobResult;
    }

    public void setJobResult(JobResult jobResult) {
        this.jobResult = jobResult;
    }

    public ComputeType getComputeType() {
        return computeType;
    }

    public void setComputeType(ComputeType computeType) {
        this.computeType = computeType;
    }

    public Properties getConfProperties() {
        return confProperties;
    }

    public List<JarFileInfo> getAttachJarInfos() {
        return attachJarInfos;
    }

    public void setAttachJarInfos(List<JarFileInfo> attachJarInfos) {
        this.attachJarInfos = attachJarInfos;
    }

    public void addAttachJarInfo(JarFileInfo jarFileInfo) {
        attachJarInfos.add(jarFileInfo);
    }

    public void doStatusCallBack(Integer status) {
        if (jobClientCallBack == null) {
            throw new PluginDefineException("not set jobClientCallBak...");
        }
        jobClientCallBack.updateStatus(status);
    }

    public void setCallBack(JobClientCallBack jobClientCallBack) {
        this.jobClientCallBack = jobClientCallBack;
    }

    public JobClientCallBack getJobCallBack() {
        return jobClientCallBack;
    }

    public String getSql() {
        return sql;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public String getClassArgs() {
        return classArgs;
    }

    public void setClassArgs(String classArgs) {
        this.classArgs = classArgs;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public JarFileInfo getCoreJarInfo() {
        return coreJarInfo;
    }

    public void setCoreJarInfo(JarFileInfo coreJarInfo) {
        this.coreJarInfo = coreJarInfo;
    }

    public long getGenerateTime() {
        if (generateTime <= 0) {
            generateTime = System.currentTimeMillis();
        }
        return generateTime;
    }

    public void setGenerateTime(long generateTime) {
        this.generateTime = generateTime;
    }

    public int getApplicationPriority() {
        return Integer.MAX_VALUE - (int) (getPriority() / 1000);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public int getMaxRetryNum() {
        return maxRetryNum;
    }

    public boolean getIsFailRetry() {
        return maxRetryNum > 0;
    }

    public void setMaxRetryNum(int maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
    }

    public String getClusterType() {
        return null;
    }

    public String getResourceType() {
        return null;
    }

    public long getLackingCount() {
        return lackingCount;
    }

    public void setLackingCount(long lackingCount) {
        this.lackingCount = lackingCount;
    }

    public long lackingCountIncrement() {
        return lackingCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobClient jobClient = (JobClient) o;
        return jobId != null ? jobClient.equals(jobClient.jobId) : jobClient.jobId == null;
    }

    public Long getSubmitCacheTime() {
        return submitCacheTime;
    }

    public void setSubmitCacheTime(Long submitCacheTime) {
        this.submitCacheTime = submitCacheTime;
    }

    public long getSubmitExpiredTime() {
        return submitExpiredTime;
    }

    public void setSubmitExpiredTime(long submitExpiredTime) {
        this.submitExpiredTime = submitExpiredTime;
    }

    public Long getRetryIntervalTime() {
        return retryIntervalTime;
    }

    public void setRetryIntervalTime(Long retryIntervalTime) {
        this.retryIntervalTime = retryIntervalTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public String toString() {
        return "JobClient{" +
                "jobClientCallBack=" + jobClientCallBack +
                ", attachJarInfos=" + attachJarInfos +
                ", coreJarInfo=" + coreJarInfo +
                ", confProperties=" + confProperties +
                ", sql='" + sql + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobId='" + jobId + '\'' +
                ", engineTaskId='" + engineTaskId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", jobType=" + jobType +
                ", computeType=" + computeType +
                ", jobResult=" + jobResult +
                ", externalPath='" + externalPath + '\'' +
                ", classArgs='" + classArgs + '\'' +
                ", groupName='" + groupName + '\'' +
                ", generateTime=" + generateTime +
                ", maxRetryNum=" + maxRetryNum +
                ", lackingCount=" + lackingCount +
                ", tenantId=" + tenantId +
                '}';
    }
}
