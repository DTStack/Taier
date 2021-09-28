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

package com.dtstack.engine.alert.factory;

import com.dtstack.engine.alert.AlterConfig;
import com.dtstack.engine.alert.client.AlterClient;
import com.dtstack.engine.alert.client.customize.CustomizeAlterClient;
import com.dtstack.engine.alert.client.ding.impl.DTDingAlterClient;
import com.dtstack.engine.alert.client.ding.impl.JarDingAlterClient;
import com.dtstack.engine.alert.client.mail.impl.DTMailAlterClient;
import com.dtstack.engine.alert.client.mail.impl.JarMailAlterClient;
import com.dtstack.engine.alert.client.phone.PhoneAlterClient;
import com.dtstack.engine.alert.client.sms.impl.JarSmsAlterClient;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 3:15 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterClientFactory {

    private static final Map<AlertGateCode, Class<? extends AlterClient>> originalMap = Maps.newConcurrentMap();

    static {
        // 自定义jar
        originalMap.put(AlertGateCode.AG_GATE_CUSTOM_JAR, CustomizeAlterClient.class);

        // 钉钉
        originalMap.put(AlertGateCode.AG_GATE_DING_DT, DTDingAlterClient.class);

        // 钉钉jar
        originalMap.put(AlertGateCode.AG_GATE_DING_JAR, JarDingAlterClient.class);

        // 邮件dt
        originalMap.put(AlertGateCode.AG_GATE_MAIL_DT, DTMailAlterClient.class);

        // 邮件jar
        originalMap.put(AlertGateCode.AG_GATE_MAIL_JAR, JarMailAlterClient.class);

        // 电话
        originalMap.put(AlertGateCode.AG_GATE_PHONE_TC, PhoneAlterClient.class);

        // 短信
        originalMap.put(AlertGateCode.AG_GATE_SMS_JAR, JarSmsAlterClient.class);
    }

    public static AlterClient getInstance(AlertGateCode alertGateCode, AlterConfig alterConfig) throws IllegalAccessException, InstantiationException {
        Class<? extends AlterClient> alterClientClass = originalMap.get(alertGateCode);

        if (alterClientClass != null) {
            AlterClient alterClient = alterClientClass.newInstance();
            alterClient.setConfig(alterConfig);
            return alterClient;
        }

        return null;
    }


}
