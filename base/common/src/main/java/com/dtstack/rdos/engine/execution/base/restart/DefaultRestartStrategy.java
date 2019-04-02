package com.dtstack.rdos.engine.execution.base.restart;



public class DefaultRestartStrategy extends IRestartStrategy {
    @Override
    public boolean checkFailureForEngineDown(String msg) {
        return false;
    }

    @Override
    public boolean checkNOResource(String msg) {
        return false;
    }

}
