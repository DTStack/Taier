package com.dtstack.rdos.engine.execution.sparkyarn;

/**
 * Created by softfly on 17/8/10.
 */
public class SparkYarnConfig {

    private String typeName;

    private String sparkYarnArchive;

    private String sparkSqlProxyPath;

    private String sparkSqlProxyMainClass;

    private String sparkPythonExtLibPath;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSparkYarnArchive() {
        return sparkYarnArchive;
    }

    public void setSparkYarnArchive(String sparkYarnArchive) {
        this.sparkYarnArchive = sparkYarnArchive;
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

    public String getSparkPythonExtLibPath() {
        return sparkPythonExtLibPath;
    }

    public void setSparkPythonExtLibPath(String sparkPythonExtLibPath) {
        this.sparkPythonExtLibPath = sparkPythonExtLibPath;
    }
}
