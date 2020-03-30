package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.BatchTaskShade;
import com.dtstack.engine.api.dto.UserDTO;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BatchTaskVO extends BatchTaskShade {

    private UserDTO createUser;
    private UserDTO modifyUser;
    private UserDTO ownerUser;
    protected Integer taskPeriodId;
    protected String taskPeriodType;
    private String nodePName;
    private Long userId;
    private Integer lockVersion;
    private List<Map> taskVariables;
    private boolean forceUpdate;

    private Long dataSourceId;

    private BatchTaskVO subNodes;

    private List<BatchTaskVO> relatedTasks;

    private String tenantName;

    private String projectName;

    /**
     * 0-向导模式，1-脚本模式
     */
    private int createModel;

    /**
     * 操作模式 0-资源模式，1-编辑模式
     */
    private int operateModel;

    /**
     * 2-python2.x,3-python3.x
     */
    private int pythonVersion;

    /**
     * 0-TensorFlow,1-MXNet
     */
    private int learningType;

    /**
     * 输入数据文件的路径
     */
    private String input;

    /**
     * 输出模型的路径
     */
    private String output;

    /**
     * 脚本的命令行参数
     */
    private String options;

    private String flowName;

    /**
     * 同步模式
     */
    private int syncModel;

    private String increColumn;

    /**
     * 是否是当前项目
     */
    private boolean currentProject;

    private List<BatchTaskVO> taskVOS;
    private List<BatchTaskVO> subTaskVOS;
    protected String cron;

    public boolean getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(boolean currentProject) {
        this.currentProject = currentProject;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getIncreColumn() {
        return increColumn;
    }

    public void setIncreColumn(String increColumn) {
        this.increColumn = increColumn;
    }

    public int getSyncModel() {
        return syncModel;
    }

    public void setSyncModel(int syncModel) {
        this.syncModel = syncModel;
    }

    public int getOperateModel() {
        return operateModel;
    }

    public void setOperateModel(int operateModel) {
        this.operateModel = operateModel;
    }

    public int getPythonVersion() {
        return pythonVersion;
    }

    public void setPythonVersion(int pythonVersion) {
        this.pythonVersion = pythonVersion;
    }

    public int getLearningType() {
        return learningType;
    }

    public void setLearningType(int learningType) {
        this.learningType = learningType;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public boolean getforceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public Integer getTaskPeriodId() {
        return taskPeriodId;
    }

    public void setTaskPeriodId(Integer taskPeriodId) {
        this.taskPeriodId = taskPeriodId;
    }

    public String getTaskPeriodType() {
        return taskPeriodType;
    }

    public void setTaskPeriodType(String taskPeriodType) {
        this.taskPeriodType = taskPeriodType;
    }

    public List<BatchTaskVO> getTaskVOS() {
        return taskVOS;
    }

    public BatchTaskVO setTaskVOS(List<BatchTaskVO> taskVOS) {
        this.taskVOS = taskVOS;
        return this;
    }

    public String getNodePName() {
        return nodePName;
    }

    public void setNodePName(String nodePName) {
        this.nodePName = nodePName;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public UserDTO getCreateUser() {
        return createUser;
    }

    public void setCreateUser(UserDTO createUser) {
        this.createUser = createUser;
    }

    public UserDTO getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(UserDTO modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Integer getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Map> getTaskVariables() {
        return taskVariables;
    }

    public void setTaskVariables(List<Map> taskVariables) {
        this.taskVariables = taskVariables;
    }

    public List<BatchTaskVO> getSubTaskVOS() {
        return subTaskVOS;
    }

    public void setSubTaskVOS(List<BatchTaskVO> subTaskVOS) {
        this.subTaskVOS = subTaskVOS;
    }

    public int getCreateModel() {
        return createModel;
    }

    public void setCreateModel(int createModel) {
        this.createModel = createModel;
    }

    public UserDTO getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(UserDTO ownerUser) {
        this.ownerUser = ownerUser;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public BatchTaskVO getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(BatchTaskVO subNodes) {
        this.subNodes = subNodes;
    }

    public List<BatchTaskVO> getRelatedTasks() {
        return relatedTasks;
    }

    public void setRelatedTasks(List<BatchTaskVO> relatedTasks) {
        this.relatedTasks = relatedTasks;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
}
