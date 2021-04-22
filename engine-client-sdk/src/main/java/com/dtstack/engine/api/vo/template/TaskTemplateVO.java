package com.dtstack.engine.api.vo.template;

import com.dtstack.engine.api.domain.BaseEntity;

/**
 * @Auther: dazhi
 * @Date: 2020/9/29 4:25 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskTemplateVO extends BaseEntity {

    private Integer computeType;
    private Integer engineType;
    private Integer taskType;

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
