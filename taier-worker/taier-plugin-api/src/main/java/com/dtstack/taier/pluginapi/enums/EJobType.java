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

package com.dtstack.taier.pluginapi.enums;

/**
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public enum EJobType {
    SQL(0),
    //默认离线MR任务---java_job
    MR(1),
    //数据同步任务
    SYNC(2),
    //离线MR任务--python_job
    PYTHON(3),
    ;
    private int type;

    EJobType(int type) {
        this.type = type;
    }

    public static EJobType getEjobType(int type) {
        EJobType[] eJobTypes = EJobType.values();
        for (EJobType eJobType : eJobTypes) {
            if (eJobType.type == type) {
                return eJobType;
            }
        }
        return null;
    }

    public int getType() {
        return this.type;
    }
}