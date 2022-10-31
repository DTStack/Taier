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

package com.dtstack.taier.develop.vo.schedule;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 10:46 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnTaskSupportTypesVO {

    /**
     * 组件code值
     */
    private Integer taskTypeCode;

    /**
     * 组件名称
     */
    private String taskTypeName;

    public Integer getTaskTypeCode() {
        return taskTypeCode;
    }

    public void setTaskTypeCode(Integer taskTypeCode) {
        this.taskTypeCode = taskTypeCode;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }
}
