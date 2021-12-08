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

import java.util.List;

/**
 * @author sanyue
 */
public enum ModelTableRule {
    /**
     * 模型层级
     */
    GRADE(1,"层级"),
    /**
     * 主题域
     */
    SUBJECT(2, "主题域"),
    /**
     * 刷新频率
     */
    REFRESH_RATE(3, "刷新频率"),
    /**
     * 增量方式
     */
    INCRE_TYPE(4, "增量"),
    /**
     * 自定义
     */
    CUSTOM(5, "自定义");

    private int key;
    private String value;
    ModelTableRule(int key, String value){
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(int key){
        for (ModelTableRule rule : ModelTableRule.values()){
            if (rule.getKey() == key){
                return rule.getValue();
            }
        }
        return null;
    }

    public static String concatString(List<Integer> result){
        StringBuilder str = new StringBuilder();
        for (ModelTableRule rule : ModelTableRule.values()){
            if (result.contains(rule.getKey())){
                str.append(rule.getValue()).append("不匹配 ");
            }
        }
        return str.toString();
    }

    public static ModelTableRule getByKey(int key) {
        for (ModelTableRule rule : ModelTableRule.values()){
            if (rule.getKey() == key){
                return rule;
            }
        }
        return null;
    }
}
