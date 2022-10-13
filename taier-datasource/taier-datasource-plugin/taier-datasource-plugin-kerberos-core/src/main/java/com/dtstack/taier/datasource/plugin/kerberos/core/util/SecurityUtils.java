package com.dtstack.taier.datasource.plugin.kerberos.core.util;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.plugin.common.DtClassThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.apache.hadoop.fs.CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;

/**
 * 安全认证工具类,支持 tbds ，simple,kerberos认证
 *
 * @author ：wangchuan
 * date：Created in 下午2:02 2021/11/19
 * company: www.dtstack.com
 */
@Slf4j
public class SecurityUtils {

    private static final String TBDS_NAME = "hadoop_security_authentication_tbds_username";

    private static final String TBDS_ID = "hadoop_security_authentication_tbds_secureid";

    private static final String TBDS_KEY = "hadoop_security_authentication_tbds_securekey";

    private static final ConcurrentHashMap<String, UGICacheData> UGI_INFO = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1, new DtClassThreadFactory("ugiCacheFactory"));

    static {
        SCHEDULED_THREAD_POOL_EXECUTOR.scheduleAtFixedRate(new CacheTimerTask(), 0, 10, TimeUnit.SECONDS);
    }


    /**
     * 认证并获取结果
     *
     * @param supplier      Supplier
     * @param configuration 配置类
     * @param <T>           返回结果范型
     * @return 执行结果
     */
    public static <T> T login(Supplier<T> supplier, Configuration configuration, Map<String, Object> confMap) {
        String value = configuration.get(HADOOP_SECURITY_AUTHENTICATION, "simple").toLowerCase(Locale.ROOT);
        switch (value) {
            case "kerberos":
                return KerberosUtil.login(confMap, supplier);
            case "tbds":
                return loginWithTBDS(supplier, configuration);
            default:
                return supplier.get();
        }
    }

    private static <T> T loginWithTBDS(Supplier<T> supplier, Configuration configuration) {
        String tbdsName = configuration.get(TBDS_NAME);
        String tbdsId = configuration.get(TBDS_ID);
        String tbdsKey = configuration.get(TBDS_KEY);
        String ugiCacheName = String.format("%s_%s_%s", tbdsName, tbdsId, tbdsKey);

        // 判断缓存UGI，如果存在则直接使用
        UGICacheData cacheData = UGI_INFO.get(ugiCacheName);
        UserGroupInformation ugi;
        if (cacheData != null) {
            ugi = cacheData.getUgi();
        } else {
            try {
                synchronized (DataSourceType.class) {
                    // 开始 认证
                    log.info("login start, tbdsName={}, tbdsId={}, tbdsKey={} ", tbdsName, tbdsId, tbdsKey);
                    UserGroupInformation.setConfiguration(configuration);
                    UserGroupInformation.loginUserFromSubject(null);
                    ugi = UserGroupInformation.getLoginUser();
                    UGI_INFO.put(ugiCacheName, new UGICacheData(ugi));
                    log.info("login success, currentUser={}", UserGroupInformation.getCurrentUser());
                }
            } catch (Exception e) {
                throw new SourceException(String.format("auth login failed,%s", e.getMessage()), e);
            }
        }
        try {
            return ugi.doAs((PrivilegedExceptionAction<T>) supplier::get);
        } catch (Exception e) {
            throw new SourceException(String.format("ugi doAs failed,%s", e.getMessage()), e);
        }
    }

    static class CacheTimerTask implements Runnable {
        @Override
        public void run() {
            for (String s : UGI_INFO.keySet()) {
                clearKey(s);
            }
        }

        private void clearKey(String principal) {
            UGICacheData ugiCacheData = UGI_INFO.get(principal);
            if (ugiCacheData == null || ugiCacheData.getUgi() == null) {
                UGI_INFO.remove(principal);
                log.info("KerberosLogin CLEAR UGI {}", principal);
                return;
            }

            if (System.currentTimeMillis() > ugiCacheData.getTimeoutStamp()) {
                UGI_INFO.remove(principal);
                log.info("KerberosLogin CLEAR UGI {}", principal);
            }
        }
    }

}
