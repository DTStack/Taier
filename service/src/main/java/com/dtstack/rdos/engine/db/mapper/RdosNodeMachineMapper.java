package com.dtstack.rdos.engine.db.mapper;

import org.apache.ibatis.annotations.Param;

import com.dtstack.rdos.engine.db.dataobject.RdosNodeMachine;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface RdosNodeMachineMapper {
	
	void insert(RdosNodeMachine rdosNodeMachine);
	
    void updateMachineType(RdosNodeMachine rdosNodeMachine);

	void disableMachineNode(RdosNodeMachine rdosNodeMachine);

	void ableMachineNode(RdosNodeMachine rdosNodeMachine);

	void updateOneTypeMachineToSlave(@Param("type") String type);

	void updateMachineToMaster(@Param("ip") String ip, @Param("appType") String appType);
}
