package com.dtstack.engine.common.exception;

import com.dtstack.dtcenter.common.exception.ExceptionEnums;

public class EngineAssert {

    public static void assertTrue(boolean expression, String message){
        if(!expression){
            throw new EngineDefineException(message);
        }
    }

    public static void assertFalse(boolean expression, String message){
        assertTrue(!expression, message);
    }

    public static void assertTrue(boolean expression, ExceptionEnums errorCode){
        if(!expression){
            throw new EngineDefineException(errorCode);
        }
    }

    public static void assertFalse(boolean expression, ExceptionEnums errorCode){
        assertTrue(!expression, errorCode);
    }

    public static void assertTrue(boolean expression, String message, ExceptionEnums errorCode){
        if(!expression){
            throw new EngineDefineException(message, errorCode);
        }
    }

    public static void assertFalse(boolean expression, String message, ExceptionEnums errorCode){
        assertTrue(!expression, message, errorCode);
    }

    public static void assertTrue(boolean expression, ExceptionEnums errorCode, Throwable cause){
        if(!expression){
            throw new EngineDefineException(errorCode, cause);
        }
    }

    public static void assertFalse(boolean expression, ExceptionEnums errorCode, Throwable cause){
        assertTrue(!expression, errorCode, cause);
    }

    public static void assertTrue(boolean expression, String message, ExceptionEnums errorCode, Throwable cause){
        if(!expression){
            throw new EngineDefineException(message, errorCode, cause);
        }
    }

    public static void assertFalse(boolean expression, String message, ExceptionEnums errorCode, Throwable cause){
        assertTrue(!expression, message, errorCode, cause);
    }
}
