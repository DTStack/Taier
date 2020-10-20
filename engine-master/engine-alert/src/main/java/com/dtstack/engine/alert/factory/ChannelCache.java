package com.dtstack.engine.alert.factory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Date: 2020/6/17
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class ChannelCache {
    private static final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(1000L).initialCapacity(1000).expireAfterAccess(10, TimeUnit.MINUTES).build();

    public static Object getChannelInstance(String jarPath, String className) throws Exception {
        if (jarPath.contains("/normal")) {
            String key = jarPath + className;

            return cache.get(key, () -> {
                JarClassLoader loader = new JarClassLoader();
                return loader.getInstance(jarPath, className);
            });
        }
        //tmp路径下的插件 不走缓存
        JarClassLoader loader = new JarClassLoader();
        return loader.getInstance(jarPath, className);
    }
}
