package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

@TableName("develop_task_version")
public class TaskVersion {
    /**
     * 任务 ID
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 'sql 文本'
     */
    @TableField("sql_text")
    private String sqlText;

    /**
     * 'sql 文本'
     */
    @TableField("origin_sql")
    private String originSql;

    /**
     * 'sql 文本'
     */
    @TableField("publish_desc")
    private String publishDesc;

    /**
     * 新建task的用户
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 'task版本'
     */
    @TableField("version")
    private Integer version;

    /**
     * 组件版本：此处对应流计算任务而言就是 flink 版本
     */
    @TableField(exist = false)
    private String componentVersion;

    /**
     * 任务描述
     */
    @TableField(exist = false)
    private String taskDesc;

    /**
     * 0 向导模式  1 脚本模式
     */
    @TableField(exist = false)
    private Integer createModel;

    @TableField(exist = false)
    private String sourceStr;

    @TableField(exist = false)
    private String targetStr;

    @TableField(exist = false)
    private String settingStr;

    /**
     * 环境参数
     */
    @TableField("task_params")
    private String taskParams;

    /**
     * 执行参数
     */
    @TableField(exist = false)
    private String exeArgs;

    /**
     * 调度配置 json格式
     */
    @TableField("schedule_conf")
    private String scheduleConf;

    /**
     * 周期类型
     */
    @TableField(exist = false)
    private Integer periodType;

    /**
     * 0未开始,1正常调度,2暂停
     */
    @TableField("schedule_status")
    private Integer scheduleStatus;
    /**
     * 依赖的任务id
     */
    @TableField("dependency_task_ids")
    private String dependencyTaskIds;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id = 0L;

    @TableField("is_deleted")
    private Integer isDeleted = 0;

    /**
     * 租户Id
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 实体创建时间
     */
    @TableField(
            value = "gmt_create",
            fill = FieldFill.INSERT
    )
    private Timestamp gmtCreate;
    /**
     * 实体修改时间
     */
    @TableField(
            value = "gmt_modified",
            fill = FieldFill.INSERT_UPDATE
    )
    private Timestamp gmtModified;

    public String getDependencyTaskIds() {
        return dependencyTaskIds;
    }

    public void setDependencyTaskIds(String dependencyTaskIds) {
        this.dependencyTaskIds = dependencyTaskIds;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

}