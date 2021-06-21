package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ComponentUserDao {

    ComponentUser getOne(@Param("id") Long id);

    Integer insert(ComponentUser component);

    void  deleteByComponentAndCluster(@Param("clusterId")Long clusterId,@Param("componentTypeCode")Integer componentTypeCode);

    void batchInsert(@Param("addComponentUserList") List<ComponentUser> addComponentUserList);

    List<ComponentUser> getComponentUserByCluster(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode);

    void deleteByComponentAndLabelUser(@Param("clusterId")Long clusterId, @Param("componentTypeCode")Integer componentTypeCode,@Param("label")String label, @Param("userName") String userName);

    ComponentUser getComponentUser(@Param("clusterId")Long clusterId, @Param("componentTypeCode")Integer componentTypeCode,@Param("label")String label, @Param("userName") String userName);
}

