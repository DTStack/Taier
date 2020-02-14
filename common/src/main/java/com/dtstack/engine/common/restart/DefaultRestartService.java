package com.dtstack.engine.common.restart;



public class DefaultRestartService extends ARestartService {
    public boolean checkFailureForEngineDown(String msg) {
        return false;
    }

    public boolean checkNOResource(String msg) {
        return false;
    }

}
