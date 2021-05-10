package com.dtstack.engine.api.vo.schedule.job;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 10:47 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobScienceJobStatusVO {

    private Integer total;
    private Integer deployCount;
    private Integer failCount;
    private Integer successCount;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getDeployCount() {
        return deployCount;
    }

    public void setDeployCount(Integer deployCount) {
        this.deployCount = deployCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
}
