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

package com.dtstack.taier.pluginapi.enums;

import com.dtstack.taier.pluginapi.exception.PluginDefineException;

public enum EDeployMode {
    PERJOB("perjob", 1),
    SESSION("session", 2),
    STANDALONE("standalone", 3);

    private final String mode;
    private final Integer type;

    public String getMode() {
        return mode;
    }

    public Integer getType() {
        return type;
    }

    EDeployMode(String mode, Integer type) {
        this.mode = mode;
        this.type = type;
    }

    public static EDeployMode getByType(Integer type) {
        for (EDeployMode value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        throw new PluginDefineException("不支持的模式");
    }
}