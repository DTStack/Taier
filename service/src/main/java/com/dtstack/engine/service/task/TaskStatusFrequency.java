package com.dtstack.engine.service.task;

/**
 * Reason:
 * Date: 2019/2/19
 * Company: www.dtstack.com
 * @author xuchao
 */

public class TaskStatusFrequency {

    private Integer status;

    private Integer num = 0;

    private Long createTime;

    public TaskStatusFrequency(Integer status){
        createTime = System.currentTimeMillis();
        this.status = status;
        this.num = 0;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
