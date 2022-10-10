package com.dtstack.taier.datasource.plugin.kerberos.core.hdfs;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:28 2020/9/1
 * @Description：Hadoop 配置信息
 */
@Slf4j
public class HadoopConfUtil {

    /**
     * 默认 Hadoop 配置
     */
    private static final Configuration defaultConfiguration = new Configuration(false);

    /**
     * 获取默认集群信息
     *
     * @return
     */
    public static Configuration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    static {
        try {
            // 默认配置
            setHadoopDefaultConfig(defaultConfiguration, null, null);
        } catch (Exception e) {
            log.error("default Hadoop setting error ：{}", e.getMessage(), e);
        }
    }

    /**
     * 组装 Hdfs 配置信息
     *
     * @param defaultFS
     * @param config
     * @param kerberosConfig
     * @return
     */
    public static Configuration getHdfsConf(String defaultFS, String config, Map<String, Object> kerberosConfig) {
        Configuration conf = new Configuration(false);
        // 设置默认属性
        setHadoopDefaultConfig(conf, defaultFS, kerberosConfig);
        return combineHdfsConfig(conf, config, kerberosConfig);
    }

    public static Configuration getHdfsConf(String tbdsUsername, String tbdsSecureId, String tbdsSecureKey, String defaultFS, String config, Map<String, Object> kerberosConfig) {
        Configuration conf = new Configuration(false);
        //tdbs 校验
        conf.set("hadoop.security.authentication", "tbds");
        conf.set("hadoop_security_authentication_tbds_username", tbdsUsername);
        conf.set("hadoop_security_authentication_tbds_secureid", tbdsSecureId);
        conf.set("hadoop_security_authentication_tbds_securekey", tbdsSecureKey);
        // 设置默认属性
        setHadoopDefaultConfig(conf, defaultFS, kerberosConfig);
        return combineHdfsConfig(conf, config, kerberosConfig);
    }

    /**
     * 设置 HDFS 配置信息
     *
     * @param conf
     * @param hdfsConfig
     * @param kerberosConfig
     * @return
     */
    private static Configuration combineHdfsConfig(Configuration conf, String hdfsConfig, Map<String, Object> kerberosConfig) {
        Map<String, Object> hdfsConf = JSONObject.parseObject(hdfsConfig);
        // 如果 Hdfs 和 高可用配置为空，则直接返回当前用户下面的配置信息
        if (MapUtils.isEmpty(hdfsConf) && StringUtils.isBlank(conf.get("fs.defaultFS"))) {
            return defaultConfiguration;
        }

        if (MapUtils.isNotEmpty(hdfsConf)) {
            // 去除压缩格式限制
            log.debug("remove param 'io.compression.codecs', origin value is {}", hdfsConf.get("io.compression.codecs"));
            hdfsConf.remove("io.compression.codecs");
            // 去除加密
            log.debug("remove param 'dfs.encrypt.data.transfer.cipher.suites', origin value is {}", hdfsConf.get("dfs.encrypt.data.transfer.cipher.suites"));
            hdfsConf.remove("dfs.encrypt.data.transfer.cipher.suites");
            for (Map.Entry<String, Object> entry : hdfsConf.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                conf.set(entry.getKey(), entry.getValue().toString());
            }
        }

        if (MapUtils.isNotEmpty(kerberosConfig)) {
            for (Map.Entry<String, Object> entry : kerberosConfig.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                conf.set(entry.getKey(), entry.getValue().toString());
            }
        }

        // 禁用缓存
        conf.setBoolean("fs.hdfs.impl.disable.cache", true);
        conf.setBoolean("fs.file.impl.disable.cache", true);

        return conf;
    }

    /**
     * 设置 Hdfs 默认配置
     */
    public static void setHadoopDefaultConfig(Configuration conf, String defaultFS, Map<String, Object> kerberosConfig) {
        if (StringUtils.isNotBlank(defaultFS)) {
            conf.set("fs.defaultFS", defaultFS);
        }

        conf.setBoolean("fs.hdfs.impl.disable.cache", true);
        conf.setBoolean("ipc.client.fallback-to-simple-auth-allowed", true);
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

        if (MapUtils.isNotEmpty(kerberosConfig)) {
            conf.set("dfs.namenode.kerberos.principal.pattern", "*");
        }
    }
}
