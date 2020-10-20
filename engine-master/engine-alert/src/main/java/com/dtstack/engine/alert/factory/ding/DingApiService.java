package com.dtstack.engine.alert.factory.ding;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.factory.conf.HttpConfig;
import com.dtstack.engine.alert.factory.conf.SmsConfig;
import com.dtstack.engine.alert.groovy.GroovyFormatService;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.DingAlertParam;
import com.dtstack.engine.common.util.HttpKit;
import com.dtstack.lang.data.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/6/11
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class DingApiService extends AbstractDingService {

    private final Logger log = LoggerFactory.getLogger(DingApiService.class);

    @Autowired
    private GroovyFormatService formatService;

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_DING_API;
    }

    @Override
    public R sendDing(AlertParam alertParam) {
        DingAlertParam dingAlertParam = (DingAlertParam) alertParam;
        Map<String, Object> extMap = dingAlertParam.getExtCfg();
        String message = dingAlertParam.getMessage();
        List<String> dings = dingAlertParam.getDings();

        extMap.put("message", message);
        extMap.put("dings", dings);
        SmsConfig smsConf = JSON.parseObject(alertParam.getAlertGatePO().getAlertGateJson(), SmsConfig.class);
        for (HttpConfig config : smsConf.getConfigs()) {
            try {
                String url = formatService.formatStr(config.getUrl(), extMap);
                Map<String, String> header = formatService.formatMapStr(config.getHeader(), extMap);
                Map<String, Object> param = formatService.formatMap(config.getBody(), extMap);
                String result = HttpKit.send(url, header, param, "GET".equalsIgnoreCase(config.getMethod()), smsConf.isCookieStore());
                log.info("sendDing url={}, header={},param={},response={}", url, header, param, result);
            } catch (IOException e) {
                log.error("HttpKit send error, httpConfig={}", config, e);
                return R.fail(e.getMessage());
            }
        }
        return R.ok();
    }
}
