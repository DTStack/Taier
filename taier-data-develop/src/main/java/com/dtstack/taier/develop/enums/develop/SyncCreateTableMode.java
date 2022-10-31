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
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:25 2019-07-04
 * @Description：数据同步建表策略
 */
public enum SyncCreateTableMode {
    /**
     * 自动建表
     */
    AUTO_CREATE(0),
    /**
     * 手动创建
     */
    MANUAL_SELECTION(1);

    private Integer mode;

    SyncCreateTableMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public static SyncCreateTableMode getByMode(Integer mode){
        SyncCreateTableMode[] values = SyncCreateTableMode.values();
        for (SyncCreateTableMode value : values) {
            if (value.getMode().equals(mode)){
                return value;
            }
        }
        return null;
    }
}
