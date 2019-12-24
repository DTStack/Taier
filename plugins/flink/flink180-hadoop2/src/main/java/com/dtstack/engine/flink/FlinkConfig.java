package com.dtstack.engine.flink;

import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.google.common.base.Strings;
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
public class FlinkConfig {

    private static final String DEFAULT_FLINK_PLUGIN_ROOT = "/opt/dtstack/flinkplugin";

    private static final String DEFAULT_JAR_TMP_DIR = "../tmp180";

    private static List<String> ENGINE_FLINK_CONFIGS = null;

    static {
        ENGINE_FLINK_CONFIGS = initEngineFlinkConfigFields();
    }

    private String typeName;

    private String flinkJobMgrUrl;

    private String jarTmpDir;

    private String flinkPluginRoot;

    private String monitorAddress;

    private String remotePluginRootDir;

    private String clusterMode; // 集群运行模式: standalone or yarn

    private String cluster;

    private String queue;

    private Map<String, Object> hadoopConf;

    private Map<String, Object> yarnConf;

    private String flinkJarPath;

    private String elasticCapacity;

    private String yarnAccepterTaskNumber;

    private boolean openKerberos;

    private String localKeytab;

    private String remoteDir;

    private Map<String, String> sftpConf;

    private Map<String, String> kerberosConfig;

    private int flinkSessionSlotCount;

    private String flinkSessionName = "Flink session";

    private boolean yarnSessionStartAuto = false;

    private boolean flinkHighAvailability = false;

    private String pluginLoadMode = "shipfile";

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

    public void setJarTmpDir(String jarTmpDir) {
        this.jarTmpDir = jarTmpDir;
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

    public boolean isOpenKerberos() {
        return openKerberos;
    }

    public void setOpenKerberos(boolean openKerberos) {
        this.openKerberos = openKerberos;
    }

    public String getLocalKeytab() {
        return localKeytab;
    }

    public void setLocalKeytab(String localKeytab) {
        this.localKeytab = localKeytab;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public Map<String, String> getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(Map<String, String> sftpConf) {
        this.sftpConf = sftpConf;
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

    public boolean getYarnSessionStartAuto() {
        return yarnSessionStartAuto;
    }

    public void setYarnSessionStartAuto(boolean yarnSessionStartAuto) {
        this.yarnSessionStartAuto = yarnSessionStartAuto;
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

    public String getJarTmpDir() {
        if (Strings.isNullOrEmpty(jarTmpDir)) {
            return DEFAULT_JAR_TMP_DIR;
        }

        return jarTmpDir;
    }

    public String getFlinkPluginRoot() {
        if(Strings.isNullOrEmpty(flinkPluginRoot)){
            return DEFAULT_FLINK_PLUGIN_ROOT;
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

    private static List<String> initEngineFlinkConfigFields() {
        Class clazz = FlinkConfig.class;
        Field[] fields = clazz.getDeclaredFields();
        List<String> engineFlinkConfigs = new ArrayList<>(fields.length);
        for (Field field : fields) {
            if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) != java.lang.reflect.Modifier.STATIC) {
                String name = field.getName();
                engineFlinkConfigs.add(name);
            }
        }
        return engineFlinkConfigs;
    }

}
