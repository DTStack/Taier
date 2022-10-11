package com.dtstack.taier.develop.datasource.convert.load;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.develop.datasource.convert.Consistent;
import com.dtstack.taier.develop.datasource.convert.kerberos.KerberosConfig;
import com.dtstack.taier.develop.utils.KerberosConfigUtil;
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
    

    @Autowired
    private ClusterService clusterService;
    

    /**
     * 获取HDFS数据源信息
     *
     * @param tenantId uic 租户 id
     * @return hdfs sourceDTO
     */
    public ISourceDTO getHdfsSource(Long tenantId) {
        Map<String, Object> hdfsConfig = getHdfsInfo(tenantId);
        return HdfsSourceDTO.builder()
                .defaultFS(getDefaultFs(hdfsConfig))
                .kerberosConfig(getKerberosConfig(hdfsConfig))
                .config(JSONObject.toJSONString(hdfsConfig)).build();
    }
    

    private <T> T buildKerberosConfig(Long tenantId, T data) {
        // 构建 kerberos 配置
        JSONObject dataJson = getJsonObject(data);

        // sftp conf
        Map<String, String> sftp = clusterService.getSftp(tenantId);

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
     * 获取 hdfs 信息，包含Kerberos
     *
     * @param tenantId uic 租户 id
     * @return hdfs 配置
     */
    public Map<String, Object> getHdfsInfo(@NotNull Long tenantId) {
        Map<String, Object> hdfsInfoWithoutKerberos = getHdfsInfoWithoutKerberos(tenantId);
        return buildKerberosConfig(tenantId, hdfsInfoWithoutKerberos);
    }

    /**
     * 获取 hdfs 信息
     *
     * @param tenantId uic 租户 id
     * @return hdfs 配置
     */
    private Map<String, Object> getHdfsInfoWithoutKerberos(@NotNull Long tenantId) {
        // 如果用户不为空，则通过account获取信息
        return clusterService.getComponentByTenantId(tenantId, EComponentType.HDFS.getTypeCode(), true, Map.class, null);
    }

    /**
     * 获取 yarn 信息
     *
     * @param tenantId uic 租户 id
     * @return hdfs 配置
     */
    private Map<String, Object> getYarnInfo(@NotNull Long tenantId) {
        return clusterService.getComponentByTenantId(tenantId, EComponentType.YARN.getTypeCode(), true, Map.class, null);
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
