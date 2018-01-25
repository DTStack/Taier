package com.dtstack.rdos.engine.execution.base;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface IResultMsgDealer {

    /**判断引擎是不是挂了*/
    boolean checkFailureForEngineDown(String msg);

    boolean checkNOResource(String msg);
}
