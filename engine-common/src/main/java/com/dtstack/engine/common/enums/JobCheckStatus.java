package com.dtstack.engine.common.enums;

/**
 * 0:可以执行
 * 1:时间未到
 * 2:依赖父任务未完成
 * 3:父任务运行失败
 * 4:超过等待时间还未执行
 * 5:当前任务处于暂停状态
 * 6:找不到该任务
 * 7:非未未提交状态
 * Date: 2017/5/28
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public enum JobCheckStatus {

    CAN_EXE(0, "可以执行"),
    TIME_NOT_REACH(1, "时间未到"),
    FATHER_JOB_NOT_FINISHED(2, "依赖父任务未完成"),
    FATHER_JOB_EXCEPTION(3, "父任务运行失败"),
    TIME_OVER_EXPIRE(4, "超过等待时间还未执行"),
    TASK_PAUSE(5, "当前任务处于冻结状态"),
    NO_TASK(6, "找不到该任务"),
    NOT_UNSUBMIT(7, "并非未提交状态"),
    SELF_PRE_PERIOD_EXCEPTION(8, "自依赖上一个周期异常"),
    TASK_DELETE(9, "该任务已经被删除"),
    FATHER_NO_CREATED(10, "父任务未生成"),
    DEPENDENCY_JOB_CANCELED(11, "依赖任务链路存在任务处于停止状态"),
    DEPENDENCY_JOB_FROZEN(12, "依赖任务链路存在任务处于冻结状态"),
    CHILD_PRE_NOT_FINISHED(13, "下游任务的上一个周期未结束"),
    DEPENDENCY_JOB_EXPIRE(14, "依赖任务链路存在任务处于过期状态"),
    CHILD_PRE_NOT_SUCCESS(15, "下游任务的上一个周期失败"),
    RESOURCE_OVER_LIMIT(16,"任务资源大小超过租户设置的大小");

    private int status;

    private String msg;

    JobCheckStatus(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return this.getStatus();
    }

    public String getMsg() {
        return msg;
    }
}
