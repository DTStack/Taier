package com.dtstack.taier.scheduler.dto.schedule;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2021/12/31 1:37 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskShadeDTO {

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务类型：
     * 虚节点:VIRTUAL(-)1,
     * SparkSQL:SPARK_SQL(0),
     * Spark:SPARK(1),
     * 数据同步:SYNC(2),
     * Shell: SHELL(3),
     * 工作流:WORK_FLOW(1)
     */
    private Integer taskType;

    /**
     * 计算类型： 0 批处理，1 流处理
     */
    private Integer computeType;

    /**
     * 存储sql字段
     */
    private String sqlText;

    /**
     * 任务环境参数
     */
    private String taskParams;

    /**
     * 离线任务id
     */
    private Long taskId;

    /**
     * 调度规则
     */
    private String scheduleConf;

    /**
     * 调度的任务类型:
     * 分钟:MIN(0),
     * 小时:HOUR(1),
     * 天:DAY(2),
     * 周:WEEK(3),
     * 月:MONTH(4),
     * 自定义cron表达式:CRON(5)
     */
    private Integer periodType;

    /**
     * 调度状态：0 正常 1冻结 2停止
     */
    private Integer scheduleStatus;

    /**
     * 生成日期
     */
    private Timestamp gmtCreate;

    /**
     * 最近一次修改日期
     */
    private Timestamp gmtModified;

    /**
     * 创建人
     */
    private Long createUserId;

    /**
     * 最近一次修改人id
     */
    private Long modifyUserId;

    /**
     * 任务负责人id
     */
    private Long ownerUserId;

    /**
     * 任务运行参数
     */
    private String extraInfo;

    /**
     * 版本id
     */
    private Integer versionId;

    /**
     * 是否逻辑删除
     */
    private Integer isDeleted;

    /**
     * 任务备注
     */
    private String taskDesc;

    /**
     * 工作流id
     */
    private Long flowId;

    /**
     * 任务组件版本
     */
    private String componentVersion;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getScheduleConf() {
        return scheduleConf;
    }

    public void setScheduleConf(String scheduleConf) {
        this.scheduleConf = scheduleConf;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }
}
