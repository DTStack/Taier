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

package com.dtstack.taier.develop.sql.hive.until;

/**
 * 判断是否是函数操作
 */
public enum ASTNodeFunctionEnum {
    TOK_FUNCTION(750),
    TOK_FUNCTIONDI(751),
    TOK_FUNCTIONSTAR(752),
    DIVIDE(15),
    PLUS(322),
    MINUS(318),
    STAR(330),
    GREATERTHAN(23),
    GREATERTHANOREQUALTO(24),
    LESSTHAN(313),
    LESSTHANOREQUALTO(314),
    EQUAL(20),
    EQUAL_NS(21),
    KW_OR(193),
    KW_AND(34),
    KW_NOT(184)
    ;
    ASTNodeFunctionEnum(Integer value) {
        this.value = value;
    }

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static Boolean isFunction(Integer type){
        for (ASTNodeFunctionEnum value : ASTNodeFunctionEnum.values()) {
            if (type.equals(value.getValue())){
                return true;
            }
        }
        return false;
    }

}
