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

package com.dtstack.engine.common.exception;


import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.ExceptionEnums;

/**
 * 业务异常
 *
 * @author maoba@dtatck.inc
 */
public class BizException extends DtCenterDefException {
    public BizException() {
        super(ErrorCode.SYS_BUSINESS_EXCEPTION);
    }

    public BizException(ExceptionEnums exEnum) {
        super(exEnum);
    }

    public BizException(ExceptionEnums exEnum, String message) {
        super(exEnum, message);
    }

    public BizException(ExceptionEnums exEnum, String message, Object... args) {
        super(exEnum, message, args);
    }

    public BizException(String message, Object... args) {
        super(ErrorCode.SYS_BUSINESS_EXCEPTION, message, args);
    }

    public BizException(Throwable throwable, ExceptionEnums exEnum) {
        super(throwable, exEnum);
    }

    public BizException(Throwable throwable, ExceptionEnums exEnum, String message) {
        super(throwable, exEnum, message);
    }

    public BizException(Throwable throwable, ExceptionEnums exEnum, String message, Object... args) {
        super(throwable, exEnum, message, args);
    }

    public BizException(Throwable throwable, String message, Object... args) {
        super(throwable, ErrorCode.SYS_BUSINESS_EXCEPTION, message, args);
    }

}
