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

package com.dtstack.batch.common.enums;

/**
 * @author yuebai
 * @date 2019-11-16
 */
public enum RelationResultType {
    /**
     * 是否只是查询
     */
    IS_QUERY(0),
    /**
     * 是否是结果
     */
    IS_RESULT(1);
    private int val;
    RelationResultType(int val){
        this.val = val;
    }
    public int getVal() {
        return val;
    }
}
