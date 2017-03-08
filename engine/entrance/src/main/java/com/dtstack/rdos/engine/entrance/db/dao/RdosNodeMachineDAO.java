package com.dtstack.rdos.engine.entrance.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.entrance.db.dataobject.RdosNodeMachine;
import com.dtstack.rdos.engine.entrance.db.mapper.RdosNodeMachineMapper;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosNodeMachineDAO {
	
	public void insert(String ip,long port,int machineType){
		final RdosNodeMachine rdosNodeMachine = new RdosNodeMachine(ip,port,machineType);
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){
			@Override
			public void execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.insert(rdosNodeMachine);
			}
		});
	}
	
	
	public void insert(String localAddress,int machineType){
		String[] args = localAddress.split(":");
		insert(args[0],Integer.parseInt(args[1]),machineType);
	}
	
	
	public void updateMachineType(String localAddress,int machineType){
		String[] args = localAddress.split(":");
		updateMachineType(args[0],Integer.parseInt(args[1]),machineType);
	}
	
	public void disableMachineNode(String localAddress,int machineType){
		String[] args = localAddress.split(":");
		disableMachineNode(args[0],Integer.parseInt(args[1]),machineType);
	}
	
	public void disableMachineNode(String ip,long port,int machineType){
		final RdosNodeMachine rdosNodeMachine = new RdosNodeMachine(ip,port,machineType);
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){
			@Override
			public void execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.disableMachineNode(rdosNodeMachine);
			}
		});
	}
	
	
	public void updateMachineType(String ip,long port,int machineType){
		final RdosNodeMachine rdosNodeMachine = new RdosNodeMachine(ip,port,machineType);
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){
			@Override
			public void execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.updateMachineType(rdosNodeMachine);
			}
		});
	}
}
