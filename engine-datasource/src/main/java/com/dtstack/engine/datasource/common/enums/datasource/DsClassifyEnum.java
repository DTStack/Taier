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

package com.dtstack.engine.datasource.common.enums.datasource;

/**
 * 数据源类目枚举类
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
public enum DsClassifyEnum {

    TOTAL(1L, "total", "全部", 10),
    MOST_USE(2L, "mostUse", "常用", 9),
    RELATIONAL(3L, "relational", "关系型", 8),
    BIG_DATA(4L, "bigData", "大数据存储", 7),
    MPP(5L, "mpp", "MPP", 6),
    SEMI_STRUCT(6L, "semiStruct", "半结构化",5),
    ANALYTIC(7L, "analytic", "分析型", 4),
    NO_SQL(8L, "NoSQL", "NoSQL", 3),
    ACTUAL_TIME(9L, "actualTime", "实时", 2),
    API(10L, "api", "接口", 1);


    DsClassifyEnum(Long classifyId, String classifyCode, String classifyName, Integer sorted) {
        this.classifyId = classifyId;
        this.classifyCode = classifyCode;
        this.classifyName = classifyName;
        this.sorted = sorted;
    }

    private Long classifyId;

    private String classifyCode;

    private String classifyName;

    private Integer sorted;

    public Long getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Long classifyId) {
        this.classifyId = classifyId;
    }

    public String getClassifyCode() {
        return classifyCode;
    }

    public void setClassifyCode(String classifyCode) {
        this.classifyCode = classifyCode;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public Integer getSorted() {
        return sorted;
    }

    public void setSorted(Integer sorted) {
        this.sorted = sorted;
    }
}
