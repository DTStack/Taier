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

package com.dtstack.engine.alert.client.sms;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.AbstractAlterClient;
import com.dtstack.engine.alert.client.mail.AlterSendMailBean;
import com.dtstack.engine.alert.exception.AlterException;
import dt.insight.plat.lang.web.R;
import org.apache.commons.lang3.StringUtils;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 11:44 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractSmsAlterClient extends AbstractAlterClient {


    @Override
    protected R send(AlterContext alterContext) throws AlterException {
        AlterSendSmsBean alterSendMailBean = buildSmsBean(alterContext);
        return sendSms(alterSendMailBean);
    }

    private AlterSendSmsBean buildSmsBean(AlterContext alterContext) throws AlterException {
        String content = alterContext.getContent();
        if (StringUtils.isBlank(content)) {
            throw new AlterException("Sending text message must have sending content");
        }

        String phone = alterContext.getPhone();
        if (StringUtils.isBlank(phone)) {
            throw new AlterException("You must have a mobile phone number to send SMS");
        }

        AlterSendSmsBean alterSendSmsBean = new AlterSendSmsBean();
        alterSendSmsBean.setTitle(alterContext.getTitle());
        alterSendSmsBean.setMessage(content);
        alterSendSmsBean.setPhone(phone);
        alterSendSmsBean.setAlertGateJson(alterContext.getAlertGateJson());
        buildBean(alterSendSmsBean,alterContext);

        return alterSendSmsBean;
    }

    protected static class ConstSmsAlter {
        public static String SMS_CLASS = "className";

        public static String PATH_CUT = "&sftp:";
    }

    /**
     * 发送短信
     *
     * @param alterSendSmsBean
     * @return
     */
    protected abstract R sendSms(AlterSendSmsBean alterSendSmsBean);

    /**
     * 构建bean
     *
     * @param alterSendSmsBean
     * @throws AlterException
     */
    public abstract void buildBean(AlterSendSmsBean alterSendSmsBean, AlterContext alterContext) throws AlterException;

}
