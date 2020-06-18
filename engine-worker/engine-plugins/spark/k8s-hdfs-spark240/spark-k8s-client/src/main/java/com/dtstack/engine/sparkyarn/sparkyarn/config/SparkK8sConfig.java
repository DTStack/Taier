package com.dtstack.engine.sparkyarn.sparkyarn.config;


import java.util.Map;

/**
 * Created by softfly on 17/8/10.
 */
public class SparkK8sConfig {

    private static final String DEFAULT_SPARK_SQL_PROXY_MAINCLASS = "com.dtstack.sql.main.SqlProxy";

    private static final String DEFAULT_SPARK_PYTHON_EXTLIBPATH = "%s/pythons/pyspark.zip,/pythons/py4j-0.10.4-src.zip";

    private String typeName;

    private String sparkSqlProxyMainClass;

    private String sparkSqlProxyPath;

    private String md5sum;

    private String sparkPythonExtLibPath;

    /**如果不是使用默认的配置---需要设置配置文件所在的hdfs路径*/
    private String confHdfsPath;

    private Map<String, Object> hadoopConf;

    private Map<String, Object> hiveConf;

    private Map<String, String> sftpConf;

    private String hadoopUserName;

    private String nameSpace;

    private String jvmOptions = "-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing";


    public static String getDefaultSparkSqlProxyMainclass() {
        return DEFAULT_SPARK_SQL_PROXY_MAINCLASS;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSparkSqlProxyMainClass() {
        return sparkSqlProxyMainClass;
    }

    public void setSparkSqlProxyMainClass(String sparkSqlProxyMainClass) {
        this.sparkSqlProxyMainClass = sparkSqlProxyMainClass;
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

    public Map<String, Object> getHadoopConf() {
        return hadoopConf;
    }

    public void setHadoopConf(Map<String, Object> hadoopConf) {
        this.hadoopConf = hadoopConf;
    }

    public Map<String, Object> getHiveConf() {
        return hiveConf;
    }

    public void setHiveConf(Map<String, Object> hiveConf) {
        this.hiveConf = hiveConf;
    }

    public String getHadoopUserName() {
        return hadoopUserName;
    }

    public void setHadoopUserName(String hadoopUserName) {
        this.hadoopUserName = hadoopUserName;
    }

    public String getSparkPythonExtLibPath() {
        return sparkPythonExtLibPath;
    }

    public void setSparkPythonExtLibPath(String sparkPythonExtLibPath) {
        this.sparkPythonExtLibPath = sparkPythonExtLibPath;
    }

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public Map<String, String> getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(Map<String, String> sftpConf) {
        this.sftpConf = sftpConf;
    }

    public String getSparkSqlProxyPath() {
        return sparkSqlProxyPath;
    }

    public void setSparkSqlProxyPath(String sparkSqlProxyPath) {
        this.sparkSqlProxyPath = sparkSqlProxyPath;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
}
