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

package com.dtstack.taier.datasource.plugin.hdfs;

import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HadoopConfUtil;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:07 2020/9/2
 * @Description：Yarn 配置信息
 */
@Slf4j
public class YarnConfUtil extends HadoopConfUtil {
    /**
     * 组装 yarn 配置信息
     *
     * @param hdfsConfig hdfs配置信息 String类型
     * @param yarnConfig yarn配置信息 map类型
     * @return YarnConfiguration
     */
    public static YarnConfiguration getYarnConfiguration(String defaultFs, String hdfsConfig, Map<String, Object> yarnConfig, Map<String, Object> kerberosConfig) {
        Configuration yarnConf;
        try {
            Configuration configuration = getHdfsConf(defaultFs, hdfsConfig, null);
            yarnConf = new YarnConfiguration(configuration);
            initYarnConfiguration((YarnConfiguration) yarnConf, yarnConfig);
        } catch (Exception e) {
            throw new SourceException(String.format("Failed to obtain yarn configuration information,%s", e.getMessage()), e);
        }
        return (YarnConfiguration) yarnConf;
    }

    /**
     * 获取 Hdfs 和 yarn 配置的总和
     *
     * @param hdfsConfig
     * @param yarnConfig
     * @return
     */
    public static Configuration getFullConfiguration(String defaultFs, String hdfsConfig, Map<String, Object> yarnConfig, Map<String, Object> kerberosConfig) {
        Configuration hadoopConf = getHdfsConf(defaultFs, hdfsConfig, kerberosConfig);
        YarnConfiguration yarnConfiguration = getYarnConfiguration(defaultFs, hdfsConfig, yarnConfig, kerberosConfig);

        for (Map.Entry<String, String> entry : yarnConfiguration) {
            hadoopConf.set(entry.getKey(), entry.getValue());
        }
        return hadoopConf;
    }

    /**
     * 初始化yarn配置信息
     *
     * @param yarnConfiguration
     * @param map
     */
    private static void initYarnConfiguration(YarnConfiguration yarnConfiguration, Map<String, Object> map) {
        if (MapUtils.isNotEmpty(map)) {
            // 去除该参数, 否则下载日志会报错
            map.remove("dfs.encrypt.data.transfer.cipher.suites");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }

                yarnConfiguration.set(entry.getKey(), entry.getValue().toString());
            }
        }
        setHadoopDefaultConfig(yarnConfiguration, null, null);
    }
}
