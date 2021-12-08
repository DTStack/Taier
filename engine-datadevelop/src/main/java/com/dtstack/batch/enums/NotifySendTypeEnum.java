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
import java.util.List;

public enum NotifySendTypeEnum {

    /**
     * 邮件
     */
    MAIL(1, "邮件"),

    /**
     * 短信
     */
    SMS(2, "短信"),

    /**
     * 微信
     */
    WEICHAT(3, "微信"),

    /**
     * 钉钉
     */
    DINGDING(4, "钉钉"),

    /**
     * 电话
     */
    PHONE(5, "电话")
    ;


    private Integer type;
    private String name;

    NotifySendTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取当前能配置的告警方式
     * @param isIncludePhone
     * @return
     */
    public static List<NotifySendTypeEnum> getNotifySendTypeList(Boolean isIncludePhone){
        List<NotifySendTypeEnum> typeList = new ArrayList<>();
        typeList.add(NotifySendTypeEnum.MAIL);
        typeList.add(NotifySendTypeEnum.SMS);
        typeList.add(NotifySendTypeEnum.DINGDING);
        if (isIncludePhone){
            typeList.add(NotifySendTypeEnum.PHONE);

        }
        return typeList;
    }
}
