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
 * Date: 2020/5/22
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public enum AlertGateCode {

    AG_GATE_SMS_YP("sms_yp"),
    AG_GATE_SMS_DY("sms_dy"),
    AG_GATE_SMS_API("sms_API"),

    AG_GATE_MAIL_DT("mail_dt"),

    AG_GATE_MAIL_API("mail_api"),

    AG_GATE_DING_DT("ding_dt"),
    AG_GATE_DING_API("ding_api"),

    AG_GATE_SMS_JAR("sms_jar"),
    AG_GATE_DING_JAR("ding_jar"),
    AG_GATE_MAIL_JAR("mail_jar"),

    AG_GATE_PHONE_TC("phone_tc"),

    AG_GATE_CUSTOM_JAR("custom_jar"),
    ;

    private String code;

    AlertGateCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static AlertGateCode parse(String code) {
        AlertGateCode[] values = values();
        for (AlertGateCode value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("unsupported code " + code);
    }

}
