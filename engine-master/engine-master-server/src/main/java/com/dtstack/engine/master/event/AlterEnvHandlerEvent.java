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

package com.dtstack.engine.master.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AdapterEventMonitor;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/4/1 2:36 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class AlterEnvHandlerEvent extends AdapterEventMonitor {

    @Autowired
    private EnvironmentContext environmentContext;

    private AlterEnvironment alterEnvironment;

    @Override
    public Boolean startEvent(AlterContext alterContext) {
        Map<String, Object> evn = alterContext.getEvn();

        if (MapUtils.isEmpty(evn)) {
            evn = Maps.newHashMap();
        }

        evn.putAll(alterEnvironment.env);

        String alertGateJson = alterContext.getAlertGateJson();
        if (StringUtils.isNotBlank(alertGateJson)) {
            JSONObject jsonObject = JSON.parseObject(alertGateJson);
            evn.putAll(jsonObject);
        }

        alterContext.setEvn(evn);
        return super.startEvent(alterContext);
    }

    @PostConstruct
    public void init(){
        this.alterEnvironment = new AlterEnvironment();
    }

    class AlterEnvironment{
        private final static String SEND_IP = "ALTER_IP";
        private final static String SEND_HOST_NAME = "ALTER_HOST_NAME";
        private final static String SEND_TIME = "ALTER_TIME";

        private final Map<String,Object> env = Maps.newHashMap();

        AlterEnvironment(){
            env.put(SEND_IP,environmentContext.getHttpAddress());
            env.put(SEND_HOST_NAME, environmentContext.getLocalAddress());
            env.put(SEND_TIME, new Date());
        }

        public Map<String, Object> getEnv() {
            return env;
        }

    }
}
