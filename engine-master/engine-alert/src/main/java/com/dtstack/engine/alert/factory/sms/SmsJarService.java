package com.dtstack.engine.alert.factory.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.channel.ISmsChannel;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.factory.AlertService;
import com.dtstack.engine.alert.factory.ChannelCache;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.SmsAlertParam;
import com.dtstack.engine.common.util.JsonUtils;
import com.dtstack.lang.data.R;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/6/11
 * Company: www.dtstack.com
 * @author xiaochen
 */
@Component
public class SmsJarService implements AlertService {

    private final Logger log = LoggerFactory.getLogger(SmsJarService.class);

    @Override
    public R send(AlertParam param) {
        JSONObject config = JSON.parseObject(param.getAlertGatePO().getAlertGateJson());
        SmsAlertParam smsAlertParam = (SmsAlertParam) param;
        String message = smsAlertParam.getMessage();
        List<String> phones = smsAlertParam.getPhones();

        Map<String, Object> extMap = new HashMap<>();
        Map<String, Object> confMap = JsonUtils.parseMap(param.getAlertGatePO().getAlertGateJson());
        if (MapUtils.isNotEmpty(confMap)) {
            extMap.putAll(confMap);
        }

        Map<String, String> dynamicParams = smsAlertParam.getDynamicParams();
        if (MapUtils.isNotEmpty(confMap)) {
            extMap.putAll(dynamicParams);
        }

        long startTime = System.currentTimeMillis();
        try {
            String jarPath = config.getString("jarPath");
            if (StringUtils.isBlank(jarPath)) {
                jarPath = param.getAlertGatePO().getFilePath();
            }
            String className = config.getString("className");
            ISmsChannel sender = (ISmsChannel) ChannelCache.getChannelInstance(jarPath, className);
            R r = sender.sendSms(message, phones, extMap);
            log.info("[sendSms] end, cost={}, phones={}, message={}, result={}",(System.currentTimeMillis() - startTime), phones, message, r);
            return r;
        } catch (Exception e) {
            log.info("[sendSms] error, cost={}, phones={}, message={}",(System.currentTimeMillis() - startTime), phones, message, e);
            return R.fail(e.getMessage());
        }

    }

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_SMS_JAR;
    }
}
