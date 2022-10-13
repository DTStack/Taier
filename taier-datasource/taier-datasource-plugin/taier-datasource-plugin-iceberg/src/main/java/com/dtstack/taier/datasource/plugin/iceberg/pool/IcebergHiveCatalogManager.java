package com.dtstack.taier.datasource.plugin.iceberg.pool;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.IcebergSourceDTO;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.datasource.plugin.common.utils.MapUtil;
import com.dtstack.taier.datasource.plugin.common.utils.PathUtils;
import com.dtstack.taier.datasource.plugin.common.utils.SftpUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.iceberg.CatalogProperties;
import org.apache.iceberg.hive.HiveCatalog;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Iceberg HiveCatalog 池管理
 *
 * @author ：wangchuan
 * date：Created in 下午9:37 2021/11/9
 * company: www.dtstack.com
 */
@Slf4j
public class IcebergHiveCatalogManager {

    private volatile static IcebergHiveCatalogManager hiveCatalogManager;

    private final Map<String, HiveCatalog> sourcePool = Maps.newConcurrentMap();

    private static final String ICEBERG_POOL_KEY = "warehouse:%s,uri:%s,confDir:%s,clients:%s";

    private static final String CORE_SITE_XML_NAME = "core-site.xml";

    private static final String HDFS_SITE_XML_NAME = "hdfs-site.xml";

    private static final String HIVE_SITE_XML_NAME = "hdfs-site.xml";

    private static final String HIVE_CATALOG_NAME = "hive_catalog";

    private static final AtomicInteger CATALOG_NUMBER = new AtomicInteger(0);

    /**
     * 私有构造方法
     */
    private IcebergHiveCatalogManager() {
    }

    /**
     * 获取事例对象
     *
     * @return IcebergHiveCatalogManager
     */
    public static IcebergHiveCatalogManager getInstance() {
        if (null == hiveCatalogManager) {
            synchronized (IcebergHiveCatalogManager.class) {
                if (null == hiveCatalogManager) {
                    hiveCatalogManager = new IcebergHiveCatalogManager();
                }
            }
        }
        return hiveCatalogManager;
    }

    /**
     * 获取 hive catalog, 为了避免频繁访问 hive metaStore, 目前只支持连接池
     *
     * @param source 数据源连接信息
     * @return hiveCatalog
     */
    public HiveCatalog getHiveCatalog(ISourceDTO source) {
        IcebergSourceDTO icebergSourceDTO = (IcebergSourceDTO) source;
        String key = getPrimaryKey(icebergSourceDTO).intern();
        HiveCatalog hiveCatalog = sourcePool.get(key);
        if (hiveCatalog == null) {
            synchronized (IcebergHiveCatalogManager.class) {
                hiveCatalog = sourcePool.get(key);
                if (hiveCatalog == null) {
                    hiveCatalog = initHiveCatalog(icebergSourceDTO);
                    sourcePool.putIfAbsent(key, hiveCatalog);
                }
            }
        }
        return hiveCatalog;
    }

    /**
     * 初始化 iceberg hive catalog
     *
     * @param source 数据源连接信息
     * @return hive catalog
     */
    protected HiveCatalog initHiveCatalog(IcebergSourceDTO source) {
        String localConfDir = SftpUtil.downloadSftpDirFromSftp(source, source.getConfDir(), PathUtils.getConfDir());
        AssertUtils.notBlank(localConfDir, "hadoop conf dir cannot be null");
        String coreSiteDir = localConfDir + File.separator + CORE_SITE_XML_NAME;
        String hdfsSiteDir = localConfDir + File.separator + HDFS_SITE_XML_NAME;
        String hiveSiteDir = localConfDir + File.separator + HIVE_SITE_XML_NAME;
        AssertUtils.isTrue(new File(coreSiteDir).exists(), "file core-site.xml not exists");
        AssertUtils.isTrue(new File(hdfsSiteDir).exists(), "file hdfs-site.xml not exists");
        Configuration configuration = buildConfiguration();
        configuration.addResource(new Path(coreSiteDir));
        configuration.addResource(new Path(hdfsSiteDir));
        if (new File(hiveSiteDir).exists()) {
            // hive-site.xml 非必须
            configuration.addResource(new Path(hiveSiteDir));
        }
        // 额外配置
        Map<String, String> properties = Maps.newHashMap();
        MapUtil.putIfValueNotBlank(properties, CatalogProperties.URI, source.getUri());
        MapUtil.putIfValueNotBlank(properties, CatalogProperties.WAREHOUSE_LOCATION, source.getWarehouse());
        if (Objects.nonNull(source.getClients())) {
            MapUtil.putIfValueNotBlank(properties, CatalogProperties.CLIENT_POOL_SIZE, String.valueOf(source.getClients()));
        }
        HiveCatalog hiveCatalog = new HiveCatalog();
        hiveCatalog.setConf(configuration);
        hiveCatalog.initialize(HIVE_CATALOG_NAME + CATALOG_NUMBER.incrementAndGet(), properties);
        return hiveCatalog;
    }

    /**
     * 获取连接池 key
     *
     * @param sourceDTO 数据源信息
     * @return pool map key
     */
    private static String getPrimaryKey(IcebergSourceDTO sourceDTO) {
        return String.format(ICEBERG_POOL_KEY, sourceDTO.getWarehouse(), sourceDTO.getUri(), sourceDTO.getConfDir(), sourceDTO.getClients());
    }

    /**
     * 构建 Configuration, 不加载默认配置, 因为会传过来这些配置
     *
     * @return hadoop Configuration
     */
    protected Configuration buildConfiguration() {
        return new Configuration(false);
    }
}
