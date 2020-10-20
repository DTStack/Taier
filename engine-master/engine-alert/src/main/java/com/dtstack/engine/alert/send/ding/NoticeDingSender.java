package com.dtstack.engine.alert.send.ding;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.domian.Notice;
import com.dtstack.engine.alert.send.AbstractSender;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.google.common.collect.Lists;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: sanyue
 * @Date: 2018/5/28 16:34
 */
@Component
public class NoticeDingSender extends AbstractSender {


    @Override
    protected String buildBody(Notice notice, ClusterAlertPO clusterAlertPO) {
        DingContent dingContent = buildDing(notice);
        String body = buildDingBody(dingContent, clusterAlertPO.getAlertGateSource());
        return body;
    }

    private DingContent buildDing(Notice notice) {
        DingContent dingContent = new DingContent();
        dingContent.setWebHooks(Arrays.asList(notice.getWebhook()));
        dingContent.setSubject(notice.getTitle());

        Map<String, Object> data = new HashMap<>(2);
        data.put("msgtype", "text");
        Map<String, String> text = new HashMap<>(1);
        String content = notice.getTitle() + "\n" + notice.getContent();
        text.put("content", content);
        data.put("text", text);
        Map<String, Object> at = new HashMap<>(2);
        String telephone = notice.getUserDTO().getTelephone();
        at.put("atMobiles", Lists.newArrayList(telephone));
        at.put("isAtAll", false);
        data.put("at", at);
        String dingMessage = JSON.toJSONString(data);
        StringEntity se = new StringEntity(dingMessage, "utf-8");
        dingContent.setEntity(se);

        dingContent.setMessage(content);
        dingContent.setAt(Lists.newArrayList(telephone));

        return dingContent;
    }

    private static String buildDingBody(DingContent dingContent,String sourceName) {
        JSONObject body = new JSONObject();
        body.put("dings", dingContent.getWebHooks());
        body.put("subject", dingContent.getSubject());
        body.put("message", dingContent.getMessage());
        body.put("dynamicParams", new JSONObject());
        body.put("source", sourceName);
        JSONObject config = new JSONObject();
        config.put("atMobiles", dingContent.getAt());
        config.put("isAtAll", false);
        body.put("extCfg", config);
        return body.toJSONString();
    }
}
