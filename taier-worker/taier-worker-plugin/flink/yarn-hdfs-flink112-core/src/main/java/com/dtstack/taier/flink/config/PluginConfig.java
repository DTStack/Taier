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

package com.dtstack.taier.flink.config;

import com.dtstack.taier.flink.constant.ConfigConstant;
import com.dtstack.taier.pluginapi.JarFileInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * 保存flinkx配置信息
 *
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */

public class PluginConfig {

    private static final Logger LOG = LoggerFactory.getLogger(PluginConfig.class);

    private String remoteChunjunDistDir;

    private String chunjunDistDir;

    /**
     * 插件上传方式
     */
    private String pluginLoadMode;

    private PluginConfig() {
    }

    public static PluginConfig newInstance(FlinkConfig flinkConfig) {
        PluginConfig pluginConfig = new PluginConfig();
        pluginConfig.init(flinkConfig);
        return pluginConfig;
    }

    public void init(FlinkConfig flinkConfig) {
        this.pluginLoadMode = flinkConfig.getPluginLoadMode();
        this.chunjunDistDir = flinkConfig.getChunjunDistDir();
        this.remoteChunjunDistDir = flinkConfig.getRemoteChunjunDistDir();
        LOG.info("---------chunjun local plugin dir is: " + chunjunDistDir);
        LOG.info("---------chunjun remote plugin dir is: " + remoteChunjunDistDir);
    }

    /**
     * find core jar path from plugin directory
     */
    public JarFileInfo getCoreJarInfo() {
        JarFileInfo jarFileInfo = new JarFileInfo();
        String coreJarPath = getCoreJarPath();
        jarFileInfo.setJarPath(coreJarPath);
        return jarFileInfo;
    }

    /**
     * find jar from localFlinkxPluginDir witch name started with flinkx-core
     *
     * @return jar name
     */
    private String getCoreJarPath() {
        File pluginDir = new File(chunjunDistDir);
        if (pluginDir.exists() && pluginDir.isDirectory()) {
            File[] jarFiles = pluginDir.listFiles((dir, name)
                    -> (name.toLowerCase().startsWith(ConfigConstant.FLINKX_CORE_JAR_PREFIX)
                    || name.toLowerCase().startsWith(ConfigConstant.CHUNJUN_CORE_JAR_PREFIX))
                    && name.toLowerCase().endsWith(".jar"));

            // todo: should check if jarFiles.length > 1
            if (jarFiles != null && jarFiles.length > 0) {
                return jarFiles[0].getAbsolutePath();
            }
        }
        throw new PluginDefineException("Can not find chunjun core jar in path: " + chunjunDistDir);
    }

    /**
     * 构建执行 com.dtstack.flinkx.Main#main所需的参数
     * {@link com.dtstack.taier.flink.config.FlinkConfig}
     * todo: 应该结合com.dtstack.flinkx.options.Options规范参数名
     */
    public List<String> buildProgramArgs(JobClient jobClient) throws IOException {
        String args = jobClient.getClassArgs();
        List<String> programArgs = Lists.newArrayList();
        if (StringUtils.isNotBlank(args)) {
            // 按空格,制表符等进行拆分
            programArgs.addAll(Arrays.asList(args.split("\\s+")));
        }

        programArgs.add("-mode");
        // 默认情况，以yarn-per-job模式构建jobGraph
        if (EDeployMode.SESSION.getType().equals(jobClient.getDeployMode())) {
            programArgs.add("yarn-session");
        } else if (EDeployMode.STANDALONE.getType().equals(jobClient.getDeployMode())) {
            programArgs.add("standalone");
        } else {
            programArgs.add("yarn-per-job");
        }

        String jobType;
        EJobType jobTypeEnum = jobClient.getJobType();
        if (EJobType.SQL.equals(jobTypeEnum)) {
            jobType = "sql";
            String jobContent = jobClient.getSql();
            programArgs.add("-job");
            programArgs.add(jobContent);

            // programArgs.add("-connectorLoadMode");
            // programArgs.add("classloader");
        } else if (EJobType.SYNC.equals(jobTypeEnum)) {
            jobType = "sync";
            int jobIndex = 0;
            for (; jobIndex < programArgs.size(); ++jobIndex) {
                if ("-job".equals(programArgs.get(jobIndex)) ||
                        "--job".equals(programArgs.get(jobIndex))) {
                    break;
                }
            }
            String jobContent = java.net.URLDecoder.decode(programArgs.get(jobIndex + 1), "UTF-8");
            programArgs.set(jobIndex + 1, jobContent);
        } else {
            throw new UnsupportedOperationException("Unsupported EJobType: " + jobTypeEnum.name());
        }

        programArgs.add("-jobType");
        programArgs.add(jobType);

        programArgs.add("-jobName");
        programArgs.add(jobClient.getJobName());

        programArgs.add("-pluginLoadMode");
        programArgs.add(pluginLoadMode);

        programArgs.add("-chunjunDistDir");
        programArgs.add(chunjunDistDir);

        programArgs.add("-remoteChunJunDistDir");
        programArgs.add(remoteChunjunDistDir);

        programArgs.add("-confProp");
        String confPropStr = PublicUtil.objToString(jobClient.getConfProperties());
        confPropStr = URLEncoder.encode(confPropStr, Charsets.UTF_8.name());
        programArgs.add(confPropStr);

        return programArgs;
    }

}
