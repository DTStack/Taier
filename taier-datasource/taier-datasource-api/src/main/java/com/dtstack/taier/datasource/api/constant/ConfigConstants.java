package com.dtstack.taier.datasource.api.constant;

/**
 * config constants
 *
 * @author ：wangchuan
 * date：Created in 10:49 2022/9/23
 * company: www.dtstack.com
 */
public final class ConfigConstants {

    // ---------------------------- base -------------------------------
    private static final String CONFIG_PREFIX = "taier.datasource.";

    // ---------------------------- plugin -------------------------------
    public static final String PLUGIN_DIR = CONFIG_PREFIX + "plugin.dir";

    // ---------------------------- retry -------------------------------
    public static final String RETRY_TIMES = CONFIG_PREFIX + "retry.times";
    public static final String RETRY_INTERVAL_TIME = CONFIG_PREFIX + "retry.interval.time";

    // ---------------------------- execute -------------------------------
    public static final String EXECUTE_TIMEOUT = CONFIG_PREFIX + "execute.timeout";
    public static final String SQL_EXECUTE_TIMEOUT = CONFIG_PREFIX + "sql.execute.timeout";
    public static final String EXECUTE_POOL_CORE_SIZE = CONFIG_PREFIX + "execute.pool.core.size";
    public static final String EXECUTE_POOL_MAX_SIZE = CONFIG_PREFIX + "execute.pool.max.size";
    public static final String EXECUTE_POOL_KEEPALIVE_TIME = CONFIG_PREFIX + "execute.keepalive.time";
    public static final String EXECUTE_POOL_QUEUE_SIZE = CONFIG_PREFIX + "execute.queue.size";
}
