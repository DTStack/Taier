package com.dtstack.taier.datasource.plugin.hdfs3_cdp;

import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HadoopConfUtil;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:07 2020/9/2
 * @Description：Yarn 配置信息
 */
@Slf4j
public class YarnConfUtil extends HadoopConfUtil {
    /**
     * 组装 yarn 配置信息
     *
     * @param hdfsConfig hdfs配置信息 String类型
     * @param yarnConfig yarn配置信息 map类型
     * @return YarnConfiguration
     */
    public static YarnConfiguration getYarnConfiguration(String defaultFs, String hdfsConfig, Map<String, Object> yarnConfig, Map<String, Object> kerberosConfig) {
        Configuration yarnConf;
        try {
            Configuration configuration = getHdfsConf(defaultFs, hdfsConfig, null);
            yarnConf = new YarnConfiguration(configuration);
            initYarnConfiguration((YarnConfiguration) yarnConf, yarnConfig);
        } catch (Exception e) {
            throw new SourceException(String.format("Failed to obtain yarn configuration information,%s", e.getMessage()), e);
        }
        return (YarnConfiguration) yarnConf;
    }

    /**
     * 获取 Hdfs 和 yarn 配置的总和
     *
     * @param hdfsConfig
     * @param yarnConfig
     * @return
     */
    public static Configuration getFullConfiguration(String defaultFs, String hdfsConfig, Map<String, Object> yarnConfig, Map<String, Object> kerberosConfig) {
        Configuration hadoopConf = getHdfsConf(defaultFs, hdfsConfig, kerberosConfig);
        YarnConfiguration yarnConfiguration = getYarnConfiguration(defaultFs, hdfsConfig, yarnConfig, kerberosConfig);

        for (Map.Entry<String, String> entry : yarnConfiguration) {
            hadoopConf.set(entry.getKey(), entry.getValue());
        }
        return hadoopConf;
    }

    /**
     * 初始化yarn配置信息
     *
     * @param yarnConfiguration
     * @param map
     */
    private static void initYarnConfiguration(YarnConfiguration yarnConfiguration, Map<String, Object> map) {
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }

                yarnConfiguration.set(entry.getKey(), entry.getValue().toString());
            }
        }
        setHadoopDefaultConfig(yarnConfiguration, null, null);
    }
}
