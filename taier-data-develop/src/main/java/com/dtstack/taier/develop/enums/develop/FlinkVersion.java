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

package com.dtstack.taier.develop.enums.develop;

/**
 * 组建版本
 *
 * @author ：wangchuan
 * date：Created in 上午10:49 2021/7/27
 * company: www.dtstack.com
 */
public enum FlinkVersion {

    FLINK_112("1.12", "112");

    private final String type;

    private final String version;

    public String getVersion() {
        return version;
    }

    FlinkVersion(String type, String version) {
        this.type = type;
        this.version = version;
    }
}
