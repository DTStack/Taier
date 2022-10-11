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

package com.dtstack.taier.common.enums;

/**
 * Reason:
 * Date: 2017/11/10
 * Company: www.dtstack.com
 * @author xuchao
 */
public enum EDeployType {
    //
    STANDALONE(0, "standalone"),
    //
    YARN(1, "yarn"),
    //
    KUBERNETES(2, "kubernetes");

    Integer type;

    String name;

    EDeployType(Integer type, String name){
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static EDeployType getDeployType(String name){
        for(EDeployType eDeployType : EDeployType.values()){
            if(eDeployType.getName().equals(name.toLowerCase())){
                return eDeployType;
            }
        }

        return null;
    }

    public static EDeployType getDeployType(Integer type) {
        for (EDeployType eType : EDeployType.values()) {
            if (eType.getType().equals(type)) {
                return eType;
            }
        }
        return null;
    }
}
