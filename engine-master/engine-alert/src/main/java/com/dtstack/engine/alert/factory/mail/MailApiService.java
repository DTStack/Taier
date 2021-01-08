package com.dtstack.engine.alert.factory.mail;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.factory.AlertService;
import com.dtstack.engine.alert.factory.conf.HttpConfig;
import com.dtstack.engine.alert.factory.conf.MailConfig;
import com.dtstack.engine.alert.groovy.GroovyFormatService;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.MailAlertParam;
import com.dtstack.engine.common.util.HttpKit;
import com.dtstack.lang.data.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/6/12
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class MailApiService implements AlertService {

    private final Logger log = LoggerFactory.getLogger(MailApiService.class);

    @Autowired
    private GroovyFormatService formatService;

    @Override
    public R send(AlertParam alertParam) {
        MailAlertParam mailAlertParam = (MailAlertParam) alertParam;

        List<String> recipients = mailAlertParam.getEmails();
        String subject = mailAlertParam.getSubject();
        String message = mailAlertParam.getMessage();
        List<File> attachFiles = mailAlertParam.getAttachFiles();
        Map<String, Object> extMap = new HashMap<>();
        extMap.put("message", message);
        extMap.put("subject", subject);
        extMap.put("recipients", recipients);
        MailConfig mailConf = JSON.parseObject(alertParam.getAlertGatePO().getAlertGateJson(), MailConfig.class);
        for(HttpConfig config : mailConf.getConfigs()){
            String url = formatService.formatStr(config.getUrl(), extMap);
            Map<String, String> header = formatService.formatMapStr(config.getHeader(),extMap);
            Map<String, Object> param = formatService.formatMap(config.getBody(), extMap);
            try {
                String result = HttpKit.send(url, header, param, "GET".equalsIgnoreCase(config.getMethod()), mailConf.isCookieStore());
                log.info("sendMail url={}, header={},param={},response={}", url, header,param,result);
            } catch (IOException e) {
                log.error("HttpKit send error, httpConfig={}", config, e);
                return R.fail(e.getMessage());
            }
        }
        return R.ok();    }

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_MAIL_API;
    }
}
