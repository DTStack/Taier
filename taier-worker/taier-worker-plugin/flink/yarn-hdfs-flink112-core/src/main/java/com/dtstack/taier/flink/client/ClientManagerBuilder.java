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

package com.dtstack.taier.flink.client;

import com.dtstack.taier.base.util.HadoopConfTool;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.flink.base.enums.ClusterMode;
import com.dtstack.taier.flink.perjob.client.PerJobClientManager;
import com.dtstack.taier.flink.session.client.SessionClientManager;
import com.dtstack.taier.flink.standalone.client.StandaloneClientManager;
import com.dtstack.taier.flink.config.FlinkConfig;
import com.dtstack.taier.flink.config.HadoopConfig;
import com.dtstack.taier.flink.constant.ConfigConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;

import java.util.Properties;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */
public class ClientManagerBuilder {

    private FlinkConfig flinkConfig;

    private HadoopConfig hadoopConfig;

    private Properties flinkExtProp;

    /**
     * perJob and session mode need to add additional config
     */
    private Configuration flinkGlobalConfiguration;

    private ClientManagerBuilder() {
    }

    public static ClientManagerBuilder newInstance(FlinkConfig flinkConfig, Properties flinkExtProp) {
        HadoopConfig hadoopConfig = new HadoopConfig(
                flinkConfig.getHadoopConf(),
                flinkConfig.getYarnConf()
        );
        ClientManagerBuilder clientManagerBuilder = new ClientManagerBuilder();
        clientManagerBuilder.setFlinkConfig(flinkConfig);
        clientManagerBuilder.setHadoopConfig(hadoopConfig);
        clientManagerBuilder.setFlinkExtProp(flinkExtProp);
        clientManagerBuilder.createFlinkGlobalConfiguration();
        return clientManagerBuilder;
    }

    public AbstractClientManager build() {
        AbstractClientManager clientManager;
        ClusterMode clusterMode = ClusterMode.getClusteMode(flinkConfig.getClusterMode());
        switch (clusterMode) {
            case PER_JOB:
                fillFlinkConfWithHadoopConf(flinkGlobalConfiguration, hadoopConfig);
                clientManager = new PerJobClientManager(flinkConfig, hadoopConfig, flinkGlobalConfiguration);
                break;
            case SESSION:
                fillFlinkConfWithHadoopConf(flinkGlobalConfiguration, hadoopConfig);
                clientManager = new SessionClientManager(flinkConfig, hadoopConfig, flinkGlobalConfiguration);
                break;
            case STANDALONE:
                clientManager = new StandaloneClientManager(flinkConfig, hadoopConfig);
                clientManager.addFlinkConfiguration(flinkGlobalConfiguration);
                break;
            default:
                throw new PluginDefineException("not support clusterMode: " + clusterMode);
        }
        return clientManager;
    }

    /**
     * common configuration for all clusterClient
     */
    public void createFlinkGlobalConfiguration() {

        Configuration config = new Configuration();

        // akka configuration
        config.setString(ConfigConstant.AKKA_CLIENT_TIMEOUT.key(),
                ConfigConstant.AKKA_CLIENT_TIMEOUT.defaultValue());
        config.setString(ConfigConstant.AKKA_ASK_TIMEOUT.key(),
                ConfigConstant.AKKA_ASK_TIMEOUT.defaultValue());
        config.setString(ConfigConstant.AKKA_TCP_TIMEOUT.key(),
                ConfigConstant.AKKA_TCP_TIMEOUT.defaultValue());
//        // yarn attempt
//        config.setInteger(ConfigConstant.YARN_APPLICATION_ATTEMPTS.key(),
//                ConfigConstant.YARN_APPLICATION_ATTEMPTS.defaultValue());
//
//        config.setInteger(ConfigConstant.YARN_APPLICATION_ATTEMPT_FAILURES_VALIDITY_INTERVAL.key(),
//                ConfigConstant.YARN_APPLICATION_ATTEMPT_FAILURES_VALIDITY_INTERVAL.defaultValue());

        //config.setBytes(ConfigConstrant.HADOOP_CONF_BYTES_KEY, HadoopConfTool.serializeHadoopConf(createHadoopConf().getConfiguration()));
        //config.setBytes(ConfigConstrant.YARN_CONF_BYTES_KEY, HadoopConfTool.serializeHadoopConf(yarnConf));

        config.setBoolean(ConfigConstant.OPEN_KERBEROS_KEY, flinkConfig.isOpenKerberos());

        if (flinkExtProp != null) {
            for (Object key : flinkExtProp.keySet()) {
                String newKey = (String) key;
                String value = flinkExtProp.getProperty(newKey);
                if (StringUtils.isEmpty(value) || FlinkConfig.getEngineFlinkConfigs().contains(key.toString())) {
                    continue;
                }
                config.setString(newKey, value);
            }
        }
        flinkGlobalConfiguration = config;
    }

    public void fillFlinkConfWithHadoopConf(Configuration flinkConfiguration, HadoopConfig hadoopConf) {
        if (hadoopConf == null) {
            return;
        }
        flinkConfiguration.setBytes(ConfigConstant.HADOOP_CONF_BYTES_KEY, HadoopConfTool.serializeHadoopConf(hadoopConf.getCoreConfiguration()));
        flinkConfiguration.setBytes(ConfigConstant.YARN_CONF_BYTES_KEY, HadoopConfTool.serializeHadoopConf(hadoopConf.getYarnConfiguration()));
    }

    public void setFlinkConfig(FlinkConfig flinkConfig) {
        this.flinkConfig = flinkConfig;
    }

    public void setHadoopConfig(HadoopConfig hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    public void setFlinkExtProp(Properties flinkExtProp) {
        this.flinkExtProp = flinkExtProp;
    }
}
