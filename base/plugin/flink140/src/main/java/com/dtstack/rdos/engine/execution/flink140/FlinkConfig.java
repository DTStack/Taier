package com.dtstack.rdos.engine.execution.flink140;

/**
 * 
 * @author sishu.yss
 *
 */
public class FlinkConfig {

	private static String DEFAULT_FLINK_PLUGIN_ROOT = "/opt/dtstack/flinkplugin";

	private static String DEFAULT_REMOTE_PLUGIN_ROOTDIR = "/opt/dtstack/flinkplugin";

    private String typeName;

	private String flinkZkAddress;

	private String flinkZkNamespace;

	private String flinkClusterId;

	private String flinkJobMgrUrl;
	
	private String flinkHighAvailabilityStorageDir;
	
	private String jarTmpDir;

	private String flinkPluginRoot;

	private String monitorAddress;

	private String remotePluginRootDir;

	private String clusterMode; // 集群运行模式: standalone or yarn


	public String getFlinkZkAddress() {
		return flinkZkAddress;
	}

	public void setFlinkZkAddress(String flinkZkAddress) {
		this.flinkZkAddress = flinkZkAddress;
	}

	public String getFlinkZkNamespace() {
		return flinkZkNamespace;
	}

	public void setFlinkZkNamespace(String flinkZkNamespace) {
		this.flinkZkNamespace = flinkZkNamespace;
	}

	public String getFlinkClusterId() {
		return flinkClusterId;
	}

	public void setFlinkClusterId(String flinkClusterId) {
		this.flinkClusterId = flinkClusterId;
	}

	public String getJarTmpDir() {
		return jarTmpDir;
	}

	public void setJarTmpDir(String jarTmpDir) {
		this.jarTmpDir = jarTmpDir;
	}

	public String getFlinkJobMgrUrl() {
		return flinkJobMgrUrl;
	}

	public void setFlinkJobMgrUrl(String flinkJobMgrUrl) {
		this.flinkJobMgrUrl = flinkJobMgrUrl;
	}

	public String getFlinkHighAvailabilityStorageDir() {
		return flinkHighAvailabilityStorageDir;
	}

	public void setFlinkHighAvailabilityStorageDir(
			String flinkHighAvailabilityStorageDir) {
		this.flinkHighAvailabilityStorageDir = flinkHighAvailabilityStorageDir;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getFlinkPluginRoot() {

	    if(flinkPluginRoot == null){
	        return DEFAULT_FLINK_PLUGIN_ROOT;
        }

		return flinkPluginRoot;
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

	    if(remotePluginRootDir == null){
	        return DEFAULT_REMOTE_PLUGIN_ROOTDIR;
        }

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

}
