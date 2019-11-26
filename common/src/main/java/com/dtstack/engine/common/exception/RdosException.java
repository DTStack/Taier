package com.dtstack.engine.common.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosException extends RuntimeException {

    private String errorMessage;

    private ErrorCode errorCode;

    public RdosException(String errorMessage){
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public RdosException(ErrorCode errorCode){
        super(buildErrorInfo(errorCode, errorCode.getDescription()));
        this.errorCode = errorCode;
        setErrorMessage("");
    }

    public RdosException(String message, ErrorCode errorCode){
        super(buildErrorInfo(errorCode, message));
        this.errorCode = errorCode;
        setErrorMessage(message);
    }

    public RdosException(ErrorCode errorCode, Throwable cause){
        super(buildErrorInfo(errorCode, errorCode.getDescription()), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public RdosException(String message, ErrorCode errorCode, Throwable cause) {
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
