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


import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;

/**
 * 实时采集任务 操作类型
 *
 * @author sanyue
 * @date 2018/9/17
 */
public enum DAoperators {

    /**
     * 插入
     */
    insert(1),

    /**
     * 更新
     */
    update(2),

    /**
     * 删除
     */
    delete(3);

    private Integer val;

    DAoperators(Integer val) {
        this.val = val;
    }

    public Integer getVal() {
        return val;
    }

    public static DAoperators getByVal(Integer val){
        DAoperators[] values = DAoperators.values();
        for (DAoperators value : values) {
            if (value.getVal().equals(val)){
                return value;
            }
        }
        throw new TaierDefineException("实时采集任务操作类型选择错误", ErrorCode.INVALID_PARAMETERS);
    }
}
