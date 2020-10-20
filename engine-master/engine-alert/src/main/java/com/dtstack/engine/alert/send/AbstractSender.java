package com.dtstack.engine.alert.send;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.client.AlertGateApiFacade;
import com.dtstack.engine.alert.domian.AlertEvent;
import com.dtstack.engine.alert.domian.Notice;
import com.dtstack.engine.alert.enums.AGgateType;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.dtstack.engine.api.enums.SenderType;
import com.dtstack.engine.dao.ClusterAlertDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Date: 2020/8/9
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public abstract class AbstractSender {
    @Autowired
    private ClusterAlertDao clusterAlertDao;
    @Autowired
    private AlertGateApiFacade alertGateApiFacade;


    public final boolean send(Notice notice) {
        SenderType senderType = SenderType.parse(notice.getSenderType());
        AGgateType aGgateType = convert(senderType);
        ClusterAlertPO clusterAlertPO = getDefaultClusterAlertPO(notice, aGgateType);
        String body = buildBody(notice, clusterAlertPO);
        AlertEvent alertEvent = JSONObject.parseObject(body, AlertEvent.class);
        alertGateApiFacade.sendAsync(alertEvent, aGgateType);
        return true;
    }

    protected abstract String buildBody(Notice notice, ClusterAlertPO clusterAlertPO);


    protected ClusterAlertPO getDefaultClusterAlertPO(Notice notice, AGgateType aGgateType) {
        ClusterAlertPO query = new ClusterAlertPO();
        query.setClusterId(0);
        query.setIsDefault(1);
        query.setAlertGateType(aGgateType.type());
        return clusterAlertDao.get(query);
    }

    private AGgateType convert(SenderType senderType) {
        Assert.notNull(senderType,"senderType should not be null");
        switch (senderType) {
            case SMS:
                return AGgateType.AG_GATE_TYPE_SMS;
            case MAIL:
                return AGgateType.AG_GATE_TYPE_MAIL;
            case DINGDING:
                return AGgateType.AG_GATE_TYPE_DING;
            case PHONE:
                return AGgateType.AG_GATE_TYPE_PHONE;
            default:
                throw new IllegalArgumentException("unsupported convert SenderType : " + senderType.name());
        }
    }



}
