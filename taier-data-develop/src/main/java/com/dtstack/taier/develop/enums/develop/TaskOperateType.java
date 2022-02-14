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

public enum TaskOperateType {

    /**
     * resource
     */
    RESOURCE(0),

    /**
     * 修改
     */
    EDIT(1),

    /**
     * 创建
     */
    CREATE(2),

    /**
     * 冻结
     */
    FROZEN(3),

    /**
     * 解冻
     */
    THAW(4),

    /**
     * 提交
     */
    COMMIT(5);

    private int type;

    TaskOperateType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
