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
 * date: 2021/4/12 5:37 下午
 * author: zhaiyue
 */
public enum  ProductCodeEnum {

    /**
     * 离线开发
     */
    RDOS(1,"RDOS", "rdos", "离线开发"),

    /**
     * 算法开发
     */
    SCIENCE(8, "RDOS", "science", "算法开发"),

    /**
     * 智能标签
     */
    TAG(4, "RDOS", "tagEngine", "智能标签");

    private Integer type;

    private String productCode;

    private String subProductCode;

    private String subProductName;

    ProductCodeEnum(Integer type, String productCode, String subProductCode, String subProductName){
        this.type = type;
        this.productCode = productCode;
        this.subProductCode = subProductCode;
        this.subProductName = subProductName;
    }

    public Integer getType(){
        return type;
    }

    public String getProjectCode(){
        return productCode;
    }

    public String getSubProductCode(){
        return subProductCode;
    }

    public String getSubProductName(){
        return subProductName;
    }


}
