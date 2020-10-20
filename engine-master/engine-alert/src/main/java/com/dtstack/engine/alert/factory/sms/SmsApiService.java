package com.dtstack.engine.alert.factory.sms;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.factory.AlertService;
import com.dtstack.engine.alert.factory.conf.HttpConfig;
import com.dtstack.engine.alert.factory.conf.SmsConfig;
import com.dtstack.engine.alert.groovy.GroovyFormatService;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.SmsAlertParam;
import com.dtstack.engine.common.util.HttpKit;
import com.dtstack.lang.data.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/6/11
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class SmsApiService implements AlertService {

    private final Logger log = LoggerFactory.getLogger(SmsApiService.class);

    @Autowired
    private GroovyFormatService formatService;

    @Override
    public R send(AlertParam alertParam) {
        SmsAlertParam smsAlertParam = (SmsAlertParam) alertParam;
        SmsConfig smsConf = JSON.parseObject(alertParam.getAlertGatePO().getAlertGateJson(), SmsConfig.class);
        List<String> phones = smsAlertParam.getPhones();
        Map<String, String> dynamicParams = smsAlertParam.getDynamicParams();
        Map<String, Object> extMap = new HashMap<>(dynamicParams);

        String message = alertParam.getMessage();
        extMap.put("message", message);

        for (String phone : phones) {
            extMap.put("phone", phone);
            for (HttpConfig config : smsConf.getConfigs()) {
                String url = formatService.formatStr(config.getUrl(), extMap);
                Map<String, String> header = formatService.formatMapStr(config.getHeader(), extMap);
                Map<String, Object> param = formatService.formatMap(config.getBody(), extMap);
                try {
                    String result = HttpKit.send(url, header, param, "GET".equalsIgnoreCase(config.getMethod()), smsConf.isCookieStore());
                    log.info("sendSms url={}, header={},param={},response={}", url, header, param, result);
                } catch (IOException e) {
                    log.error("HttpKit send error, httpConfig={}", config, e);
                    return R.fail(e.getMessage());
                }
            }
        }
        return R.ok();
    }

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_SMS_API;
    }
}
