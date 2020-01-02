package com.dtstack.engine.common.restart;



public class DefaultRestartService extends ARestartService {
    @Override
    public boolean checkFailureForEngineDown(String msg) {
        return false;
    }

    @Override
    public boolean checkNOResource(String msg) {
        return false;
    }

}
