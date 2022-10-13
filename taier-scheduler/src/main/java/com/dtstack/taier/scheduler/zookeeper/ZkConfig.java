/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.scheduler.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.util.AddressUtil;
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
            localAddress = String.format("%s:%s", AddressUtil.getOneIp(), "8090");
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
