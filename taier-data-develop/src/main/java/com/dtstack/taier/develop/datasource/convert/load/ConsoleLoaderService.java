package com.dtstack.taier.develop.datasource.convert.load;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.datasource.api.dto.SSLConfig;
import com.dtstack.taier.datasource.api.dto.source.AbstractSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RestfulSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.develop.datasource.convert.Consistent;
import com.dtstack.taier.develop.datasource.convert.dto.PluginInfoDTO;
import com.dtstack.taier.develop.datasource.convert.dto.PluginInfoUtils;
import com.dtstack.taier.develop.datasource.convert.enums.EComponent2Others;
import com.dtstack.taier.develop.datasource.convert.enums.Engine2DTOService;
import com.dtstack.taier.develop.datasource.convert.kerberos.KerberosConfig;
import com.dtstack.taier.develop.datasource.convert.kerberos.KerberosConfigUtil;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.scheduler.service.ClusterService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ：nanqi
 * date：Created in 下午5:15 2021/7/29
 * company: www.dtstack.com
 */
@Service
@SuppressWarnings("unchecked")
public class ConsoleLoaderService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLoaderService.class);

    static {
        OBJECT_MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 获取集群信息
     */
    private static final String ERROR_MSG_CLUSTER_INFO = "集群ID:%s，获取组件标识:%s，信息为空";

    @Autowired
    private ClusterService clusterService;

    /**
     * 构建 datasourceX 需要的 RestfulDTO
     *
     * @param dtTenantId 租户 id
     * @param url        url
     * @return restful sourceDTO
     */
    public ISourceDTO buildYarnRestfulDTO(Long dtTenantId, String url) {
        Map<String, Object> yarnConfig = getYarnInfo(dtTenantId);
        JSONObject yarnJson = new JSONObject(yarnConfig);
        JSONObject sslJson = yarnJson.getJSONObject("sslClient");
        Map<String, String> sftp = clusterService.getSftp(dtTenantId);
        RestfulSourceDTO sourceDTO = RestfulSourceDTO.builder()
                .url(url)
                .sftpConf(sftp)
                .kerberosConfig(getKerberosConfig(yarnConfig)).build();
        if (Objects.nonNull(sslJson)) {
            SSLConfig sslConfig = SSLConfig.builder()
                    .sslFileTimestamp((Timestamp) sslJson.getTimestamp("sslFileTimestamp"))
                    .remoteSSLDir(SourceLoaderService.buildSftpPath(sftp, sslJson.getString("remoteSSLDir")))
                    .sslClientConf(sslJson.getString("sslClientConf")).build();
            sourceDTO.setSslConfig(sslConfig);
        }
        return sourceDTO;
    }

    /**
     * 获取HDFS数据源信息
     *
     * @param uicTenantId uic 租户 id
     * @return hdfs sourceDTO
     */
    public ISourceDTO getHdfsSource(Long uicTenantId) {
        Map<String, Object> hdfsConfig = getHdfsInfo(uicTenantId);
        return HdfsSourceDTO.builder()
                .defaultFS(getDefaultFs(hdfsConfig))
                .kerberosConfig(getKerberosConfig(hdfsConfig))
                .config(JSONObject.toJSONString(hdfsConfig)).build();
    }


    /**
     * 获取集群组件ISourceDTO信息
     *
     * @param uicTenantId   uic 租户 id
     * @param uicUserId     uic 用户 id
     * @param dbName        db 名称
     * @param componentType 组件类型
     * @return 对应的 sourceDTO
     */
    public ISourceDTO getJdbcSourceDTO(Long uicTenantId, Long uicUserId, String dbName, EComponentType componentType) {
        // 兼容 HDFS 配置
        if (Objects.equals(EComponentType.HDFS.getTypeCode(), componentType.getTypeCode())) {
            return getHdfsSource(uicTenantId);
        }
        PluginInfoDTO pluginInfo = getJdbcInfo(uicTenantId, uicUserId, componentType, null);
        DataSourceType dataSourceType = EComponent2Others.getDataSourceType(componentType.getTypeCode(), pluginInfo.getVersion());
        Engine2DTOService engine2DTOEnum = Engine2DTOService.getSourceDTOType(dataSourceType.getVal());
        Map<String, String> sftp = clusterService.getSftp(uicTenantId);
        ISourceDTO sourceDTO = engine2DTOEnum.getSourceDTO(pluginInfo, uicTenantId, dbName);
        ((AbstractSourceDTO) sourceDTO).setSftpConf(sftp);
        return sourceDTO;
    }

    /**
     * 根据EJob获取组件数据源信息
     *
     * @param uicTenantId uic 租户 id
     * @param uicUserId   uic 用户 id
     * @param dbName      db 名称
     * @param jobType     任务类型
     * @return 对应 sourceDTO
     */
    public ISourceDTO getJdbcSourceDTO(Long uicTenantId, Long uicUserId, String dbName, EJobType jobType) {
        return getJdbcSourceDTO(uicTenantId, uicUserId, dbName, EComponent2Others.getComponentTypeByEJob(jobType.getType()));
    }

    /**
     * 根据数据源类型获取组件数据源信息
     *
     * @param uicTenantId    uic 租户 id
     * @param uicUserId      uic 用户 id
     * @param dbName         db 名称
     * @param dataSourceType 数据源类型
     * @return 对应呢 sourceDTO
     */
    public ISourceDTO getJdbcSourceDTO(Long uicTenantId, Long uicUserId, String dbName, DataSourceType dataSourceType) {
        return getJdbcSourceDTO(uicTenantId, uicUserId, dbName, EComponent2Others.getComponentTypeBySourceType(dataSourceType.getVal()));
    }

    /**
     * 获取集群组件 JDBC 信息
     *
     * @param uicTenantId      uic 租户 id
     * @param uicUserId        uic 用户 id
     * @param componentType    组件类型
     * @param componentVersion 版本
     * @return 组件信息
     */
    public PluginInfoDTO getJdbcInfo(Long uicTenantId, Long uicUserId, EComponentType componentType, String componentVersion) {
        PluginInfoDTO pluginInfo = getJdbcInfoWithoutKerberos(uicTenantId, uicUserId, componentType, componentVersion);
        // 处理存储类型
        if (pluginInfo.getStoreType() != null && Objects.equals(pluginInfo.getStoreType(), EComponentType.HDFS.getTypeCode())) {
            Map<String, Object> hadoopConfig = getHdfsInfoWithoutKerberos(uicTenantId);
            pluginInfo.setDefaultFs(getDefaultFs(hadoopConfig));
            pluginInfo.setHadoopConfig(JSONObject.toJSONString(hadoopConfig));
        }
        pluginInfo.setKerberosConfig(buildKerberosConfig(uicTenantId, pluginInfo).getKerberosConfig());
        return pluginInfo;
    }

    private <T> T buildKerberosConfig(Long uicTenantId, T data) {
        // 构建 kerberos 配置
        JSONObject dataJson = getJsonObject(data);

        // sftp conf
        Map<String, String> sftp = clusterService.getSftp(uicTenantId);

        if (MapUtils.isEmpty(dataJson) || Objects.isNull(dataJson.getJSONObject(Consistent.KERBEROS_CONFIG_KEY))) {
            return data;
        }
        JSONObject kerberosConfigJson = dataJson.getJSONObject(Consistent.KERBEROS_CONFIG_KEY);
        // to Java Bean
        KerberosConfig kerberosConfig = kerberosConfigJson.toJavaObject(KerberosConfig.class);

        if (Objects.nonNull(kerberosConfig)) {
            AssertUtils.notNull(kerberosConfig.getPrincipal(), "组件Kerberos Principal不能为空");
            AssertUtils.notNull(kerberosConfig.getRemotePath(), "组件Kerberos RemotePath不能为空");
            // kerberos 文件夹在 sftp 上的绝对路径
            String remotePath = kerberosConfig.getRemotePath();

            // 文件名称集合
            List<String> fileNames = KerberosConfigUtil.listFileNameFromSftp(remotePath, sftp);

            String principalFile = kerberosConfig.getPrincipalFile();
            // 判断控制台是否有返回 principalFile，规则是顾虑隐藏文件同时获取第一个以 keytab 为后缀的文件
            if (StringUtils.isBlank(kerberosConfig.getPrincipalFile())) {
                principalFile = fileNames.stream().filter(fileName -> !fileName.startsWith(".") && fileName.endsWith("keytab")).findFirst().orElseThrow(() -> new DtCenterDefException("keytab文件不存在"));
            }

            String krb5Conf = fileNames.stream().filter(fileName -> !fileName.startsWith(".") && fileName.equalsIgnoreCase("krb5.conf")).findFirst().orElse(null);
            if (StringUtils.isNotBlank(krb5Conf)) {
                // 设置 krb5.conf
                kerberosConfigJson.put("java.security.krb5.conf", krb5Conf);
            }
            kerberosConfigJson.put("keytabPath", principalFile);
            kerberosConfigJson.put("principalFile", principalFile);
            kerberosConfigJson.putAll(Optional.ofNullable(kerberosConfigJson.getJSONObject("hdfsConfig")).orElse(new JSONObject()));
            // 设置 datasourceX 需要的配置
            kerberosConfigJson.put(Consistent.SFTP_CONF, sftp);
            kerberosConfigJson.put(Consistent.KERBEROS_REMOTE_PATH, remotePath);
            // 拍平 hdfsConfig
            kerberosConfigJson.remove("hdfsConfig");
        }
        return convertJsonOverBack(data, dataJson);
    }

    /**
     * 类型转换
     *
     * @param data    原始数据
     * @param dataMap json
     * @param <T>     范型
     * @return 原始类型数据
     */
    @SuppressWarnings("unchecked")
    private <T> T convertJsonOverBack(T data, JSONObject dataMap) {
        if (data instanceof String) {
            data = (T) dataMap.toString();
        } else {
            try {
                data = OBJECT_MAPPER.readValue(dataMap.toString(), (Class<T>) data.getClass());
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
        return data;
    }

    /**
     * 获取 hdfs default.fs
     *
     * @param hdfsConfig hdfs 配置
     * @return default.fs
     */
    private String getDefaultFs(Map<String, Object> hdfsConfig) {
        return MapUtils.getString(hdfsConfig, Consistent.FS_DEFAULT_FS, "");
    }

    /**
     * 获取 hdfs kerberos 配置
     *
     * @param hdfsConfig hdfs 配置
     * @return kerberos 配置
     */
    private Map<String, Object> getKerberosConfig(Map<String, Object> hdfsConfig) {
        Object kerberosConfig = hdfsConfig.get(Consistent.KERBEROS_CONFIG_KEY);
        return getJsonObject(kerberosConfig);
    }

    /**
     * 获取集群组件 JDBC 信息
     *
     * @param uicTenantId      uic 租户 id
     * @param uicUserId        uic 用户 id
     * @param componentType    组件类型
     * @param componentVersion 组件版本
     * @return 对应组件信息
     */
    public PluginInfoDTO getJdbcInfoWithoutKerberos(@NotNull Long uicTenantId, Long uicUserId, @NotNull EComponentType componentType, String componentVersion) {
        // 如果用户不为空，则通过account获取信息
        PluginInfoDTO pluginInfoDTO = clusterService.getComponentByTenantId(uicTenantId, componentType.getTypeCode(), true, PluginInfoDTO.class, componentVersion);
        return PluginInfoUtils.setNullPropertiesToDefaultValue(pluginInfoDTO);
    }

    /**
     * 获取 hdfs 信息，包含Kerberos
     *
     * @param uicTenantId uic 租户 id
     * @return hdfs 配置
     */
    public Map<String, Object> getHdfsInfo(@NotNull Long uicTenantId) {
        Map<String, Object> hdfsInfoWithoutKerberos = getHdfsInfoWithoutKerberos(uicTenantId);
        return buildKerberosConfig(uicTenantId, hdfsInfoWithoutKerberos);
    }

    /**
     * 获取 hdfs 信息
     *
     * @param uicTenantId uic 租户 id
     * @return hdfs 配置
     */
    private Map<String, Object> getHdfsInfoWithoutKerberos(@NotNull Long uicTenantId) {
        // 如果用户不为空，则通过account获取信息
        return clusterService.getComponentByTenantId(uicTenantId, EComponentType.HDFS.getTypeCode(), true, Map.class, null);
    }

    /**
     * 获取 yarn 信息
     *
     * @param uicTenantId uic 租户 id
     * @return hdfs 配置
     */
    private Map<String, Object> getYarnInfo(@NotNull Long uicTenantId) {
        return clusterService.getComponentByTenantId(uicTenantId, EComponentType.YARN.getTypeCode(), true, Map.class, null);
    }

    private <T> JSONObject getJsonObject(T data) {
        JSONObject dataMap = new JSONObject();
        if (data == null) {
            return dataMap;
        }
        if (data instanceof String) {
            dataMap = JSONObject.parseObject((String) data);
        } else {
            dataMap = (JSONObject) JSONObject.toJSON(data);
        }
        return dataMap;
    }
}
