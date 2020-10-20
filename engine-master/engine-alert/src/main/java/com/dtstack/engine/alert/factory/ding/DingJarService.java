package com.dtstack.engine.alert.factory.ding;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.channel.IDingChannel;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.factory.ChannelCache;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.DingAlertParam;
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
 * Date: 2020/6/12
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class DingJarService extends AbstractDingService {

    private final Logger log = LoggerFactory.getLogger(DingDTService.class);

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_DING_JAR;
    }

    @Override
    public R send(AlertParam param) {
        JSONObject config = JSON.parseObject(param.getAlertGatePO().getAlertGateJson());
        DingAlertParam dingAlertParam = (DingAlertParam) param;

        Map<String, Object> extMap = new HashMap<>();
        if (MapUtils.isNotEmpty(dingAlertParam.getExtCfg())) {
            extMap.putAll(dingAlertParam.getExtCfg());
        }
        Map<String, Object> confMap = JsonUtils.parseMap(param.getAlertGatePO().getAlertGateJson());
        if (MapUtils.isNotEmpty(confMap)) {
            extMap.putAll(confMap);
        }

        String message = dingAlertParam.getMessage();
        Map<String, String> dynamicParams = dingAlertParam.getDynamicParams();
        List<String> dings = dingAlertParam.getDings();

        String jarPath = config.getString("jarPath");
        if (StringUtils.isBlank(jarPath)) {
            jarPath = param.getAlertGatePO().getFilePath();
        }

        String className = config.getString("className");
        long startTime = System.currentTimeMillis();
        try {
            IDingChannel sender = (IDingChannel) ChannelCache.getChannelInstance(jarPath, className);
            R r = sender.sendDing(dings, message, dynamicParams, extMap);
            log.info("[sendDing] end, time cost={}, dings={}, message={}, result={}", (System.currentTimeMillis() - startTime), dings, message, r);
            return r;
        } catch (Exception e) {
            log.error("[sendDing] error, time cost={}, dings={}, message={}", (System.currentTimeMillis() - startTime), dings, message, e);
            return R.fail(e.getMessage());
        }


    }
}
