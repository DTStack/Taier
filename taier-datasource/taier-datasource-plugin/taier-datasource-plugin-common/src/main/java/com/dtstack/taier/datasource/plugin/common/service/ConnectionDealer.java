package com.dtstack.taier.datasource.plugin.common.service;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.thread.CustomThreadFactory;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * connection dealer, 防止连接数过多, 所有获取到 connection 都会缓存于此
 * 后期可以扩展一些功能, 针对 datasource 获取到的不同类型数据源的 connection 进行数量监控等
 *
 * @author ：wangchuan
 * date：Created in 14:33 2022/9/27
 * company: www.dtstack.com
 */
@Slf4j
public class ConnectionDealer {

    /**
     * check map 缓存
     */
    public static final Map<Integer, ConnectionCheck> CHECK_MAP = new ConcurrentHashMap<>();

    /**
     * connection 过期时间, 默认 30 min
     */
    public static final long EXPIRE_TIME = 30 * 60 * 1000L;

    /**
     * connection 周期清理
     */
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("connection-dealer"));

    static {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(ConnectionDealer::clearCache, 10L, 60L, TimeUnit.SECONDS);
    }

    /**
     * 定时清理过期的 connection
     */
    public static void clearCache() {
        log.debug("start clear expire connection, quantity to be checked: {}", CHECK_MAP.size());
        long timeNow = System.currentTimeMillis();
        for (Map.Entry<Integer, ConnectionCheck> checkEntry : CHECK_MAP.entrySet()) {
            ConnectionCheck check = checkEntry.getValue();
            // 过期清理
            if (timeNow - check.getTimestamp() > check.getExpireTime()) {
                Connection connection = check.getConnection();
                // 强制关闭 connection
                DBUtil.closeDBResources(null, null, connection, true);
                CHECK_MAP.remove(connection.hashCode());
            }
        }
    }

    /**
     * 将 jdbc connection 放入缓存
     *
     * @param connection     jdbc connection
     * @param externalObtain 是否是外部获取
     * @param sourceType     数据源类型
     */
    public static void put(Connection connection, boolean externalObtain, Integer sourceType) {
        put(connection, externalObtain, EXPIRE_TIME, sourceType);
    }

    /**
     * 将 jdbc connection 放入缓存
     *
     * @param connection     jdbc connection
     * @param externalObtain 是否是外部获取
     * @param expireTime     过期时间
     * @param sourceType     数据源类型
     */
    public static void put(Connection connection, boolean externalObtain, long expireTime, Integer sourceType) {
        if (Objects.isNull(connection)) {
            throw new SourceException("connection can't be null.");
        }
        CHECK_MAP.put(connection.hashCode(), new ConnectionCheck(connection, System.currentTimeMillis(), externalObtain, expireTime, sourceType));
    }

    /**
     * 判断是否需要关闭连接, 如果没有缓存则需要进行关闭
     *
     * @param connection 需要处理的连接
     * @return 是否需要关闭
     */
    public static boolean needClose(Connection connection) {
        ConnectionCheck check = CHECK_MAP.get(connection.hashCode());
        if (Objects.isNull(check)) {
            return true;
        }
        // 非外部获取的 connection 需要进行关闭
        return !check.isExternalObtain();
    }
}
