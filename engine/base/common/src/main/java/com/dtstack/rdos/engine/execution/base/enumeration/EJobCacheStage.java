package com.dtstack.rdos.engine.execution.base.enumeration;

/**
 * Reason:
 * Date: 2018/1/10
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum EJobCacheStage {

    IN_PRIORITY_QUEUE(1), IN_SUBMIT_QUEUE(2);

    int stage;

    EJobCacheStage(int stage){
        this.stage = stage;
    }

    public int getStage(){
        return stage;
    }
}
