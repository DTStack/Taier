package com.dtstack.engine.master.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class ZkConfig {

    private String nodeZkAddress;
    private String localAddress;
    private Map<String, String> security;

    public String getNodeZkAddress() {
        return nodeZkAddress;
    }

    public void setNodeZkAddress(String nodeZkAddress) {
        this.nodeZkAddress = nodeZkAddress;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        if (StringUtils.isBlank(localAddress)) {
            localAddress = String.format("%s:%s", AddressUtil.getOneIP(),"8090");
        }
        this.localAddress = localAddress;
    }

    public Map<String, String> getSecurity() {
        return security;
    }

    public void setSecurity(String securityStr) {
        if (StringUtils.isBlank(securityStr)) {
            return;
        }
        this.security = JSONObject.parseObject(securityStr, Map.class);
    }

    public void setSecurity(Map<String, String> security) {
        this.security = security;
    }
}
