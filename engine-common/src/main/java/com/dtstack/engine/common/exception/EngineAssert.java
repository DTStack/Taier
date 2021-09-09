package com.dtstack.engine.common.exception;


import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;

public class EngineAssert {

    public static void assertTrue(boolean expression, String message){
        if(!expression){
            throw new RdosDefineException(message);
        }
    }

    public static void assertTrue(boolean expression, String message, ErrorCode errorCode){
        if(!expression){
            throw new RdosDefineException(message, errorCode);
        }
    }

    public static void assertTrue(boolean expression, ErrorCode errorCode){
        if(!expression){
            throw new RdosDefineException(errorCode);
        }
    }

}
