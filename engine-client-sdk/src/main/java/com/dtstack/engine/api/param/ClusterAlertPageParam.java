package com.dtstack.engine.api.param;

import java.util.List;

/**
 * Date: 2020/8/7
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class ClusterAlertPageParam extends PageParam {
    private Integer clusterId;

    private List<Integer> alertGateType;

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public List<Integer> getAlertGateType() {
        return alertGateType;
    }

    public void setAlertGateType(List<Integer> alertGateType) {
        this.alertGateType = alertGateType;
    }
}
