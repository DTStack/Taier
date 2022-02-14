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
 * 数据同步写入模式
 *
 * @author sanyue
 * @date 2019/1/17
 */
public enum SyncWriteMode {

    /**
     * replace
     */
    REPLACE("replace"),

    /**
     * insert
     */
    INSERT("insert"),

    /**
     * overwrite
     */
    HIVE_OVERWRITE("overwrite"),

    /**
     * append
     */
    HIVE_APPEND("append");

    private String mode;

    SyncWriteMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static String tranferHiveMode(String writeMode) {
        if (writeMode != null) {
            if (writeMode.equalsIgnoreCase(SyncWriteMode.REPLACE.getMode())) {
                writeMode = HIVE_OVERWRITE.getMode();
            } else if (writeMode.equalsIgnoreCase(SyncWriteMode.INSERT.getMode())) {
                writeMode = HIVE_APPEND.getMode();
            }
        }
        return writeMode;
    }
}
