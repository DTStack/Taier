package com.dtstack.batch.engine.rdbms.common;

import com.dtstack.dtcenter.common.engine.ConsoleSend;
import com.dtstack.dtcenter.common.util.PublicUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author sishu.yss
 */
public class HadoopConf {

    private static Logger logger = LoggerFactory.getLogger(HadoopConf.class);

    private static ConsoleSend consoleSend;

    private static Map<String, Object> defaultConfiguration = new HashMap<>(8);

    @Deprecated
    public static Map<String, Object> getDefaultConfiguration() {
        return getConfiguration(ConsoleSend.getDefaultCluster());
    }

    public static Map<String, Object> getConfiguration(long dtuicTenantId) {
        if (consoleSend == null) {
            return defaultConfiguration;
        }
        Map<String, Object> configuration = new HashMap<>();
        try {
            configuration = consoleSend.getHdfs(dtuicTenantId);
        } catch (Exception e) {
            logger.error("{}", e);
        }

        return configuration;
    }

    public static Map<String, Object> getHadoopKerberosConf(long dtuicTenantId) {
        if (consoleSend == null) {
            return new HashMap<>();
        }
        try {
            Map<String, Object> hadoop = consoleSend.getHdfs(dtuicTenantId);
            if (MapUtils.isNotEmpty(hadoop)) {
                Object kerberosConfig = hadoop.get("kerberosConfig");
                if (Objects.isNull(kerberosConfig)) {
                    return new HashMap<>(4);
                }
                if (kerberosConfig instanceof Map) {
                    return PublicUtil.objectToMap(kerberosConfig);
                }
                if (kerberosConfig instanceof String) {
                    return PublicUtil.strToMap((String) kerberosConfig);
                }
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return new HashMap<>(4);
    }

    public static String getDefaultFs(Long dtuicTenantId) {
        return getConfiguration(dtuicTenantId).getOrDefault("fs.defaultFS", "").toString();
    }

    public static void setConsoleSend(ConsoleSend consoleSend) {
        HadoopConf.consoleSend = consoleSend;
    }
}
