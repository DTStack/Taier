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

package com.dtstack.taiga.develop.enums.develop;

import com.google.common.collect.Lists;

import java.util.List;


/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description  任务开发下面，初始化的模板任务的目录
 * @date 2022/1/16 11:23 上午
 */

public enum TemplateCatalogue {
    ODS(1, "原始数据层(ODS)", "exam_ods_ddl"),
    DWD(2, "数仓明细层(DWD)", "exam_dwd_sales_ord_df"),
    DWS(3, "数仓汇总层(DWS)", "exam_dws_sales_shop_1d"),
    ADS(4, "应用数据层(ADS)", "exam_ads_sales_all_d"),
    DIM(5, "数据维度(DIM)", "exam_dim_shop");

    /**
     * 模板任务的目录类型
     */
    private Integer type;

    /**
     * 模板任务的目录显示名称
     */
    private String value;

    /**
     * 模板任务的名称
     */
    private String fileName;

    TemplateCatalogue(Integer type, String value, String fileName) {
        this.value = value;
        this.type = type;
        this.fileName = fileName;
    }

    public String getValue() {
        return value;
    }

    public Integer getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * 所有的模板任务的目录
     * @return
     */
    public static List<TemplateCatalogue> getValues(){
        List<TemplateCatalogue> list = Lists.newLinkedList();
        for (TemplateCatalogue temp : TemplateCatalogue.values()){
           list.add(temp);
       }
        return list;
    }
}
