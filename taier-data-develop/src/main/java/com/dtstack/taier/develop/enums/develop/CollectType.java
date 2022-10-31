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

/**
 * @author huoyun
 * @date 2021/4/13 3:00 下午
 * @company: www.dtstack.com
 */
public enum CollectType {
    /**
     * 从任务运行时开始
     */
    ALL(0),

    /**
     * 按时间选择
     */
    TIME(1),

    /**
     * 按文件选择
     */
    FILE(2),

    /**
     * 用户手动输入
     */
    SCN(3),

    /**
     * 从begin点开始
     */
    BEGIN(4),

    /**
     * 从lsn开始
     */
    LSN(5);

    private Integer collectType;

    CollectType(Integer collectType){
        this.collectType = collectType;
    }

    public Integer getCollectType() {
        return collectType;
    }
}
