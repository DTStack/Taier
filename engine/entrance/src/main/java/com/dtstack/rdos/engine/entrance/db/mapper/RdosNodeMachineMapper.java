package com.dtstack.rdos.engine.entrance.db.mapper;

import com.dtstack.rdos.engine.entrance.db.dataobject.RdosNodeMachine;

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
}
