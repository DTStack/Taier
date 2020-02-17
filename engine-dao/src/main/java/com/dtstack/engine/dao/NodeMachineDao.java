package com.dtstack.engine.dao;

import com.dtstack.engine.domain.NodeMachine;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface NodeMachineDao {

	void insert(NodeMachine nodeMachine);

	void updateMachineType(NodeMachine nodeMachine);

	void disableMachineNode(NodeMachine nodeMachine);

	void ableMachineNode(NodeMachine nodeMachine);

	void updateOneTypeMachineToSlave(@Param("type") String type);

	void updateMachineToMaster(@Param("ip") String ip, @Param("appType") String appType);

	List<NodeMachine> listByAppType(@Param("appType") String appType);

	NodeMachine getByAppTypeAndMachineType(@Param("appType") String appType, @Param("machineType") int machineType);

}
