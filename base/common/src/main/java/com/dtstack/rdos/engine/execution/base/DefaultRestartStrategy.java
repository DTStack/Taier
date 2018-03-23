package com.dtstack.rdos.engine.execution.base;


public class DefaultRestartStrategy implements IRestartStrategy {
    @Override
    public boolean checkFailureForEngineDown(String msg) {
        return false;
    }

    @Override
    public boolean checkNOResource(String msg) {
        return false;
    }

    @Override
    public boolean checkCanRestart(String engineJobId, IClient client) {
        return false;
    }

}
