package com.dtstack.engine.alert.factory.phone;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.factory.AlertService;
import com.dtstack.engine.alert.factory.conf.TencentCloudConfig;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.SmsAlertParam;
import com.dtstack.engine.common.util.HttpKit;
import com.dtstack.lang.data.R;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * 腾讯云电话服务
 */
@Service
public class TencentCloudPhoneService implements AlertService {

    private final Logger log = LoggerFactory.getLogger(TencentCloudPhoneService.class);

    @Override
    public R send(AlertParam param) {
        SmsAlertParam smsAlertParam = (SmsAlertParam) param;
        String confJson = smsAlertParam.getAlertGatePO().getAlertGateJson();
        TencentCloudConfig tencentCloudConfig = JSON.parseObject(confJson, TencentCloudConfig.class);
        String message = smsAlertParam.getMessage();
        List<String> phones = smsAlertParam.getPhones();

        if (CollectionUtils.isEmpty(phones)) {
            log.info("没有发送人");
            return R.ok();
        }
        phones.parallelStream()
                .forEach(phone -> {
                    String url = null;
                    try {
                        url = String.format("%s?appId=%s&appKey=%s&tplId=%s&phone=%s&param1=%s&param2=%s", tencentCloudConfig.getUrl(), tencentCloudConfig.getAppId(),
                                tencentCloudConfig.getAppKey(), tencentCloudConfig.getTemplate(), phone, URLEncoder.encode(message, "UTF-8"), "无用消息");
                        String result = HttpKit.send(url, new HashMap<>(), new HashMap<>(), true, false);
                        log.info("sendSms url={}, response={}", tencentCloudConfig.getUrl(), result);
                    } catch (Exception e) {
                        log.error("HttpKit send error, httpConfig={}", tencentCloudConfig, e);
                    }
                });
        return R.ok();
    }

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_PHONE_TC;
    }
}
