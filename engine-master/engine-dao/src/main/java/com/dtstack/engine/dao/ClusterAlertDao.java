package com.dtstack.engine.dao;


import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Date: 2020/8/7
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public interface ClusterAlertDao {

    int insert(ClusterAlertPO clusterAlertPO);

    int update(ClusterAlertPO clusterAlertPO);

    List<ClusterAlertPO> list(ClusterAlertPO clusterAlertPO);

    ClusterAlertPO get(ClusterAlertPO clusterAlertPO);

    int delete(@Param("alertId") Integer alertId);

}
