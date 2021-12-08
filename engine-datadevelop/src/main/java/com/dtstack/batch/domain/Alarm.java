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

package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantEntity;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/26
 */
@SuppressWarnings("serial")
@Data
public class Alarm extends TenantEntity {

    /**
     * 告警名称
     */
    private String name;

    private Long taskId;

    /**
     * 触发条件   0 失败
     */
    private Integer myTrigger;

    /**
     * 告警状态   0 正常 1关闭 2删除
     */
    private Integer status;

    private Integer senderType;

    /** 接收方式 */
    private String receiveTypes;


    private Long createUserId;



    //任务负责人 1 有任务负责人  0无任务负责人
    private  Integer  isTaskHolder;

    //告警类型  1：项目报告，2：任务告警
    private Integer alarmType;

    // 项目报告发送时间
    private String sendTime;

    public Integer getIsTaskHolder() {
        return isTaskHolder;
    }

    public void setIsTaskHolder(Integer isTaskHolder) {
        this.isTaskHolder = isTaskHolder;
    }
}
