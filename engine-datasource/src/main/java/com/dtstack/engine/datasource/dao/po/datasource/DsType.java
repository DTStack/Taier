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
@TableName("dsc_type")
public class DsType extends BaseModel<DsType> {

    /**
     * 数据源类型唯一 如Mysql, Oracle, Hive
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 数据源分类栏主键id
     */
    @TableField("data_classify_id")
    private Long dataClassifyId;

    /**
     * 数据源权重
     */
    @TableField("weight")
    private Double weight;

    /**
     * 数据源logo图片地址
     */
    @TableField("img_url")
    private String imgUrl;

    /**
     * 排序值
     */
    @TableField("sorted")
    private Integer sorted;

    /**
     * 是否隐藏
     */
    @TableField("invisible")
    private Integer invisible;


}
