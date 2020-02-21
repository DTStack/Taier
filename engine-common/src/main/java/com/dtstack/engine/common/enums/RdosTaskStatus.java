package com.dtstack.engine.common.enums;

import com.google.common.base.Strings;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

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

    FAILING(22), COMPUTING(23);
	
	private int status;

    private final static List<Integer> CAN_STOP_STATUS = Lists.newArrayList(
            UNSUBMIT.getStatus(),
            CREATED.getStatus(), SCHEDULED.getStatus(),
            DEPLOYING.getStatus(), RUNNING.getStatus(),
            SUBMITTING.getStatus(), RESTARTING.getStatus(),
            SUBMITTED.getStatus(), WAITENGINE.getStatus(),
            WAITCOMPUTE.getStatus()
    );

    private final static List<Integer> STOPPED_STATUS = Lists.newArrayList(
            RdosTaskStatus.FAILED.getStatus(),
            RdosTaskStatus.CANCELED.getStatus(),
            RdosTaskStatus.SUBMITFAILD.getStatus(),
            RdosTaskStatus.KILLED.getStatus(),
            RdosTaskStatus.FINISHED.getStatus()
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

    public static boolean canStartAgain(Byte status){
		int sta = status.intValue();
        if(sta == RdosTaskStatus.SUBMITTING.getStatus() || sta == RdosTaskStatus.UNSUBMIT.getStatus()){
    	    return true;
        }

        return false;
    }

    public static boolean canReset(Byte currStatus){
        int sta = currStatus.intValue();
        return STOPPED_STATUS.contains(sta) || sta == RdosTaskStatus.UNSUBMIT.getStatus();

    }

    public static List<Integer> getCanStopStatus(){
        return CAN_STOP_STATUS;
    }

    public static boolean isStopped(Byte status) {
        return STOPPED_STATUS.contains(status.intValue());
    }


    public static List<Integer> getStoppedStatus() {
        return STOPPED_STATUS;
    }
}
