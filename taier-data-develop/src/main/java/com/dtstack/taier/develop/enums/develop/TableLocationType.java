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

package com.dtstack.taier.develop.enums.develop;

/**
 * Date: 2019/12/16
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public enum TableLocationType {

    /**
     * hive
     */
    HIVE("hive"),

    /**
     * kudu
     */
    KUDU("kudu"),
    ;


    private String value;

    TableLocationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String key() {
        return "tableLocationType";
    }


    public static TableLocationType getTableLocationType(String value) {
        for (TableLocationType tableLocationType : TableLocationType.values()) {
            if (tableLocationType.getValue().equals(value)) {
                return tableLocationType;
            }
        }
        return null;
    }


}
