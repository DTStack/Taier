package com.dtstack.rdos.engine.execution.sparkyarn;

/**
 * Created by softfly on 17/8/10.
 */
public class SparkYarnConfig {
    private String typeName;
    private String yarnConfDir;
    private String sparkYarnArchive;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getYarnConfDir() {
        return yarnConfDir;
    }

    public void setYarnConfDir(String yarnConfDir) {
        this.yarnConfDir = yarnConfDir;
    }

    public String getSparkYarnArchive() {
        return sparkYarnArchive;
    }

    public void setSparkYarnArchive(String sparkYarnArchive) {
        this.sparkYarnArchive = sparkYarnArchive;
    }
}
