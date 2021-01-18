package com.dtstack.engine.alert.client.mail.impl;

import com.dtstack.channel.IMailChannel;
import com.dtstack.engine.alert.client.mail.AbstractMailAlterClient;
import com.dtstack.engine.alert.client.mail.AlterSendMailBean;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.lang.data.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 2:32 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JarMailAlterClient extends AbstractMailAlterClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected R sendMail(AlterSendMailBean alterSendMailBean) {
        try {
//            IMailChannel sender = (IMailChannel) channelCache.getChannelInstance(jarPath, className);


        } catch (Exception e) {

        }

        return null;
    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_MAIL_JAR.code();
    }
}
