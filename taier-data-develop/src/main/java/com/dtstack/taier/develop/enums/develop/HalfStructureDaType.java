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

import com.dtstack.taier.common.exception.TaierDefineException;

/**
 * 半结构型数据库实时采集类型
 * @author bnyte
 * @since 1.0.0
 */
public enum HalfStructureDaType {
    FILE(0),
    ;
    private int code;

    HalfStructureDaType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


    public static HalfStructureDaType getRdbmsDaType(Integer code) {
        //默认为file
        if (code == null) {
            return FILE;
        }
        for (HalfStructureDaType halfStructureDaType : values()) {
            if (halfStructureDaType.getCode() == code) {
                return halfStructureDaType;
            }
        }
        throw new TaierDefineException("not support HalfStructureDaType");
    }
}
