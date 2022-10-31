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

package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Hdfs3SourceDTO extends RdbmsSourceDTO {
    /**
     * Hadoop defaultFS
     */
    @Builder.Default
    private String defaultFS = "";

    /**
     * Hadoop/ Hbase 配置信息
     */
    private String config;

    /************************************临时处理 yarn 日志下载*********************************************/
    /**
     * appid 字符串
     */
    private String appIdStr;

    /**
     * 读取长度限制
     */
    @Builder.Default
    private Integer readLimit = 4095;

    /**
     * 日志下载角色
     */
    private String user;

    /**
     * 日志类型
     */
    private String logType = null;

    /**
     * yarn 配置
     */
    private Map<String, Object> yarnConf;

    /**
     * taskmanager id
     */
    private String containerId;


    @Override
    public Integer getSourceType() {
        return DataSourceType.HDFS3.getVal();
    }
}
