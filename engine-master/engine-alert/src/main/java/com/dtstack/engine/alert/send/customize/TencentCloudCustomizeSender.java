package com.dtstack.engine.alert.send.customize;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.domian.Notice;
import com.dtstack.engine.alert.send.AbstractSender;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2020/12/3 4:32 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class TencentCloudCustomizeSender extends AbstractSender {


    @Override
    protected String buildBody(Notice notice, ClusterAlertPO clusterAlertPO) {
        return buildCustomize(notice.getData(),clusterAlertPO.getAlertGateSource());
    }


    private String buildCustomize(Object data, String source ) {
        JSONObject body = new JSONObject();
        Map<String, String> dynamicParams = new HashMap<>();
        dynamicParams.put("message", data.toString());
        body.put("data",data);
        body.put("source",source);
        body.put("dynamicParams", dynamicParams);
        return body.toJSONString();
    }
}
