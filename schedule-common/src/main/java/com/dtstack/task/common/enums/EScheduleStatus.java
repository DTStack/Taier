package com.dtstack.task.common.enums;

/**
 * Reason:
 * Date: 2017/5/5
 * Company: www.dtstack.com
 * @author xuchao
 */
public enum EScheduleStatus {

    //1正常调度,2暂停
    NORMAL(1), PAUSE(2), UNSTARTED(0);

    private Integer val;

    EScheduleStatus(Integer val){
        this.val = val;
    }

    public Integer getVal() {
        return val;
    }

    public static EScheduleStatus getStatus(Integer val){
        for(EScheduleStatus status : EScheduleStatus.values()){
            if(status.getVal().equals(val)){
                return status;
            }
        }

        return null;
    }
}
