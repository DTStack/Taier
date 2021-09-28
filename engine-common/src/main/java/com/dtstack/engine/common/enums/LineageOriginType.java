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
 * @author chener
 * @Classname LineageOriginType
 * @Description 血缘来源
 * @Date 2020/11/4 15:30
 * @Created chener@dtstack.com
 */
public enum LineageOriginType {
    /**
     * sql解析
     */
    SQL_PARSE(0),

    /**
     * 手动维护
     */
    MANUAL_ADD(1),

    /**
     * json解析
     */
    JSON_PARSE(2),
    ;

    public int getType() {
        return type;
    }

    private int type;

    LineageOriginType(int type) {
        this.type = type;
    }

}
