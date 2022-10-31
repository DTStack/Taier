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

package com.dtstack.taier.common.enums;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @description:
 * @create: 2021-12-16 00:15
 **/
public enum CatalogueLevel {
    /**
     * 目录层级顶级
     */
    ONE(0),
    /**
     * 目录层级二级
     */
    SECOND(1),
    /**
     * 目录层级其他（多级）
     */
    OTHER(2);

    private int level;

    CatalogueLevel(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
