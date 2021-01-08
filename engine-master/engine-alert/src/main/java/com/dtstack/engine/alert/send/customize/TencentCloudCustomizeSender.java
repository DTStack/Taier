package com.dtstack.engine.alert.send.customize;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.domian.Notice;
import com.dtstack.engine.alert.send.AbstractSender;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import org.springframework.stereotype.Component;

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
        body.put("data",data);
        body.put("source",source);
        return body.toJSONString();
    }
}
