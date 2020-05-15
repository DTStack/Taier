package com.dtstack.engine.common.exception;


import org.apache.commons.lang3.StringUtils;

/**
 * @author sishu.yss
 */
public class RdosDefineException extends RuntimeException {


    private String errorMessage;

    private ErrorCode errorCode;

    public RdosDefineException(Throwable cause){
        super(cause);
    }

    public RdosDefineException(String errorMessage){
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public RdosDefineException(String errorMessage, Throwable cause){
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public RdosDefineException(ErrorCode errorCode){
        super(buildErrorInfo(errorCode, errorCode.getDescription()));
        this.errorCode = errorCode;
        setErrorMessage("");
    }

    public RdosDefineException(String message, ErrorCode errorCode){
        super(buildErrorInfo(errorCode, message));
        this.errorCode = errorCode;
        setErrorMessage(message);
    }

    public RdosDefineException(ErrorCode errorCode, Throwable cause){
        super(buildErrorInfo(errorCode, errorCode.getDescription()), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public RdosDefineException(String message, ErrorCode errorCode, Throwable cause) {
        super(buildErrorInfo(errorCode, message), cause);
        this.errorCode = errorCode;
        setErrorMessage(message);
    }

    private void setErrorMessage(String extMsg){
        if(StringUtils.isEmpty(extMsg)){
            this.errorMessage = errorCode.getDescription();
        }else{
            this.errorMessage = errorCode.getDescription() + "-" + extMsg;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorMsg() {
        return errorMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    private static String buildErrorInfo(ErrorCode errorCode, String errorMessage) {
        return "{errorCode=" + errorCode.getCode() +
                ", errorMessage=" + errorMessage + "}";
    }
}
