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


import com.dtstack.taier.common.exception.RdosDefineException;

/**
 * Date: 2020/1/8
 * Company: www.dtstack.com
 * 关系型数据库实时采集类型
 * 1. binlog(数据库支持的同步方案，mysql为binlog oracle为logminer，sqlServer为cdc)
 * 2. 间隔轮询
 * @author xiaochen
 */
public enum RdbmsDaType {
    /**
     * binlog 日志
     */
    Binlog(1),

    /**
     * 间隔轮询
     */
    Poll(2),

    /**
     * CDC
     */
    CDC(3),

    /**
     * logminer
     */
    LOGMINER(4),

    ;


    private int code;

    RdbmsDaType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


    public static RdbmsDaType getRdbmsDaType(Integer code) {
        //默认为Binlog
        if (code == null) {
            return Binlog;
        }
        for (RdbmsDaType rdbmsDaType : values()) {
            if (rdbmsDaType.getCode() == code) {
                return rdbmsDaType;
            }
        }
        throw new RdosDefineException("not support RdbmsDaType");
    }
}
