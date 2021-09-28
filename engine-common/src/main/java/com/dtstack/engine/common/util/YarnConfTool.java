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

package com.dtstack.engine.common.util;

import com.dtstack.engine.pluginapi.util.MathUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 解析配置获取yarnconf
 * Date: 2018/5/3
 * Company: www.dtstack.com
 * @author xuchao
 */

public class YarnConfTool {

    public static final String YARN_RESOURCEMANAGER_HA_RM_IDS = "yarn.resourcemanager.ha.rm-ids";
    public static final String YARN_RESOURCEMANAGER_ADDRESS = "yarn.resourcemanager.address.%s";
    public static final String YARN_RESOURCEMANAGER_HA_ENABLED = "yarn.resourcemanager.ha.enabled";
    public static final String YARN_RESOURCEMANAGER_WEBAPP_ADDRESS = "yarn.resourcemanager.webapp.address.%s";
    public static final String YARN_RESOURCEMANAGER_ADDRESS_SIMPLE = "yarn.resourcemanager.address";
    public static final String YARN_RESOURCEMANAGER_WEBAPP_ADDRESS_SIMPLE = "yarn.resourcemanager.webapp.address";

    public static String getYarnResourcemanagerHaRmIds(Map<String, Object> conf){
        String resourceManagerHaRmIds = MathUtil.getString(conf.get(YARN_RESOURCEMANAGER_HA_RM_IDS));
        return resourceManagerHaRmIds;
    }

    public static List<String> getYarnResourceManagerAddressKeys(Map<String, Object> conf){
        String haRmIds = getYarnResourcemanagerHaRmIds(conf);
        if (StringUtils.isBlank(haRmIds)){
            return Lists.newArrayList(YARN_RESOURCEMANAGER_ADDRESS_SIMPLE);
        }
        String[] haRmIdArr = haRmIds.split(",");

        List<String> rmAddressKeys = Lists.newArrayList();
        for(String rmId : haRmIdArr){
            String rmAddress = String.format(YARN_RESOURCEMANAGER_ADDRESS, rmId);
            rmAddressKeys.add(rmAddress);
        }

        return rmAddressKeys;
    }

    public static String getYarnResourceManagerAddressVal(Map<String, Object> conf, String key){
        String rmAddress = MathUtil.getString(conf.get(key));
        return rmAddress;
    }

    public static String getYarnResourcemanagerHaEnabled(Map<String, Object> conf){
        String haEnable = MathUtil.getString(conf.get(YARN_RESOURCEMANAGER_HA_ENABLED));
        if(StringUtils.isEmpty(haEnable)){
            if (StringUtils.isEmpty(getYarnResourcemanagerHaRmIds(conf))){
                return Boolean.toString(Boolean.FALSE);
            } else {
                return Boolean.toString(Boolean.TRUE);
            }
        }

        return haEnable;
    }

    public static String getYarnResourceManagerWebAppAddressVal(Map<String, Object> conf, String key){
        return MathUtil.getString(conf.get(key));
    }

    public static List<String> getYarnResourceManagerWebAppAddressKeys(Map<String, Object> conf){
        String haRmIds = getYarnResourcemanagerHaRmIds(conf);
        if (StringUtils.isBlank(haRmIds)){
            return Lists.newArrayList(YARN_RESOURCEMANAGER_WEBAPP_ADDRESS_SIMPLE);
        }
        String[] haRmIdArr = haRmIds.split(",");

        List<String> rmWebAppAddressKeys = Lists.newArrayList();
        for(String rmId : haRmIdArr){
            String rmWebAppAddress = String.format(YARN_RESOURCEMANAGER_WEBAPP_ADDRESS, rmId);
            rmWebAppAddressKeys.add(rmWebAppAddress);
        }

        return rmWebAppAddressKeys;
    }

}
