package com.dtstack.taier.dao.domain;


public class TaskVersion extends TenantEntity {
    /**
     * 任务 ID
     */
    private Long taskId;

    /**
     * 'sql 文本'
     */
    private String sqlText;

    /**
     * 'sql 文本'
     */
    private String originSql;

    /**
     * 'sql 文本'
     */
    private String publishDesc;

    /**
     * 新建task的用户
     */
    private Long createUserId;

    /**
     * 最后修改task的用户
     */
    private Long modifyUserId;

    /**
     * 'task版本'
     */
    private Integer version;

    /**
     * 组件版本：此处对应流计算任务而言就是 flink 版本
     */
    private String componentVersion;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 0 向导模式  1 脚本模式
     */
    private Integer createModel;

    private String sourceStr;

    private String targetStr;

    private String settingStr;

    /**
     * 环境参数
     */
    private String taskParams;

    /**
     * 执行参数
     */
    private String exeArgs;

    /**
     * 调度配置 json格式
     */
    private String scheduleConf;

    /**
     * 周期类型
     */
    private Integer periodType;

    /**
     * 0未开始,1正常调度,2暂停
     */
    private Integer scheduleStatus;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getOriginSql() {
        return originSql;
    }

    public void setOriginSql(String originSql) {
        this.originSql = originSql;
    }

    public String getPublishDesc() {
        return publishDesc;
    }

    public void setPublishDesc(String publishDesc) {
        this.publishDesc = publishDesc;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public Integer getCreateModel() {
        return createModel;
    }

    public void setCreateModel(Integer createModel) {
        this.createModel = createModel;
    }

    public String getSourceStr() {
        return sourceStr;
    }

    public void setSourceStr(String sourceStr) {
        this.sourceStr = sourceStr;
    }

    public String getTargetStr() {
        return targetStr;
    }

    public void setTargetStr(String targetStr) {
        this.targetStr = targetStr;
    }

    public String getSettingStr() {
        return settingStr;
    }

    public void setSettingStr(String settingStr) {
        this.settingStr = settingStr;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public String getExeArgs() {
        return exeArgs;
    }

    public void setExeArgs(String exeArgs) {
        this.exeArgs = exeArgs;
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
}