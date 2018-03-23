package com.dtstack.rdos.engine.execution.base;

/**
 * 各个插件对失败作业的重启策略
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface IRestartStrategy {

    /**判断引擎是不是挂了*/
    boolean checkFailureForEngineDown(String msg);

    boolean checkNOResource(String msg);

    boolean checkCanRestart(String engineJobId, IClient client);



}
