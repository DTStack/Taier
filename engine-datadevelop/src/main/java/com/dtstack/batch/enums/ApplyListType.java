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
 * @author jiangbo
 * @date 2018/5/22 19:33
 */
public enum ApplyListType {

    /**
     * 待我审批
     */
    REPLY_BY_ME(0),

    /**
     * 我申请的
     */
    MY_APPLY(1),

    /**
     * 我已处理的
     */
    REPLIED_BY_ME(2),

    /**
     * 权限收回
     */
    PERMISSION_REVOKE(3);

    private Integer type;

    ApplyListType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
