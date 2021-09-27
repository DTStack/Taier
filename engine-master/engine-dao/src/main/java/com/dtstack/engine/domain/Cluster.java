package com.dtstack.engine.domain;

import com.dtstack.engine.common.annotation.Unique;
import io.swagger.annotations.ApiModel;

@ApiModel
public class Cluster extends BaseEntity {

    @Unique
    private String clusterName;

    private String hadoopVersion;

    public String getHadoopVersion() {
        return hadoopVersion;
    }

    public void setHadoopVersion(String hadoopVersion) {
        this.hadoopVersion = hadoopVersion;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
