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

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 组建版本
 *
 * @author ：wangchuan
 * date：Created in 上午10:49 2021/7/27
 * company: www.dtstack.com
 */
public enum FlinkVersion {

    FLINK_112("1.12", Arrays.asList("1.12-on-yarn", "1.12-standalone"));

    private final String type;

    private final List<String> versions;

    public  String getType() {
        return type;
    }

    public List<String> getVersions() {
        return versions;
    }

    FlinkVersion(String type, List<String> versions) {
        this.type = type;
        this.versions = versions;
    }

    /**
     * 获取 flink 版本枚举
     *
     * @param version 版本 string
     * @return flink 枚举
     */
    public static FlinkVersion getVersion(String version) {
        if (StringUtils.isNotBlank(version)) {
            for (FlinkVersion componentVersion : FlinkVersion.values()) {
                if (StringUtils.equalsIgnoreCase(version, componentVersion.getType())) {
                    return componentVersion;
                }
                if (componentVersion.getVersions().contains(version)) {
                    return componentVersion;
                }
            }
        }
        return FLINK_112;
    }
}
