package com.dtstack.engine.alert.client.mail.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.channel.IMailChannel;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.mail.AbstractMailAlterClient;
import com.dtstack.engine.alert.client.mail.AlterSendMailBean;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.alert.load.JarCache;
import com.dtstack.lang.data.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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
        long startTime = System.currentTimeMillis();
        try {
            IMailChannel iMailChannel = (IMailChannel) JarCache.getInstance()
                    .getChannelInstance(alterSendMailBean.getJarPath(), alterSendMailBean.getClassName());

            R r = iMailChannel.sendMail(alterSendMailBean.getEmails(),
                    alterSendMailBean.getSubject(),
                    alterSendMailBean.getContent(),
                    alterSendMailBean.getAttachFiles(), JSONObject.parseObject(alterSendMailBean.getAlertGateJson()));
            logger.info("[sendMail] end, cost={}, mails={}, subject={}, message={}, result={}", (System.currentTimeMillis() - startTime),
                    alterSendMailBean.getEmails(), alterSendMailBean.getSubject(), alterSendMailBean.getContent(), r);
            return r;
        } catch (Exception e) {
            logger.info("[sendMail] end, cost={}, mails={}, subject={}, message={}", (System.currentTimeMillis() - startTime), alterSendMailBean.getEmails(), alterSendMailBean.getSubject(),
                    alterSendMailBean.getContent(), e);
            return R.fail("jarPath:" + alterSendMailBean.getJarPath() + "加载失败，请检查配置！原因:" + e.getLocalizedMessage());
        }
    }

    @Override
    public void buildBean(AlterSendMailBean alterSendMailBean, JSONObject jsonObject, AlterContext alterContext) throws AlterException {
        String className = jsonObject.getString(ConstMailAlter.MAIL_CLASS);
        if (StringUtils.isBlank(className)) {
            throw new AlterException("发送邮件自定义jar必须配置jar包的完整类名，请在配置中配置字段:"+ ConstMailAlter.MAIL_CLASS);
        }

        String jarPath = alterContext.getJarPath();
        if (StringUtils.isBlank(jarPath)) {
            throw new AlterException("自定义jar必须传入jar路径");
        }
    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_MAIL_JAR.code();
    }
}
