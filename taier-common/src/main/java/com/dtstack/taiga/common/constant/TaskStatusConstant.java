package com.dtstack.taiga.common.constant;

import com.dtstack.taiga.common.enums.TaskStatus;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author sishu.yss
 *
 */
public class TaskStatusConstant {

    public final static List<Integer> UNSUBMIT_STATUS = Lists.newArrayList(TaskStatus.UNSUBMIT.getStatus());
    public final static List<Integer> RUNNING_STATUS = Lists.newArrayList(TaskStatus.RUNNING.getStatus(), TaskStatus.FAILING.getStatus(), TaskStatus.NOTFOUND.getStatus());
    public final static List<Integer> FINISH_STATUS = Lists.newArrayList(TaskStatus.FINISHED.getStatus(), TaskStatus.MANUALSUCCESS.getStatus());
    public final static List<Integer> FAILED_STATUS = Lists.newArrayList(TaskStatus.FAILED.getStatus(), TaskStatus.SUBMITFAILD.getStatus(),
            TaskStatus.PARENTFAILED.getStatus());
    public final static List<Integer> SUBMITFAILD_STATUS = Lists.newArrayList(TaskStatus.SUBMITFAILD.getStatus());
    public final static List<Integer> PARENTFAILED_STATUS = Lists.newArrayList(TaskStatus.PARENTFAILED.getStatus());
    public final static List<Integer> RUN_FAILED_STATUS = Lists.newArrayList(TaskStatus.FAILED.getStatus());
    public final static List<Integer> WAIT_STATUS = Lists.newArrayList(TaskStatus.WAITENGINE.getStatus(), TaskStatus.WAITCOMPUTE.getStatus(),
            TaskStatus.RESTARTING.getStatus(), TaskStatus.SUBMITTED.getStatus(), TaskStatus.ENGINEACCEPTED.getStatus(),
            TaskStatus.ENGINEDISTRIBUTE.getStatus(), TaskStatus.SCHEDULED.getStatus(), TaskStatus.CREATED.getStatus(),
            TaskStatus.DEPLOYING.getStatus(), TaskStatus.COMPUTING.getStatus(),TaskStatus.LACKING.getStatus());
    public final static List<Integer> SUBMITTING_STATUS = Lists.newArrayList(TaskStatus.SUBMITTING.getStatus());
    public final static List<Integer> STOP_STATUS = Lists.newArrayList(TaskStatus.KILLED.getStatus(), TaskStatus.CANCELING.getStatus(), TaskStatus.CANCELED.getStatus(),TaskStatus.EXPIRE.getStatus(),TaskStatus.AUTOCANCELED.getStatus());
    public final static List<Integer> EXPIRE_STATUS = Lists.newArrayList(TaskStatus.EXPIRE.getStatus(),TaskStatus.AUTOCANCELED.getStatus());
    public final static List<Integer> FROZEN_STATUS = Lists.newArrayList(TaskStatus.FROZEN.getStatus());

    public final static List<Integer> END_STATUS_LIST = Lists.newArrayList(TaskStatus.CANCELED.getStatus(), TaskStatus.MANUALSUCCESS.getStatus(), TaskStatus.KILLED.getStatus(),
            TaskStatus.FINISHED.getStatus(), TaskStatus.FAILED.getStatus(), TaskStatus.SUBMITFAILD.getStatus(), TaskStatus.PARENTFAILED.getStatus(), TaskStatus.FROZEN.getStatus(),
            TaskStatus.FROZEN.getStatus(), TaskStatus.EXPIRE.getStatus(),TaskStatus.AUTOCANCELED.getStatus());

    public final static Map<Integer, List<Integer>> STATUS = new HashMap<>();

    public final static Map<Integer, List<Integer>> STATUS_FAILED_DETAIL = new HashMap<>();

    static {
        STATUS.put(TaskStatus.UNSUBMIT.getStatus(), UNSUBMIT_STATUS);
        STATUS.put(TaskStatus.RUNNING.getStatus(), RUNNING_STATUS);
        STATUS.put(TaskStatus.FINISHED.getStatus(), FINISH_STATUS);
        STATUS.put(TaskStatus.FAILED.getStatus(), FAILED_STATUS);
        STATUS.put(TaskStatus.WAITENGINE.getStatus(), WAIT_STATUS);
        STATUS.put(TaskStatus.SUBMITTING.getStatus(), SUBMITTING_STATUS);
        STATUS.put(TaskStatus.CANCELED.getStatus(), STOP_STATUS);
        STATUS.put(TaskStatus.FROZEN.getStatus(), FROZEN_STATUS);
        //需要有自动取消的筛选条件
        STATUS.put(TaskStatus.EXPIRE.getStatus(), EXPIRE_STATUS);

    }

    static {
        STATUS_FAILED_DETAIL.put(TaskStatus.UNSUBMIT.getStatus(), UNSUBMIT_STATUS);
        STATUS_FAILED_DETAIL.put(TaskStatus.RUNNING.getStatus(), RUNNING_STATUS);
        STATUS_FAILED_DETAIL.put(TaskStatus.FINISHED.getStatus(), FINISH_STATUS);
        STATUS_FAILED_DETAIL.put(TaskStatus.FAILED.getStatus(), RUN_FAILED_STATUS);
        STATUS_FAILED_DETAIL.put(TaskStatus.SUBMITFAILD.getStatus(), SUBMITFAILD_STATUS);
        STATUS_FAILED_DETAIL.put(TaskStatus.PARENTFAILED.getStatus(), PARENTFAILED_STATUS);
        STATUS_FAILED_DETAIL.put(TaskStatus.WAITENGINE.getStatus(), WAIT_STATUS);
        STATUS_FAILED_DETAIL.put(TaskStatus.SUBMITTING.getStatus(), SUBMITTING_STATUS);
        STATUS_FAILED_DETAIL.put(TaskStatus.CANCELED.getStatus(), STOP_STATUS);
        STATUS_FAILED_DETAIL.put(TaskStatus.FROZEN.getStatus(), FROZEN_STATUS);
        //统计状态数的时候 自动取消(过期)需要归到取消中
        STATUS_FAILED_DETAIL.put(TaskStatus.EXPIRE.getStatus(), EXPIRE_STATUS);
    }

    public static String getCode(Integer status) {
        String key = null;
        if (FINISH_STATUS.contains(status)) {
            key = TaskStatus.FINISHED.name();
        } else if (RUNNING_STATUS.contains(status)) {
            key = TaskStatus.RUNNING.name();
        } else if (PARENTFAILED_STATUS.contains(status)) {
            key = TaskStatus.PARENTFAILED.name();
        } else if (SUBMITFAILD_STATUS.contains(status)) {
            key = TaskStatus.SUBMITFAILD.name();
        } else if (RUN_FAILED_STATUS.contains(status)) {
            key = TaskStatus.FAILED.name();
        } else if (UNSUBMIT_STATUS.contains(status)) {
            key = TaskStatus.UNSUBMIT.name();
        } else if (WAIT_STATUS.contains(status)) {
            key = TaskStatus.WAITENGINE.name();
        } else if (SUBMITTING_STATUS.contains(status)) {
            key = TaskStatus.SUBMITTING.name();
        } else if (STOP_STATUS.contains(status)) {
            key = TaskStatus.CANCELED.name();
        } else if (FROZEN_STATUS.contains(status)) {
            key = TaskStatus.FROZEN.name();
        } else {
            key = TaskStatus.UNSUBMIT.name();
        }
        return key;
    }


    public static int getShowStatus(Integer status) {
        if (FAILED_STATUS.contains(status)) {
            status = TaskStatus.FAILED.getStatus();
        } else {
            status = getShowStatusWithoutStop(status);
        }
        return status;
    }

    /**
     * 将过程细化的status归并为 已完成、正在运行、等待提交、等待运行、提交中、取消、冻结
     * 不需要对stop状态做归并处理（stop状态用户需要直接查看）
     *
     * @param status
     * @return
     */
    public static int getShowStatusWithoutStop(Integer status) {
        if (FINISH_STATUS.contains(status)) {
            status = TaskStatus.FINISHED.getStatus();
        } else if (RUNNING_STATUS.contains(status)) {
            status = TaskStatus.RUNNING.getStatus();
        } else if (UNSUBMIT_STATUS.contains(status)) {
            status = TaskStatus.UNSUBMIT.getStatus();
        } else if (WAIT_STATUS.contains(status)) {
            status = TaskStatus.WAITENGINE.getStatus();
        } else if (SUBMITTING_STATUS.contains(status)) {
            status = TaskStatus.SUBMITTING.getStatus();
        } else if (EXPIRE_STATUS.contains(status)){
            status = TaskStatus.EXPIRE.getStatus();
        } else if (STOP_STATUS.contains(status)) {
            status = TaskStatus.CANCELED.getStatus();
        } else if (FROZEN_STATUS.contains(status)) {
            status = TaskStatus.FROZEN.getStatus();
        }
        return status;
    }

}
