package com.dtstack.engine.master.event;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AdapterEventMonitor;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.master.impl.ComponentService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2021/1/20 3:49 下午
 * @Email:dazhi@dtstack.com
 * @Description: 下载sftp的jar
 */
@Component
@ConditionalOnProperty(name = "console.sftp.open",havingValue = "true",matchIfMissing = true)
public class SftpDownloadEvent extends AdapterEventMonitor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Cache<String, String> cacheSftpJar = CacheBuilder.newBuilder()
            .maximumSize(1000L).initialCapacity(1000).expireAfterAccess(5, TimeUnit.MINUTES).build();

    @Autowired
    private ComponentService componentService;

    @Override
    public void leaveQueueAndSenderBeforeEvent(AlterContext alterContext) {
        try {
            String jarPath = alterContext.getJarPath();
            String destPath = jarPath;
            String sftpPath = null;

            if (jarPath.contains(GlobalConst.PATH_CUT)) {
                try {
                    destPath = jarPath.substring(0, jarPath.indexOf(GlobalConst.PATH_CUT));
                    sftpPath = jarPath.substring(jarPath.indexOf(GlobalConst.PATH_CUT)+GlobalConst.PATH_CUT.length());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            String ifPresent = cacheSftpJar.getIfPresent(jarPath);
            if (StringUtils.isBlank(ifPresent)) {
                com.dtstack.engine.api.domain.Component sftpComponent = componentService.getComponentByClusterId(-1L, 10);
                if (sftpComponent != null) {
                    SftpConfig sftpConfig = JSONObject.parseObject(sftpComponent.getComponentConfig(), SftpConfig.class);
                    if (sftpConfig != null) {
                        try {
                            SftpFileManage sftpManager = SftpFileManage.getSftpManager(sftpConfig);
                            sftpManager.downloadFile(sftpPath, destPath);
                            cacheSftpJar.put(jarPath, System.currentTimeMillis() + "");
                        } catch (Exception e) {
                            logger.error("下载sftp失败:", e);
                        }
                    }
                } else {
                    logger.error("未配置sftp:");
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getErrorMessage(e));
        }
    }

    public String uploadFileToSftp(MultipartFile file, String filePath, String destPath, String dbPath) {
        com.dtstack.engine.api.domain.Component sftpComponent = componentService.getComponentByClusterId(-1L, EComponentType.SFTP.getTypeCode());
        if (sftpComponent != null) {
            SftpConfig sftpConfig = JSONObject.parseObject(sftpComponent.getComponentConfig(), SftpConfig.class);
            if (sftpConfig != null) {
                try {
                    String remoteDir = sftpConfig.getPath() + File.separator + filePath;
                    SftpFileManage sftpManager = SftpFileManage.getSftpManager(sftpConfig);
                    sftpManager.uploadFile(remoteDir ,destPath);

                    dbPath = dbPath + GlobalConst.PATH_CUT + remoteDir + File.separator + file.getOriginalFilename();
                    setCache(dbPath);
                } catch (Exception e) {
                    logger.error("上传sftp失败:",e);
                }
            }
        }
        return dbPath;
    }

    private void setCache(String path) {
        if (StringUtils.isNotBlank(path)) {
            cacheSftpJar.put("path",System.currentTimeMillis() + "");
        }
    }

}
