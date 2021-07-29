package com.dtstack.batch.enums;

/**
 * Reason:
 * Date: 2017/6/20
 * Company: www.dtstack.com
 * @author xuchao
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
