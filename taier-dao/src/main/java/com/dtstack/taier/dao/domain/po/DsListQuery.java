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

package com.dtstack.taier.dao.domain.po;


import java.util.List;

/**
 * 数据源列表参数类
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/26
 */
public class DsListQuery extends DaoPageParam {
    /**
     * 搜索参数
     */
    private String search;
    /**
     * 数据源类型
     */
    private List<String> dataTypeList;

    /**
     * 数据源类型code 码
     */
    private Integer dataTypeCode;

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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getDataTypeList() {
        return dataTypeList;
    }

    public void setDataTypeList(List<String> dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public List<Integer> getAppTypeList() {
        return appTypeList;
    }

    public void setAppTypeList(List<Integer> appTypeList) {
        this.appTypeList = appTypeList;
    }

    public Integer getIsMeta() {
        return isMeta;
    }

    public void setIsMeta(Integer isMeta) {
        this.isMeta = isMeta;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public Integer getDataTypeCode() {
        return dataTypeCode;
    }

    public void setDataTypeCode(Integer dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }
}
