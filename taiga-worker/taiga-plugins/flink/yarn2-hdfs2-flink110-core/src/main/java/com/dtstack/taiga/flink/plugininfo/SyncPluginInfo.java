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

package com.dtstack.taiga.flink.plugininfo;

import com.dtstack.taiga.flink.FlinkClient;
import com.dtstack.taiga.pluginapi.exception.PluginDefineException;
import com.dtstack.taiga.pluginapi.JarFileInfo;
import com.dtstack.taiga.pluginapi.JobClient;
import com.dtstack.taiga.flink.FlinkConfig;
import com.dtstack.taiga.flink.base.enums.ClusterMode;
import com.dtstack.taiga.flink.constrant.ConfigConstrant;
import com.dtstack.taiga.flink.util.FlinkUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据同步插件
 * Date: 2018/5/3
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SyncPluginInfo {

    private static final Logger LOG = LoggerFactory.getLogger(SyncPluginInfo.class);

    //同步模块在flink集群加载插件
    private String remoteSyncPluginDir;

    private String localSyncPluginDir;

    //同步模块的monitorAddress, 用于获取错误记录数等信息
    private String monitorAddress;

    private String pluginLoadMode;

    private FlinkConfig flinkConfig;

    private SyncPluginInfo(){
    }

    public static SyncPluginInfo create(FlinkConfig flinkConfig){
        SyncPluginInfo syncPluginInfo = new SyncPluginInfo();
        syncPluginInfo.init(flinkConfig);
        return syncPluginInfo;
    }

    public void init(FlinkConfig flinkConfig){
        this.flinkConfig = flinkConfig;
        this.remoteSyncPluginDir = getSyncPluginDir(flinkConfig.getRemotePluginRootDir());
        this.localSyncPluginDir = getSyncPluginDir(flinkConfig.getFlinkPluginRoot());
        this.monitorAddress = flinkConfig.getMonitorAddress();
        this.pluginLoadMode = flinkConfig.getPluginLoadMode();
        LOG.info("---------local syncplugin dir is:" + localSyncPluginDir);
        LOG.info("---------remote syncplugin dir is:" + remoteSyncPluginDir);
    }

    public List<URL> getClassPaths(List<String> programArgList){
        return remoteSyncPluginDir != null ?
                getUserClassPath(programArgList, remoteSyncPluginDir) : new ArrayList<>();
    }

    public List<String> createSyncPluginArgs(JobClient jobClient, FlinkClient flinkClient){
        String args = jobClient.getClassArgs();
        List<String> programArgList = Lists.newArrayList();
        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }

        ClusterMode clusterMode = ClusterMode.getClusteMode(flinkConfig.getClusterMode());

        programArgList.add("-monitor");
        if(StringUtils.isNotEmpty(monitorAddress)) {
            programArgList.add(monitorAddress);
        } else {
            programArgList.add(flinkClient.getReqUrl(clusterMode));
        }
        programArgList.add("-pluginLoadMode");
        programArgList.add(pluginLoadMode);

        programArgList.add("-mode");
        if (ClusterMode.isPerjob(clusterMode)){
            programArgList.add("yarnPer");
        }else {
            programArgList.add("yarn");
        }

        // 数据源keytab
        StringBuilder fileName = new StringBuilder(32);
        String taskWorkspace = FlinkUtil.getTaskWorkspace(jobClient.getJobId());
        String taskKeytabDirPath = taskWorkspace + ConfigConstrant.SP + "resource";
        File taskKeytabDir = new File(taskKeytabDirPath);
        File[] taskKeytabFiles = taskKeytabDir.listFiles();
        if (taskKeytabFiles != null && taskKeytabFiles.length > 0) {
            for (File file : taskKeytabFiles) {
                fileName.append(file.getAbsoluteFile()).append(",");
            }
        }
        if(StringUtils.isNotBlank(fileName)){
            programArgList.add("-shipfile");
            programArgList.add(fileName.toString());
        }

        return programArgList;
    }

    public JarFileInfo createAddJarInfo(){
        JarFileInfo jarFileInfo = new JarFileInfo();
        String coreJarFileName = getCoreJarFileName();
        String jarFilePath  = localSyncPluginDir + ConfigConstrant.SP + coreJarFileName;
        jarFileInfo.setJarPath(jarFilePath);
        return jarFileInfo;
    }

    private String getCoreJarFileName (){
        String coreJarFileName = null;
        File pluginDir = new File(localSyncPluginDir);
        if (pluginDir.exists() && pluginDir.isDirectory()){
            File[] jarFiles = pluginDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().startsWith(ConfigConstrant.FLINKX_CORE_JAR_PREFIX) && name.toLowerCase().endsWith(".jar");
                }
            });

            if (jarFiles != null && jarFiles.length > 0){
                coreJarFileName = jarFiles[0].getName();
            }
        }

        if (StringUtils.isEmpty(coreJarFileName)){
            throw new PluginDefineException("Can not find core jar file in syncPlugin path: " + localSyncPluginDir);
        }

        return coreJarFileName;
    }

    public String getSyncPluginDir(String pluginRoot){
        return pluginRoot + ConfigConstrant.SP + ConfigConstrant.SYNCPLUGIN_DIR;
    }

    // 数据同步专用: 获取flink端插件classpath, 在programArgsList中添加engine端plugin根目录
    private List<URL> getUserClassPath(List<String> programArgList, String flinkSyncPluginRoot) {
        List<URL> urlList = new ArrayList<>();
        if(programArgList == null || flinkSyncPluginRoot == null){
            return urlList;
        }

        int i = 0;
        for(; i < programArgList.size() - 1; ++i){
            if("-job".equals(programArgList.get(i)) || "--job".equals(programArgList.get(i))){
                break;
            }
        }

        if(i == programArgList.size() - 1){
            return urlList;
        }

        programArgList.add("-pluginRoot");
        programArgList.add(localSyncPluginDir);

        String job = programArgList.get(i + 1);

        try {
            job = java.net.URLDecoder.decode(job, "UTF-8");
            programArgList.set(i + 1, job);
        } catch (Exception e) {
            LOG.error("Sync job URLDecoder error:", e);
        }
        return urlList;
    }

}
