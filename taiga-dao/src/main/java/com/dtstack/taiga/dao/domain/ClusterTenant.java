package com.dtstack.taiga.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("console_cluster_tenant")
public class ClusterTenant extends BaseEntity {

    private Long tenantId;

    private Long queueId;

    private Long clusterId;

    private String tenantName;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }
}

