package com.dtstack.batch.vo;

import com.dtstack.engine.domain.BatchTask;
import com.dtstack.batch.domain.Catalogue;
import lombok.Data;

import java.util.List;

/**
 * @author toutian
 */
@Data
public class TaskCatalogueVO extends CatalogueVO {


    private Integer scheduleStatus;
    private Integer submitStatus;
    private List<? extends Catalogue> catalogues;
    private List<BatchTask> tasks;
    private Integer taskType;
    private List<BatchTask> dependencyTasks;
    private List<List<Object>>  lists;


    @Override
    public Integer getTaskType() {
        return taskType;
    }

    @Override
    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public TaskCatalogueVO() {
    }

    public TaskCatalogueVO(BatchTaskBatchVO task, Long parentId) {
        this.setId(task.getId());
        this.setName(task.getName());
        this.setType("file");
        this.setLevel(null);
        this.setChildren(null);
        this.setScheduleStatus(task.getScheduleStatus());
        this.setSubmitStatus(task.getSubmitStatus());
        this.setParentId(parentId);
        this.setTaskType(task.getTaskType());
        this.setReadWriteLockVO(task.getReadWriteLockVO());
        this.setVersion(task.getVersion());
    }
}
