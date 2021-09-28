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

public enum DeployModeEnum {

    /**
     * session
     */
    SESSION("session",2),

    /**
     * perjob
     */
    PERJOB("perjob",1),

    /**
     * standalone
     */
    STANDALONE("standalone",3);

    private String name;
    private Integer type;

    DeployModeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    /**
     * 解决taskParms 中对应的类型
     * @param taskParams
     * @return
     */
    public static DeployModeEnum parseDeployTypeByTaskParams(String taskParams) {
        if (org.apache.commons.lang.StringUtils.isBlank(taskParams)) {
            return DeployModeEnum.SESSION;
        }
        String[] split = taskParams.split("\n");
        if (split.length <= 0) {
            return DeployModeEnum.SESSION;
        }
        for (String s : split) {
            String trim = s.toLowerCase().trim();
            if (trim.startsWith("#")){
                continue;
            }
            if (trim.contains("flinktaskrunmode")) {
                if (trim.contains("session")) {
                    return DeployModeEnum.SESSION;
                } else if (trim.contains("per_job")) {
                    return DeployModeEnum.PERJOB;
                } else if (trim.contains("standalone")) {
                    return DeployModeEnum.STANDALONE;
                }
            }
        }
        return DeployModeEnum.SESSION;
    }

}