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
 * @author sanyue
 * @date 2018/12/11
 */
public enum CarbonDataPartitionType {

    /**
     * native_hive
     */
    NATIVE_HIVE(1),

    /**
     * hash
     */
    HASH(2),

    /**
     * range
     */
    RANGE(3),

    /**
     * list
     */
    LIST(4);

    private Integer type;

    CarbonDataPartitionType(Integer type) {
        this.type = type;

    }

    public Integer getType() {
        return type;
    }
}
