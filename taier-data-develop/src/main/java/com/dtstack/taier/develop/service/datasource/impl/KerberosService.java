/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.service.datasource.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.PubSvcDefineException;
import com.dtstack.taier.common.kerberos.KerberosConfigVerify;
import com.dtstack.taier.common.sftp.SFTPHandler;
import com.dtstack.taier.scheduler.service.ClusterService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Kerberos 服务类
 *
 * @author liu
 * date 2021/3/19
 */
@Service
public class KerberosService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KerberosService.class);

    private static final String DS_CENTER = "DsCenter";

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    /**
     * 上传本地配置目录到sftp
     *
     * @param configMap     sftp 配置
     * @param srcDir        本地文件路径
     * @param dataSourceKey sourceKey
     */
    public static void uploadDirFinal(Map<String, String> configMap, String srcDir, String dataSourceKey) {
        SFTPHandler handler = SFTPHandler.getInstance(configMap);
        String dstDir = configMap.get("path");
        String dstDirPath = getSftpPath(configMap, dataSourceKey);
        try {
            KerberosConfigVerify.uploadLockFile(srcDir, dstDirPath, handler);
            handler.uploadDir(dstDir, srcDir);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            handler.close();
        }
    }

    /**
     * 设置SFTP路径
     *
     * @param configMap     sftp 配置
     * @param dataSourceKey sourceKey
     * @return sftp 绝对路径
     */
    public static String getSftpPath(Map<String, String> configMap, String dataSourceKey) {
        String dstDir = configMap.get("path");
        return dstDir + File.separator + dataSourceKey;
    }

    /**
     * 设置 Kerberos 临时文件目录
     *
     * @param userId 用户 id
     * @return kerberos 文件临时存放目录
     */
    public String getTempLocalKerberosConf(Long userId) {
        return getLocalKerberosPath(null) + File.separator + "USER_" + userId.toString();
    }

    /**
     * 获取特定数据源 Kerberos 配置文件地址
     *
     * @param sourceId 本地 kerberos 临时目录
     * @return kerberos 目录
     */
    public String getLocalKerberosPath(Long sourceId) {
        String kerberosPath = environmentContext.getTempDir();
        String key = getSourceKey(sourceId, null);
        return kerberosPath + File.separator + key;
    }

    /**
     * 数据源地址规则
     *
     * @param sourceId 数据源 id
     * @param prefix   sourceKey 前缀
     * @return 数据源 sftp 配置路径
     */
    public String getSourceKey(Long sourceId, String prefix) {
        prefix = StringUtils.isBlank(prefix) ? DS_CENTER :
                StringUtils.toRootUpperCase(prefix);
        return prefix + "_" + Optional.ofNullable(sourceId).orElse(0L);
    }

    /**
     * 获取集群SFTP配置信息
     *
     * @param tenantId 租户 id
     * @return sftp 配置
     */
    public Map<String, String> getSftpMap(Long tenantId) {
        Map<String, String> map = new HashMap<>();
        // 解析SFTP配置信息
        JSONObject sftpConfig = clusterService.getConfigByKey(tenantId, EComponentType.SFTP.getConfName(), null);
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
