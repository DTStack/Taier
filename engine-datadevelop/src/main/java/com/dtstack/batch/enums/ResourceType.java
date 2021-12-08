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
 * @author chener
 * @Classname ResourceType
 * @Description 用于发布时，关联资源表 rdos_batch_test_produce_resource 中类型支持函数
 * @Date 2020/5/28 15:33
 * @Created chener@dtstack.com
 */
public enum ResourceType {
    /**
     * 资源
     */
    RESOURCE(0),
    /**
     * 函数
     */
    FUNCTION(1),
    ;

    private int type;

    public int getType() {
        return type;
    }

    ResourceType(int typeCode) {
        this.type = typeCode;
    }
    public static ResourceType getByTypeCode(int code){
        for (ResourceType type:values()){
            if (type.type == code){
                return type;
            }
        }
        return null;
    }
}
