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

package com.dtstack.engine.datasource.dao.bo.query;

import com.dtstack.engine.common.param.DtInsightPageAuthParam;
import lombok.Data;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
public class DsListQuery extends DtInsightPageAuthParam {
    /**
     * 搜索参数
     */
    private String search;
    /**
     * 数据源类型
     */
    private List<String> dataTypeList;
    /**
     * 产品类型
     */
    private List<Integer> appTypeList;
    /**
     * 是否显示默认数据库，0为不显示，1为显示
     */
    private Integer isMeta;
    /**
     * 连接状态
     */
    private List<Integer> status;

}
