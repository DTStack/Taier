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

package com.dtstack.taier.common.util;

import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author yuebai
 * @date 2020-12-25
 */
public class JavaPolicyUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaPolicyUtils.class);

    public static void checkJavaPolicy() {
        String policyPath = System.getProperty("java.security.policy");
        if (StringUtils.isBlank(policyPath)) {
            LOGGER.error("java.security.policy command is null ");
            throw new RuntimeException("启动参数上请加上java.security.policy 命令");
        }
        File policyFile = new File(policyPath);
        if (!policyFile.exists()) {
            LOGGER.error(String.format("java.security.policy file path [%s] is null ", policyPath));
            throw new RuntimeException(String.format("启动参数上java.security.policy 文件路径 %s 不正确", policyPath));
        }
        try {
            boolean isKrb5ConfRead = false;
            String policyConfig = Xml2JsonUtil.readFile(policyFile);
            if (!StringUtils.isBlank(policyConfig)) {
                String[] split = policyConfig.split(";");
                for (String conf : split) {
                    String trimConf = conf.replace("\n", "").trim();
                    if (trimConf.startsWith("permission") && trimConf.contains("java.security.krb5.conf")) {
                        isKrb5ConfRead = trimConf.contains("read");
                    }
                }
            }
            if (!isKrb5ConfRead) {
                LOGGER.error(String.format("java.security.policy file path [%s] java.security.krb5.conf permission is not read ", policyPath));
                throw new RuntimeException(policyPath + " java.security.policy 不为read");
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtil.getErrorMessage(e));
            throw new RuntimeException(e);
        }
    }

}
