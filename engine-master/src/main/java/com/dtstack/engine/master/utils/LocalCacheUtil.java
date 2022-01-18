package com.dtstack.engine.master.utils;

import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.master.zookeeper.watcher.LocalCacheWatcher;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-01-18 20:40
 */
@Component
public class LocalCacheUtil {
    /**
     * {group:Cache}
     */
    private static Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    private static ZkService zkService;

    @Autowired
    private void setZkService(ZkService zkService) {
        LocalCacheUtil.zkService = zkService;
    }

    /**
     * 获取缓存内容
     *
     * @param group 分组
     * @param key   key
     * @return 缓存对象
     */
    public static Object get(String group, String key) {
        PathUtil.check(group, "group");
        PathUtil.check(key, "key");
        Cache cache = cacheMap.get(group);
        if (cache == null) {
            return null;
        }
        return cache.getIfPresent(key);
    }

    /**
     * 写缓存
     *
     * @param group  分组
     * @param key    key
     * @param val    值
     * @param expireInMillisecond 过期时间，不支持动态传入
     */
    public static void put(String group, String key, Object val, Long expireInMillisecond) {
        PathUtil.check(group, "group");
        PathUtil.check(key, "key");
        Cache cache = getCache(group, expireInMillisecond);
        cache.put(key, val);
        // todo 设置监听事件
        zkService.setWatcher(group, key, LocalCacheWatcher.getInstance());
    }

    /**
     * 删除缓存
     *
     * @param group 分组
     * @param key   key
     */
    public static void remove(String group, String key) {
        PathUtil.check(group, "group");
        PathUtil.check(key, "key");
        Cache cache = cacheMap.get(group);
        if (cache != null) {
            cache.invalidate(key);
        }
        // todo test
        zkService.delete(group, key);
    }

    static void removeLocal(String group, String key) {
        PathUtil.check(group, "group");
        PathUtil.check(key, "key");
        Cache cache = cacheMap.get(group);
        if (cache != null) {
            cache.invalidate(key);
        }
        // todo why not delete
    }

    static void removeAll() {
        for (String group : cacheMap.keySet()) {
            Cache cache = cacheMap.get(group);
            if (cache != null) {
                cache.invalidateAll();
            }
        }
    }

    /**
     * 获取cache
     *
     * @param group               分组
     * @param expireInMillisecond 超时时间
     * @return cache
     */
    private static Cache getCache(String group, Long expireInMillisecond) {
        // todo test
        return cacheMap.computeIfAbsent(group, k -> CacheBuilder.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(expireInMillisecond, TimeUnit.MILLISECONDS)
                .build());
    }
}
