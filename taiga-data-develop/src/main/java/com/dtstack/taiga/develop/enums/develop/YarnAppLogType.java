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

package com.dtstack.taiga.develop.enums.develop;

/**
 * @author toutian
 * @date 2019/5/07
 */
public enum YarnAppLogType {

    /**
     * 数据输出
     */
    DTSTDOUT(1),

    /**
     * 数据错误
     */
    DTERROR(2),

    /**
     * 标准输出
     */
    STDOUT(3),

    /**
     * 错误文件
     */
    STDERR(4);

    private Integer type;

    YarnAppLogType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static YarnAppLogType getType(String name) {
        for (YarnAppLogType type : YarnAppLogType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
