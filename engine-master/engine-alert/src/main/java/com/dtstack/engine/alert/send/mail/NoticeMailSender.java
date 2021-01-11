package com.dtstack.engine.alert.send.mail;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.domian.Notice;
import com.dtstack.engine.alert.send.AbstractSender;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * Reason:
 * Date: 2017/5/17
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
@Component
public class NoticeMailSender extends AbstractSender {

    @Override
    protected String buildBody(Notice notice, ClusterAlertPO clusterAlertPO) {
        Mail mail = buildMail(notice);
        String body = buildMailBody(mail, clusterAlertPO.getAlertGateSource());
        return body;
    }

    private Mail buildMail(Notice notice) {
        Mail mail = new Mail();
        mail.setContent(notice.getContent());
        mail.setTitle(notice.getTitle());
        mail.setTo(Lists.newArrayList(notice.getUserDTO().getEmail()));
        return mail;
    }

    private static String buildMailBody(Mail mail,String source) {
        JSONObject body = new JSONObject();
        body.put("emails", mail.getTo());
        body.put("subject", mail.getTitle());
        Map<String, String> dynamicParams = new HashMap<>();
        dynamicParams.put("message", mail.getContent());
        body.put("dynamicParams", dynamicParams);
        body.put("source", source);
        return body.toJSONString();
    }
}
