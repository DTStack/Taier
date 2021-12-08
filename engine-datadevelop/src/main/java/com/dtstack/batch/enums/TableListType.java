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
 * @author jiangbo
 * @date 2018/5/22 20:33
 */
public enum TableListType {

    /**
     * 全部
     */
    ALL(0),

    /**
     * 我管理的表
     */
    MANAGED_BY_ME(1),

    /**
     * 被授权的表
     */
    PERMISSION_SUCCESS(2),

    /**
     * 我收藏的表
     */
    COLLECT(3);

    private Integer type;

    TableListType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
