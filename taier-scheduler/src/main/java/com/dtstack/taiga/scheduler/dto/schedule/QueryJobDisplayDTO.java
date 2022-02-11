package com.dtstack.taiga.scheduler.dto.schedule;

/**
 * @Auther: dazhi
 * @Date: 2021/12/27 10:07 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryJobDisplayDTO {

    /**
     * 任务id
     */
    private String jobId;

    /**
     * 查询层级
     */
    private Integer level;

    /**
     * 方向
     */
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
