package com.dtstack.batch.service.datasource.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.DataSourceTypeEnum;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.PubSvcDefineException;
import com.dtstack.batch.utils.DataSourceUtils;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.kerberos.KerberosConfigVerify;
import com.dtstack.dtcenter.common.sftp.SFTPHandler;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IKerberos;
import com.dtstack.dtcenter.loader.kerberos.HadoopConfTool;
import com.dtstack.engine.common.constrant.FormNames;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.po.DsInfoBO;
import com.dtstack.engine.master.impl.ClusterService;
import com.jcraft.jsch.SftpException;
import dt.insight.plat.lang.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;


/**
 * Kerberos 服务类
 * @description:
 * @author: liuxx
 * @date: 2021/3/19
 */
@Slf4j
@Service
public class KerberosService {

    private static final String DS_CENTER = "DsCenter";

    private static final String SFTP_CONF = "sftpConf";


    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;


    /**
     * 上传本地配置目录到sftp
     * @param configMap
     * @param srcDir
     * @param dataSourceKey
     */
    public static void uploadDirFinal(Map<String, String> configMap, String srcDir, String dataSourceKey) {
        SFTPHandler handler = SFTPHandler.getInstance(configMap);
        String dstDir = configMap.get("path");
        String dstDirPath = getSftpPath(configMap, dataSourceKey);
        try {
            KerberosConfigVerify.uploadLockFile(srcDir, dstDirPath, handler);
            handler.uploadDir(dstDir, srcDir);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            handler.close();
        }
    }

    /**
     * 设置SFTP路径
     * @param configMap
     * @param dataSourceKey
     * @return
     */
    private static String getSftpPath(Map<String, String> configMap, String dataSourceKey) {
        String dstDir = configMap.get("path");
        return dstDir + File.separator + dataSourceKey;
    }

    /**
     * 设置 Kerberos 临时文件目录
     * @param userId
     * @return
     */
    public String getTempLocalKerberosConf(Long userId, Long projectId) {
        return getLocalKerberosPath(null) + File.separator + "USER_" + userId.toString();
    }


    /**
     * 获取特定数据源 Kerberos 配置文件地址
     * @param sourceId
     * @return
     */
    public String getLocalKerberosPath(Long sourceId) {
        String kerberosPath = environmentContext.getKerberosLocalPath();
        String key = getSourceKey(sourceId, null);
        return kerberosPath + File.separator + key;
    }


    /**
     * 数据源地址规则
     * @param sourceId
     * @param prefix
     * @return
     */
    public String getSourceKey(Long sourceId, String prefix) {
        prefix = StringUtils.isBlank(prefix) ? DS_CENTER :
                StringUtils.toRootUpperCase(prefix);
        return prefix + "_" + Optional.ofNullable(sourceId).orElse(0L);
    }

    /**
     * ssl证书地址规则
     * @param sourceId
     * @param prefix
     * @return
     */
    public String getSSLKey(Long sourceId, String prefix) {
        return String.format("%s%s","ssl_",getSourceKey(sourceId,prefix));
    }

    /**
     * 从SFTP上下载特定数据源的ssl信息
     * @param sourceId
     * @param dataJson
     * @param dtuicTenantId
     * @throws SftpException
     */
    public void downloadSSLFromSftp(Integer isMeta, Long sourceId, JSONObject dataJson, String localSSLConf, Long dtuicTenantId) throws SftpException {
        // 需要读取配置文件
        Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
        String sslPath = getSSLKey(sourceId,null);
        KerberosConfigVerify.downloadKerberosFromSftp(sslPath, localSSLConf, sftpMap, null);
    }

    /**
     * 从SFTP上下载特定数据源的信息
     * @param sourceId
     * @param dataJson
     * @param localKerberosConf
     * @param dtuicTenantId
     * @throws SftpException
     */
    public void downloadKerberosFromSftp(Integer isMeta, Long sourceId, JSONObject dataJson, String localKerberosConf, Long dtuicTenantId) throws SftpException {
        // 需要读取配置文件
        Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
        String kerberosDir;
        if (isMeta == 1){
            JSONObject kerberosConfig = dataJson.getJSONObject(FormNames.KERBEROS_CONFIG);
            String remotePath = kerberosConfig.getString("remotePath");
            kerberosDir = remotePath.substring(remotePath.indexOf("CONSOLE"));
        }else{
            kerberosDir = getSourceKey(sourceId, null);
        }
        KerberosConfigVerify.downloadKerberosFromSftp(kerberosDir, localKerberosConf, sftpMap, dataJson.getTimestamp(FormNames.KERBEROS_FILE_TIMESTAMP));
    }

    /**
     * 获取集群SFTP配置信息
     * @param dtuicTenantId
     * @return
     */
    public Map<String, String> getSftpMap(Long dtuicTenantId) {
        Map<String,String> map = new HashMap<>();
        // 解析SFTP配置信息
        // todo 获取集群信息
        JSONObject clusterJson = new JSONObject();
        JSONObject sftpConfig = clusterJson.getJSONObject(SFTP_CONF);
        if (Objects.isNull(sftpConfig)) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_FIND_SFTP);
        } else {
            for (String key : sftpConfig.keySet()) {
                map.put(key, sftpConfig.getString(key));
            }
        }
        return map;
    }
    @Autowired
    private DsTypeService dsTypeService;

    /**
     * 预处理Kerberos配置
     */
    public void prepareKerberosConfig(DsInfoBO dsInfoBO){
        if (dsInfoBO.getKerberosConfig()==null) {
            return;
        }
        try {
            // 获取kerberos本地路径 TODO 数据查询
            String localKerberosConf = getLocalKerberosPath(dsInfoBO.getId());
            downloadKerberosFromSftp(dsInfoBO.getIsMeta(), dsInfoBO.getId(),
                    DataSourceUtils.getDataSourceJson(dsInfoBO.getDataJson()), localKerberosConf, dsInfoBO.getDtuicTenantId());
        } catch (SftpException e) {
            throw new DtCenterDefException(String.format("获取kerberos认证文件失败,Caused by: %s", e.getMessage()), e);
        }
        String localKerberosPath = getLocalKerberosPath(dsInfoBO.getId());
        JSONObject dataJson = dsInfoBO.getData();
        //principal 键
        String principal = dataJson.getString(FormNames.PRINCIPAL);
        Map<String, Object> kerberosConfig = dsInfoBO.getKerberosConfig();
        if (Strings.isNotBlank(principal)) {
            kerberosConfig.put(HadoopConfTool.PRINCIPAL, principal);
        }
        //Hbase master kerberos Principal
        String hbaseMasterPrincipal = dataJson.getString(FormNames.HBASE_MASTER_PRINCIPAL);
        if (Strings.isNotBlank(hbaseMasterPrincipal)) {
            kerberosConfig.put(HadoopConfTool.HBASE_MASTER_PRINCIPAL, hbaseMasterPrincipal);
        }
        //Hbase region kerberos Principal
        String hbasePrincipal = dataJson.getString(FormNames.HBASE_REGION_PRINCIPAL);
        if (Strings.isNotBlank(hbasePrincipal)) {
            kerberosConfig.put(HadoopConfTool.HBASE_REGION_PRINCIPAL, hbasePrincipal);
        }
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dsInfoBO.getDataType(), dsInfoBO.getDataVersion());
        IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
        kerberos.prepareKerberosForConnect(kerberosConfig, localKerberosPath);
        dsInfoBO.setKerberosConfig(kerberosConfig);
    }

}
