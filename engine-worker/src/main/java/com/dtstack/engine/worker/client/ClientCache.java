package com.dtstack.engine.worker.client;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.worker.loader.DtClassLoader;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
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

    private static String userDir = System.getProperty("user.dir");

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
                        client = buildPluginClient(pluginInfo);
                        client.init(properties);
                        clientMap.putIfAbsent(md5sum, client);
                    }
                }
            }

            return client;
        } catch (Throwable e) {
            throw new ClientAccessException(e);
        }
    }

    private IClient getDefaultPlugin(String engineType){
        IClient defaultClient = defaultClientMap.get(engineType);
        if(defaultClient == null){
            LOG.error("-------job.pluginInfo is empty, either can't find plugin('In console is the typeName') which engineType:{}", engineType);
            throw new IllegalArgumentException("job.pluginInfo is empty, either can't find plugin('In console is the typeName') which engineType:" + engineType);
        }

        return defaultClient;
    }

    public IClient buildPluginClient(String pluginInfo) throws Exception {

        Map<String, Object> params = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
        String clientTypeStr = MathUtil.getString(params.get(ConfigConstant.TYPE_NAME_KEY));
        if (StringUtils.isBlank(clientTypeStr)) {
            throw new RuntimeException("not support for typeName:" + clientTypeStr + " pluginInfo:" + pluginInfo);
        }
        loadComputerPlugin(clientTypeStr);
        return ClientFactory.createPluginClass(clientTypeStr);
    }

    private void loadComputerPlugin(String pluginType) throws Exception{

        if(ClientFactory.checkContainClassLoader(pluginType)){
            return;
        }

        String plugin = String.format("%s/pluginLibs/%s", userDir, pluginType);
        File finput = new File(plugin);
        if(!finput.exists()){
            throw new Exception(String.format("%s directory not found",plugin));
        }

        ClientFactory.addClassLoader(pluginType, getClassLoad(finput));
    }

    private URLClassLoader getClassLoad(File dir) throws IOException {
        File[] files = dir.listFiles();
        List<URL> urlList = new ArrayList<>();
        int index = 0;
        if (files!=null && files.length>0){
            for(File f : files){
                String jarName = f.getName();
                if(f.isFile() && jarName.endsWith(".jar")){
                    urlList.add(f.toURI().toURL());
                    index = index+1;
                }
            }
        }
        URL[] urls = urlList.toArray(new URL[urlList.size()]);
        return new DtClassLoader(urls, this.getClass().getClassLoader());
    }

    private void addDefaultClient(String engineType, IClient client, String pluginInfoMd5){

        if(defaultClientMap.get(engineType) != null){
            LOG.error("------setting error: conflict default plugin key:{}-----", engineType);
        }

        defaultClientMap.putIfAbsent(engineType, client);

        Map<String, IClient> clientMap = cache.computeIfAbsent(engineType, key -> Maps.newConcurrentMap());
        clientMap.put(pluginInfoMd5, client);
    }
}
