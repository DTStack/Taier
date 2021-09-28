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

package com.dtstack.engine.alert.client.phone;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.AbstractAlterClient;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.http.HttpKit;
import dt.insight.plat.lang.web.R;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 2:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class PhoneAlterClient extends AbstractAlterClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected R send(AlterContext alterContext) throws Exception {
        String confJson = alterContext.getAlertGateJson();
        TencentCloudBean tencentCloudConfig = JSON.parseObject(confJson, TencentCloudBean.class);
        String message = alterContext.getContent();
        List<String> phones = Lists.newArrayList(alterContext.getPhone());

        phones.parallelStream()
                .forEach(phone -> {
                    String url;
                    try {
                        url = String.format("%s?appId=%s&appKey=%s&tplId=%s&phone=%s&param1=%s&param2=%s", tencentCloudConfig.getUrl(), tencentCloudConfig.getAppId(),
                                tencentCloudConfig.getAppKey(), tencentCloudConfig.getTemplate(), phone, URLEncoder.encode(message, "UTF-8"), "无用消息");
                        String result = HttpKit.send(url, new HashMap<>(), new HashMap<>(), true, false);
                        logger.info("sendSms url={}, response={}", tencentCloudConfig.getUrl(), result);
                    } catch (Exception e) {
                        logger.error("HttpKit send error, httpConfig={}", tencentCloudConfig, e);
                    }
                });
        return R.ok(null);
    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_PHONE_TC.code();
    }
}
