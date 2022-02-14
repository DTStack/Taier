package com.dtstack.taier.common.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public enum TaskStatus {
    //MANUALSUCCESS: 手动设置为成功 EXPIRE 离线平台设置的自动取消  AUTOCANCELED engine 的自动取消
    UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)
    ,SUBMITFAILD(9), SUBMITTING(10), RESTARTING(11), MANUALSUCCESS(12), KILLED(13), SUBMITTED(14), NOTFOUND(15), WAITENGINE(16),
    WAITCOMPUTE(17), FROZEN(18), ENGINEACCEPTED(19), ENGINEDISTRIBUTE(20), PARENTFAILED(21), FAILING(22), COMPUTING(23), EXPIRE(24),LACKING(25),AUTOCANCELED(26);

    private int status;

    private static List<Integer> canStopStatus = Lists.newArrayList(
            UNSUBMIT.getStatus(),
            CREATED.getStatus(), SCHEDULED.getStatus(),
            DEPLOYING.getStatus(), RUNNING.getStatus(),
            SUBMITTING.getStatus(), RESTARTING.getStatus(),
            SUBMITTED.getStatus(), WAITENGINE.getStatus(),
            WAITCOMPUTE.getStatus(), ENGINEACCEPTED.getStatus(),
            ENGINEDISTRIBUTE.getStatus(),LACKING.getStatus(),NOTFOUND.getStatus());

    public static List<Integer> canRestartStatus = Lists.newArrayList(
            FINISHED.getStatus(), CANCELED.getStatus(),
            SUBMITFAILD.getStatus(), FAILED.getStatus(),
            MANUALSUCCESS.getStatus(), UNSUBMIT.getStatus(),
            KILLED.getStatus(), PARENTFAILED.getStatus(), EXPIRE.getStatus(),LACKING.getStatus(),AUTOCANCELED.getStatus(),NOTFOUND.getStatus()
    );

    TaskStatus(int status){
        this.status = status;
    }

    public Integer getStatus(){
        return this.status;
    }

    /**
     * 需要捕获无法转换异常
     * @param taskStatus
     * @return
     */
    public static TaskStatus getTaskStatus(String taskStatus){
        return TaskStatus.valueOf(taskStatus);
    }

    public static TaskStatus getTaskStatusByVal(int val){
        for(TaskStatus taskStatus : TaskStatus.values()){
            if(taskStatus.getStatus() == val){
                return taskStatus;
            }
        }

        return null;
    }

    public static boolean needClean(Byte status){
        int sta = status.intValue();
        if(sta==TaskStatus.FINISHED.status||sta==TaskStatus.CANCELED.status||sta==TaskStatus.FAILED.status){
            return true;
        }
        return false;
    }

    public static boolean canRestart(Integer jobStatus){
        for(Integer status : canRestartStatus){
            if(jobStatus.equals(status)){
                return true;
            }
        }

        return false;
    }

    public static List<Integer> getCanStopStatus(){
        return canStopStatus;
    }
}
