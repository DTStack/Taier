package com.dtstack.taier.datasource.plugin.odps.pool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.pool.PoolConfig;
import com.dtstack.taier.datasource.plugin.odps.common.OdpsFields;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OdpsSourceDTO;
import com.google.common.collect.Maps;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午3:30 2020/8/3
 * @Description：
 */
@Slf4j
@NoArgsConstructor
public class OdpsManager {
    private volatile static OdpsManager manager;

    private volatile Map<String, OdpsPool> sourcePool = Maps.newConcurrentMap();

    private static final String ODPS_KEY = "endPoint:%s,accessId:%s,accessKey:%s,project:%s,packageAuthorizedProject:%s,accountType:%s";

    public static OdpsManager getInstance() {
        if (null == manager) {
            synchronized (OdpsManager.class) {
                if (null == manager) {
                    manager = new OdpsManager();
                }
            }
        }
        return manager;
    }

    public OdpsPool getConnection(ISourceDTO source) {
        String key = getPrimaryKey(source).intern();
        OdpsPool odpsPool = sourcePool.get(key);
        if (odpsPool == null) {
            synchronized (OdpsManager.class) {
                odpsPool = sourcePool.get(key);
                if (odpsPool == null) {
                    odpsPool = initSource(source);
                    sourcePool.putIfAbsent(key, odpsPool);
                }
            }
        }
        return odpsPool;
    }

    /**
     * 初始化odps pool
     * @param source
     * @return odps pool
     */
    public OdpsPool initSource(ISourceDTO source) {
        OdpsSourceDTO odpsSourceDTO = (OdpsSourceDTO) source;
        PoolConfig poolConfig = odpsSourceDTO.getPoolConfig();
        OdpsPoolConfig config = new OdpsPoolConfig();
        config.setMaxWaitMillis(poolConfig.getConnectionTimeout());
        config.setMinIdle(poolConfig.getMinimumIdle());
        config.setMaxIdle(poolConfig.getMaximumPoolSize());
        config.setMaxTotal(poolConfig.getMaximumPoolSize());
        config.setTimeBetweenEvictionRunsMillis(poolConfig.getMaxLifetime() / 10);
        config.setMinEvictableIdleTimeMillis(poolConfig.getMaxLifetime());
        // 闲置实例校验标识，如果校验失败会删除当前实例
        config.setTestWhileIdle(Boolean.TRUE);
        JSONObject odpsConfig = JSON.parseObject(odpsSourceDTO.getConfig());

        // 配置odps连接信息
        config.setOdpsServer(odpsConfig.getString(OdpsFields.KEY_ODPS_SERVER));
        config.setAccessId(odpsConfig.getString(OdpsFields.KEY_ACCESS_ID));
        config.setAccessKey(odpsConfig.getString(OdpsFields.KEY_ACCESS_KEY));
        config.setProject(odpsConfig.getString(OdpsFields.KEY_PROJECT));
        config.setPackageAuthorizedProject(odpsConfig.getString(OdpsFields.PACKAGE_AUTHORIZED_PROJECT));
        config.setAccountType(odpsConfig.getString(OdpsFields.KEY_ACCOUNT_TYPE));
        OdpsPool pool = new OdpsPool(config);
        //初始化 实例个数
        pool.addObjects(poolConfig.getMinimumIdle());
        return pool;
    }

    /**
     * 获取odps唯一key
     * @param sourceDTO
     * @return
     */
    private String getPrimaryKey (ISourceDTO sourceDTO) {
        OdpsSourceDTO odpsSourceDTO = (OdpsSourceDTO) sourceDTO;
        JSONObject odpsConfig = JSON.parseObject(odpsSourceDTO.getConfig());
        return String.format(ODPS_KEY,
                odpsConfig.getString(OdpsFields.KEY_ODPS_SERVER),
                odpsConfig.getString(OdpsFields.KEY_ACCESS_ID),
                odpsConfig.getString(OdpsFields.KEY_ACCESS_KEY),
                odpsConfig.getString(OdpsFields.KEY_PROJECT),
                odpsConfig.getString(OdpsFields.PACKAGE_AUTHORIZED_PROJECT),
                odpsConfig.getString(OdpsFields.KEY_ACCOUNT_TYPE));
    }

}
