package com.dtstack.engine.master.router.cache;


/**
 * @author sishu.yss
 */
public class ConsoleUtil {

    private static ConsoleCache consoleCache;
    private static final String SPLIT = "_";

    public static <T> T getCluster(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.CLUSTER.name(), clazz);
    }

    public static void setCluster(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.CLUSTER.name(), value);
    }

    public static <T> T getPlugin(String tenantId, String engineType, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, getPluginKey(engineType), clazz);
    }

    public static void setPlugin(String tenantId, String engineType, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, getPluginKey(engineType), value);
    }

    public static <T> T getHive(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.HIVE.name(), clazz);
    }

    public static void setHive(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.HIVE.name(), value);
    }


    public static <T> T getHiveServer(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.HIVE_SERVER.name(), clazz);
    }

    public static void setHiveServer(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.HIVE_SERVER.name(), value);
    }

    public static <T> T getHadoop(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.HDFS.name(), clazz);
    }

    public static void setHadoop(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.HDFS.name(), value);
    }

    public static <T> T getSftp(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.SFTP.name(), clazz);
    }

    public static void setSftp(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.SFTP.name(), value);
    }

    public static <T> T getLibra(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.LIBRA.name(), clazz);
    }

    public static void setLibra(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.LIBRA.name(), value);
    }

    public static <T> T getCarbon(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.CARBONDATA.name(), clazz);
    }

    public static void setCarbon(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.CARBONDATA.name(), value);
    }

    public static <T> T getSftpDir(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.SFTP_REMOTE_DIR.name(), clazz);
    }

    public static void setSftpDir(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.SFTP_REMOTE_DIR.name(), value);
    }

    public static String getSupportEngine(String tenantId) {
        return consoleCache.get(tenantId, CacheKey.SUPPORT_ENGINE_LIST.name(), String.class);
    }

    public static void setSupportEngine(String tenantId, String val) {
        if (consoleCache == null) {
            return;
        }

        consoleCache.set(tenantId, CacheKey.SUPPORT_ENGINE_LIST.name(), val);
    }


    public static void pulish(String tenantId) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.publishRemoveMessage(tenantId);
    }

    public static <T> T getExtCluster(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.CLUSTEREXT.name(), clazz);
    }

    public static void setExtCluster(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.CLUSTEREXT.name(), value);
    }


    public static void setConsoleCache(ConsoleCache bean) {
        consoleCache = bean;
    }

    private static String getPluginKey(String engineType) {
        return CacheKey.PLUGIN.name() + SPLIT + engineType;
    }

    public static <T> T getImpala(String tenantId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(tenantId, CacheKey.IMPALA.name(), clazz);
    }

    public static void setImpala(String tenantId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(tenantId, CacheKey.IMPALA.name(), value);
    }

    public static <T> T getTiDB(String tenantId,String userId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(String.format("%s.%s",tenantId,userId), CacheKey.TIDB.name(), clazz);
    }

    public static void setTiDB(String tenantId,String userId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(String.format("%s.%s",tenantId,userId), CacheKey.TIDB.name(), value);
    }

    public static <T> T getPresto(String tenantId,String userId, Class<T> clazz) {
        if (consoleCache == null) {
            return null;
        }
        return consoleCache.get(String.format("%s.%s",tenantId,userId), CacheKey.PRESTO.name(), clazz);
    }

    public static void setPresto(String tenantId,String userId, Object value) {
        if (consoleCache == null) {
            return;
        }
        consoleCache.set(String.format("%s.%s",tenantId,userId), CacheKey.PRESTO.name(), value);
    }

    public enum CacheKey {
        CLUSTER,
        PLUGIN,
        HIVE,
        HIVE_SERVER,
        HDFS,
        LIBRA,
        CARBONDATA,
        SUPPORT_ENGINE_LIST,
        ENGINE_PLUGIN_LIST,
        CLUSTEREXT,
        SFTP_REMOTE_DIR,
        SFTP,
        IMPALA,
        TIDB,
        PRESTO;
    }

    public static String getEnginePluginInfo(String tenantId, Integer engineType) {

        String key = tenantId + engineType;
        return consoleCache.get(key, CacheKey.ENGINE_PLUGIN_LIST.name(), String.class);
    }

    public static void setEnginePluginInfo(String tenantId, Integer engineType, String val) {
        if (consoleCache == null) {
            return;
        }

        String key = tenantId + engineType;
        consoleCache.set(key, CacheKey.ENGINE_PLUGIN_LIST.name(), val);
    }

}
