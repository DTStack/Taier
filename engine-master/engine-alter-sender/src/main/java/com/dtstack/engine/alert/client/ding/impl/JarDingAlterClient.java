package com.dtstack.engine.alert.client.ding.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.channel.IDingChannel;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.ding.AbstractDingAlterClient;
import com.dtstack.engine.alert.client.ding.AlterSendDingBean;
import com.dtstack.engine.alert.client.sms.AbstractSmsAlterClient;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.enums.DingTypeEnums;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.alert.load.JarCache;
import com.dtstack.lang.data.R;
import com.dtstack.lang.exception.BizException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 1:49 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JarDingAlterClient extends AbstractDingAlterClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    protected void buildBean(AlterContext alterContext, AlterSendDingBean alterSendDingBean) throws AlterException {
        JSONObject jsonObject = JSONObject.parseObject(alterContext.getAlertGateJson());
        String className = jsonObject.getString(ConstDingAlter.DING_CLASS);
        if (StringUtils.isBlank(className)) {
            throw new AlterException("Send mail custom jar must configure the full class name of the jar package, please configure the field in the configuration:" + ConstDingAlter.DING_CLASS);
        }

        String jarPath = alterContext.getJarPath();

        if (jarPath.contains(ConstDingAlter.PATH_CUT)) {
            jarPath = jarPath.substring(0, jarPath.indexOf(ConstDingAlter.PATH_CUT));
        }

        if (StringUtils.isBlank(jarPath)) {
            throw new AlterException("Custom jar must be passed in the jar path");
        }

        alterSendDingBean.setJarPath(jarPath);
        alterSendDingBean.setClassName(className);
    }

    @Override
    protected R sendDing(AlterSendDingBean alterSendDingBean) {
        long startTime = System.currentTimeMillis();
        R r;
        try {
            JSONObject jsonObject = alterSendDingBean.getAlertGateJsonObject();
            String dingMsgType = alterSendDingBean.getDingMsgType();
            IDingChannel iDingChannel = (IDingChannel) JarCache.getInstance()
                    .getChannelInstance(alterSendDingBean.getJarPath(), alterSendDingBean.getClassName());

            String ding = alterSendDingBean.getDing();

            if (DingTypeEnums.TEXT.getMsg().equalsIgnoreCase(dingMsgType)) {
                r = iDingChannel.sendDing(Lists.newArrayList(ding),alterSendDingBean.getContent(),null,jsonObject);
            } else if (DingTypeEnums.MARKDOWN.getMsg().equalsIgnoreCase(dingMsgType)) {
                r = iDingChannel.sendDingWithMarkDown(Lists.newArrayList(ding),alterSendDingBean.getTitle(),alterSendDingBean.getContent(),null,jsonObject);
            } else {
                throw new BizException(String.format("Unsupported DingTalk message types，msgtype=%s", dingMsgType));
            }
            logger.info("[sendMail] end, cost={}, mails={}, title={}, message={}, result={}", (System.currentTimeMillis() - startTime),
                    alterSendDingBean.getDing(), alterSendDingBean.getTitle(), alterSendDingBean.getContent(), r);

            return r;
        } catch (Exception e) {
            logger.info("[sendMail] end, cost={}, mails={}, title={}, message={}", (System.currentTimeMillis() - startTime), alterSendDingBean.getDing(), alterSendDingBean.getTitle(),
                    alterSendDingBean.getContent(), e);
            return R.fail("jarPath:" + alterSendDingBean.getJarPath() + " loading failed, please check the configuration! the reason:" + e.getLocalizedMessage());
        }
    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_DING_JAR.code();
    }
}
