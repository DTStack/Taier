package com.dtstack.taier.develop.utils;

import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 任务状态检查
 */
public class TaskStatusCheckUtil {

    /**
     * 任务不能删除的状态
     */
    public final static List<Integer> CANNOT_DELETE_STATUS = Lists.newArrayList(
            TaskStatus.WAITENGINE.getStatus(),
//            TaskStatus.CANCELING.getStatus(),
            TaskStatus.SUBMITTING.getStatus(),
//            TaskStatus.FAILING.getStatus(),
            TaskStatus.RESTARTING.getStatus(),
            TaskStatus.RUNNING.getStatus()
    );


    /**
     * 续跑或重跑的状态
     */
    public final static List<Integer> CAN_RESET_STATUS = Lists.newArrayList(
            TaskStatus.FAILED.getStatus(),
            TaskStatus.UNSUBMIT.getStatus(),
            TaskStatus.CANCELED.getStatus(),
            TaskStatus.SUBMITFAILD.getStatus(),
            TaskStatus.KILLED.getStatus(),
            TaskStatus.FINISHED.getStatus()
//            TaskStatus.EXPIRE.getStatus()
    );

    /**
     * 可以运行的状态
     */
    public final static List<Integer> CAN_RUN_STATUS = Lists.newArrayList(
            TaskStatus.UNSUBMIT.getStatus(),    //未提交
            TaskStatus.FAILED.getStatus(),      //失败
            TaskStatus.FINISHED.getStatus(),    //完成
            TaskStatus.CANCELED.getStatus(),    //取消
            TaskStatus.KILLED.getStatus(),      //已停止
            TaskStatus.SUBMITFAILD.getStatus() //提交失败
//            TaskStatus.EXPIRE.getStatus()       //超时取消
    );
}
