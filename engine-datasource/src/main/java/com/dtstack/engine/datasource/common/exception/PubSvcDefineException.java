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

package com.dtstack.engine.datasource.common.exception;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.exception.ExceptionEnums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
public class PubSvcDefineException extends DtCenterDefException {

    public PubSvcDefineException(String message) {
        super(message);
    }

    public PubSvcDefineException(String message, Throwable cause) {
        super(message, cause);
    }

    public PubSvcDefineException(ExceptionEnums errorCode) {
        super(errorCode.getDescription());
    }

    public PubSvcDefineException(String message, ExceptionEnums errorCode) {
        super(message, errorCode);
    }

    public PubSvcDefineException(ExceptionEnums errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
    }

    public PubSvcDefineException(String message, ExceptionEnums errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
