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
 * @author sanyue
 */
@Data
public class Notify extends TenantEntity {
    /**
     * 业务类型：1 实时  2 离线
     */
    private Integer bizType;
    /**
     * 关联任务id
     */
    private Long relationId;
    /**
     * 通知名称
     */
    private String name;
    /**
     * 触发类型  0 失败触发... 参照AlarmTrigger类
     */
    private Integer triggerType;
    /**
     * 钉钉告警-》自定义机器人的webhook
     */
    private String webhook;
    /**
     * batch 任务 未完成超时的时间设置,HH:mm
     */
    private String uncompleteTime;
    /**
     * 通知方式，从右到左如果不为0即选中（索引位从0开始，第1位：邮件，第2位: 短信，第3位: 微信，第4位: 钉钉）
     */
    private String sendWay;
    /**
     * 允许通知的开始时间，如5：00，早上5点
     */
    private String startTime;
    /**
     * 允许通知的结束时间，如22：00，不接受告警
     */
    private String endTime;
    /**
     * 0：正常，1：停止 2：删除
     */
    private Integer status;
    /**
     * 创建的用户
     */
    private Long createUserId;

    private Integer alarmType;

}
