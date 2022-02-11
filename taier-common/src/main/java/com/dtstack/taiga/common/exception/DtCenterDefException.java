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

package com.dtstack.taiga.common.exception;

import com.dtstack.taiga.common.util.Strings;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 猫爸
 */
public class DtCenterDefException extends RuntimeException {
    private int code;

    public DtCenterDefException(String message){
        super(message);
        this.code = ErrorCode.UNKNOWN_ERROR.getCode();
    }
    public DtCenterDefException(String message,Throwable throwable){
        super(message,throwable);
        this.code = ErrorCode.UNKNOWN_ERROR.getCode();
    }

    public DtCenterDefException(ExceptionEnums exEnum) {
        super(exEnum.getDescription());
        code = exEnum.getCode();
    }

    public DtCenterDefException(ExceptionEnums exEnum, String message) {

        super(StringUtils.isNotBlank(message) ? message : exEnum.getDescription());
        code = exEnum.getCode();
    }

    public DtCenterDefException(ExceptionEnums exEnum, String message, Object... args) {
        super(StringUtils.isNotBlank(message) ? Strings.format(message, args) : exEnum.getDescription());
        code = exEnum.getCode();
    }

    public DtCenterDefException(Throwable throwable, ExceptionEnums exEnum) {
        super(exEnum.getDescription(), throwable);
        code = exEnum.getCode();
    }

    public DtCenterDefException(Throwable throwable, ExceptionEnums exEnum, String message) {
        super(StringUtils.isNotBlank(message) ? message : exEnum.getDescription(), throwable);
        code = exEnum.getCode();
    }

    public DtCenterDefException(Throwable throwable, ExceptionEnums exEnum, String message, Object... args) {
        super(StringUtils.isNotBlank(message) ? Strings.format(message, args) : exEnum.getDescription(), throwable);
        code = exEnum.getCode();
    }

    public int getCode() {
        return code;
    }
}
