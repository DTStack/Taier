package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import io.swagger.annotations.ApiModel;

/**
 * 补数据任务所需的任务参数
 * -- ps 简化了batchTask
 *
 * @author sanyue
 * @date 2019/1/18
 */
@ApiModel
public class ScheduleTaskForFillDataDTO extends TenantProjectEntity {

    private String name;

    private Integer taskType;

    private Long createUserId;

    private Long ownerUserId;

    private UserDTO createUser;

    private UserDTO ownerUser;

    private String scheduleConf;

    private Long taskId;

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

    public UserDTO getCreateUser() {
        return createUser;
    }

    public void setCreateUser(UserDTO createUser) {
        this.createUser = createUser;
    }

    public UserDTO getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(UserDTO ownerUser) {
        this.ownerUser = ownerUser;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
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
}
