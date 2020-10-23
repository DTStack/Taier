package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.Cluster;
import io.swagger.annotations.ApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@ApiModel
public class ClusterEngineVO extends Cluster {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterEngineVO.class);

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

    public static ClusterEngineVO toVO(Cluster cluster) {
        ClusterEngineVO vo = new ClusterEngineVO();
        try {
            BeanUtils.copyProperties(cluster, vo);
            vo.setClusterId(cluster.getId());
        } catch (Throwable e) {
            LOGGER.error("ClusterEngineVO.toVO error:",e);
        }
        return vo;
    }

    public static List<ClusterEngineVO> toVOs(List<Cluster> clusterList) {
        List<ClusterEngineVO> vos = new ArrayList<>();
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

