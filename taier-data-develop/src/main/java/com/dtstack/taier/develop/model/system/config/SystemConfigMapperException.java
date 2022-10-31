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

package com.dtstack.taier.develop.model.system.config;

import com.dtstack.taier.common.util.Strings;

/**
 * System config mapper exception.
 */
public class SystemConfigMapperException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Invalid system config: '{}'. {}";

    private final String configName;
    private final String message;

    public SystemConfigMapperException(String configName, String message) {
        this.configName = configName;
        this.message = message;
    }

    public SystemConfigMapperException(String configName, String message, Throwable cause) {
        this(configName, message);
        this.initCause(cause);
    }

    @Override
    public String getMessage() {
        return Strings.format(MESSAGE_TEMPLATE, this.configName, this.message);
    }

}
