package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.Cluster;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@ApiModel
public class ClusterVO extends Cluster {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterVO.class);

    private Long clusterId;


    private Long dtUicTenantId;

    private Long dtUicUserId;

    /**
     * 组件类型
     */
    @ApiModelProperty(notes = "组件类型")
    private List<SchedulingVo> scheduling;

    public List<SchedulingVo> getScheduling() {
        return scheduling;
    }

    public void setScheduling(List<SchedulingVo> scheduling) {
        this.scheduling = scheduling;
    }

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
            LOGGER.error("ClusterVO.toVO error:",e);
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
}

