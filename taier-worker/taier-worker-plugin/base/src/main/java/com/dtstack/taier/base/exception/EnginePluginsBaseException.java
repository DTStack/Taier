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

package com.dtstack.taier.base.exception;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class EnginePluginsBaseException extends RuntimeException {
    private String errorMessage;
    private ErrorCode errorCode;

    public EnginePluginsBaseException() {
    }

    public EnginePluginsBaseException(Throwable cause) {
        super(cause);
    }

    public EnginePluginsBaseException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public EnginePluginsBaseException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public EnginePluginsBaseException(ErrorCode errorCode) {
        super(buildErrorInfo(errorCode, errorCode.getDescription()));
        this.errorCode = errorCode;
        this.setErrorMessage("");
    }

    public EnginePluginsBaseException(String message, ErrorCode errorCode) {
        super(buildErrorInfo(errorCode, message));
        this.errorCode = errorCode;
        this.setErrorMessage(message);
    }

    public EnginePluginsBaseException(String message, ErrorCode errorCode, String url) {
        super(buildErrorInfo(errorCode, message, url));
        this.errorCode = errorCode;
        this.setErrorMessage(message);
    }

    public EnginePluginsBaseException(ErrorCode errorCode, Throwable cause) {
        super(buildErrorInfo(errorCode, errorCode.getDescription()), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public EnginePluginsBaseException(String message, ErrorCode errorCode, Throwable cause) {
        super(buildErrorInfo(errorCode, message), cause);
        this.errorCode = errorCode;
        this.setErrorMessage(message);
    }

    private void setErrorMessage(String extMsg) {
        if (StringUtils.isEmpty(extMsg)) {
            this.errorMessage = this.errorCode.getDescription();
        } else {
            this.errorMessage = this.errorCode.getDescription() + "-" + extMsg;
        }

    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getErrorMsg() {
        return this.errorMessage;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    private static String buildErrorInfo(ErrorCode errorCode, String errorMessage) {
        return "{errorCode=" + errorCode.getCode() + ", errorMessage=" + errorMessage + "}";
    }

    private static String buildErrorInfo(ErrorCode errorCode, String errorMessage, String url) {
        return "{errorCode=" + errorCode.getCode() + ", errorMessage=" + errorMessage + ", url=" + url + "}";
    }

    public enum ErrorCode implements ExceptionEnums, Serializable {
        UNKNOWN_ERROR(10, "unknown error"),
        ;

        private final int code;
        private final String enMsg;

        ErrorCode(int code, String enMsg) {
            this.code = code;
            this.enMsg = enMsg;
        }

        @Override
        public int getCode() {
            return this.code;
        }

        @Override
        public String getDescription() {
            return getMsg();
        }

        public String getMsg() {
            return this.enMsg;
        }
    }

    public interface ExceptionEnums {

        int getCode();

        String getDescription();
    }
}
