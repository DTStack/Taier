package com.dtstack.engine.alert.load;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 2:42 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JarCache {

    private JarCache instance = new JarCache();
    private static final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(1000L).initialCapacity(1000).expireAfterAccess(10, TimeUnit.MINUTES).build();

    private JarCache(){
    }

    public Object getChannelInstance(String jarPath, String className) throws Exception {
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
