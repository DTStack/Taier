package com.dtstack.engine.vo;

import com.dtstack.engine.domain.Cluster;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ClusterVO extends Cluster {

    private Long clusterId;

    private List<EngineVO> engines;

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

