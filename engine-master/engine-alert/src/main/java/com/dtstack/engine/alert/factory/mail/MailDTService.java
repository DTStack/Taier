package com.dtstack.engine.alert.factory.mail;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.constant.AGConstant;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.factory.AlertService;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.MailAlertParam;
import com.dtstack.lang.data.R;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Objects;


/**
 * Date: 2020/6/12
 * Company: www.dtstack.com
 * todo 根据配置缓存htmlEmail对象
 * @author xiaochen
 */

@Component
public class MailDTService implements AlertService {

    private final Logger log = LoggerFactory.getLogger(MailDTService.class);


    @Override
    public R send(AlertParam param) {
        JSONObject config = JSON.parseObject(param.getAlertGatePO().getAlertGateJson());
        String host = config.getString(AGConstant.MAIL_HOST);
        String port = config.getString(AGConstant.MAIL_PORT);
        boolean ssl = false;
        if (config.containsKey(AGConstant.MAIL_SSL)) {
            ssl = config.getBoolean(AGConstant.MAIL_SSL);
        }
        String username = config.getString(AGConstant.MAIL_USERNAME);
        String password = config.getString(AGConstant.MAIL_PASSWORD);
        String from = config.getString(AGConstant.MAIL_FROM);

        HtmlEmail htmlEmail = new HtmlEmail();
        htmlEmail.setHostName(host);
        htmlEmail.setSmtpPort(Integer.parseInt(port));
        htmlEmail.setAuthentication(username, password);
        htmlEmail.setSSLOnConnect(ssl);
        htmlEmail.setCharset("UTF-8");

        MailAlertParam mailAlertParam = (MailAlertParam) param;
        List<String> recipients = mailAlertParam.getEmails();
        String subject = mailAlertParam.getSubject();
        String message = mailAlertParam.getMessage();
        List<File> attachFiles = mailAlertParam.getAttachFiles();

        long startTime = System.currentTimeMillis();
        try {
            Objects.requireNonNull(from);
            Objects.requireNonNull(recipients);
            htmlEmail.setFrom(from);
            htmlEmail.addTo(recipients.toArray(new String[] {}));
            htmlEmail.setSubject(subject);
            htmlEmail.setHtmlMsg(message);
            if (attachFiles != null) {
                attachFiles.forEach(x -> {
                    try {
                        htmlEmail.attach(x);
                    } catch (EmailException e) {
                        log.error("[DTMailService.attach] error", e);
                    }
                });
            }
            String result = htmlEmail.send();
            log.info("[sendMail] end, cost={}, mails={}, subject={}, message={}, result={}", (System.currentTimeMillis() - startTime), recipients, subject,
                    message, result);
            return R.ok();
        } catch (Exception e) {
            log.error("[sendMail] error, cost={}, mails={}, subject={}, message={}", (System.currentTimeMillis() - startTime), recipients, subject,
                    message, e);
            return R.fail(e.getMessage());
        }
    }

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_MAIL_DT;
    }
}
