package com.dtstack.engine.spark210;


/**
 * 
 * @author sishu.yss
 *
 */
public class SparkStandaloneConfig {
	
	  private String typeName;
	  
      private String sparkWebMaster;
      
      private String sparkMaster;
      
      private String sparkSqlProxyPath;
      
      private String sparkSqlProxyMainClass;
      
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getSparkWebMaster() {
		return sparkWebMaster;
	}
	public void setSparkWebMaster(String sparkWebMaster) {
		this.sparkWebMaster = sparkWebMaster;
	}
	public String getSparkMaster() {
		return sparkMaster;
	}
	public void setSparkMaster(String sparkMaster) {
		this.sparkMaster = sparkMaster;
	}
	public String getSparkSqlProxyPath() {
		return sparkSqlProxyPath;
	}
	public void setSparkSqlProxyPath(String sparkSqlProxyPath) {
		this.sparkSqlProxyPath = sparkSqlProxyPath;
	}
	public String getSparkSqlProxyMainClass() {
		return sparkSqlProxyMainClass;
	}
	public void setSparkSqlProxyMainClass(String sparkSqlProxyMainClass) {
		this.sparkSqlProxyMainClass = sparkSqlProxyMainClass;
	}
      
}
