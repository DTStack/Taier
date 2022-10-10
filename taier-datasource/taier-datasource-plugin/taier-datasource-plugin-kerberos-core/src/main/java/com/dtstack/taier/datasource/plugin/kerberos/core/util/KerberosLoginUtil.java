package com.dtstack.taier.datasource.plugin.kerberos.core.util;

import com.dtstack.taier.datasource.plugin.common.DtClassThreadFactory;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HadoopConfUtil;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import sun.security.krb5.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * kerberos 登录相关操作
 *
 * @author ：wangchuan
 * date：Created in 下午3:35 2022/2/24
 * company: www.dtstack.com
 */
@Slf4j
public class KerberosLoginUtil {
    /**
     * Kerberos 默认角色配置信息
     */
    private static final String SECURITY_TO_LOCAL = "hadoop.security.auth_to_local";
    private static final String SECURITY_TO_LOCAL_DEFAULT = "RULE:[1:$1] RULE:[2:$1]";

    private static final ConcurrentHashMap<String, UGICacheData> UGI_INFO = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1, new DtClassThreadFactory("ugiCacheFactory"));

    static {
        SCHEDULED_THREAD_POOL_EXECUTOR.scheduleAtFixedRate(new KerberosLoginUtil.CacheTimerTask(), 0, 10, TimeUnit.SECONDS);
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

    public static UserGroupInformation loginWithUGI(Map<String, Object> confMap) {
        // 处理 kerberos 逻辑
        KerberosUtil.downloadAndReplace(confMap);
        synchronized (DataSourceType.class) {
            // 非 Kerberos 认证，需要重新刷 UGI 信息
            if (MapUtils.isEmpty(confMap)) {
                try {
                    UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
                    if (UserGroupInformation.isSecurityEnabled() || !UserGroupInformation.AuthenticationMethod.SIMPLE.equals(currentUser.getAuthenticationMethod())) {
                        Config.refresh();
                        UserGroupInformation.setConfiguration(HadoopConfUtil.getDefaultConfiguration());
                    }
                    return currentUser;
                } catch (Exception e) {
                    throw new SourceException(String.format("simple login failed,%s", e.getMessage()), e);
                }
            }

            // Kerberos 认证属性
            String principal = MapUtils.getString(confMap, KerberosConstant.PRINCIPAL);
            String keytab = MapUtils.getString(confMap, KerberosConstant.PRINCIPAL_FILE);
            String krb5Conf = MapUtils.getString(confMap, KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF);
            // 兼容历史逻辑
            if (StringUtils.isNotEmpty(keytab) && !keytab.contains("/")) {
                keytab = MapUtils.getString(confMap, "keytabPath");
                confMap.put(KerberosConstant.PRINCIPAL_FILE, keytab);
            }
            // 如果前端没传 Principal 则直接从 Keytab 中获取第一个 Principal
            if (StringUtils.isEmpty(principal) && StringUtils.isNotEmpty(keytab)) {
                principal = KerberosConfigUtil.getPrincipals(keytab).get(0);
                confMap.put(KerberosConstant.PRINCIPAL, principal);
            }
            // 校验 Principal 和 Keytab 文件
            if (StringUtils.isEmpty(principal) || StringUtils.isEmpty(keytab)) {
                throw new SourceException("Kerberos Login fail, principal or keytab is null");
            }

            // 因为 Hive 需要下载，所有优先设置 ResourceManager Principal
            confMap.putIfAbsent(KerberosConstant.RM_PRINCIPAL, principal);

            // 处理 auth_to_local 规则，兼容所有 principal 短名处理
            confMap.put(SECURITY_TO_LOCAL, SECURITY_TO_LOCAL_DEFAULT);

            // 判断缓存UGI，如果存在则直接使用
            UGICacheData cacheData = UGI_INFO.get(principal + "_" + keytab);
            if (cacheData != null) {
                return cacheData.getUgi();
            }

            try {
                // 设置 Krb5 配置文件
                if (StringUtils.isNotEmpty(krb5Conf)) {
                    System.setProperty(KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF, krb5Conf);
                }

                // 开始 Kerberos 认证
                log.info("login kerberos, currentUser={}, principal={}, principalFilePath={}, krb5ConfPath={}", UserGroupInformation.getCurrentUser(), principal, keytab, krb5Conf);
                Config.refresh();
                Configuration config = KerberosConfigUtil.getConfig(confMap);
                config.set("hadoop.security.authentication", "Kerberos");
                UserGroupInformation.setConfiguration(config);
                UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytab);
                UGI_INFO.put(principal + "_" + keytab, new UGICacheData(ugi));
                log.info("login kerberos success, currentUser={}", UserGroupInformation.getCurrentUser());
                return ugi;
            } catch (Exception var6) {
                throw new SourceException("login kerberos failed", var6);
            }
        }
    }
}
