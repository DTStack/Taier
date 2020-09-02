package com.dtstack.engine.api.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuemo
 * @company www.dtstack.com
 * @Date 2020-04-15
 */
@ApiModel
public class ParamActionExt extends ParamAction {
    private static final Logger logger = LoggerFactory.getLogger(ParamActionExt.class);

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    /**
     * RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)
     */
    @ApiModelProperty(notes = "RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)")
    private Integer appType;

    private String jobKey;

    public Long getTaskSourceId() {
        return taskSourceId;
    }

    public void setTaskSourceId(Long taskSourceId) {
        this.taskSourceId = taskSourceId;
    }

    /**
     * 任务id
     */
    private Long taskSourceId;

    /**
     * 发起操作的用户
     */
    @ApiModelProperty(notes = "发起操作的用户")
    private Long createUserId;

    /**
     * 0正常调度 1补数据 2临时运行
     */
    @ApiModelProperty(notes = "0正常调度 1补数据 2临时运行")
    private Integer type;

    private Integer isRestart;

    /**
     * 业务日期 yyyymmddhhmmss,调度时间-1d
     */
    @ApiModelProperty(notes = "业务日期 yyyymmddhhmmss,调度时间-1d")
    private String businessDate;

    /***
     * 任务调度时间 yyyymmddhhmmss
     */
    @ApiModelProperty(notes = "任务调度时间 yyyymmddhhmmss")
    private String cycTime;

    private Integer dependencyType;

    private String flowJobId;

    private Integer versionId;

    private Long projectId;

    @Deprecated
    private Boolean isFailRetry;

    private Integer periodType;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public Integer getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(Integer dependencyType) {
        this.dependencyType = dependencyType;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getIsRestart() {
        return isRestart;
    }

    public void setIsRestart(Integer isRestart) {
        this.isRestart = isRestart;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public Boolean getIsFailRetry() {
        return isFailRetry;
    }

    public void setIsFailRetry(Boolean failRetry) {
        isFailRetry = failRetry;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }
}
