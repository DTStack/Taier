package com.dtstack.engine.dao;

import com.dtstack.engine.domain.AlertChannel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 9:43 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */

public interface AlertChannelDao {

    AlertChannel selectById(@Param("id") Long id);

    List<AlertChannel> selectByQuery(@Param("alertChannel") AlertChannel alertChannel);

    List<AlertChannel> selectListByGateSources(@Param("isDefault") Integer isDefault, @Param("alertGateSources") List<String> alertGateSources);

    List<AlertChannel> selectList(@Param("isDefault") Integer isDefault, @Param("alertGateTypes") List<Integer> alertGateTypes, @Param("clusterId") Integer clusterId);

    Integer insert(@Param("alertChannel") AlertChannel alertChannel);

    Integer updateById(@Param("alertChannel") AlertChannel alertChannel);

    List<AlertChannel> selectAll();

    void updateDefaultAlertByType(@Param("alertGateType") Integer alertGateType, @Param("isDefault") Integer isDefault, @Param("isDelete") Integer isDelete);

    List<AlertChannel> selectInType(@Param("queryAlertChannel") AlertChannel queryAlertChannel, @Param("defaultAlert") List<Integer> defaultAlert);
}
