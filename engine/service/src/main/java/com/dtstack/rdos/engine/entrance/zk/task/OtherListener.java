package com.dtstack.rdos.engine.entrance.zk.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.db.dao.RdosNodeMachineDAO;
import com.dtstack.rdos.engine.entrance.enumeration.MachineAppType;
import com.dtstack.rdos.engine.entrance.enumeration.RdosNodeMachineType;
import com.dtstack.rdos.engine.execution.base.JobClient;


/**
 * 
 * @author sishu.yss
 *
 */
public class OtherListener implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(OtherListener.class);
	
	private final static int listener = 10000;
	
	private MasterListener masterListener;
	
	private RdosNodeMachineDAO rdosNodeMachineDAO = new RdosNodeMachineDAO();

	public OtherListener(MasterListener masterListener){
		this.masterListener = masterListener;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			while(true){
				logger.warn("OtherListener start again...");
				if(this.masterListener.isMaster()){
					JobClient.getJobMaster().forEach((k,v)->{
						MachineAppType machineAppType = MachineAppType.getMachineAppType(k);
						rdosNodeMachineDAO.updateOneTypeMachineToSlave(machineAppType.getType());
						rdosNodeMachineDAO.insert(v, RdosNodeMachineType.MASTER.getType(),machineAppType);
					});
				}
				Thread.sleep(listener);
			}
		}catch(Throwable e){
			logger.error("OtherListener error:{}",ExceptionUtil.getErrorMessage(e));
		}
	}

}
