package com.dtstack.engine.alert.factory.mail;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.channel.IMailChannel;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.factory.AlertService;
import com.dtstack.engine.alert.factory.ChannelCache;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.MailAlertParam;
import com.dtstack.engine.common.util.JsonUtils;
import com.dtstack.lang.data.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/6/12
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class MailJarService implements AlertService {

    private final Logger log = LoggerFactory.getLogger(MailJarService.class);

    @Override
    public R send(AlertParam param) {
        MailAlertParam mailAlertParam = (MailAlertParam) param;
        List<String> recipients = mailAlertParam.getEmails();
        String subject = mailAlertParam.getSubject();
        String message = mailAlertParam.getMessage();
        List<File> attachFiles = mailAlertParam.getAttachFiles();

        JSONObject config = JSON.parseObject(param.getAlertGatePO().getAlertGateJson());
        String jarPath = config.getString("jarPath");
        if (StringUtils.isBlank(jarPath)) {
            jarPath = mailAlertParam.getAlertGatePO().getFilePath();
        }

        String className = config.getString("className");
        long startTime = System.currentTimeMillis();

        // 为什么不直接传JSONObject config
        // 用了插件模式，建议只传
        Map<String, Object> confMap = JsonUtils.parseMap(param.getAlertGatePO().getAlertGateJson());
        try {
            IMailChannel sender = (IMailChannel) ChannelCache.getChannelInstance(jarPath, className);
            R r = sender.sendMail(recipients, subject, message, attachFiles, confMap);
            log.info("[sendMail] end, cost={}, mails={}, subject={}, message={}, result={}", (System.currentTimeMillis() - startTime),
                    recipients, subject, message, r);
            return r;
        } catch (Exception e) {
            log.info("[sendMail] error, cost={}, mails={}, subject={}, message={}", (System.currentTimeMillis() - startTime),
                    recipients, subject, message, e);
            return R.fail(e.getMessage());
        }
    }

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_MAIL_JAR;
    }
}
