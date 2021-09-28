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

package com.dtstack.engine.alert;

import com.dtstack.engine.alert.client.AlterClient;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.alert.factory.AlterClientFactory;
import dt.insight.plat.lang.web.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: dazhi
 * @Date: 2021/1/11 4:02 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DefaultAlterSender implements AlterSender {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Map<String, AlterClient> alterSender;
    private final AlterConfig alterConfig;

    public DefaultAlterSender(AlterConfig alterConfig){
        if (alterConfig == null) {
            // 使用默认的配置
            alterConfig = new AlterConfig();
        }
        this.alterConfig = alterConfig;
        alterSender = new ConcurrentHashMap<>(AlertGateTypeEnum.values().length);
    }

    @Override
    public R sendSyncAlter(AlterContext alterContext, List<EventMonitor> eventMonitors) throws Exception {
        LOGGER.info("Start sending alert sendSyncAlter: id {}",alterContext.getMark());
        AlterClient alterClient = getAlterClient(alterContext);
        return alterClient.sendSyncAlter(alterContext, eventMonitors);
    }

    @Override
    public void sendAsyncAAlter(AlterContext alterContext, List<EventMonitor> eventMonitors) throws Exception {
        LOGGER.info("Start sending alert sendAsyncAAlter: id {}",alterContext.getMark());
        AlterClient alterClient = getAlterClient(alterContext);
        alterClient.sendAsyncAAlter(alterContext, eventMonitors);
    }

    private AlterClient getAlterClient(AlterContext alterContext) throws Exception {
        LOGGER.info("get client: id {}",alterContext.getMark());
        AlertGateCode alertGateCode = alterContext.getAlertGateCode();

        if (alertGateCode == null) {
            throw new AlterException("The context object must have an alarm type");
        }

        AlterClient alterClient = alterSender.computeIfAbsent(alertGateCode.name(), a -> {
            try {
                return getClient(alertGateCode, alterConfig);
            } catch (Exception e) {
                return null;
            }
        });
        if (alterClient == null) {
            throw new AlterException(alertGateCode.name() + " type channel does not exist, please contact technical staff");
        }

        LOGGER.info("Get client success : id {} alterClient: {}",alterContext.getMark(),alertGateCode.name());
        return alterClient;
    }

    private AlterClient getClient(AlertGateCode alertGateCode, AlterConfig alterConfig) throws Exception {
        return AlterClientFactory.getInstance(alertGateCode,alterConfig);
    }
}
