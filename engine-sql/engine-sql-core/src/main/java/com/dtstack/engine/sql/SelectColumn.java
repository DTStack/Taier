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

/**
 * 查询中的字段
 *
 * @author jiangbo
 * @date 2019/5/22
 */
public class SelectColumn {

    public static final String CONSTANT = "_CONSTANT_";

    /**
     * 字段名称，格式：tb.name
     */
    private String name;

    /**
     * 字段别名 as alias
     */
    private String alias;

    public SelectColumn() {
    }

    public SelectColumn(String name, String alias) {
        this.name = name;
        this.alias = alias == null ? name : alias;
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

    @Override
    public SelectColumn clone() {
        return new SelectColumn(name, alias);
    }
}
