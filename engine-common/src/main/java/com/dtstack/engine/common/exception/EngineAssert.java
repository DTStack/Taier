package com.dtstack.engine.common.exception;


public class EngineAssert {

    public static void assertTrue(boolean expression, String message){
        if(!expression){
            throw new RdosDefineException(message);
        }
    }

}
