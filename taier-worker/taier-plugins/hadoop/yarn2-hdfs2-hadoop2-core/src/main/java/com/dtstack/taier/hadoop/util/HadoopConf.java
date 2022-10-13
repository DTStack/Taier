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

package com.dtstack.taier.hadoop.util;


import com.dtstack.taier.base.util.HadoopConfTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.Map;

/**
 * @author sishu.yss
 */
public class HadoopConf {

    private Configuration configuration;

    private Configuration yarnConfiguration;

    public HadoopConf() {

    }

    public void initHadoopConf(Map<String, Object> conf) {
        if (null == conf) {
            return;
        }

        configuration = new Configuration();

        conf.keySet().forEach(key -> {
            Object value = conf.get(key);
            if (value instanceof String) {
                configuration.set(key, (String) value);
            } else if (value instanceof Boolean) {
                configuration.setBoolean(key, (boolean) value);
            }
        });
        configuration.setBoolean(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);

    }

    public void initYarnConf(Map<String, Object> conf) {
        if (null == conf) {
            return;
        }

        yarnConfiguration = configuration == null ? new YarnConfiguration() : new YarnConfiguration(configuration);

        conf.keySet().forEach(key -> {
            Object value = conf.get(key);
            if (value instanceof String) {
                yarnConfiguration.set(key, (String) value);
            } else if (value instanceof Boolean) {
                yarnConfiguration.setBoolean(key, (boolean) value);
            }
        });
        HadoopConfTool.setDefaultYarnConf(yarnConfiguration, conf);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Configuration getYarnConfiguration() {
        return yarnConfiguration;
    }

    public String getDefaultFs() {
        return configuration.get("fs.defaultFS");
    }
}
