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

package com.dtstack.engine.alert.client.ding;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.AbstractAlterClient;
import com.dtstack.engine.alert.enums.DingTypeEnums;
import com.dtstack.engine.alert.exception.AlterException;
import dt.insight.plat.lang.web.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 4:50 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractDingAlterClient extends AbstractAlterClient {


    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    protected R send(AlterContext alterContext) throws AlterException {
        AlterSendDingBean alterSendMailBean = buildAlterSendDingBean(alterContext);
        return sendDing(alterSendMailBean);
    }

    public AlterSendDingBean buildAlterSendDingBean(AlterContext alterContext) throws AlterException {
        String content = alterContext.getContent();
        AlterSendDingBean alterSendDingBean = new AlterSendDingBean();
        if (StringUtils.isBlank(content)) {
            throw new AlterException("Sending Dingding must have content");
        }

        String title = alterContext.getTitle();
        if (StringUtils.isBlank(title)) {
            title = ConstDingAlter.DING_DEFAULT_TITLE;
        }

        String alertGateJson = alterContext.getAlertGateJson();
        JSONObject jsonObject = null;
        if (StringUtils.isBlank(alertGateJson)) {
            jsonObject = new JSONObject();
            jsonObject.put(ConstDingAlter.DING_MSG_TYPE_KEY, DingTypeEnums.MARKDOWN.getMsg());
            alertGateJson = jsonObject.toJSONString();
        } else {
            try {
                jsonObject = JSON.parseObject(alertGateJson);
            } catch (Exception e) {
                throw new AlterException("The configuration format is illegal! Please check the alertGateJson field");
            }
        }

        alterSendDingBean.setContent(content);
        alterSendDingBean.setTitle(title);
        alterSendDingBean.setAlertGateJsonObject(jsonObject);
        alterSendDingBean.setAlertGateJson(alertGateJson);
        alterSendDingBean.setDingMsgType(StringUtils.isBlank(jsonObject.getString(ConstDingAlter.DING_MSG_TYPE_KEY))?DingTypeEnums.MARKDOWN.getMsg():jsonObject.getString(ConstDingAlter.DING_MSG_TYPE_KEY));
        buildBean(alterContext,alterSendDingBean);
        return alterSendDingBean;
    }


    protected static class ConstDingAlter {
        // 钉钉类型
        public static String DING_MSG_TYPE_KEY = "msgtype";

        public static String DING_DEFAULT_TITLE = "您收到一条钉钉消息";

        public static String DING_CLASS = "className";

        public static String PATH_CUT = "&sftp:";
    }

    /**
     * 构建bean
     *
     * @param alterContext
     * @param alterSendDingBean
     */
    protected abstract void buildBean(AlterContext alterContext, AlterSendDingBean alterSendDingBean) throws AlterException;

    /**
     * 发送钉钉
     *
     * @param alterSendDingBean
     * @return
     */
    protected abstract R sendDing(AlterSendDingBean alterSendDingBean);


}
