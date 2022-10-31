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

package com.dtstack.taier.datasource.api.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:58 2020/9/12
 * @Description：表信息
 */
@Data
public class Table {
    /**
     * 表所在数据库
     */
    private String db;

    /**
     * 所有者
     */
    private String owner;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 最近访问时间
     */
    private String lastAccess;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String comment;

    /**
     * 分隔符，hive表属性
     */
    private String delim;

    /**
     * 存储格式，hive表属性
     */
    private String storeType;

    /**
     * 表路径
     */
    private String path;

    /**
     * 表类型:EXTERNAL-外部表，MANAGED-内部表
     */
    private String externalOrManaged;

    /**
     * 字段
     */
    private List<ColumnMetaDTO> columns = Lists.newArrayList();

    /**
     * 是否是事务表
     */
    private Boolean isTransTable = false;

    /**
     * 是否是视图
     */
    private Boolean isView = false;

    /**
     * 是否是分区表
     */
    private Boolean isPartitionTable = false;

    /**
     * 是否是单字符作为分隔符
     */
    private Boolean isLazySimpleSerDe = true;
}
