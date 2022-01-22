package com.dtstack.engine.master.utils;

import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.master.zookeeper.watcher.LocalCacheWatcher;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 本地缓存
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-01-18 20:40
 */
@Component
public class LocalCacheUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCacheUtil.class);

    /**
     * {group:Cache}
     */
    private static Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    private static ZkService zkService;

    private static String SYMBOL_COLON = ":";

    private static final String NULL_STRING = "null";

    /**
     * 一天
     * */
    public static long ONE_DAY_IN_MS = 24 * 3600 * 1000L;

    /**
     * 一周
     */
    public static long ONE_WEEK_IN_MS = 7 * ONE_DAY_IN_MS;

    @Autowired
    private void setZkService(ZkService zkService) {
        LocalCacheUtil.zkService = zkService;
    }

    /**
     * 用 ":" 拼接参数
     * @param params
     * @return
     */
    public static String generateKey(Object... params) {
        if (ObjectUtils.isEmpty(params)) {
            return NULL_STRING;
        }
        return StringUtils.arrayToDelimitedString(params, SYMBOL_COLON);
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
     * @param expireInMillisecond 过期时间
     */
    public static void put(String group, String key, Object val, Long expireInMillisecond) {
        PathUtil.check(group, "group");
        PathUtil.check(key, "key");
        Cache cache = getCache(group, expireInMillisecond);
        cache.put(key, val);
        zkService.setWatcher(group, key, LocalCacheWatcher.getInstance());
    }

    /**
     * 删除缓存，并实现多节点最终一致性
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
        zkService.delete(group, key);
    }

    /**
     * 删除缓存，并实现多节点最终一致性
     *
     * @param group 分组
     */
    public static void removeGroup(String group) {
        PathUtil.check(group, "group");
        Cache cache = cacheMap.get(group);
        if (cache != null) {
            cache.invalidateAll();
        }
        zkService.deleteGroup(group);
    }

    /**
     * 仅删除本地缓存
     *
     * @param group
     * @param key
     */
    public static void removeLocal(String group, String key) {
        PathUtil.check(group, "group");
        PathUtil.check(key, "key");
        Cache cache = cacheMap.get(group);
        if (cache != null) {
            cache.invalidate(key);
        }
    }

    /**
     * 仅删除本地全部缓存
     */
    public static void removeLocalAll() {
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
        return cacheMap.computeIfAbsent(group, k -> CacheBuilder.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(expireInMillisecond, TimeUnit.MILLISECONDS)
                // 注册缓存数据被移除后的异步监听器
                .removalListener(RemovalListeners.asynchronous((notification) -> {
                    RemovalCause cause = notification.getCause();
                    if (cause == RemovalCause.EXPIRED) {
                        // 缓存过期则删除 zk 节点
                        LOGGER.info("key={},value={},reason={}", notification.getKey(), notification.getValue(), cause);
                        zkService.delete(group, (String) notification.getKey());
                    }
                }, Executors.newFixedThreadPool(3)))
                .build());
    }
}
