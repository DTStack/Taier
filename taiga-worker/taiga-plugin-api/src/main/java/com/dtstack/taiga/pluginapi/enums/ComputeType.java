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

package com.dtstack.taiga.pluginapi.enums;


/**
 *
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public enum ComputeType {
    //
    STREAM(0),
    //
    BATCH(1);

    private Integer type;

    ComputeType(Integer type) {
        this.type = type;
    }

    public static ComputeType getType(int type) {
        ComputeType[] computeTypes = ComputeType.values();
        for (ComputeType computeType : computeTypes) {
            if (computeType.type == type) {
                return computeType;
            }
        }
        return null;
    }

    public Integer getType() {
        return this.type;
    }

    public boolean typeEqual(Integer targetType){
        if(type.equals(targetType)){
            return true;
        }

        return false;
    }

}
