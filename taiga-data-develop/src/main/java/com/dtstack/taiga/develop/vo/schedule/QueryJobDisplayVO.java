package com.dtstack.taiga.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 11:26 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryJobDisplayVO {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id",required  = true)
    private String jobId;

    /**
     * 查询层级
     */
    @ApiModelProperty(value = "查询层级")
    private Integer level;

    /**
     * 方向
     */
    @ApiModelProperty(value = "查询方向:\n" +
            "FATHER(1):向上查询 \n" +
            "CHILD(2):向下查询",required = true)
    private Integer directType;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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
