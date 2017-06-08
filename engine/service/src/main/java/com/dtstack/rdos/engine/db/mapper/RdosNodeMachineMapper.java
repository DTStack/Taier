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
	
	public void insert(RdosNodeMachine rdosNodeMachine);
	
    public void updateMachineType(RdosNodeMachine rdosNodeMachine);

	public void disableMachineNode(RdosNodeMachine rdosNodeMachine);

	public void ableMachineNode(RdosNodeMachine rdosNodeMachine);

	public void updateOneTypeMachineToSlave(@Param("type") String type);
}
