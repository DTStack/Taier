package com.dtstack.engine.alert.factory;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.dao.ComponentDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Date: 2020/6/17
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class ChannelCache {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(1000L).initialCapacity(1000).expireAfterAccess(10, TimeUnit.MINUTES).build();

    private static final Cache<String, String> cacheSftpJar = CacheBuilder.newBuilder()
            .maximumSize(1000L).initialCapacity(1000).expireAfterAccess(5, TimeUnit.MINUTES).build();

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Value("${user.dir}")
    private String uploadPath;

    public Object getChannelInstance(String jarPath, String className) throws Exception {
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

        if (environmentContext.getOpenConsoleSftp() && StringUtils.isNotBlank(sftpPath)) {
            String ifPresent = cacheSftpJar.getIfPresent(jarPath);
            if (StringUtils.isBlank(ifPresent)) {
                com.dtstack.engine.api.domain.Component sftpComponent = componentDao.getByClusterIdAndComponentType(-1L, 10);
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
            }
        }

        if (jarPath.contains("/normal")) {
            String key = jarPath + className;
            String finalDestPath = destPath;

            return cache.get(key, () -> {
                JarClassLoader loader = new JarClassLoader();
                return loader.getInstance(finalDestPath, className);
            });
        }
        //tmp路径下的插件 不走缓存
        JarClassLoader loader = new JarClassLoader();
        return loader.getInstance(jarPath, className);
    }
}
