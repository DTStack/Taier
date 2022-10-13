package com.dtstack.taier.datasource.plugin.kerberos.core.util;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Jaas 文件工具类
 *
 * @author ：wangchuan
 * date：Created in 下午5:37 2021/6/2
 * company: www.dtstack.com
 */
@Slf4j
public class JaasUtil {

    /**
     * cache 缓存
     */
    private static final ConcurrentHashMap<String, String> JAAS_CACHE = new ConcurrentHashMap<>();

    /**
     * JAAS CONF 内容
     */
    public static final String JAAS_CONTENT = "Client {\n" +
            "    com.sun.security.auth.module.Krb5LoginModule required\n" +
            "    useKeyTab=true\n" +
            "    storeKey=true\n" +
            "    keyTab=\"%s\"\n" +
            "    useTicketCache=false\n" +
            "    principal=\"%s\";\n" +
            "};";


    /**
     * Kafka JAAS 内容
     */
    public static final String KAFKA_JAAS_CONTENT = "KafkaClient {\n" +
            "    com.sun.security.auth.module.Krb5LoginModule required\n" +
            "    useKeyTab=true\n" +
            "    storeKey=true\n" +
            "    keyTab=\"%s\"\n" +
            "    principal=\"%s\";\n" +
            "};";


    /**
     * 写 jaas文件，同时处理 krb5.conf
     *
     * @param kerberosConfig kerberos 配置文件
     * @return jaas文件绝对路径
     */
    public synchronized static String writeJaasConf(Map<String, Object> kerberosConfig, String jaasContent) {
        KerberosUtil.downloadAndReplace(kerberosConfig);
        log.info("初始化 jaas.conf 文件, kerberosConfig : {}", kerberosConfig);
        if (MapUtils.isEmpty(kerberosConfig)) {
            return null;
        }

        // 处理 krb5.conf
        if (kerberosConfig.containsKey(KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF)) {
            System.setProperty(KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF, MapUtils.getString(kerberosConfig, KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF));
        }
        String keytabPath = MapUtils.getString(kerberosConfig, KerberosConstant.PRINCIPAL_FILE);
        String principal = MapUtils.getString(kerberosConfig, KerberosConstant.PRINCIPAL);
        String cacheKey = String.format("%s_%s", keytabPath, principal);
        // 缓存中存在则直接返回
        String cacheLoginConf = JAAS_CACHE.get(cacheKey);
        if (StringUtils.isNotBlank(cacheLoginConf) && new File(cacheLoginConf).exists()) {
            return cacheLoginConf;
        }
        try {
            File file = new File(keytabPath);
            File jaas = new File(file.getParent() + File.separator + "jaas.conf");
            if (jaas.exists()) {
                boolean deleteCheck = jaas.delete();
                if (!deleteCheck) {
                    log.error("delete file: {} fail", jaas.getAbsolutePath());
                }
            }
            FileUtils.write(jaas, String.format(jaasContent, keytabPath, principal));
            String loginConf = jaas.getAbsolutePath();
            log.info("Init Kerberos:login-conf:{}\n principal:{}", keytabPath, principal);
            JAAS_CACHE.put(cacheKey, loginConf);
            return loginConf;
        } catch (IOException e) {
            throw new SourceException(String.format("Write a jaas.conf configuration file exception: %s", e.getMessage()), e);
        }
    }
}
