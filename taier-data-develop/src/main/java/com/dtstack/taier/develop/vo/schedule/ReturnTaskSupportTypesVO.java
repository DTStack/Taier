package com.dtstack.taier.develop.vo.schedule;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 10:46 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnTaskSupportTypesVO {

    /**
     * 组件code值
     */
    private Integer taskTypeCode;

    /**
     * 组件名称
     */
    private String taskTypeName;

    public Integer getTaskTypeCode() {
        return taskTypeCode;
    }

    public void setTaskTypeCode(Integer taskTypeCode) {
        this.taskTypeCode = taskTypeCode;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }
}
