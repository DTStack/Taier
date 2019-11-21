package com.dtstack.engine.flink150;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sishu.yss
 */
public class FlinkConfig {

    private static final String DEFAULT_FLINK_PLUGIN_ROOT = "/opt/dtstack/flinkplugin";

    private static final String DEFAULT_JAR_TMP_DIR = "../tmp150";

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

    private String elasticCapacity = "true";

    private String yarnAccepterTaskNumber;

    private boolean isSecurity;

    private String flinkPrincipal;

    private String flinkKeytabPath;

    private String flinkKrb5ConfPath;

    private String zkPrincipal;

    private String zkKeytabPath;

    private String zkLoginName;

    private String flinkSessionName = "Flink session";

    private boolean yarnSessionStartAuto = false;

    private boolean flinkHighAvailability = false;


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

    public void setQueue(String queue) {
        this.queue = queue;
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

    public boolean getElasticCapacity() {
        return StringUtils.isBlank(elasticCapacity) ? true: Boolean.valueOf(elasticCapacity);
    }

    public void setElasticCapacity(String elasticCapacity) {
        this.elasticCapacity = elasticCapacity;
    }

    public void setYarnAccepterTaskNumber(String yarnAccepterTaskNumber) {
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
    }

    public boolean isSecurity() {
        return isSecurity;
    }

    public void setSecurity(boolean security) {
        isSecurity = security;
    }

    public String getFlinkPrincipal() {
        return flinkPrincipal;
    }

    public void setFlinkPrincipal(String flinkPrincipal) {
        this.flinkPrincipal = flinkPrincipal;
    }

    public String getFlinkKeytabPath() {
        return flinkKeytabPath;
    }

    public void setFlinkKeytabPath(String flinkKeytabPath) {
        this.flinkKeytabPath = flinkKeytabPath;
    }

    public String getFlinkKrb5ConfPath() {
        return flinkKrb5ConfPath;
    }

    public void setFlinkKrb5ConfPath(String flinkKrb5ConfPath) {
        this.flinkKrb5ConfPath = flinkKrb5ConfPath;
    }

    public String getZkPrincipal() {
        return zkPrincipal;
    }

    public void setZkPrincipal(String zkPrincipal) {
        this.zkPrincipal = zkPrincipal;
    }

    public String getZkKeytabPath() {
        return zkKeytabPath;
    }

    public void setZkKeytabPath(String zkKeytabPath) {
        this.zkKeytabPath = zkKeytabPath;
    }

    public String getZkLoginName() {
        return zkLoginName;
    }

    public void setZkLoginName(String zkLoginName) {
        this.zkLoginName = zkLoginName;
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

    public String getJarTmpDir() {
        if (Strings.isNullOrEmpty(jarTmpDir)) {
            return DEFAULT_JAR_TMP_DIR;
        }
        return jarTmpDir;
    }

    public String getFlinkPluginRoot() {
        if (Strings.isNullOrEmpty(flinkPluginRoot)) {
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

    public int getYarnAccepterTaskNumber() {
        return StringUtils.isBlank(yarnAccepterTaskNumber) ? 1 : NumberUtils.toInt(yarnAccepterTaskNumber, 2);
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