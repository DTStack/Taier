package com.dtstack.engine.alert.client.ding.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.ding.AbstractDingAlterClient;
import com.dtstack.engine.alert.client.ding.AlterSendDingBean;
import com.dtstack.engine.alert.client.ding.bean.*;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.enums.DingTypeEnums;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.alert.http.HttpKit;
import com.dtstack.lang.data.R;
import com.dtstack.lang.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 5:14 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DTDingAlterClient extends AbstractDingAlterClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void buildBean(AlterContext alterContext, AlterSendDingBean alterSendDingBean) throws AlterException {
        String ding = alterContext.getDing();

        if (StringUtils.isBlank(ding)) {
            throw new AlterException("钉钉通道必须配置url");
        }

        alterSendDingBean.setDing(ding);
    }

    @Override
    protected R sendDing(AlterSendDingBean alterSendDingBean) {
        long startTime = System.currentTimeMillis();
        String alertGateJson = alterSendDingBean.getAlertGateJson();
        JSONObject jsonObject = JSONObject.parseObject(alertGateJson);

        DingAt dingAt = new DingAt();
        dingAt.setAtAll(alterSendDingBean.getAtAll());
        dingAt.setAtMobiles(alterSendDingBean.getAtMobiles());

        DingBean dingBean;
        String dingMsgType = jsonObject.getString(ConstDingAlter.DING_MSG_TYPE_KEY);
        if (DingTypeEnums.TEXT.getMsg().equalsIgnoreCase(dingMsgType)) {
            dingBean = sendTest(alterSendDingBean, dingAt);
        } else if (DingTypeEnums.MARKDOWN.getMsg().equalsIgnoreCase(dingMsgType)) {
            dingBean = sendDingWithMarkDown(alterSendDingBean, dingAt);
        } else {
            throw new BizException(String.format("不支持的钉钉消息类型，msgtype=%s", dingMsgType));
        }

        try {
            Map<String,String> header = new HashMap<>();
            header.put("Accept", "application/json");
            header.put("Content-Type", "application/json;charset=utf-8");
            String result = HttpKit.send(alterSendDingBean.getDing(), header, dingBean, false, false);
            logger.info("[sendDing] hookUrl={},result={}", alterSendDingBean.getDing(), result);
            DingResultBean dingResultBean = JSONObject.parseObject(result, DingResultBean.class);
            if (dingResultBean != null && StringUtils.isNotBlank(dingResultBean.getErrmsg())) {
                logger.info("[sendDing] usage of time = {}, the message = {} result:{}", (System.currentTimeMillis() - startTime), alterSendDingBean.getContent(),dingResultBean.getErrmsg());
                return R.fail(dingResultBean.getErrmsg());
            } else {
                logger.info("[sendDing] usage of time = {}, the message = {}", (System.currentTimeMillis() - startTime), alterSendDingBean.getContent());
                return R.ok();
            }
        } catch (Exception e) {
            logger.info("[sendDing] error, time cost = {}, the message = {}", (System.currentTimeMillis() - startTime), alterSendDingBean.getContent(), e);
            return R.fail(e.getMessage());
        }

    }

    private DingBean sendDingWithMarkDown(AlterSendDingBean alterSendDingBean,DingAt dingAt) {
        DingMarkdown dingMarkdown = new DingMarkdown();
        dingMarkdown.setTitle(alterSendDingBean.getTitle());
        dingMarkdown.setContent(alterSendDingBean.getContent());

        DingBean dingBean = new DingBean();
        dingBean.setMsgtype(DingTypeEnums.MARKDOWN.getMsg());
        dingBean.setDingMarkdown(dingMarkdown);
        dingBean.setAt(dingAt);

        return dingBean;
    }

    private DingBean sendTest(AlterSendDingBean alterSendDingBean,DingAt dingAt) {
        DingText dingText = new DingText();
        dingText.setContent(alterSendDingBean.getContent());

        DingBean dingBean = new DingBean();
        dingBean.setMsgtype(DingTypeEnums.MARKDOWN.getMsg());
        dingBean.setText(dingText);
        dingBean.setAt(dingAt);

        return dingBean;
    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_DING_DT.code();
    }
}
