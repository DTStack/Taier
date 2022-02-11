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

package com.dtstack.taier.flink;

import com.dtstack.taier.base.BaseConfig;
import com.dtstack.taier.flink.constrant.ConfigConstrant;
import com.google.common.base.Strings;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sishu.yss
 *
 */
public class FlinkConfig extends BaseConfig {

    private static List<String> ENGINE_FLINK_CONFIGS = null;

    static {
        ENGINE_FLINK_CONFIGS = initEngineFlinkConfigFields();
    }

    private String typeName;

    private String flinkJobMgrUrl;

    private String flinkPluginRoot;

    private String monitorAddress;

    private String remotePluginRootDir;

    private String clusterMode;

    private String cluster;

    private String queue;

    private Map<String, Object> hadoopConf;

    private Map<String, Object> yarnConf;

    private String flinkJarPath;

    private String elasticCapacity;

    private String yarnAccepterTaskNumber;

    private int asyncCheckYarnClientThreadNum = 3;

    private Map<String, String> kerberosConfig;

    private int flinkSessionSlotCount;

    private String flinkSessionName = "Flink session";

    private int sessionRetryNum = 5;

    private boolean sessionStartAuto = false;

    private boolean flinkHighAvailability = false;

    private String pluginLoadMode = "shipfile";

    private int checkSubmitJobGraphInterval = 60;

    private int monitorElectionWaitTime = 5 * 1000;

    private long submitTimeout = 5;

    private int zkConnectionTimeout = 5000;

    private int zkSessionTimeout = 5000;

    private String remoteFlinkJarPath;

    public int getZkConnectionTimeout() {
        return zkConnectionTimeout;
    }

    public void setZkConnectionTimeout(int zkConnectionTimeout) {
        this.zkConnectionTimeout = zkConnectionTimeout;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public void setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    public int getAsyncCheckYarnClientThreadNum() {
        return asyncCheckYarnClientThreadNum;
    }

    public void setAsyncCheckYarnClientThreadNum(int asyncCheckYarnClientThreadNum) {
        this.asyncCheckYarnClientThreadNum = asyncCheckYarnClientThreadNum;
    }

    private String sessionCheckJarPath;

    public String getSessionCheckJarPath() {
        if(Strings.isNullOrEmpty(sessionCheckJarPath)){
            return ConfigConstrant.DEFAULT_SESSION_CHECK_PATH;
        }
        return sessionCheckJarPath;
    }

    public void setSessionCheckJarPath(String sessionCheckJarPath) {
        this.sessionCheckJarPath = sessionCheckJarPath;
    }


    public int getSessionRetryNum() {
        return sessionRetryNum;
    }

    public void setSessionRetryNum(int sessionRetryNum) {
        this.sessionRetryNum = sessionRetryNum;
    }

    private boolean monitorAcceptedApp = false;

    public boolean getMonitorAcceptedApp() {
        return monitorAcceptedApp;
    }

    public void setMonitorAcceptedApp(boolean monitorAcceptedApp) {
        this.monitorAcceptedApp = monitorAcceptedApp;
    }

    public int getMonitorElectionWaitTime() {
        return monitorElectionWaitTime;
    }

    public void setMonitorElectionWaitTime(int monitorElectionWaitTime) {
        this.monitorElectionWaitTime = monitorElectionWaitTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getFlinkJobMgrUrl() {
        return flinkJobMgrUrl;
    }

    public void setFlinkJobMgrUrl(String flinkJobMgrUrl) {
        this.flinkJobMgrUrl = flinkJobMgrUrl;
    }

    public void setFlinkPluginRoot(String flinkPluginRoot) {
        this.flinkPluginRoot = flinkPluginRoot;
    }

    public String getMonitorAddress() {
        return monitorAddress;
    }

    public void setMonitorAddress(String monitorAddress) {
        this.monitorAddress = monitorAddress;
    }

    public String getRemotePluginRootDir() {
        return remotePluginRootDir;
    }

    public void setRemotePluginRootDir(String remotePluginRootDir) {
        this.remotePluginRootDir = remotePluginRootDir;
    }

    public String getClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(String clusterMode) {
        this.clusterMode = clusterMode;
    }

    public Map<String, Object> getHadoopConf() {
        return hadoopConf;
    }

    public void setHadoopConf(Map<String, Object> hadoopConf) {
        this.hadoopConf = hadoopConf;
    }

    public Map<String, Object> getYarnConf() {
        return yarnConf;
    }

    public void setYarnConf(Map<String, Object> yarnConf) {
        this.yarnConf = yarnConf;
    }

    public String getFlinkJarPath() {
        return flinkJarPath;
    }

    public void setFlinkJarPath(String flinkJarPath) {
        this.flinkJarPath = flinkJarPath;
    }

    public void setElasticCapacity(String elasticCapacity) {
        this.elasticCapacity = elasticCapacity;
    }

    public void setYarnAccepterTaskNumber(String yarnAccepterTaskNumber) {
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
    }

    public Map<String, String> getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(Map<String, String> kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    public int getFlinkSessionSlotCount() {
        return flinkSessionSlotCount;
    }

    public void setFlinkSessionSlotCount(int flinkSessionSlotCount) {
        this.flinkSessionSlotCount = flinkSessionSlotCount;
    }

    public String getFlinkSessionName() {
        return flinkSessionName;
    }

    public void setFlinkSessionName(String flinkSessionName) {
        this.flinkSessionName = flinkSessionName;
    }

    public boolean getSessionStartAuto() {
        return sessionStartAuto;
    }

    public void setSessionStartAuto(boolean sessionStartAuto) {
        this.sessionStartAuto = sessionStartAuto;
    }

    public boolean getFlinkHighAvailability() {
        return flinkHighAvailability;
    }

    public void setFlinkHighAvailability(boolean flinkHighAvailability) {
        this.flinkHighAvailability = flinkHighAvailability;
    }

    public String getPluginLoadMode() {
        if (Strings.isNullOrEmpty(pluginLoadMode)) {
            return ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD;
        }
        return pluginLoadMode;
    }

    public void setPluginLoadMode(String pluginLoadMode) {
        this.pluginLoadMode = pluginLoadMode;
    }

    public String getFlinkPluginRoot() {
        if(Strings.isNullOrEmpty(flinkPluginRoot)){
            return ConfigConstrant.DEFAULT_FLINK_PLUGIN_ROOT;
        }

        return flinkPluginRoot;
    }

    public String getCluster() {
        return StringUtils.isBlank(cluster) ? "default" : cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getQueue() {
        return StringUtils.isBlank(queue) ? "default" : queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public boolean getElasticCapacity() {
        return StringUtils.isBlank(elasticCapacity) ? true: Boolean.valueOf(elasticCapacity);
    }

    public int getYarnAccepterTaskNumber() {
        return StringUtils.isBlank(yarnAccepterTaskNumber) ? 1: NumberUtils.toInt(yarnAccepterTaskNumber,2);
    }

    public static List<String> getEngineFlinkConfigs() {
        return ENGINE_FLINK_CONFIGS;
    }

    public static void setEngineFlinkConfigs(List<String> engineFlinkConfigs) {
        ENGINE_FLINK_CONFIGS = engineFlinkConfigs;
    }

    public int getCheckSubmitJobGraphInterval() {
        return checkSubmitJobGraphInterval;
    }

    public void setCheckSubmitJobGraphInterval(int checkSubmitJobGraphInterval) {
        this.checkSubmitJobGraphInterval = checkSubmitJobGraphInterval;
    }

    private static List<String> initEngineFlinkConfigFields() {
        Class baseClazz = BaseConfig.class;
        Field[] baseConfigFields = baseClazz.getDeclaredFields();

        Class FlinkConfigClazz = FlinkConfig.class;
        Field[] flinkConfigFields = FlinkConfigClazz.getDeclaredFields();

        Field[] fields = ArrayUtils.addAll(flinkConfigFields, baseConfigFields);
        List<String> engineFlinkConfigs = new ArrayList<>(fields.length);
        for (Field field : fields) {
            if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) != java.lang.reflect.Modifier.STATIC) {
                String name = field.getName();
                engineFlinkConfigs.add(name);
            }
        }
        return engineFlinkConfigs;
    }

    public long getSubmitTimeout() {
        return submitTimeout;
    }

    public void setSubmitTimeout(long submitTimeout) {
        this.submitTimeout = submitTimeout;
    }

    public String getRemoteFlinkJarPath() {
        return remoteFlinkJarPath;
    }

    public void setRemoteFlinkJarPath(String remoteFlinkJarPath) {
        this.remoteFlinkJarPath = remoteFlinkJarPath;
    }
}
