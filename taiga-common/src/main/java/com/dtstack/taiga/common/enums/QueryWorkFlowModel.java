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

package com.dtstack.taiga.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public enum QueryWorkFlowModel {
    /**
     * 1.排除工作流子节点                       flow_job_id = 0
     * 2.只查询工作流子节点                     flow_job_id != 0
     * 3.父子节点都有查                        无参数
     * 4.排除工作流父节点                      task_type != 10
     */

    Eliminate_Workflow_SubNodes(1),
    Only_Workflow_SubNodes(2),
    Full_Workflow_Job(3),
    Eliminate_Workflow_ParentNodes(4);

    private Integer type;

    QueryWorkFlowModel(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
