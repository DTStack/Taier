package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 11:26 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryTaskDisplayVO {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id",required = true)
    private Long taskId;

    /**
     * 查询层级
     */
    @ApiModelProperty(value = "查询层级: 默认查询一层,该值范围 0<level<20")
    private Integer level;

    /**
     * 查询方向:
     * FATHER(1):向上查询
     * CHILD(2):向下查询
     */
    @ApiModelProperty(value = "查询方向:\n" +
            "FATHER(1):向上查询 \n" +
            "CHILD(2):向下查询")
    private Integer directType;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getDirectType() {
        return directType;
    }

    public void setDirectType(Integer directType) {
        this.directType = directType;
    }
}
