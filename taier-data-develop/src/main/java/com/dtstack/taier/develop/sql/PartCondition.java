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

package com.dtstack.taier.develop.sql;

/**
 * 分区操作描述
 *
 * @author jiangbo
 */
public class PartCondition {

    /**
     * 分区字段
     */
    private String key;

    /**
     * 字段值
     */
    private String value;

    /**
     * 操作
     */
    private String operate;

    public PartCondition(String key, String operate, String value) {
        this.key = key;
        this.value = value;
        this.operate = operate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    @Override
    public String toString() {
        return "PartCondition{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", operate='" + operate + '\'' +
                '}';
    }
}
