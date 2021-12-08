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

package com.dtstack.batch.enums;

import com.dtstack.dtcenter.common.enums.EJobType;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sanyue
 */

public enum TemplateCatalogue {
    //这个是libra独有的
    ODS_LIBRA(0, "原始数据层(ODS)", "fx_customer_base"),
    ODS(1, "原始数据层(ODS)", "exam_ods_ddl"),
    DWD(2, "数仓明细层(DWD)", "exam_dwd_sales_ord_df"),
    DWS(3, "数仓汇总层(DWS)", "exam_dws_sales_shop_1d"),
    ADS(4, "应用数据层(ADS)", "exam_ads_sales_all_d"),
    DIM(5, "数据维度(DIM)", "exam_dim_shop");

    private Integer type;
    private String value;
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

    public static List<String> getValues(){
        TemplateCatalogue[] values = TemplateCatalogue.values();
        List<TemplateCatalogue> valueList = Arrays.asList(values);
        List<String> strings = valueList.stream().map(TemplateCatalogue::getValue).collect(Collectors.toList());
        return strings;
    }

    /**
     * 根据引擎类型获取不同结果
     * @param type
     * @return
     */
    public static List<TemplateCatalogue> getValues(Integer type){
        List<TemplateCatalogue> list = Lists.newLinkedList();
        for (TemplateCatalogue temp : TemplateCatalogue.values()){
           if (ODS_LIBRA.getType().equals(temp.getType()) && !EJobType.LIBRA_SQL.getVal().equals(type)){
                continue;
           }
           list.add(temp);

       }
        return list;
    }
}
