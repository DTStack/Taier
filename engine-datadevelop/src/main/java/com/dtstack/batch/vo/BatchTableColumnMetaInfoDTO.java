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

package com.dtstack.batch.vo;

import lombok.Data;

/**
 * 表字段元数据信息
 *
 * date: 2021/6/16 5:53 下午
 * author: zhaiyue
 */
@Data
public class BatchTableColumnMetaInfoDTO {

    /**
     * 字段名
     */
    private String key;

    /**
     * 类型
     */
    private String type;

    /**
     * 备注
     */
    private String comment;

    /**
     * 是否是分区字段
     */
    private Boolean part = false;

    /**
     * 小数点右边的指定列的位数
     */
    private Integer scale;

    /**
     * 指定列的指定列大小
     */
    private Integer precision;

}
