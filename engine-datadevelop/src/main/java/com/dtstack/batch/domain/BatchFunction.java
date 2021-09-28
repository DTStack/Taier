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

package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

@Data
public class BatchFunction extends TenantProjectEntity {

    /**
     * 函数名称
     */
    private String name;

    /**
     * main函数类名
     */
    private String className;

    /**
     * 函数用途
     */
    private String purpose;

    /**
     * 函数命令格式
     */
    private String commandFormate;

    /**
     * 函数参数说明
     */
    private String paramDesc;

    /**
     * 父文件夹id
     */
    private Long nodePid;

    private Long createUserId;

    private Long modifyUserId;

    /**
     * 0：自定义函数  1：系统函数  2：存储过程
     */
    private Integer type;

    private Integer engineType;

    /**
     * 导入导出添加，函数资源名称
     */
    private String resourceName;

    /**
     * 存储过程sql
     */
    private String sqlText;

}
