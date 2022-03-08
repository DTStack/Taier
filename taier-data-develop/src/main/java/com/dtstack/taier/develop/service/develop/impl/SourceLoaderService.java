package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IKerberos;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.enums.develop.SourceDTOType;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.utils.KerberosConfigUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhiChen
 * @date 2022/3/4 16:29
 */
@Service
public class SourceLoaderService {
    /**
     * kerberos 配置 key
     */
    String KERBEROS_CONFIG_KEY = "kerberosConfig";

    /**
     * kerberos 配置文件相对路径 key
     */
    String KERBEROS_PATH_KEY = "kerberosDir";

    /**
     * ssl 认证文件路径
     */
    String CONF_LOCAL_DIR = "confLocalDir";

    /**
     * ssl 认证文件路径
     */
    String SSL_LOCAL_DIR = "sslLocalDir";

    /**
     * ssl 认证文件路径
     */
    String KEY_PATH = "keyPath";

    /**
     * kerberos 文件时间戳 key
     */
    String KERBEROS_FILE_TIMESTAMP_KEY = "kerberosFileTimestamp";
    @Autowired
    private  DsInfoService dsInfoService;

    @Autowired
    private DatasourceService datasourceService;

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
        DsInfo sourceInfoDTO = getServiceInfoByDtCenterId(datasourceId);
        Map<String, Object> kerberosConfig = kerberosPrepare(sourceInfoDTO);
        Map<String, Object> expandConfig = expandConfigPrepare(sourceInfoDTO);
        return SourceDTOType.getSourceDTO(JSONObject.parseObject(sourceInfoDTO.getDataJson()), Integer.valueOf(sourceInfoDTO.getDataType()), kerberosConfig, schema, expandConfig);
    }

    /**
     * 预处理 kerberos 配置，包括判断是否开启 kerberos、是否需要从 SFTP 重新下载 kerberos 配置、相对路径转换绝对路径等
     *
     * @param sourceInfo 数据源中心数据源信息
     * @return 处理后的 kerberos 配置
     */
    private Map<String, Object> kerberosPrepare(DsInfo sourceInfo) {
        JSONObject dataJson = JSONObject.parseObject(sourceInfo.getDataJson());
        JSONObject kerberosConfig = dataJson.getJSONObject(KERBEROS_CONFIG_KEY);
        if (MapUtils.isEmpty(kerberosConfig)) {
            return Collections.emptyMap();
        }
        String kerberosDir = kerberosConfig.getString(KERBEROS_PATH_KEY);
        if (StringUtils.isEmpty(kerberosDir)) {
            return Collections.emptyMap();
        }
        Map<String, String> sftpMap = datasourceService.getSftpMap(sourceInfo.getTenantId());
        KerberosConfigUtil.downloadFileFromSftp(kerberosDir, KerberosConfigUtil.getLocalKerberosPath(sourceInfo.getId()), sftpMap, dataJson.getTimestamp(KERBEROS_FILE_TIMESTAMP_KEY));
        if (MapUtils.isEmpty(kerberosConfig)) {
            return Collections.emptyMap();
        }
        Map<String, Object> kerberosConfigClone = new HashMap<>(kerberosConfig);
        IKerberos kerberos = ClientCache.getKerberos(Integer.valueOf(sourceInfo.getDataType()));
        // 替换相对路径为绝对路径
        kerberos.prepareKerberosForConnect(kerberosConfigClone, KerberosConfigUtil.getLocalKerberosPath(sourceInfo.getId()));
        return kerberosConfigClone;
    }


    /**
     * 从数据源中心获取数据源信息
     *
     * @param datasourceId 数据源中心数据源id
     * @return 数据源信息
     */
    private DsInfo getServiceInfoByDtCenterId(Long datasourceId) {
        return  dsInfoService.getOneById(datasourceId);
    }

    /**
     * 拓展配置 ssl 配置信息等
     *
     * @param sourceInfo
     * @return
     */
    private Map<String, Object> expandConfigPrepare(DsInfo sourceInfo) {
        JSONObject dataJson = JSONObject.parseObject(sourceInfo.getDataJson());
        String sftpDir = dataJson.getString(KEY_PATH);
        if (StringUtils.isEmpty(sftpDir)) {
            return Collections.emptyMap();
        }
        Map<String, Object> config = new HashMap<>();
        Map<String, String> sftpMap = datasourceService.getSftpMap(sourceInfo.getTenantId());
        KerberosConfigUtil.downloadFileFromSftp(sftpDir, KerberosConfigUtil.getLocalSslDir(sourceInfo.getId()), sftpMap, null);
        config.put(SSL_LOCAL_DIR, KerberosConfigUtil.getLocalSslDir(sourceInfo.getId()));
        return config;
    }
}
