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

package com.dtstack.engine.common.client;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.pluginapi.client.IClient;
import com.dtstack.engine.pluginapi.constrant.ConfigConstant;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.pluginapi.util.MD5Util;
import com.dtstack.engine.pluginapi.util.MathUtil;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * 插件客户端
 * Date: 2018/2/5
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ClientCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCache.class);

    private static final String MD5_SUM_KEY = "md5sum";
    private static final String MD5_ZIP_KEY = "md5zip";
    private String pluginPath;

    private Map<String, IClient> defaultClientMap = Maps.newConcurrentMap();

    private Map<String, Map<String, IClient>> cache = Maps.newConcurrentMap();

    private static ClientCache singleton = new ClientCache();

    private ClientCache(){}

    public static ClientCache getInstance(String pluginPath){
        singleton.pluginPath = pluginPath;
        return singleton;
    }

    /**
     * engineType是不带版本信息的
     * @param engineType 引擎类型：flink、spark、dtscript
     * @param pluginInfo 集群配置信息
     * @return
     */
    public IClient getClient(String engineType, String pluginInfo) throws ClientAccessException {
        try {
            if(Strings.isNullOrEmpty(pluginInfo)){
                return getDefaultPlugin(engineType);
            }

            Map<String, IClient> clientMap = cache.computeIfAbsent(engineType, k -> Maps.newConcurrentMap());

            Properties properties = PublicUtil.jsonStrToObjectWithOutNull(pluginInfo, Properties.class);

            String md5plugin = MD5Util.getMd5String(pluginInfo);
            String md5sum = null;
            if(!properties.containsKey(MD5_SUM_KEY) || (md5sum = MathUtil.getString(properties.get(MD5_SUM_KEY))) == null){
                String md5zip = MathUtil.getString(properties.get(MD5_ZIP_KEY));
                if (md5zip == null) {
                    md5zip = "";
                }
                md5sum = md5zip + md5plugin;
                properties.setProperty(MD5_SUM_KEY, md5sum);
            }

            IClient client = clientMap.get(md5sum);
            if(client == null){
                synchronized (clientMap) {
                    client = clientMap.get(md5sum);
                    if (client == null){
                        client = ClientFactory.buildPluginClient(pluginInfo,pluginPath);
                        client.init(properties);
                        clientMap.putIfAbsent(md5sum, client);
                    }
                }
            }

            return client;
        } catch (Throwable e) {
            LOGGER.error("------- engineType {}  plugin info {} get client error ", engineType, pluginInfo, e);
            throw new ClientAccessException(e);
        }
    }

    public IClient getDefaultPlugin(String engineType){
        IClient defaultClient = defaultClientMap.get(engineType);
        try {
            if(defaultClient == null){
                synchronized (defaultClientMap) {
                    defaultClient = defaultClientMap.get(engineType);
                    if (defaultClient == null){
                        JSONObject pluginInfo = new JSONObject();
                        pluginInfo.put(ConfigConstant.TYPE_NAME_KEY,engineType);
                        defaultClient = ClientFactory.buildPluginClient(pluginInfo.toJSONString(),pluginPath);
                        defaultClientMap.putIfAbsent(engineType, defaultClient);
                    }
                }

            }
        } catch (Throwable e) {
            LOGGER.error("-------job.pluginInfo is empty, either can't find plugin('In console is the typeName') which engineType:{}", engineType, e);
            throw new IllegalArgumentException("job.pluginInfo is empty, either can't find plugin('In console is the typeName') which engineType:" + engineType);
        }
        return defaultClient;
    }




}
