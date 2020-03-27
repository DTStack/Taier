package com.dtstack.engine.vo;

import com.dtstack.engine.domain.Cluster;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ClusterVO extends Cluster {

    private Long clusterId;

    private List<EngineVO> engines;

    private Long dtUicTenantId;

    private Long dtUicUserId;

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Long getDtUicUserId() {
        return dtUicUserId;
    }

    public void setDtUicUserId(Long dtUicUserId) {
        this.dtUicUserId = dtUicUserId;
    }

    public static ClusterVO toVO(Cluster cluster) {
        ClusterVO vo = new ClusterVO();
        try {
            BeanUtils.copyProperties(cluster, vo);
            vo.setClusterId(cluster.getId());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return vo;
    }

    public static List<ClusterVO> toVOs(List<Cluster> clusterList) {
        List<ClusterVO> vos = new ArrayList<>();
        for (Cluster cluster : clusterList) {
            vos.add(toVO(cluster));
        }
        return vos;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<EngineVO> getEngines() {
        return engines;
    }

    public void setEngines(List<EngineVO> engines) {
        this.engines = engines;
    }
}

