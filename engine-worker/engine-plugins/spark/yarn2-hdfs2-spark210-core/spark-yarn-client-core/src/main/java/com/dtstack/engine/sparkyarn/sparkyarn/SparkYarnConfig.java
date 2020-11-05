package com.dtstack.engine.sparkyarn.sparkyarn;


import com.dtstack.engine.base.BaseConfig;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

/**
 * Created by softfly on 17/8/10.
 */
public class SparkYarnConfig extends BaseConfig {

    private static final String DEFAULT_SPARK_YARN_ARCHIVE = "%s/sparkjars/jars";

    private static final String DEFAULT_SPARK_SQL_PROXY_JAR_PATH = "%s/user/spark/spark-sql-proxy.jar";

    private static final String DEFAULT_SPARK_PYTHON_EXTLIBPATH = "%s/pythons/pyspark.zip,/pythons/py4j-0.10.4-src.zip";

    private static final String DEFAULT_SPARK_SQL_PROXY_MAINCLASS = "com.dtstack.sql.main.SqlProxy";

    public static final String DEFAULT_CARBON_SQL_PROXY_MAINCLASS = "com.dtstack.sql.main.CarbondataSqlProxy";

    private static final String HDFS_FLAG = "hdfs";

    private String typeName;

    private String sparkYarnArchive;

    private String sparkSqlProxyPath;

    private String sparkSqlProxyMainClass;

    private String sparkPythonExtLibPath;

    private String md5sum;

    /**如果不是使用默认的配置---需要设置配置文件所在的hdfs路径*/
    private String confHdfsPath;

    private Map<String, Object> hadoopConf;

    private Map<String, Object> yarnConf;

    private Map<String, Object> hiveConf;

    private String defaultFs;

    private String hadoopUserName;
    //队列名称
    private String queue;
    //是否支持弹性，true/false
    private String elasticCapacity = "true";
    //在yarn队列中允许等待执行的任务数量
    private String yarnAccepterTaskNumber;

    private String jvmOptions = "-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing";

    private String carbonStorePath;

    private Map<String, String> kerberosConfig;

    private boolean monitorAcceptedApp = false;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Map<String, Object> getHiveConf() {
        return hiveConf;
    }

    public void setHiveConf(Map<String, Object> hiveConf) {
        this.hiveConf = hiveConf;
    }

    public String getSparkYarnArchive() {
        if(Strings.isNullOrEmpty(sparkYarnArchive)){
            return String.format(DEFAULT_SPARK_YARN_ARCHIVE, defaultFs);
        }

        if(!sparkYarnArchive.trim().startsWith(HDFS_FLAG)){
            sparkYarnArchive = sparkYarnArchive.trim();
            sparkYarnArchive = defaultFs + sparkYarnArchive;
        }

        return sparkYarnArchive;
    }

    public void setSparkYarnArchive(String sparkYarnArchive) {
        this.sparkYarnArchive = sparkYarnArchive;
    }

    public String getSparkSqlProxyPath() {
        if(Strings.isNullOrEmpty(sparkSqlProxyPath)){
            return String.format(DEFAULT_SPARK_SQL_PROXY_JAR_PATH, defaultFs);
        }

        if(!sparkSqlProxyPath.trim().startsWith(HDFS_FLAG)){
            sparkSqlProxyPath = sparkSqlProxyPath.trim();
            sparkSqlProxyPath = defaultFs + sparkSqlProxyPath;
        }

        return sparkSqlProxyPath;
    }

    public void setSparkSqlProxyPath(String sparkSqlProxyPath) {
        this.sparkSqlProxyPath = sparkSqlProxyPath;
    }

    public String getSparkSqlProxyMainClass() {

        if(Strings.isNullOrEmpty(sparkSqlProxyMainClass)){
            return DEFAULT_SPARK_SQL_PROXY_MAINCLASS;
        }

        return sparkSqlProxyMainClass;
    }

    public void setSparkSqlProxyMainClass(String sparkSqlProxyMainClass) {
        this.sparkSqlProxyMainClass = sparkSqlProxyMainClass;
    }

    public String getSparkPythonExtLibPath() {
        if(Strings.isNullOrEmpty(sparkPythonExtLibPath)){
            return String.format(DEFAULT_SPARK_PYTHON_EXTLIBPATH, defaultFs);
        }

        if(!sparkPythonExtLibPath.startsWith(HDFS_FLAG)){
            sparkPythonExtLibPath = sparkPythonExtLibPath.trim();
            sparkPythonExtLibPath = defaultFs + sparkPythonExtLibPath;
        }

        return sparkPythonExtLibPath;
    }

    public void setSparkPythonExtLibPath(String sparkPythonExtLibPath) {
        this.sparkPythonExtLibPath = sparkPythonExtLibPath;
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

    public String getDefaultFs() {
        return defaultFs;
    }

    public void setDefaultFs(String defaultFs) {
        this.defaultFs = defaultFs;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public String getConfHdfsPath() {
        return confHdfsPath;
    }

    public void setConfHdfsPath(String confHdfsPath) {
        this.confHdfsPath = confHdfsPath;
    }

    public String getHadoopUserName() {
        return hadoopUserName;
    }

    public void setHadoopUserName(String hadoopUserName) {
        this.hadoopUserName = hadoopUserName;
    }

    public String getQueue() {
        return StringUtils.isBlank(queue)? "default" : queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public boolean getElasticCapacity() {
        return  StringUtils.isBlank(elasticCapacity) ? true: Boolean.valueOf(elasticCapacity);
    }

    public void setElasticCapacity(String elasticCapacity) {
        this.elasticCapacity = elasticCapacity;
    }

    public int getYarnAccepterTaskNumber() {
        return StringUtils.isBlank(yarnAccepterTaskNumber) ? 1: NumberUtils.toInt(yarnAccepterTaskNumber,2);
    }

    public void setYarnAccepterTaskNumber(String yarnAccepterTaskNumber) {
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
    }

    public String getCarbonStorePath() {
        return carbonStorePath;
    }

    public void setCarbonStorePath(String carbonStorePath) {
        this.carbonStorePath = carbonStorePath;
    }

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public Map<String, String> getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(Map<String, String> kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    public boolean getMonitorAcceptedApp() {
        return monitorAcceptedApp;
    }

    public void setMonitorAcceptedApp(boolean monitorAcceptedApp) {
        this.monitorAcceptedApp = monitorAcceptedApp;
    }
}
