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

import lombok.Data;

/**
 * 数据库信息
 *
 * @author ：wangchuan
 * date：Created in 下午1:56 2021/9/14
 * company: www.dtstack.com
 */
@Data
public class Database {

    /**
     * 数据库名称
     */
    private String DbName;

    /**
     * 数据库注释
     */
    private String comment;

    /**
     * 数据库存储位置
     */
    private String location;

    /**
     * 所有者名称
     */
    private String ownerName;

}
