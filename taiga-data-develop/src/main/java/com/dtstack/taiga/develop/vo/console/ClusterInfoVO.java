package com.dtstack.taiga.develop.vo.console;


import com.dtstack.taiga.dao.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

public class ClusterInfoVO extends BaseEntity {

    @ApiModelProperty(notes = "集群名称")
    private String clusterName;

    @ApiModelProperty(notes = "集群id")
    private Long clusterId;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private Integer isDeleted = 0;

    @ApiModelProperty(notes = "是否能修改切换metadata")
    private boolean canModifyMetadata = true;

    public boolean isCanModifyMetadata() {
        return canModifyMetadata;
    }

    public void setCanModifyMetadata(boolean canModifyMetadata) {
        this.canModifyMetadata = canModifyMetadata;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
