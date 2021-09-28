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

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/11/9
 */
@Data
public class BatchScript extends TenantProjectEntity {

    /**
     * 脚本名称
     */
    private String name;

    /**
     * 脚本描述
     */
    private String scriptDesc;

    /**
     * 父文件夹id
     */
    private Long nodePid;

    /**
     * 创建者用户id
     */
    private Long createUserId;

    /**
     * 修改者用户id
     */
    private Long modifyUserId;

    /**
     * 脚本类型,0-sql,1-python,2-shell
     */
    private Integer type;

    /**
     * 脚本内容
     */
    private String scriptText;

    /**
     * 脚本环境参数
     */
    private String taskParams;

    /**
     * 脚本版本号
     */
    private Integer version;

}
