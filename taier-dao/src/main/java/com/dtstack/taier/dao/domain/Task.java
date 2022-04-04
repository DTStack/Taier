package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;


@TableName("develop_task")
public class Task  {

    @TableId(value="id", type= IdType.AUTO)
    private Long id;

    /**
     * 所属工作流id
     */
    private Long flowId;
    /**
     * '任务名称'
     */
    private String name;

    /**
     * '计算类型 0实时，1 离线'
     */
    private Integer computeType;

    /**
     * 'sql 文本'
     */
    private String sqlText;

    /**
     * '任务参数'
     */
    private String taskParams;

    /**
     * 执行参数
     */
    private String exeArgs;

    /**
     * 最后修改task的用户
     */
    @TableField("modify_user_id")
    private Long modifyUserId;

    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 'task版本'
     */
    private Integer version;

    /**
     * 组件版本：此处对应流计算任务而言就是 flink 版本
     */
    private String componentVersion;

    /**
     * 所在目录
     */
    private Long nodePid;

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

    private String sideStr;

    private String settingStr;

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

    /**
     * 任务类型 @see EDataSyncJobType
     */
    private Integer taskType ;

    /**
     * 任务状态默认等待提交状态
     * @See TaskSubmitStatusEnum
     */
    private Integer submitStatus ;

    private String mainClass;

    private Timestamp gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE, update = "now()",value = "gmt_modified")
    private Timestamp gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    @TableField("tenant_id")
    private Long tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }


    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getExeArgs() {
        return exeArgs;
    }

    public void setExeArgs(String exeArgs) {
        this.exeArgs = exeArgs;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
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

    public Long getNodePid() {
        return nodePid;
    }

    public void setNodePid(Long nodePid) {
        this.nodePid = nodePid;
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

    public String getSideStr() {
        return sideStr;
    }

    public void setSideStr(String sideStr) {
        this.sideStr = sideStr;
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

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
    }
}