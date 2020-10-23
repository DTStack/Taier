package com.dtstack.engine.master.vo;

import java.util.Map;

/**
 * @Author tengzhen
 * @Description: 任务类型对应的资源限制模板
 * @Date: Created in 8:06 下午 2020/10/14
 */
public class TaskTypeResourceTemplateVO {

    /**任务类型**/
    private Integer taskType;
    /**任务类型名称**/
    private String taskTypeName;
    /**资源限制模板**/
    private Map<String,String> params;

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
