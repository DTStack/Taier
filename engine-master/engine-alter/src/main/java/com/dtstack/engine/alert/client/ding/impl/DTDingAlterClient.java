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

package com.dtstack.engine.alert.client.ding.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.ding.AbstractDingAlterClient;
import com.dtstack.engine.alert.client.ding.AlterSendDingBean;
import com.dtstack.engine.alert.client.ding.bean.*;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.enums.DingTypeEnums;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.alert.http.HttpKit;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import dt.insight.plat.lang.web.R;
import com.dtstack.engine.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 5:14 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DTDingAlterClient extends AbstractDingAlterClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void buildBean(AlterContext alterContext, AlterSendDingBean alterSendDingBean) throws AlterException {
        String ding = alterContext.getDing();

        if (StringUtils.isBlank(ding)) {
            throw new AlterException("Dingding channel must be configured with url");
        }

        alterSendDingBean.setDing(ding);
    }

    @Override
    protected R sendDing(AlterSendDingBean alterSendDingBean) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> dingBean;
        String dingMsgType = alterSendDingBean.getDingMsgType();
        if (DingTypeEnums.TEXT.getMsg().equalsIgnoreCase(dingMsgType)) {
            dingBean = sendTest(alterSendDingBean);
        } else if (DingTypeEnums.MARKDOWN.getMsg().equalsIgnoreCase(dingMsgType)) {
            dingBean = sendDingWithMarkDown(alterSendDingBean);
        } else {
            throw new BizException(String.format("Unsupported DingTalk message types，msgtype=%s", dingMsgType));
        }

        try {
            Map<String,String> header = new HashMap<>();
            header.put("Accept", "application/json");
            header.put("Content-Type", "application/json;charset=utf-8");
            String result = HttpKit.send(alterSendDingBean.getDing(), header, dingBean, false, false);
            logger.info("[sendDing] hookUrl={},result={}", alterSendDingBean.getDing(), result);
            DingResultBean dingResultBean = JSONObject.parseObject(result, DingResultBean.class);
            if (dingResultBean != null && !"0".equals(dingResultBean.getErrcode())) {
                logger.info("[sendDing] usage of time = {}, the message = {} result:{}", (System.currentTimeMillis() - startTime), alterSendDingBean.getContent(),dingResultBean.getErrmsg());
                return R.fail(ErrorCode.SERVER_EXCEPTION.getCode(), dingResultBean.getErrmsg());
            } else {
                logger.info("[sendDing] usage of time = {}, the message = {}", (System.currentTimeMillis() - startTime), alterSendDingBean.getContent());
                return R.ok(null);
            }
        } catch (Exception e) {
            logger.info("[sendDing] error, time cost = {}, the message = {}", (System.currentTimeMillis() - startTime), alterSendDingBean.getContent(), e);
            return R.fail(ErrorCode.SERVER_EXCEPTION.getCode(), e.getMessage());
        }

    }

    private Map<String, Object> sendDingWithMarkDown(AlterSendDingBean alterSendDingBean) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> at = new HashMap<>();
        data.put("msgtype", "markdown");
        Map<String, String> content = new HashMap<>();
        content.put("title", alterSendDingBean.getTitle());
        content.put("text", alterSendDingBean.getContent());
        at.put("atMobiles", alterSendDingBean.getAtMobiles());
        at.put("isAtAll", alterSendDingBean.getAtAll());
        data.put("markdown", content);
        data.put("at", at);

        return data;
    }

    private Map<String, Object> sendTest(AlterSendDingBean alterSendDingBean) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> at = new HashMap<>();
        data.put("msgtype", "text");
        Map<String, String> content = new HashMap<>();
        content.put("content", alterSendDingBean.getContent());
        at.put("atMobiles", alterSendDingBean.getAtMobiles());
        at.put("isAtAll", alterSendDingBean.getAtAll());
        data.put("text", content);
        data.put("at", at);
        return data;
    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_DING_DT.code();
    }
}
