package com.dtstack.schedule.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/28
 */
public enum ESubmitStatus {
    //未发布任务
    UNSUBMIT(0),
    //已经发布
    SUBMIT(1);

    int status;

    ESubmitStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return this.status;
    }

}
