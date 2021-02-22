package com.dtstack.engine.alert.exception;

/**
 * <p>
 *     自定义告警通道异常
 * </p>
 * @author 青涯
 */
public class AlertGateException extends RuntimeException{

    public AlertGateException(){
        super();
    }

    public AlertGateException(String message){
        super(message);
    }
    public AlertGateException(String message, Throwable cause){
    	super(message, cause);
    }

}
