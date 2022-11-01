/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common.exception;


import org.apache.commons.lang3.StringUtils;

/**
 * @author sishu.yss
 */
public class TaierDefineException extends RuntimeException {


    private String errorMessage;

    private ErrorCode errorCode;

    public TaierDefineException() {
    }

    public TaierDefineException(Throwable cause) {
        super(cause);
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public TaierDefineException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public TaierDefineException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public TaierDefineException(ErrorCode errorCode) {
        super(buildErrorInfo(errorCode, errorCode.getDescription()));
        this.errorCode = errorCode;
        setErrorMessage("");
    }

    public TaierDefineException(String message, ErrorCode errorCode) {
        super(buildErrorInfo(errorCode, message));
        this.errorCode = errorCode;
        setErrorMessage(message);
    }

    public TaierDefineException(String message, ErrorCode errorCode, String url) {
        super(buildErrorInfo(errorCode, message, url));
        this.errorCode = errorCode;
        setErrorMessage(message);
    }

    public TaierDefineException(ErrorCode errorCode, Throwable cause) {
        super(buildErrorInfo(errorCode, errorCode.getDescription()), cause);
        this.errorCode = errorCode;
    }

    public TaierDefineException(String message, ErrorCode errorCode, Throwable cause) {
        super(buildErrorInfo(errorCode, message), cause);
        this.errorCode = errorCode;
        setErrorMessage(message);
    }

    private void setErrorMessage(String extMsg) {
        if (StringUtils.isEmpty(extMsg)) {
            this.errorMessage = errorCode.getDescription();
        } else {
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
        return errorMessage;
    }

    private static String buildErrorInfo(ErrorCode errorCode, String errorMessage, String url) {
        return errorMessage + url;
    }
}
