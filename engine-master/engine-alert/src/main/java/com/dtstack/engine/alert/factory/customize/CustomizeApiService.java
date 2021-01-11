package com.dtstack.engine.alert.factory.customize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.channel.ICustomizeChannel;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.factory.AlertService;
import com.dtstack.engine.alert.factory.ChannelCache;
import com.dtstack.engine.alert.factory.ding.DingDTService;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.CustomizeAlertParam;
import com.dtstack.engine.common.util.JsonUtils;
import com.dtstack.lang.data.R;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 2020/6/12
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class CustomizeApiService implements AlertService {

    private final Logger log = LoggerFactory.getLogger(CustomizeApiService.class);

    @Autowired
    private ChannelCache channelCache;

    @Override
    public R send(AlertParam param) {
        JSONObject config = JSON.parseObject(param.getAlertGatePO().getAlertGateJson());
        CustomizeAlertParam customizeAlertParam = (CustomizeAlertParam)param;

        Map<String, Object> extMap = new HashMap<>();
        if (MapUtils.isNotEmpty(param.getExtCfg())) {
            extMap.putAll(param.getExtCfg());
        }
        Map<String, Object> confMap = JsonUtils.parseMap(param.getAlertGatePO().getAlertGateJson());
        if (MapUtils.isNotEmpty(confMap)) {
            extMap.putAll(confMap);
        }

        String jarPath = config.getString("jarPath");
        if (StringUtils.isBlank(jarPath)) {
            jarPath = param.getAlertGatePO().getFilePath();
        }

        String className = config.getString("className");
        long startTime = System.currentTimeMillis();
        Object data = customizeAlertParam.getData();
        try {
            ICustomizeChannel sender = (ICustomizeChannel) channelCache.getChannelInstance(jarPath, className);
            R r = sender.sendCustomizeAlert(data,extMap);
            log.info("[CustomizeAlert] end, cost={}, data={}, result={}", (System.currentTimeMillis() - startTime), data, r);
            return r;
        } catch (Exception e) {
            log.info("[CustomizeAlert] error, cost={}, data={}",(System.currentTimeMillis() - startTime), data, e);
            return R.fail("jarPath:"+jarPath +"加载失败，请检查配置！");
        }

    }

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_CUSTOM_JAR;
    }
}
