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

package com.dtstack.engine.datasource.dao.bo.datasource;

import lombok.Data;

import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
public class DsServiceListBO {

    /**
     * 数据源主键id
     */
    private Long dataInfoId;

    /**
     * 数据源名称
     */
    private String dataName;
    /**
     * 数据源类型加版本号
     */
    private String dataType;

    /**
     * 数据源版本
     */
    private String dataVersion;

    /**
     * 数据源描述
     */
    private String dataDesc;

    /**
     * 数据源连接信息 json
     */
    private String linkJson;

    /**数据愿配置信息**/
    private String dataJson;

    /**
     * 是否有meta标志 0-否 1-是
     */
    private Integer isMeta;

    /**
     * 连接状态 0-连接失败, 1-正常
     */
    private Integer status;

    /**
     * 最近修改时间
     */
    private Date gmtModified;

    /**数据库名称**/
    private String schemaName;

    /**
     * 数据源code
     */
    private Integer dataTypeCode;

}
