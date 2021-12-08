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

public enum PackageItemType {

    /**
     * 任务
     */
    TASK(0),

    /**
     * 表
     */
    TABLE(1),

    /**
     * 资源
     */
    RESOURCE(2),

    /**
     * 函数
     */
    FUNCTION(3),

    /**
     * 存储过程
     */
    PROCEDURE(4);

    private int type;

    PackageItemType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
