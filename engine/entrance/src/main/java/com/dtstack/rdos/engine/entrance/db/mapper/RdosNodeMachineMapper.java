package com.dtstack.rdos.engine.entrance.db.mapper;

import com.dtstack.rdos.engine.entrance.db.dataobject.RdosNodeMachine;

public interface RdosNodeMachineMapper {
	
	public void insert(RdosNodeMachine rdosNodeMachine);
	
	public void deleteUnavaiableMasterNode();

}
