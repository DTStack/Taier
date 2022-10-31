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

package com.dtstack.taier.scheduler.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 11:17 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum RestartType {
    /**
     * 重跑当前节点
     */
    RESTART_CURRENT_NODE(0),

    /**
     * 重跑及其下游
     */
    RESTART_CURRENT_AND_DOWNSTREAM_NODE(1),

    /**
     * 置成功并恢复调度
     */
    SET_SUCCESSFULLY_AND_RESUME_SCHEDULING(2),

    /**
     * 置成功不恢复调度
     */
    SET_SUCCESSFULLY(3),
    ;

    /**
     * 重跑类型
     */
    private final Integer type;

    RestartType(Integer type) {
        this.type = type;
    }

    public static RestartType getByCode(Integer type){
        if (type == null) {
            return null;
        }
        for (RestartType et:values()){
            if (et.getType().equals(type)){
                return et;
            }
        }
        return null;
    }


    public Integer getType() {
        return type;
    }
}
