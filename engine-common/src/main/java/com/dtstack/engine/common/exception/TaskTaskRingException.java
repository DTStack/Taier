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
import com.dtstack.engine.pluginapi.exception.RdosDefineException;

/**
 * @Auther: dazhi
 * @Date: 2021/4/9 4:35 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskTaskRingException extends RdosDefineException {
    public TaskTaskRingException(Throwable cause) {
        super(cause);
    }

    public TaskTaskRingException(String errorMessage) {
        super(errorMessage);
    }

    public TaskTaskRingException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public TaskTaskRingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TaskTaskRingException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TaskTaskRingException(String message, ErrorCode errorCode, String url) {
        super(message, errorCode, url);
    }

    public TaskTaskRingException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public TaskTaskRingException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
