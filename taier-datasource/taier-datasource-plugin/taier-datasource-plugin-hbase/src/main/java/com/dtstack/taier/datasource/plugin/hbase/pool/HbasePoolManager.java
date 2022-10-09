package com.dtstack.taier.datasource.plugin.hbase.pool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.utils.JSONUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.JaasUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.HbaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import sun.security.krb5.Config;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-10-31 14:37
 * @Description:
 */
@Slf4j
public class HbasePoolManager {

    private volatile static HbasePoolManager manager;

    private volatile static Map<String, Connection> sourcePool = Maps.newConcurrentMap();

    private static final String HBASE_KEY = "zookeeperUrl:%s,zNode:%s";

    private HbasePoolManager() {
    }

    public static HbasePoolManager getInstance() {
        if (null == manager) {
            synchronized (HbasePoolManager.class) {
                if (null == manager) {
                    manager = new HbasePoolManager();
                }
            }
        }
        return manager;
    }

    public static Connection getConnection(ISourceDTO source, SqlQueryDTO queryDTO) {
        HbaseSourceDTO hbaseSourceDTO = (HbaseSourceDTO) source;
        String key = getPrimaryKey(hbaseSourceDTO).intern();
        Connection conn = sourcePool.get(key);
        if (conn == null) {
            synchronized (HbasePoolManager.class) {
                conn = sourcePool.get(key);
                if (conn == null) {
                    conn = initHbaseConn(hbaseSourceDTO, queryDTO);
                    sourcePool.putIfAbsent(key, conn);
                }
            }
        }
        return conn;
    }

    private static String getPrimaryKey(HbaseSourceDTO hbaseSourceDTO) {
        return String.format(HBASE_KEY, hbaseSourceDTO.getUrl(), hbaseSourceDTO.getPath());
    }

    public static Connection initHbaseConn(HbaseSourceDTO source, SqlQueryDTO queryDTO) {
        Map<String, Object> sourceToMap = sourceToMap(source, queryDTO);
        Configuration hConfig = HBaseConfiguration.create();
        for (Map.Entry<String, Object> entry : sourceToMap.entrySet()) {
            hConfig.set(entry.getKey(), Objects.nonNull(entry.getValue()) ? entry.getValue().toString() : null);
        }

        Map<String, Object> kerberosConfig = source.getKerberosConfig();
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            if (!kerberosConfig.containsKey(KerberosConstant.HBASE_MASTER_PRINCIPAL)) {
                throw new SourceException(String.format("HBASE   must setting %s ", KerberosConstant.HBASE_MASTER_PRINCIPAL));
            }

            if (!kerberosConfig.containsKey(KerberosConstant.HBASE_REGION_PRINCIPAL)) {
                log.info("setting hbase.regionserver.kerberos.principal 为 {}", kerberosConfig.get(KerberosConstant.HBASE_MASTER_PRINCIPAL));
                kerberosConfig.put(KerberosConstant.HBASE_REGION_PRINCIPAL, kerberosConfig.get(KerberosConstant.HBASE_MASTER_PRINCIPAL));
            }
        }

        log.info("get Hbase connection, url : {}, path : {}, kerberosConfig : {}", source.getUrl(), source.getUsername(), source.getKerberosConfig());
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<Connection>) () -> {
                    try {
                        // 每次都清空 Configuration
                        javax.security.auth.login.Configuration.setConfiguration(null);
                        boolean effectZookeeper = BooleanUtils.toBoolean((String) sourceToMap.getOrDefault(KerberosConstant.HBASE_KERBEROS_EFFECT_ZOOKEEPER, "true"));
                        if (MapUtils.isNotEmpty(kerberosConfig) && effectZookeeper) {
                            // hbase zk kerberos 需要写 jaas 文件
                            String jaasConf = JaasUtil.writeJaasConf(kerberosConfig,JaasUtil.JAAS_CONTENT);
                            // 刷新kerberos认证信息，在设置完java.security.krb5.conf后进行，否则会使用上次的krb5文件进行 refresh 导致认证失败
                            try {
                                Config.refresh();
                            } catch (Exception e) {
                                log.error("hbase kerberos认证信息刷新失败！");
                            }
                            System.setProperty("java.security.auth.login.config", jaasConf);
                            log.info("java.security.auth.login.config : {}", jaasConf);
                            System.setProperty("javax.security.auth.useSubjectCredsOnly", "true");
                        }
                        return ConnectionFactory.createConnection(hConfig);
                    } catch (Exception e) {
                        throw new SourceException(String.format("get hbase connection exception,%s", e.getMessage()), e);
                    }
                }
        );
    }

    /**
     * 数据源 改成 HBase 需要的 Map 信息
     *
     * @param iSource
     * @return
     */
    public static Map<String, Object> sourceToMap(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        HbaseSourceDTO hbaseSourceDTO = (HbaseSourceDTO) iSource;
        Map<String, Object> hbaseMap = new HashMap<>();
        //对于直接传config的 走直接生成的逻辑

        if (StringUtils.isNotBlank(hbaseSourceDTO.getConfig())) {
            JSONObject jsonObject = JSON.parseObject(hbaseSourceDTO.getConfig());
            hbaseMap.putAll(jsonObject);
        } else {

            if (StringUtils.isBlank(hbaseSourceDTO.getUrl())) {
                throw new SourceException("The cluster address cannot be empty");
            }
            // 设置集群地址
            hbaseMap.put(DtClassConsistent.HBaseConsistent.KEY_HBASE_ZOOKEEPER_QUORUM, hbaseSourceDTO.getUrl());

            // 设置根路径
            if (StringUtils.isNotBlank(hbaseSourceDTO.getPath())) {
                hbaseMap.put(DtClassConsistent.HBaseConsistent.KEY_ZOOKEEPER_ZNODE_PARENT, hbaseSourceDTO.getPath());
            }

        }

        // 设置 Kerberos 信息
        if (MapUtils.isNotEmpty(hbaseSourceDTO.getKerberosConfig())) {
            hbaseMap.putAll(hbaseSourceDTO.getKerberosConfig());
            hbaseMap.put("hadoop.security.authentication", "Kerberos");
            hbaseMap.put("hbase.security.authentication", "Kerberos");
            hbaseMap.put("hbase.master.kerberos.principal", hbaseMap.get("hbase.master.kerberos.principal"));
            log.info("getHbaseConnection principalFile:{}", hbaseMap.get("principalFile"));
        }

        // 设置默认信息
        hbaseMap.put("hbase.rpc.timeout", queryDTO == null || queryDTO.getQueryTimeout() == null ? "60000" : String.valueOf(queryDTO.getQueryTimeout() * 1000));
        hbaseMap.put("ipc.socket.timeout", "20000");
        hbaseMap.put("hbase.client.retries.number", "3");
        hbaseMap.put("hbase.client.pause", "100");
        hbaseMap.put("zookeeper.recovery.retry", "3");
        hbaseMap.put("hbase.client.ipc.pool.type", "RoundRobinPool");
        Integer poolSize = hbaseSourceDTO.getPoolConfig() == null ? 10 : hbaseSourceDTO.getPoolConfig().getMaximumPoolSize();
        hbaseMap.put("hbase.client.ipc.pool.size", String.valueOf(poolSize));

        // 设置其他信息
        hbaseMap.putAll(JSONUtil.parseMap(hbaseSourceDTO.getOthers()));
        return hbaseMap;
    }

    @PreDestroy
    public void doDestroy() {
        for (Map.Entry<String, Connection> entry : sourcePool.entrySet()) {
            Connection connection = entry.getValue();
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    throw new SourceException(String.format("hbase connection closed failed,%s", e.getMessage()), e);
                }
            }
        }
    }
}
