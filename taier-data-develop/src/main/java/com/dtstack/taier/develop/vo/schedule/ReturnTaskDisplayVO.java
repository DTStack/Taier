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

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 11:27 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnTaskDisplayVO {

    /**
     * 方向 0 向上 1 向下
     */
    @ApiModelProperty(value = "查询方向:\n" +
            "FATHER(1):向上查询 \n" +
            "CHILD(2):向下查询",example = "1")
    private Integer directType;

    /**
     * 顶节点（就是vo传过来的节点）
     */
    @ApiModelProperty(value = "顶节点（就是vo传过来的节点）")
    private TaskNodeVO rootTaskNode;

    public Integer getDirectType() {
        return directType;
    }

    public void setDirectType(Integer directType) {
        this.directType = directType;
    }

    public TaskNodeVO getRootTaskNode() {
        return rootTaskNode;
    }

    public void setRootTaskNode(TaskNodeVO rootTaskNode) {
        this.rootTaskNode = rootTaskNode;
    }
}
