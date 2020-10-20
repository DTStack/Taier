package com.dtstack.engine.alert.serivce;

import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.dtstack.engine.dao.ClusterAlertDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Date: 2020/8/7
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Service
public class ClusterAlertService {
    @Autowired
    private ClusterAlertDao clusterAlertDao;


    public ClusterAlertPO edit(ClusterAlertPO param) {
        //判断是否设置默认告警通道
        if (param.getIsDefault() == 1) {
            resetDefaultAlert(param.getClusterId(),param.getAlertGateType());
        }

        //插入、更新 数据
        ClusterAlertPO query = new ClusterAlertPO();
        query.setAlertId(param.getAlertId());
        query.setClusterId(param.getClusterId());
        ClusterAlertPO clusterAlertPO = clusterAlertDao.get(query);
        if (clusterAlertPO == null) {
            clusterAlertDao.insert(param);
            return param;
        }
        //更新逻辑
        clusterAlertPO.setIsDefault(param.getIsDefault());
        clusterAlertDao.update(clusterAlertPO);
        return clusterAlertPO;
    }

    public void setDefaultAlert(Integer clusterId,Integer alertGateType,Integer alertId ) {
        ClusterAlertPO query = new ClusterAlertPO();
        query.setAlertId(alertId);
        query.setClusterId(clusterId);
        ClusterAlertPO clusterAlertPO = clusterAlertDao.get(query);
        Assert.notNull(clusterAlertPO,"告警通道不存在");
        resetDefaultAlert(clusterId, alertGateType);
        clusterAlertPO.setIsDefault(1);
        clusterAlertDao.update(clusterAlertPO);
    }

    /**
     * 将指定集群的指定告警通道类型中的默认告警通道置空
     * @param clusterId 集群id
     * @param alertGateType 告警通道类型
     */
    private void resetDefaultAlert(Integer clusterId,Integer alertGateType) {
        //判断是否存在此类型默认告警通道
        ClusterAlertPO query = new ClusterAlertPO();
        query.setClusterId(clusterId);
        query.setAlertGateType(alertGateType);
        query.setIsDefault(1);
        ClusterAlertPO clusterAlertPO = clusterAlertDao.get(query);
        if (clusterAlertPO != null) {
            //修改原来的默认告警通道
            clusterAlertPO.setIsDefault(0);
            clusterAlertDao.update(clusterAlertPO);
        }
    }


}
