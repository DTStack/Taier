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

package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 9:20 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum DbType {

    Oracle(2),
    TiDB(31),
    GREENPLUM6(36),
    ANALYTICDB_FOR_PG(54),
    MYSQL(1),
    OCEANBASE(55);

    private int typeCode;

    DbType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }
}
