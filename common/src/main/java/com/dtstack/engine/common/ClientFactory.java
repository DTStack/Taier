package com.dtstack.engine.common;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import com.dtstack.engine.common.callback.ClassLoaderCallBack;
import com.dtstack.engine.common.callback.ClassLoaderCallBackMethod;

import com.google.common.collect.Maps;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/12
 */
public class ClientFactory {

    private static Map<String, ClassLoader> pluginClassLoader = Maps.newConcurrentMap();

    public static ClassLoader getClassLoader(String pluginType) {
        return pluginClassLoader.get(pluginType);
    }

    public static IClient createPluginClass(String pluginType) throws Exception {

        ClassLoader classLoader = pluginClassLoader.get(pluginType);
        return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<IClient>() {

            @Override
            public IClient execute() throws Exception {
                ServiceLoader<IClient> iClients = ServiceLoader.load(IClient.class);
                Iterator<IClient> iClientIterator = iClients.iterator();
                if (!iClientIterator.hasNext()) {
                    throw new RuntimeException("not support for engine type " + pluginType);
                }

                IClient client = iClientIterator.next();
                return new ClientProxy(client);
            }
        }, classLoader, true);
    }

    public static void addClassLoader(String pluginType, ClassLoader classLoader) {
        if (pluginClassLoader.containsKey(pluginType)) {
            return;
        }

        pluginClassLoader.putIfAbsent(pluginType, classLoader);
    }

    public static boolean checkContainClassLoader(String pluginType) {
        return pluginClassLoader.containsKey(pluginType);
    }
}
