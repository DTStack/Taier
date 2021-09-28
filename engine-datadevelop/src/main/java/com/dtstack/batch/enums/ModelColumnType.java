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

/**
 * 原子指标中的指标类型
 *
 * @author sanyue
 */
public enum ModelColumnType {
    /**
     * 原子指标
     */
    ATOM(1, "原子指标"),
    /**
     * 修饰词
     */
    qualify(2, "修饰词");

    private int type;
    private String value;

    ModelColumnType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }


    public String getValue() {
        return value;
    }

    public static ModelColumnType getByType(int type) {
        for (ModelColumnType rule : ModelColumnType.values()) {
            if (rule.getType() == type) {
                return rule;
            }
        }
        return null;
    }
}
