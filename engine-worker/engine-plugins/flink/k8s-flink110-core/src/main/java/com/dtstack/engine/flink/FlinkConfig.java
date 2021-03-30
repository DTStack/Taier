package com.dtstack.engine.flink;

import com.dtstack.engine.base.BaseConfig;
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

    private String cluster;

    private String namespace;

    private Map<String, Object> hadoopConf;

    private String flinkJarPath;

    private String elasticCapacity;

    private Map<String, String> kerberosConfig;

    private int flinkSessionSlotCount;

    private String flinkSessionName = ConfigConstrant.FLINK_SESSION_PREFIX;

    private boolean sessionStartAuto = true;

    private boolean flinkHighAvailability = false;

    private String pluginLoadMode = "shipfile";

    private long submitTimeout = 3;

    private String md5sum;

    private String kubernetesConfigName;

    private String clusterMode;

    public String getClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(String clusterMode) {
        this.clusterMode = clusterMode;
    }

    public String getKubernetesConfigName() {
        return kubernetesConfigName;
    }

    public void setKubernetesConfigName(String kubernetesConfigName) {
        this.kubernetesConfigName = kubernetesConfigName;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
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

    public Map<String, Object> getHadoopConf() {
        return hadoopConf;
    }

    public void setHadoopConf(Map<String, Object> hadoopConf) {
        this.hadoopConf = hadoopConf;
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
        // k8s default classpath mode
        if (Strings.isNullOrEmpty(pluginLoadMode)) {
            return ConfigConstrant.FLINK_PLUGIN_CLASSPATH_LOAD;
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

    public String getNamespace() {
        return StringUtils.isBlank(namespace) ? "default" : namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public boolean getElasticCapacity() {
        return StringUtils.isBlank(elasticCapacity) ? true: Boolean.valueOf(elasticCapacity);
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

    public long getSubmitTimeout() {
        return submitTimeout;
    }

    public void setSubmitTimeout(long submitTimeout) {
        this.submitTimeout = submitTimeout;
    }
}
