package com.dtstack.engine.sparkk8s.config;


import com.dtstack.engine.base.BaseConfig;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by softfly on 17/8/10.
 */
public class SparkK8sConfig extends BaseConfig {

    private static final String DEFAULT_SPARK_SQL_PROXY_MAINCLASS = "com.dtstack.sql.main.SqlProxy";
    // 镜像中的存储位置
    private static final String DEFAULT_SPARK_PYTHON_EXTLIBPATH = "local:///opt/spark/python/lib/pyspark.zip,local:///opt/spark/python/lib/py4j-0.10.7-src.zip";

    private static final String LOCAL_FLAG = "local://";

    private String typeName;

    private String sparkSqlProxyMainClass = "";

    private String sparkSqlProxyPath;

    private String md5sum;

    private String sparkPythonExtLibPath;

    /**如果不是使用默认的配置---需要设置配置文件所在的hdfs路径*/
    private String confHdfsPath;

    private String hadoopUserName;

    private String nameSpace = "default";

    private String remoteDir;

    private String kubernetesConfigName;

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
        return StringUtils.isEmpty(sparkSqlProxyMainClass) ? DEFAULT_SPARK_SQL_PROXY_MAINCLASS : sparkSqlProxyMainClass;
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

    public String getHadoopUserName() {
        return hadoopUserName;
    }

    public void setHadoopUserName(String hadoopUserName) {
        this.hadoopUserName = hadoopUserName;
    }

    public String getSparkPythonExtLibPath() {
        if (Strings.isNullOrEmpty(sparkPythonExtLibPath)) {
            return DEFAULT_SPARK_PYTHON_EXTLIBPATH;
    }

        if(!sparkPythonExtLibPath.startsWith(LOCAL_FLAG)){
            sparkPythonExtLibPath = sparkPythonExtLibPath.trim();
            sparkPythonExtLibPath = LOCAL_FLAG + sparkPythonExtLibPath;
    }

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

    public String getSparkSqlProxyPath() {
        return sparkSqlProxyPath.startsWith(LOCAL_FLAG) ?
                sparkSqlProxyPath.trim() : LOCAL_FLAG + sparkSqlProxyPath.trim();
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

    public String getRemoteDir() {
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public String getKubernetesConfigName() {
        return kubernetesConfigName;
    }

    public void setKubernetesConfigName(String kubernetesConfigName) {
        this.kubernetesConfigName = kubernetesConfigName;
    }
}
