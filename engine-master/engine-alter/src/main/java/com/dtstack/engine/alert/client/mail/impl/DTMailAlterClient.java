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

package com.dtstack.engine.alert.client.mail.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.mail.AbstractMailAlterClient;
import com.dtstack.engine.alert.client.mail.AlterSendMailBean;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import dt.insight.plat.lang.web.R;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 2:03 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DTMailAlterClient extends AbstractMailAlterClient {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    protected R sendMail(AlterSendMailBean alterSendMailBean) {
        long startTime = System.currentTimeMillis();
        try {
            HtmlEmail htmlEmail = new HtmlEmail();
            htmlEmail.setHostName(alterSendMailBean.getHost());
            htmlEmail.setSmtpPort(alterSendMailBean.getPort());
            htmlEmail.setAuthentication(alterSendMailBean.getUsername(), alterSendMailBean.getPassword());
            htmlEmail.setSSLOnConnect(alterSendMailBean.getSsl());
            htmlEmail.setCharset("UTF-8");

            htmlEmail.setFrom(alterSendMailBean.getFrom());
            htmlEmail.addTo(alterSendMailBean.getEmails().toArray(new String[]{}));
            htmlEmail.setSubject(alterSendMailBean.getSubject());
            htmlEmail.setHtmlMsg(alterSendMailBean.getContent());

            List<File> attachFiles = alterSendMailBean.getAttachFiles();
            if (attachFiles != null) {
                attachFiles.forEach(x -> {
                    try {
                        htmlEmail.attach(x);
                    } catch (EmailException e) {
                        LOGGER.error("Failed to load file！");
                    }
                });
            }

            String result = htmlEmail.send();
            LOGGER.info("[sendMail] end, cost={}, mails={}, subject={}, message={}, result={}", (System.currentTimeMillis() - startTime), alterSendMailBean.getEmails(), alterSendMailBean.getSubject(),
                    alterSendMailBean.getContent(), result);
            return R.ok(null);
        } catch (EmailException e) {
            LOGGER.info("[sendMail] end, cost={}, mails={}, subject={}, message={}", (System.currentTimeMillis() - startTime), alterSendMailBean.getEmails(), alterSendMailBean.getSubject(),
                    alterSendMailBean.getContent(), e);
            return R.fail(ErrorCode.SERVER_EXCEPTION.getCode(), e.getLocalizedMessage());
        }

    }

    @Override
    public void buildBean(AlterSendMailBean alterSendMailBean, JSONObject jsonObject, AlterContext alterContext) throws AlterException {
        // DT 必传项
        String host = jsonObject.getString(ConstMailAlter.MAIL_HOST);
        if (StringUtils.isBlank(host)) {
            throw new AlterException("To send mail, you must have a mailbox server address. Please configure the field in configuration:"+ ConstMailAlter.MAIL_HOST);
        }

        Integer port = jsonObject.getInteger(ConstMailAlter.MAIL_PORT);
        if (port == null) {
            throw new AlterException("Sending mail must have mailbox server port, please configure field in configuration:"+ ConstMailAlter.MAIL_PORT);
        }

        String username = jsonObject.getString(ConstMailAlter.MAIL_USERNAME);
        if (StringUtils.isBlank(username)) {
            throw new AlterException("To send mail, you must have a mailbox sender account. Please configure the fields in the configuration :"+ ConstMailAlter.MAIL_USERNAME);
        }

        String password = jsonObject.getString(ConstMailAlter.MAIL_PASSWORD);
        if (StringUtils.isBlank(password)) {
            throw new AlterException("There must be a mailbox sender to send mail. Please configure the field in configuration:"+ ConstMailAlter.MAIL_PASSWORD);
        }

        String from = jsonObject.getString(ConstMailAlter.MAIL_FROM);
        if (StringUtils.isBlank(from)) {
            from = username;
        }

        Boolean ssl = jsonObject.getBoolean(ConstMailAlter.MAIL_SSL);
        if (ssl == null) {
            ssl= Boolean.FALSE;
        }

        alterSendMailBean.setHost(host);
        alterSendMailBean.setPort(port);
        alterSendMailBean.setUsername(username);
        alterSendMailBean.setPassword(password);
        alterSendMailBean.setFrom(from);
        alterSendMailBean.setSsl(ssl);
    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_MAIL_DT.code();
    }
}
