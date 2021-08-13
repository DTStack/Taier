package com.dtstack.batch.engine.rdbms.common;

import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
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

    public static Map<String, Object> getConfiguration(long dtuicTenantId) {
        return Engine2DTOService.getHdfs(dtuicTenantId);
    }

    public static Map<String, Object> getHadoopKerberosConf(long dtuicTenantId) {
        try {
            Map<String, Object> hadoop = Engine2DTOService.getHdfs(dtuicTenantId);
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
}
