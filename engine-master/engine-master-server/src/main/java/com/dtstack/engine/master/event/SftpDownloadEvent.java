package com.dtstack.engine.master.event;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AdapterEventMonitor;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.domain.ComponentConfig;
import com.dtstack.engine.domain.ScheduleDict;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.sftp.SftpConfig;
import com.dtstack.engine.pluginapi.sftp.SftpFileManage;
import com.dtstack.engine.master.utils.ComponentConfigUtils;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.impl.ComponentConfigService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2021/1/20 3:49 下午
 * @Email:dazhi@dtstack.com
 * @Description: 下载sftp的jar
 */
@Component
@ConditionalOnProperty(name = "console.sftp.open",havingValue = "true",matchIfMissing = true)
public class SftpDownloadEvent extends AdapterEventMonitor implements InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Long componentTemplateId=-9999L;

    private final Cache<String, String> cacheSftpJar = CacheBuilder.newBuilder()
            .maximumSize(1000L).initialCapacity(1000).expireAfterAccess(5, TimeUnit.MINUTES).build();

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private ScheduleDictDao scheduleDictDao;

    @Autowired
    private ComponentConfigService componentConfigService;

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
                    LOGGER.error(e.getMessage());
                    return;
                }
            }

            if (StringUtils.isBlank(sftpPath) || StringUtils.isBlank(destPath)) {
                return;
            }

            LOGGER.info("sftpPath:{} and destPath {}",sftpPath,destPath);
            String ifPresent = cacheSftpJar.getIfPresent(jarPath);
            if (StringUtils.isBlank(ifPresent)) {
                SftpConfig sftpConfig = getSftpConfig();

                if (sftpConfig != null) {
                    try {
                        SftpFileManage sftpManager = SftpFileManage.getSftpManager(sftpConfig);
                        if (StringUtils.isNotBlank(sftpPath) && StringUtils.isNotBlank(destPath)) {
                            sftpManager.downloadFile(sftpPath, destPath);
                            cacheSftpJar.put(jarPath, System.currentTimeMillis() + "");
                        } else {
                            LOGGER.error("sftpPath: and destPath is null so downloadFile error");
                        }
                    } catch (Exception e) {
                        LOGGER.error("sftp download failed:", e);
                    }
                } else {
                    LOGGER.error("not configured sftp");
                }
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtil.getErrorMessage(e));
        }
    }

    public String uploadFileToSftp(MultipartFile file, String filePath, String destPath, String dbPath) {
        SftpConfig sftpConfig = getSftpConfig();
        if (sftpConfig != null) {
            try {
                String remoteDir = sftpConfig.getPath() + File.separator + filePath;
                SftpFileManage sftpManager = SftpFileManage.getSftpManager(sftpConfig);
                sftpManager.uploadFile(remoteDir ,destPath);

                dbPath = dbPath + GlobalConst.PATH_CUT + remoteDir + File.separator + file.getOriginalFilename();
                setCache(dbPath);
            } catch (Exception e) {
                LOGGER.error("sftp upload failed:",e);
            }
        }
        return dbPath;
    }

    private void setCache(String path) {
        if (StringUtils.isNotBlank(path)) {
            cacheSftpJar.put("path",System.currentTimeMillis() + "");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(Constant.COMPONENT_TEMPLATE_ID, Boolean.FALSE);

        if (CollectionUtils.isEmpty(componentConfigs)) {
            initSftp();
        }
    }

    private void initSftp() {
        // 加载一下sftp模板
        try {
            com.dtstack.engine.domain.Component component = componentDao.getByClusterIdAndComponentType(-1L, EComponentType.SFTP.getTypeCode(), null,null);

            if (component != null) {
                List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(component.getId(), Boolean.FALSE);

                for (ComponentConfig componentConfig : componentConfigs) {
                    componentConfig.setComponentId(Constant.COMPONENT_TEMPLATE_ID);
                    componentConfig.setClusterId(Constant.COMPONENT_TEMPLATE_ID);
                }

                componentConfigDao.insertBatch(componentConfigs);
            } else {
                // 加载默认的模板
                String pluginName = EComponentType.convertPluginNameByComponent(EComponentType.SFTP);
                ScheduleDict typeNameMapping = scheduleDictDao.getByNameValue(DictType.TYPENAME_MAPPING.type, pluginName.trim(), null,null);
                if (null != typeNameMapping) {
                    List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(Long.parseLong(typeNameMapping.getDictValue()), true);

                    for (ComponentConfig componentConfig : componentConfigs) {
                        componentConfig.setComponentId(Constant.COMPONENT_TEMPLATE_ID);
                        componentConfig.setClusterId(Constant.COMPONENT_TEMPLATE_ID);
                    }

                    componentConfigDao.insertBatch(componentConfigs);
                }
            }
        } catch (Exception e) {
            LOGGER.error("",e);
        }
    }

    public SftpConfig getSftpConfig() {
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(Constant.COMPONENT_TEMPLATE_ID, Boolean.FALSE);

        if (CollectionUtils.isNotEmpty(componentConfigs)) {
            Map<String, Object> configToMap = ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
            return JSONObject.parseObject(JSONObject.toJSONString(configToMap), SftpConfig.class);
        }
        return null;
    }

    public interface Constant {

        Long COMPONENT_TEMPLATE_ID = -9999L;
    }
}
