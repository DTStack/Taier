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

package com.dtstack.taiga.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/2/9
 */
public enum AppType {

    RDOS(1,"离线开发"),
    DQ(2,"数据质量"),
    API(3,"数据api"),
    TAG(4,"标签引擎"),
    MAP(5,"数据地图"),
    CONSOLE(6,"控制台"),
    STREAM(7,"实时开发"),
    DATASCIENCE(8,"数据科学"),
    DATAASSETS(9,"数据资产"),
    INDEX(10,"指标"),
    DAGSCHEDULEX(99,"调度");

    private int type;

    private String name;

    AppType(int type,String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static AppType getValue(int value) {
        AppType[] values = AppType.values();
        for (AppType appType : values) {
            if (appType.getType() == value) {
                return appType;
            }
        }
        return null;
    }
}
