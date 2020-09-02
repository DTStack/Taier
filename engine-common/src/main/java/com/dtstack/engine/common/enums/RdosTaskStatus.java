package com.dtstack.engine.common.enums;

import com.google.common.base.Strings;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public enum RdosTaskStatus implements Serializable {
    //
	UNSUBMIT(0),
    //
    CREATED(1),
    //
    SCHEDULED(2),
    //
    DEPLOYING(3),
    //
    RUNNING(4),
    //
    FINISHED(5),
    //
    CANCELLING(6),
    //
    CANCELED(7),
    //
    FAILED(8),
    //
    SUBMITFAILD(9),
    //
	SUBMITTING(10),
    //
    RESTARTING(11),
    //
    MANUALSUCCESS(12),
    //
    KILLED(13),
    //
    SUBMITTED(14),
    //
    NOTFOUND(15),
    //
    WAITENGINE(16),
    //
    WAITCOMPUTE(17),
    //
    FROZEN(18),
    //
    ENGINEACCEPTED(19),
    //
    ENGINEDISTRIBUTE(20),
    //
    PARENTFAILED(21),

    FAILING(22),

    COMPUTING(23),

    EXPIRE(24),

    LACKING(25),

    AUTOCANCELED(26);
	
	private int status;

    private final static List<Integer> CAN_STOP_STATUS = Lists.newArrayList(
            UNSUBMIT.getStatus(),
            CREATED.getStatus(), SCHEDULED.getStatus(),
            DEPLOYING.getStatus(), RUNNING.getStatus(),
            SUBMITTING.getStatus(), RESTARTING.getStatus(),
            SUBMITTED.getStatus(), WAITENGINE.getStatus(),
            WAITCOMPUTE.getStatus(), LACKING.getStatus(),NOTFOUND.getStatus()
    );

    private final static List<Integer> STOPPED_STATUS = Lists.newArrayList(
            MANUALSUCCESS.getStatus(),
            PARENTFAILED.getStatus(),
            FAILED.getStatus(),
            CANCELED.getStatus(),
            SUBMITFAILD.getStatus(),
            KILLED.getStatus(),
            FINISHED.getStatus(),
            EXPIRE.getStatus(),
            FROZEN.getStatus(),
            AUTOCANCELED.getStatus()
    );

    private static final Logger logger = LoggerFactory.getLogger(RdosTaskStatus.class);

    private static final long serialVersionUID = 1L;
	
	RdosTaskStatus(int status){
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
    public static RdosTaskStatus getTaskStatus(String taskStatus){

        if(Strings.isNullOrEmpty(taskStatus)){
            return null;
        }else if("error".equalsIgnoreCase(taskStatus)){
            return RdosTaskStatus.FAILED;
        } else if ("RESTARTING".equalsIgnoreCase(taskStatus)) {
            //yarn做重试认为运行中
            return RdosTaskStatus.RUNNING;
        }

	    try {
            return RdosTaskStatus.valueOf(taskStatus);
        }catch (Exception e){
            logger.info("No enum constant :" + taskStatus);
	        return null;
        }
    }

    public static RdosTaskStatus getTaskStatus(int status){
        for(RdosTaskStatus tmp : RdosTaskStatus.values()){
            if(tmp.getStatus() == status){
                return tmp;
            }
        }

        return null;
    }
    
    public static boolean needClean(Integer status){

        if(STOPPED_STATUS.contains(status) || RdosTaskStatus.RESTARTING.getStatus().equals(status)){
            return true;
        }
        return false;
    }

    public static boolean canStart(Integer status){
        if(RdosTaskStatus.SUBMITTING.getStatus().equals(status) || RdosTaskStatus.UNSUBMIT.getStatus().equals(status)){
    	    return true;
        }

        return false;
    }

    public static boolean canReset(Integer currStatus){
        return STOPPED_STATUS.contains(currStatus) || RdosTaskStatus.UNSUBMIT.getStatus().equals(currStatus);

    }

    public static List<Integer> getCanStopStatus(){
        return CAN_STOP_STATUS;
    }

    public static boolean isStopped(Integer status) {
        return STOPPED_STATUS.contains(status);
    }


    public static List<Integer> getStoppedStatus() {
        return STOPPED_STATUS;
    }

    public static List<Integer> getFinishStatus() {
        return FINISH_STATUS;
    }

    public static List<Integer> getWaitStatus() {
        return WAIT_STATUS;
    }

    public final static List<Integer> UNSUBMIT_STATUS = Lists.newArrayList(UNSUBMIT.getStatus());
    public final static List<Integer> RUNNING_STATUS = Lists.newArrayList(RUNNING.getStatus());
    public final static List<Integer> FINISH_STATUS = Lists.newArrayList(FINISHED.getStatus(), MANUALSUCCESS.getStatus());
    public final static List<Integer> FAILED_STATUS = Lists.newArrayList(FAILED.getStatus(), SUBMITFAILD.getStatus(),
            PARENTFAILED.getStatus(), FAILING.getStatus());
    public final static List<Integer> SUBMITFAILD_STATUS = Lists.newArrayList(SUBMITFAILD.getStatus());
    public final static List<Integer> PARENTFAILED_STATUS = Lists.newArrayList(PARENTFAILED.getStatus());
    public final static List<Integer> RUN_FAILED_STATUS = Lists.newArrayList(FAILED.getStatus(), FAILING.getStatus());
    public final static List<Integer> WAIT_STATUS = Lists.newArrayList(WAITENGINE.getStatus(), WAITCOMPUTE.getStatus(),
            RESTARTING.getStatus(), SUBMITTED.getStatus(), ENGINEACCEPTED.getStatus(),
            ENGINEDISTRIBUTE.getStatus(), SCHEDULED.getStatus(), CREATED.getStatus(),
            DEPLOYING.getStatus(), COMPUTING.getStatus(), LACKING.getStatus());
    public final static List<Integer> SUBMITTING_STATUS = Lists.newArrayList(SUBMITTING.getStatus());
    public final static List<Integer> STOP_STATUS = Lists.newArrayList(KILLED.getStatus(), CANCELLING.getStatus(), CANCELED.getStatus(), EXPIRE.getStatus(), AUTOCANCELED.getStatus());
    public final static List<Integer> EXPIRE_STATUS = Lists.newArrayList(EXPIRE.getStatus(),AUTOCANCELED.getStatus());
    public final static List<Integer> FROZEN_STATUS = Lists.newArrayList(FROZEN.getStatus());

    public static String getCode(Integer status) {
        String key = null;
        if (FINISH_STATUS.contains(status)) {
            key = FINISHED.name();
        } else if (RUNNING_STATUS.contains(status)) {
            key = RUNNING.name();
        } else if (PARENTFAILED_STATUS.contains(status)) {
            key = PARENTFAILED.name();
        } else if (SUBMITFAILD_STATUS.contains(status)) {
            key = SUBMITFAILD.name();
        } else if (RUN_FAILED_STATUS.contains(status)) {
            key = FAILED.name();
        } else if (UNSUBMIT_STATUS.contains(status)) {
            key = UNSUBMIT.name();
        } else if (WAIT_STATUS.contains(status)) {
            key = WAITENGINE.name();
        } else if (SUBMITTING_STATUS.contains(status)) {
            key = SUBMITTING.name();
        } else if (STOP_STATUS.contains(status)) {
            key = CANCELED.name();
        } else if (FROZEN_STATUS.contains(status)) {
            key = FROZEN.name();
        } else {
            key = UNSUBMIT.name();
        }
        return key;
    }


    private final static List<Integer> UNFINISHED_STATUSES = Lists.newArrayList(
            RUNNING.getStatus(),
            UNSUBMIT.getStatus(),
            RESTARTING.getStatus(),
            SUBMITTING.getStatus());

    static {
        UNFINISHED_STATUSES.addAll(WAIT_STATUS);
    }

    /**
     * 未完成的job
     */
    public static List<Integer> getUnfinishedStatuses() {
        return UNFINISHED_STATUSES;
    }

    private final static Map<Integer, List<Integer>> COLLECTION_STATUS = new HashMap<>();

    static {
        COLLECTION_STATUS.put(UNSUBMIT.getStatus(), Lists.newArrayList(UNSUBMIT.getStatus()));
        COLLECTION_STATUS.put(RUNNING.getStatus(), Lists.newArrayList(RUNNING.getStatus(), NOTFOUND.getStatus()));
        COLLECTION_STATUS.put(FINISHED.getStatus(), FINISH_STATUS);
        COLLECTION_STATUS.put(FAILED.getStatus(), FAILED_STATUS);
        COLLECTION_STATUS.put(WAITENGINE.getStatus(), WAIT_STATUS);
        COLLECTION_STATUS.put(SUBMITTING.getStatus(), Lists.newArrayList(SUBMITTING.getStatus()));
        COLLECTION_STATUS.put(CANCELED.getStatus(), STOP_STATUS);
        COLLECTION_STATUS.put(FROZEN.getStatus(), Lists.newArrayList(FROZEN.getStatus()));
        COLLECTION_STATUS.put(EXPIRE.getStatus(), EXPIRE_STATUS);
    }

    private final static Map<Integer, List<Integer>> STATUS_FAILED_DETAIL = new HashMap<>();

    static {
        STATUS_FAILED_DETAIL.put(UNSUBMIT.getStatus(), Lists.newArrayList(UNSUBMIT.getStatus()));
        STATUS_FAILED_DETAIL.put(RUNNING.getStatus(), Lists.newArrayList(RUNNING.getStatus(), NOTFOUND.getStatus()));
        STATUS_FAILED_DETAIL.put(FINISHED.getStatus(), FINISH_STATUS);
        STATUS_FAILED_DETAIL.put(FAILED.getStatus(), Lists.newArrayList(FAILED.getStatus(), FAILING.getStatus()));
        STATUS_FAILED_DETAIL.put(SUBMITFAILD.getStatus(), Lists.newArrayList(SUBMITFAILD.getStatus()));
        STATUS_FAILED_DETAIL.put(PARENTFAILED.getStatus(), Lists.newArrayList(PARENTFAILED.getStatus()));
        STATUS_FAILED_DETAIL.put(WAITENGINE.getStatus(), WAIT_STATUS);
        STATUS_FAILED_DETAIL.put(SUBMITTING.getStatus(), Lists.newArrayList(SUBMITTING.getStatus()));
        STATUS_FAILED_DETAIL.put(CANCELED.getStatus(), STOP_STATUS);
        STATUS_FAILED_DETAIL.put(FROZEN.getStatus(), Lists.newArrayList(FROZEN.getStatus()));
        //统计状态数的时候 自动取消(过期)需要归到取消中
        STATUS_FAILED_DETAIL.put(EXPIRE.getStatus(), EXPIRE_STATUS);

    }


    public static List<Integer> getCollectionStatus(Integer status) {
        return COLLECTION_STATUS.computeIfAbsent(status, k -> new ArrayList<>(0));
    }

    public static Map<Integer, List<Integer>> getCollectionStatus() {
        return COLLECTION_STATUS;
    }

    public static Map<Integer, List<Integer>> getStatusFailedDetail() {
        return STATUS_FAILED_DETAIL;
    }

    public static List<Integer> getStatusFailedDetail(Integer status) {
        return STATUS_FAILED_DETAIL.computeIfAbsent(status, k -> new ArrayList<>(0));
    }

    public static int getShowStatus(Integer status) {
        if (FAILED_STATUS.contains(status)) {
            status = FAILED.getStatus();
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
            status = FINISHED.getStatus();
        } else if (RUNNING_STATUS.contains(status)) {
            status = RUNNING.getStatus();
        } else if (UNSUBMIT_STATUS.contains(status)) {
            status = UNSUBMIT.getStatus();
        } else if (WAIT_STATUS.contains(status)) {
            status = WAITENGINE.getStatus();
        } else if (SUBMITTING_STATUS.contains(status)) {
            status = SUBMITTING.getStatus();
        } else if (EXPIRE_STATUS.contains(status)){
            status = EXPIRE.getStatus();
        } else if (STOP_STATUS.contains(status)) {
            status = CANCELED.getStatus();
        } else if (FROZEN_STATUS.contains(status)) {
            status = FROZEN.getStatus();
        }
        return status;
    }

    public static List<Integer> getStoppedAndNotFound() {
        List<Integer> status = new ArrayList<>();
        status.addAll(STOPPED_STATUS);
        status.add(SUBMITTED.getStatus());
        status.add(NOTFOUND.getStatus());
        return status;
    }
}
