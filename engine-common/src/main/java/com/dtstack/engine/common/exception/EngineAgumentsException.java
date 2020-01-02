package com.dtstack.engine.common.exception;

/**
 * Created by sishu.yss on 2017/3/28.
 */
public class EngineAgumentsException extends Exception{

    private static String template = "%s Not Allowed Null";

    public EngineAgumentsException(String agument){
        super(String.format(template,agument));
    }
}


