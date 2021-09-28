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

package com.dtstack.engine.datasource.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.engine.datasource.dao.po.BaseModel;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@TableName("dsc_app_list")
public class DsAppList extends BaseModel<DsAppList> {

    /**
     * 产品type code
     */
    @TableField("app_type")
    private Integer appType;

    /**
     * 产品编码
     */
    @TableField("app_code")
    private String appCode;

    /**
     * 产品名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 是否隐藏 0-不隐藏 1-隐藏
     */
    @TableField("invisible")
    private Integer invisible;

    /**
     * 排序值
     */
    @TableField("sorted")
    private Integer sorted;
}
