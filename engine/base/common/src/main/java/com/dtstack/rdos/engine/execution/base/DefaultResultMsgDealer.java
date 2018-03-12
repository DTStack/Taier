package com.dtstack.rdos.engine.execution.base;


public class DefaultResultMsgDealer implements IResultMsgDealer {
    @Override
    public boolean checkFailureForEngineDown(String msg) {
        return false;
    }

    @Override
    public boolean checkNOResource(String msg) {
        return false;
    }
}
