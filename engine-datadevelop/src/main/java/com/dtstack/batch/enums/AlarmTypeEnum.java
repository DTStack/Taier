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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum AlarmTypeEnum {
    /**
     * 邮件
     */
    MAIL(1, "邮件", "default_MAIL_2", 2),
    /**
     * 短信
     */
    SMS(2, "短信", "default_SMS_1", 1),
    /**
     * 钉钉
     */
    DINGDING(4, "钉钉", "default_DINGDING_3", 3),
    /**
     * 电话
     */
    PHONE(5, "电话", "default_PHONE_5", 5);

    // 类别（历史数据）
    private Integer type;

    // 通道名称
    private String alertGateName;

    // 通道标识
    private String alertGateSource;

    // 通道类别
    private Integer alertGateType;

    AlarmTypeEnum(Integer type, String alertGateName, String alertGateSource, Integer alertGateType) {
        this.type = type;
        this.alertGateName = alertGateName;
        this.alertGateSource = alertGateSource;
        this.alertGateType = alertGateType;
    }

    /**
     * 获取到所有的配置方式
     *
     * @return
     */
    public static List<AlarmTypeEnum> getAlarmTypeList() {
        List<AlarmTypeEnum> typeList = new ArrayList<>();
        typeList.add(AlarmTypeEnum.MAIL);
        typeList.add(AlarmTypeEnum.SMS);
        typeList.add(AlarmTypeEnum.DINGDING);
        typeList.add(AlarmTypeEnum.PHONE);
        return typeList;
    }

    /**
     * 获取历史告警通道类别和现在告警通道的映射关系
     *
     * @return
     */
    public static Map<Integer, String> getAlarmTypeMap() {
        Map<Integer, String> alarmTypeMap = new HashMap<>();
        alarmTypeMap.put(AlarmTypeEnum.MAIL.type, AlarmTypeEnum.MAIL.alertGateSource);
        alarmTypeMap.put(AlarmTypeEnum.SMS.type, AlarmTypeEnum.SMS.alertGateSource);
        alarmTypeMap.put(AlarmTypeEnum.DINGDING.type, AlarmTypeEnum.DINGDING.alertGateSource);
        alarmTypeMap.put(AlarmTypeEnum.PHONE.type, AlarmTypeEnum.PHONE.alertGateSource);
        return alarmTypeMap;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAlertGateName() {
        return alertGateName;
    }

    public void setAlertGateName(String alertGateName) {
        this.alertGateName = alertGateName;
    }

    public String getAlertGateSource() {
        return alertGateSource;
    }

    public void setAlertGateSource(String alertGateSource) {
        this.alertGateSource = alertGateSource;
    }

    public Integer getAlertGateType() {
        return alertGateType;
    }

    public void setAlertGateType(Integer alertGateType) {
        this.alertGateType = alertGateType;
    }
}
