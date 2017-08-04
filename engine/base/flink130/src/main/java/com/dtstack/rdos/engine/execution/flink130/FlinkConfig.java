package com.dtstack.rdos.engine.execution.flink130;

/**
 * 
 * @author sishu.yss
 *
 */
public class FlinkConfig {
	
	private String typeName;

	private String flinkZkAddress;

	private String flinkZkNamespace;

	private String flinkClusterId;

	private String flinkJobMgrUrl;
	
	private String flinkHighAvailabilityStorageDir;
	
	private String jarTmpDir;

	private String flinkPluginRoot;

	private String monitorAddress;

	private String sqlPluginRootDir;

	private String yarnConfPath;


	private String remoteSqlPluginRootDir;


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

	public String getSqlPluginRootDir() {
		return sqlPluginRootDir;
	}

	public void setSqlPluginRootDir(String sqlPluginRootDir) {
		this.sqlPluginRootDir = sqlPluginRootDir;
	}

	public String getYarnConfPath() {
		return yarnConfPath;
	}

	public void setYarnConfPath(String yarnConfPath) {
		this.yarnConfPath = yarnConfPath;
	}

    public String getRemoteSqlPluginRootDir() {
        return remoteSqlPluginRootDir;
    }

    public void setRemoteSqlPluginRootDir(String remoteSqlPluginRootDir) {
        this.remoteSqlPluginRootDir = remoteSqlPluginRootDir;
    }
}
