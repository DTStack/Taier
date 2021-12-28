package com.dtstack.batch.vo.schedule;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/27 10:13 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobNodeVO {

    /**
     * 实例id
     */
    private String jobId;

    /**
     * 实例状态
     */
    private Integer status;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 发布时间
     */
    private Timestamp taskGmtCreate;

    /**
     * 子节点
     */
    private List<JobNodeVO> childNode;

    /**
     * 父节点
     */
    private List<JobNodeVO> parentNode;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Timestamp getTaskGmtCreate() {
        return taskGmtCreate;
    }

    public void setTaskGmtCreate(Timestamp taskGmtCreate) {
        this.taskGmtCreate = taskGmtCreate;
    }

    public List<JobNodeVO> getChildNode() {
        return childNode;
    }

    public void setChildNode(List<JobNodeVO> childNode) {
        this.childNode = childNode;
    }

    public List<JobNodeVO> getParentNode() {
        return parentNode;
    }

    public void setParentNode(List<JobNodeVO> parentNode) {
        this.parentNode = parentNode;
    }
}
