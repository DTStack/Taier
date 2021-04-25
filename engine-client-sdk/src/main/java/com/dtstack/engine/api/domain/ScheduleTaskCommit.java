package com.dtstack.engine.api.domain;

/**
 * @Auther: dazhi
 * @Date: 2020/12/14 4:56 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskCommit extends BaseEntity {

    private static final long serialVersionUID = 3381927562994232021L;
    private Long id;

    private Long taskId;

    private Integer appType;

    private String commitId;

    private String taskJson;

    private String extraInfo;

    private Integer isCommit;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getTaskJson() {
        return taskJson;
    }

    public void setTaskJson(String taskJson) {
        this.taskJson = taskJson;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Integer getIsCommit() {
        return isCommit;
    }

    public void setIsCommit(Integer isCommit) {
        this.isCommit = isCommit;
    }
}
