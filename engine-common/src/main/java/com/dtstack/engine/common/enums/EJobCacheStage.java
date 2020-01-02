package com.dtstack.engine.common.enums;

/**
 * 区分engine接收到任务之后所处的阶段,用于在master切换之后主动恢复队列数据
 * Date: 2018/1/10
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum EJobCacheStage {
    /**1:在节点的优先级队列上还未下发, 2:已经下发到执行节点上*/
    IN_PRIORITY_QUEUE(1), IN_SUBMIT_QUEUE(2);

    int stage;

    EJobCacheStage(int stage){
        this.stage = stage;
    }

    public int getStage(){
        return stage;
    }
}
