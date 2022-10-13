package com.dtstack.taier.develop.datasource.convert.load;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.DataSourceTypeEnum;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.source.SourceDTOLoader;
import com.dtstack.taier.dao.domain.DevelopDataSource;
import com.dtstack.taier.datasource.api.dto.SSLConfig;
import com.dtstack.taier.datasource.api.dto.source.AbstractSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.datasource.convert.dto.ConfigDTO;
import com.dtstack.taier.develop.datasource.convert.enums.SourceDTOType;
import com.dtstack.taier.develop.dto.devlop.DataSourceVO;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.dtstack.taier.develop.datasource.convert.Consistent.CONF_DIR;
import static com.dtstack.taier.develop.datasource.convert.Consistent.KERBEROS_CONFIG_KEY;
import static com.dtstack.taier.develop.datasource.convert.Consistent.KERBEROS_PATH_KEY;
import static com.dtstack.taier.develop.datasource.convert.Consistent.KERBEROS_REMOTE_PATH;
import static com.dtstack.taier.develop.datasource.convert.Consistent.KEY_PATH;
import static com.dtstack.taier.develop.datasource.convert.Consistent.PATH;
import static com.dtstack.taier.develop.datasource.convert.Consistent.REMOTE_SSL_DIR;
import static com.dtstack.taier.develop.datasource.convert.Consistent.SEPARATOR;
import static com.dtstack.taier.develop.datasource.convert.Consistent.SFTP_CONF;
import static com.dtstack.taier.develop.datasource.convert.Consistent.SSL_CLIENT_CONF;
import static com.dtstack.taier.develop.datasource.convert.Consistent.SSL_CONFIG;
import static com.dtstack.taier.develop.datasource.convert.Consistent.SSL_FILE_TIMESTAMP;

/**
 * 用于 Loader 数据源，通过数据源中心 sourceID ，调用数据源中心 获取数据源信息，进行转化为 common-loader
 * 中需要的 ISourceDTO，并进行一些 kerberos 的处理：像替换路径、SFTP kerberos 配置下载等
 *
 * @author ：wangchuan
 * date：Created in 上午10:47 2021/7/5
 * company: www.dtstack.com
 */
@Service
public class SourceLoaderService implements SourceDTOLoader {

    /**
     * 数据源中心
     */
    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private ClusterService clusterService;

    /**
     * 构建供 common-loader 使用的 ISourceDTO
     *
     * @param datasourceId 数据源ID(数据源中心)
     * @return 转化后的 ISourceDTO
     * @see ISourceDTO
     */
    public ISourceDTO buildSourceDTO(Long datasourceId) {
        return buildSourceDTO(datasourceId, null);
    }

    /**
     * 构建供 common-loader 使用的 ISourceDTO
     *
     * @param datasourceId 数据源ID(数据源中心)
     * @param schema       数据源 schema 信息
     * @return 转化后的 ISourceDTO
     * @see ISourceDTO
     */
    public ISourceDTO buildSourceDTO(Long datasourceId, String schema) {
        DevelopDataSource developDataSource = getServiceInfoByDtCenterId(datasourceId);
        return buildSourceDTO(developDataSource, schema);
    }

    /**
     * 构建供 common-loader 使用的 ISourceDTO, 可供数据源中心调用
     *
     * @param developDataSource 数据源信息
     * @return 转化后的 ISourceDTO
     * @see ISourceDTO
     */
    public ISourceDTO buildSourceDTO(DevelopDataSource developDataSource) {
        return buildSourceDTO(developDataSource, null);
    }

    /**
     * 构建供 common-loader 使用的 ISourceDTO
     *
     * @param developDataSource 数据源信息
     * @param schema            数据源 schema 信息
     * @return 转化后的 ISourceDTO
     * @see ISourceDTO
     */
    public ISourceDTO buildSourceDTO(DevelopDataSource developDataSource, String schema) {
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setSchema(schema);
        // 处理配置
        sftpPrepare(configDTO, developDataSource);
        kerberosPrepare(configDTO, developDataSource);
        expandConfigPrepare(configDTO, developDataSource);
        return SourceDTOType.getSourceDTO(developDataSource.getDataJson(), developDataSource.getType(), configDTO);
    }

    /**
     * 构建供 common-loader 使用的 ISourceDTO, 可供数据源中心调用
     *
     * @param dataSourceVO   数据源信息
     * @param kerberosConfig kerberos 配置信息
     * @return 转化后的 ISourceDTO
     * @see ISourceDTO
     */
    public ISourceDTO buildSourceDTO(DataSourceVO dataSourceVO, Map<String, Object> kerberosConfig) {
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSourceVO.getDataType(), dataSourceVO.getDataVersion());
        DevelopDataSource dsServiceInfoDTO = new DevelopDataSource();
        dsServiceInfoDTO.setTenantId(dataSourceVO.getTenantId());
        dsServiceInfoDTO.setDataJson(dataSourceVO.getDataJson().toJSONString());
        dsServiceInfoDTO.setType(typeEnum.getVal());
        dsServiceInfoDTO.setTenantId(dataSourceVO.getTenantId());
        ISourceDTO sourceDTO = buildSourceDTO(dsServiceInfoDTO);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            ((AbstractSourceDTO) sourceDTO).setKerberosConfig(kerberosConfig);
        }
        return sourceDTO;
    }

    /**
     * 设置 sftp 配置
     *
     * @param configDTO         配置类
     * @param developDataSource 数据源信息
     */
    private void sftpPrepare(ConfigDTO configDTO, DevelopDataSource developDataSource) {
        if (Objects.isNull(developDataSource.getTenantId())) {
            throw new RuntimeException("租户 id 不能为空");
        }
        if (Objects.isNull(configDTO.getSftpConf())) {
            configDTO.setSftpConf(clusterService.getSftp(developDataSource.getTenantId()));
        }
    }

    /**
     * 预处理 kerberos 配置，包括判断是否开启 kerberos、是否需要从 SFTP 重新下载 kerberos 配置、相对路径转换绝对路径等
     *
     * @param sourceInfo 数据源中心数据源信息
     */
    private void kerberosPrepare(ConfigDTO configDTO, DevelopDataSource sourceInfo) {
        JSONObject dataJson = JSONObject.parseObject(sourceInfo.getDataJson());
        JSONObject kerberosConfig = dataJson.getJSONObject(KERBEROS_CONFIG_KEY);
        if (MapUtils.isEmpty(kerberosConfig)) {
            return;
        }
        String kerberosDir = kerberosConfig.getString(KERBEROS_PATH_KEY);
        if (StringUtils.isEmpty(kerberosDir)) {
            return;
        }
        // 不再进行 kerberos 文件下载，逻辑放到 datasourceX 中来实现，此处设置 kerberos 文件配置的绝对路径
        Map<String, Object> kerberosConfigClone = new HashMap<>(kerberosConfig);
        kerberosConfigClone.put(KERBEROS_REMOTE_PATH, buildSftpPath(configDTO.getSftpConf(), kerberosDir));
        kerberosConfigClone.put(SFTP_CONF, configDTO.getSftpConf());
        configDTO.setKerberosConfig(kerberosConfigClone);
    }

    /**
     * 从数据源中心获取数据源信息
     *
     * @param datasourceId 数据源中心数据源id
     * @return 数据源信息
     */
    private DevelopDataSource getServiceInfoByDtCenterId(Long datasourceId) {
        return datasourceService.getOne(datasourceId);
    }

    /**
     * 拓展配置 ssl 配置信息等
     *
     * @param sourceInfo 数据源信息
     */
    private void expandConfigPrepare(ConfigDTO configDTO, DevelopDataSource sourceInfo) {
        JSONObject dataJson = JSONObject.parseObject(sourceInfo.getDataJson());
        Map<String, Object> config = new HashMap<>();
        configDTO.setExpendConfig(config);
        if (DataSourceType.ICEBERG.getVal().equals(sourceInfo.getType())) {
            // iceberg 处理 core-site.xml、hdfs-site.xml
            String confDir = dataJson.getString(CONF_DIR);
            if (StringUtils.isBlank(confDir)) {
                throw new DtCenterDefException("iceberg 数据源 confDir 不能为空");
            }
            config.put(CONF_DIR, buildSftpPath(configDTO.getSftpConf(), confDir));
        } else if (DataSourceType.HIVE3_CDP.getVal().equals(sourceInfo.getType())
                || DataSourceType.HIVE.getVal().equals(sourceInfo.getType())
                || DataSourceType.HIVE1X.getVal().equals(sourceInfo.getType())
                || DataSourceType.HIVE3X.getVal().equals(sourceInfo.getType())) {
            // hive ssl 认证
            JSONObject sslConfig = dataJson.getJSONObject(SSL_CONFIG);
            if (MapUtils.isEmpty(sslConfig)) {
                return;
            }
            SSLConfig hiveSslConfig = SSLConfig.builder()
                    .remoteSSLDir(buildSftpPath(configDTO.getSftpConf(), sslConfig.getString(KEY_PATH)))
                    .sslClientConf(sslConfig.getString(SSL_CLIENT_CONF))
                    .sslFileTimestamp((Timestamp) sslConfig.getTimestamp(SSL_FILE_TIMESTAMP)).build();
            configDTO.setSslConfig(hiveSslConfig);
        } else if (DataSourceType.ES7.getVal().equals(sourceInfo.getType())) {
            // es7 ssl 认证
            String sftpDir = dataJson.getString(KEY_PATH);
            if (StringUtils.isEmpty(sftpDir)) {
                return;
            }
            SSLConfig sslConfig = SSLConfig.builder()
                    .remoteSSLDir(buildSftpPath(configDTO.getSftpConf(), sftpDir)).build();
            configDTO.setSslConfig(sslConfig);
        } else if (DataSourceType.TRINO.getVal().equals(sourceInfo.getType())) {
            JSONObject sslJson = dataJson.getJSONObject(SSL_CONFIG);
            if (Objects.isNull(sslJson) || sslJson.isEmpty()) {
                return;
            }
            SSLConfig sslConfig = SSLConfig.builder()
                    .sslFileTimestamp((Timestamp) sslJson.getTimestamp(SSL_FILE_TIMESTAMP))
                    .remoteSSLDir(sslJson.getString(REMOTE_SSL_DIR))
                    .sslClientConf(sslJson.getString(SSL_CLIENT_CONF))
                    .build();
            configDTO.setSslConfig(sslConfig);
        } else if (DataSourceType.Confluent5.getVal().equals(sourceInfo.getType())) {
            // 处理 confluent5 ssl
            Map<String, Object> otherConfig = Maps.newHashMap(dataJson);
            SSLConfig sslConfig = SSLConfig.builder()
                    .otherConfig(otherConfig).build();
            configDTO.setSslConfig(sslConfig);
        }
    }

    /**
     * 构建 sftp 绝对路径
     *
     * @param sftpConf sftp 配置
     * @param sftpDir  sftp 文件夹名称
     * @return sftp 绝对路径
     */
    public static String buildSftpPath(Map<String, String> sftpConf, String sftpDir) {
        return MapUtils.getString(sftpConf, PATH) + SEPARATOR + sftpDir;
    }
}
