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
public enum ModelColumnMonitor {

    /**
     * 字段名称不合理
     */
    BAD_NAME(1, "字段名称不合理"),

    /**
     * 字段类型不合理
     */
    BAD_DATA_TYPE(2, "字段类型不合理"),

    /**
     * 字段描述不合理
     */
    BAD_DESC(3, "字段描述不合理");

    private int type;

    private String value;

    ModelColumnMonitor(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public static String concatString(List<Integer> types) {
        StringBuilder str = new StringBuilder();
        for (ModelColumnMonitor modelColumnMonitor : ModelColumnMonitor.values()) {
            if (types.contains(modelColumnMonitor.getType())) {
                str.append(modelColumnMonitor.getValue()).append(" ");
            }
        }
        return str.toString();
    }

    public String getValue() {
        return value;
    }
}
