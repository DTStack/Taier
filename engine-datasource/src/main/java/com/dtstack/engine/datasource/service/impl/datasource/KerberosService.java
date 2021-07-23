package com.dtstack.engine.datasource.service.impl.datasource;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.kerberos.KerberosConfigVerify;
import com.dtstack.dtcenter.common.sftp.SFTPHandler;
import com.dtstack.engine.datasource.common.env.EnvironmentContext;
import com.dtstack.engine.datasource.common.constant.FormNames;
import com.dtstack.engine.datasource.common.exception.ErrorCode;
import com.dtstack.engine.datasource.common.exception.PubSvcDefineException;
import com.dtstack.engine.datasource.dao.po.datasource.DsImportRef;
import com.dtstack.sdk.core.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Slf4j
@Service
public class KerberosService {

    private static final String DS_CENTER = "DsCenter";

    private static final String SFTP_CONF = "sftpConf";


    @Autowired
    private EnvironmentContext environmentContext;

//    @Autowired
//    private ClusterService clusterService;

    @Autowired
    private DsImportRefService importRefService;

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
     * 从SFTP上下载特定数据源的信息
     * @param sourceId
     * @param dataJson
     * @param localKerberosConf
     * @param dtuicTenantId
     * @throws Exception
     */
    public void downloadKerberosFromSftp(Integer isMeta, Long sourceId, JSONObject dataJson, String localKerberosConf, Long dtuicTenantId) throws Exception {
        // 需要读取配置文件
        Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
        List<DsImportRef> dsImportRefs = importRefService.getImportDsByInfoId(sourceId);
        if(dsImportRefs.size() > 1){
            throw new PubSvcDefineException(String.format("dirty data exists, dataInfoId: %s", sourceId));
        }
        DsImportRef dsImportRef = dsImportRefs.size() == 0 ? null : dsImportRefs.get(0);
        String kerberosDir;
        if(Objects.nonNull(dsImportRef)){
            if(isMeta == 1){
                JSONObject kerberosConfig = dataJson.getJSONObject(FormNames.KERBEROS_CONFIG);
                String remotePath = kerberosConfig.getString("remotePath");
                kerberosDir = remotePath.substring(remotePath.indexOf("CONSOLE"));
            } else{
                kerberosDir = getSourceKey(dsImportRef.getOldDataInfoId(), AppType.getValue(dsImportRef.getAppType()).name());
            }
        } else{
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

//        ApiResponse<String> apiResponse = clusterService.clusterInfo(dtuicTenantId);
//        if (apiResponse == null || apiResponse.getData() == null) {
//            throw new PubSvcDefineException(ErrorCode.HTTP_CONSOLE_ERROR);
//        }
//        apiResponse.getData();
        String sftpData = "";//TODO

        // 解析SFTP配置信息
        Map<String, String> map = new HashMap<>();
        JSONObject clusterJson = JSONObject.parseObject(sftpData);
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

}
