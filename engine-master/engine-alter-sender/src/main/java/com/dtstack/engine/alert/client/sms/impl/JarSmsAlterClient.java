package com.dtstack.engine.alert.client.sms.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.channel.ISmsChannel;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.sms.AbstractSmsAlterClient;
import com.dtstack.engine.alert.client.sms.AlterSendSmsBean;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.alert.load.JarCache;
import com.dtstack.lang.data.R;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 2:34 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JarSmsAlterClient extends AbstractSmsAlterClient {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    protected R sendSms(AlterSendSmsBean alterSendSmsBean) {
        long startTime = System.currentTimeMillis();
        String jarPath = alterSendSmsBean.getJarPath();
        String className = alterSendSmsBean.getClassName();
        List<String> phones = Lists.newArrayList(alterSendSmsBean.getPhone());
        String message = alterSendSmsBean.getMessage();
        try {
            ISmsChannel sender = (ISmsChannel) JarCache.getInstance().getChannelInstance(jarPath, className);
            R r = sender.sendSms(message, phones, alterSendSmsBean.getEnv());
            LOGGER.info("[sendSms] end, cost={}, phones={}, message={}, result={}",(System.currentTimeMillis() - startTime), phones, message, r);
            return r;
        } catch (Exception e) {
            LOGGER.info("[sendSms] error, cost={}, phones={}, message={}",(System.currentTimeMillis() - startTime), phones, message, e);
            return R.fail("jarPath:"+jarPath +"加载失败，请检查配置！");
        }
    }

    @Override
    public void buildBean(AlterSendSmsBean alterSendSmsBean, AlterContext alterContext) throws AlterException {
        JSONObject jsonObject = JSONObject.parseObject(alterContext.getAlertGateJson());
        String className = jsonObject.getString(ConstSmsAlter.SMS_CLASS);
        if (StringUtils.isBlank(className)) {
            throw new AlterException("The complete class name of the jar package must be configured for sending mail custom jar. Please configure the field in the configuration:" + ConstSmsAlter.SMS_CLASS);
        }

        String jarPath = alterContext.getJarPath();
        if (jarPath.contains(ConstSmsAlter.PATH_CUT)) {
            jarPath = jarPath.substring(0, jarPath.indexOf(ConstSmsAlter.PATH_CUT));
        }

        if (StringUtils.isBlank(jarPath)) {
            throw new AlterException("The custom jar must be passed into the jar path");
        }
        alterSendSmsBean.setClassName(className);
        alterSendSmsBean.setJarPath(jarPath);
        alterSendSmsBean.setEnv(alterContext.getEvn());
    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_SMS_JAR.code();
    }

}
