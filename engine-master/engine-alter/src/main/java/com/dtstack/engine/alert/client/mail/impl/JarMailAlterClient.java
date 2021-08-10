package com.dtstack.engine.alert.client.mail.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.channel.IMailChannel;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.mail.AbstractMailAlterClient;
import com.dtstack.engine.alert.client.mail.AlterSendMailBean;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.alert.load.JarCache;
import com.dtstack.engine.common.exception.ErrorCode;
import dt.insight.plat.lang.web.R;
import org.apache.commons.lang3.StringUtils;
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
        long startTime = System.currentTimeMillis();
        try {
            IMailChannel iMailChannel = (IMailChannel) JarCache.getInstance()
                    .getChannelInstance(alterSendMailBean.getJarPath(), alterSendMailBean.getClassName());

            R r = iMailChannel.sendMail(alterSendMailBean.getEmails(),
                    alterSendMailBean.getSubject(),
                    alterSendMailBean.getContent(),
                    alterSendMailBean.getAttachFiles(), alterSendMailBean.getEnv());
            logger.info("[sendMail] end, cost={}, mails={}, subject={}, message={}, result={}", (System.currentTimeMillis() - startTime),
                    alterSendMailBean.getEmails(), alterSendMailBean.getSubject(), alterSendMailBean.getContent(), r);
            return r;
        } catch (Exception e) {
            logger.info("[sendMail] end, cost={}, mails={}, subject={}, message={}", (System.currentTimeMillis() - startTime), alterSendMailBean.getEmails(), alterSendMailBean.getSubject(),
                    alterSendMailBean.getContent(), e);
            return R.fail(ErrorCode.SERVER_EXCEPTION.getCode(), "jarPath:" + alterSendMailBean.getJarPath() + " loading failed, please check the configuration! reason:" + e.getLocalizedMessage());
        }
    }

    @Override
    public void buildBean(AlterSendMailBean alterSendMailBean, JSONObject jsonObject, AlterContext alterContext) throws AlterException {
        String className = jsonObject.getString(ConstMailAlter.MAIL_CLASS);
        if (StringUtils.isBlank(className)) {
            throw new AlterException("The complete class name of the jar package must be configured for sending mail custom jar. Please configure the field in the configuration:"+ ConstMailAlter.MAIL_CLASS);
        }

        String jarPath = alterContext.getJarPath();

        if (jarPath.contains(ConstMailAlter.PATH_CUT)) {
            jarPath = jarPath.substring(0, jarPath.indexOf(ConstMailAlter.PATH_CUT));
        }
        if (StringUtils.isBlank(jarPath)) {
            throw new AlterException("The custom jar must be passed into the jar path");
        }
        alterSendMailBean.setJarPath(jarPath);
        alterSendMailBean.setClassName(className);
        alterSendMailBean.setEnv(alterContext.getEvn());
    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_MAIL_JAR.code();
    }
}
