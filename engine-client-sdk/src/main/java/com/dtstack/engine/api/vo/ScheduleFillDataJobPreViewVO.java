package com.dtstack.engine.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class ScheduleFillDataJobPreViewVO {

    private Long id;

    private String fillDataJobName;

    private String fromDay;

    private String toDay;

    private String createTime;

    private String dutyUserName;

    /**
     * 成功job数量
     */
    @ApiModelProperty(notes = "成功job数量")
    private Long finishedJobSum;

    /**
     * 所有job数量
     */
    @ApiModelProperty(notes = "所有job数量")
    private Long allJobSum;

    /**
     * 完成的job数量
     */
    @ApiModelProperty(notes = "完成的job数量")
    private Long doneJobSum;

    @ApiModelProperty(notes = "项目id")
    private Long projectId;

    /**
     * 责任人
     * @return
     */
    @ApiModelProperty(notes = "责任人")
    private Long dutyUserId;

    public Long getDutyUserId() {
        return dutyUserId;
    }

    public void setDutyUserId(Long dutyUserId) {
        this.dutyUserId = dutyUserId;
    }

    public Long getAllJobSum() {
        return allJobSum;
    }

    public void setAllJobSum(Long allJobSum) {
        this.allJobSum = allJobSum;
    }

    public Long getDoneJobSum() {
        return doneJobSum;
    }

    public void setDoneJobSum(Long doneJobSum) {
        this.doneJobSum = doneJobSum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFillDataJobName() {
        return fillDataJobName;
    }

    public void setFillDataJobName(String fillDataJobName) {
        this.fillDataJobName = fillDataJobName;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDutyUserName() {
        return dutyUserName;
    }

    public void setDutyUserName(String dutyUserName) {
        this.dutyUserName = dutyUserName;
    }

    public Long getFinishedJobSum() {
        return finishedJobSum;
    }

    public void setFinishedJobSum(Long finishedJobSum) {
        this.finishedJobSum = finishedJobSum;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
