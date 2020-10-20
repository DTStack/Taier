package com.dtstack.engine.alert.factory.ding;

import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.DingAlertParam;
import com.dtstack.engine.common.util.HttpKit;
import com.dtstack.lang.data.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
public class DingDTService extends AbstractDingService {

    private final Logger log = LoggerFactory.getLogger(DingDTService.class);

    @Override
    public AlertGateCode alertGateCode() {
        return AlertGateCode.AG_GATE_DING_DT;
    }

    @Override
    public R sendDing(AlertParam param) {
        DingAlertParam dingAlertParam = (DingAlertParam) param;
        Map<String, Object> extMap = dingAlertParam.getExtCfg();
        String message = dingAlertParam.getMessage();
        List<String> hookUrls = dingAlertParam.getDings();

        long startTime = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> at = new HashMap<>();
        data.put("msgtype", "text");
        Map<String, String> content = new HashMap<>();
        content.put("content", message);
        at.put("atMobiles", extMap.get("atMobiles"));
        at.put("isAtAll", extMap.get("isAtAll"));
        data.put("text", content);
        data.put("at", at);
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Accept", "application/json");
            header.put("Content-Type", "application/json;charset=utf-8");
            for (String hookUrl : hookUrls) {
                String result = HttpKit.send(hookUrl, header, data, false, false);
                log.info("[sendDing] hookUrl={},result={}", hookUrl, result);
            }
            log.info("[sendDing] usage of time = {}, the message = {}", (System.currentTimeMillis() - startTime), content);
            return R.ok();
        } catch (Exception e) {
            log.info("[sendDing] error, time cost = {}, the message = {}", (System.currentTimeMillis() - startTime), content, e);
            return R.fail(e.getMessage());
        }
    }


    @Override
    public R sendDingWithMarkDown(AlertParam param) {
        DingAlertParam dingAlertParam = (DingAlertParam) param;
        Map<String, Object> extMap = dingAlertParam.getExtCfg();
        String message = dingAlertParam.getMessage();
        List<String> hookUrls = dingAlertParam.getDings();
        String title = dingAlertParam.getSubject();

        long startTime = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> at = new HashMap<>();
        data.put("msgtype", "markdown");
        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("text", message);
        at.put("atMobiles", extMap.get("atMobiles"));
        at.put("isAtAll", extMap.get("isAtAll"));
        data.put("markdown", content);
        data.put("at", at);
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Accept", "application/json");
            header.put("Content-Type", "application/json;charset=utf-8");
            for (String hookUrl : hookUrls) {
                String result = HttpKit.send(hookUrl, header, data, false, false);
                log.info("[sendDing] hookUrl={},result={}", hookUrl, result);
            }
            log.info("[sendDing] usage of time = {}, the message = {}", (System.currentTimeMillis() - startTime), content);
            return R.ok();
        } catch (Exception e) {
            log.info("[sendDing] error, time cost = {}, the message = {}", (System.currentTimeMillis() - startTime), content, e);
            return R.fail(e.getMessage());
        }
    }
}
