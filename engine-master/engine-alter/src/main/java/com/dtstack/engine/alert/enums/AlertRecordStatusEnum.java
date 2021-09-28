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

package com.dtstack.engine.alert.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/1/14 2:58 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum AlertRecordStatusEnum {

    NO_WARNING(0,"未告警"),
    ALARM_QUEUE(1,"告警队列中"),
    SENDING_ALARM(2,"告警发送中"),
    ALERT_SUCCESS(3,"告警成功"),
    TO_BE_SCANNED(4,"待扫描中"),
    ;

    private Integer type;

    private String msg;

    AlertRecordStatusEnum(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
