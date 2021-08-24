package com.dtstack.engine.local.test;

import com.dtstack.engine.common.callback.ClassLoaderCallBackMethod;
import com.dtstack.engine.common.client.ClientProxy;
import com.dtstack.engine.common.client.IClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.loader.DtClassLoader;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;


public class ClientFactory {

    private static Map<String, ClassLoader> pluginClassLoader = Maps.newConcurrentMap();

    public static IClient createPluginClass(ClassLoader classLoader) throws Exception {
        return ClassLoaderCallBackMethod.callbackAndReset(()-> {
            ServiceLoader<IClient> serviceLoader = ServiceLoader.load(IClient.class);

            List<IClient> matchingClient = new ArrayList<>();
            serviceLoader.iterator().forEachRemaining(matchingClient::add);

            if (matchingClient.size() != 1) {
                throw new RuntimeException("zero or more than one plugin client found" + matchingClient);
            }
            return matchingClient.get(0);
        }, classLoader, true);
    }

    public static IClient buildPluginClient(String pluginInfo,String pluginPath) throws Exception {
        Map<String, Object> params = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
        String clientTypeStr = MathUtil.getString(params.get(ConfigConstant.TYPE_NAME_KEY));
        if (StringUtils.isBlank(clientTypeStr)) {
            throw new RuntimeException("not support for typeName:" + clientTypeStr + " pluginInfo:" + pluginInfo);
        }

        ClassLoader classLoader = pluginClassLoader.computeIfAbsent(clientTypeStr, type -> {
            String plugin = pluginPath + File.separator + type;
            File pluginFile = new File(plugin);
            if (!pluginFile.exists()) {
                throw new RuntimeException(String.format("%s directory not found", plugin));
            }
            return createDtClassLoader(pluginFile);
        });

        return ClientFactory.createPluginClass(classLoader);
    }


    private static URLClassLoader createDtClassLoader(File dir) {
        File[] files = dir.listFiles();
        URL[] urls = Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().endsWith(".jar"))
                .map(file -> {
                    try {
                        return file.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("file to url error ", e);
                    }
                })
                .toArray(URL[]::new);

        return new DtClassLoader(urls, ClientFactory.class.getClassLoader());
    }
}

