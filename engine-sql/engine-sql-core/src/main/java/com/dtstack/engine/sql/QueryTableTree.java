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


package com.dtstack.engine.sql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 简化的sql查询树，可以在各sql解析器里构造此对象，然后统一处理字段映射
 *
 * @author jiangbo
 * @date 2019/5/22
 */
public class QueryTableTree {

    /**
     * 表真实名称
     */
    private String name;

    /**
     * 表或子查询的别名
     */
    private String alias;

    /**
     * with语句中的查询
     */
    private boolean cetQuery;

    /**
     * 查询的字段
     */
    private List<SelectColumn> columns;

    private List<QueryTableTree> children = new ArrayList<>();

    private QueryTableTree parent;

    public QueryTableTree() {
    }

    public void addChild(QueryTableTree child){
        if(StringUtils.isNotEmpty(child.name) && StringUtils.isEmpty(child.getAlias())){
            child.setAlias(child.name);
        }

        children.add(child);
    }

    public QueryTableTree getParent() {
        return parent;
    }

    public void setParent(QueryTableTree parent) {
        this.parent = parent;
    }

    public boolean isCetQuery() {
        return cetQuery;
    }

    public void setCetQuery(boolean cetQuery) {
        this.cetQuery = cetQuery;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<SelectColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<SelectColumn> columns) {
        this.columns = columns;
    }

    public List<QueryTableTree> getChildren() {
        return children;
    }

    public void setChildren(List<QueryTableTree> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return getJsonObject().toJSONString();
    }

    private JSONObject getJsonObject(){
        JSONObject object = new JSONObject(true);
        object.put("name",String.valueOf(name));
        object.put("alias",String.valueOf(alias));
        object.put("cetQuery",cetQuery);
        object.put("columns",columns);

        JSONArray childrenJson = new JSONArray();
        for (QueryTableTree child : children) {
            childrenJson.add(child.getJsonObject());
        }
        object.put("children",childrenJson);

        return object;
    }
}
