package com.dtstack.engine.common.client;

import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
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

    private static final Logger LOG = LoggerFactory.getLogger(ClientCache.class);

    private static final String MD5_SUM_KEY = "md5sum";
    private static final String MD5_ZIP_KEY = "md5zip";

    private Map<String, IClient> defaultClientMap = Maps.newConcurrentMap();

    private Map<String, Map<String, IClient>> cache = Maps.newConcurrentMap();

    private static ClientCache singleton = new ClientCache();

    private ClientCache(){}

    public static ClientCache getInstance(){
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

            Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);

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
                        client = ClientFactory.buildPluginClient(pluginInfo);
                        client.init(properties);
                        clientMap.putIfAbsent(md5sum, client);
                    }
                }
            }

            return client;
        } catch (Throwable e) {
            LOG.error("------- engineType {}  plugin info {} get client error ", engineType, pluginInfo, e);
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
                        defaultClient = ClientFactory.buildPluginClient("");
                        defaultClientMap.putIfAbsent(engineType, defaultClient);
                    }
                }

            }
        } catch (Exception e) {
            LOG.error("-------job.pluginInfo is empty, either can't find plugin('In console is the typeName') which engineType:{}", engineType);
            throw new IllegalArgumentException("job.pluginInfo is empty, either can't find plugin('In console is the typeName') which engineType:" + engineType);
        }
        return defaultClient;
    }


}
