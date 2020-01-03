package com.dtstack.engine.dao;

import com.dtstack.engine.callback.MybatisSessionCallbackMethod;
import com.dtstack.engine.common.enums.MachineAppType;
import com.dtstack.engine.domain.RdosNodeMachine;
import org.apache.ibatis.session.SqlSession;

import com.dtstack.engine.callback.MybatisSessionCallback;
import com.dtstack.engine.mapper.RdosNodeMachineMapper;

import java.util.List;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosNodeMachineDAO {
	
	public void insert(String ip, long port, int machineType, MachineAppType machineAppType, String deployInfo){
		final RdosNodeMachine rdosNodeMachine = new RdosNodeMachine(ip, port, machineType, machineAppType.getType(), deployInfo);

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.insert(rdosNodeMachine);
				return null;
			}
		});
	}
	
	
	public void insert(String localAddress, int machineType, MachineAppType machineAppType, String deployInfo){
		String[] args = localAddress.split(":");
		insert(args[0], Integer.parseInt(args[1]), machineType, machineAppType, deployInfo);
	}
	
	
	public void updateMachineType(String localAddress,int machineType){
		String[] args = localAddress.split(":");
		updateMachineType(args[0],Integer.parseInt(args[1]),machineType);
	}
	
	public void disableMachineNode(String localAddress,int machineType){
		String[] args = localAddress.split(":");
		disableMachineNode(args[0],Integer.parseInt(args[1]),machineType);
	}


    public void ableMachineNode(String localAddress,int machineType){
        String[] args = localAddress.split(":");
        ableMachineNode(args[0],Integer.parseInt(args[1]),machineType);
    }

	public void ableMachineNode(String ip,long port,int machineType){
		final RdosNodeMachine rdosNodeMachine = new RdosNodeMachine(ip, port, machineType,
				MachineAppType.ENGINE.getType(), "");

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.ableMachineNode(rdosNodeMachine);
				return null;
			}
		});
	}
	
	public void disableMachineNode(String ip,long port,int machineType){
		final RdosNodeMachine rdosNodeMachine = new RdosNodeMachine(ip, port, machineType,
				MachineAppType.ENGINE.getType(), "");

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.disableMachineNode(rdosNodeMachine);
				return null;
			}
		});
	}
	
	
	public void updateMachineType(String ip,long port,int machineType){
		final RdosNodeMachine rdosNodeMachine = new RdosNodeMachine(ip, port, machineType,
				MachineAppType.ENGINE.getType(), "");

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.updateMachineType(rdosNodeMachine);
				return null;
			}
		});
	}

	public void updateOneTypeMachineToSlave(String type) {
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.updateOneTypeMachineToSlave(type);
				return null;
			}
		});
	}

    public void updateMachineToMaster(String ip, String appType) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
                rdosNodeMachineMapper.updateMachineToMaster(ip, appType);
                return null;
            }
        });
    }

    public List<RdosNodeMachine> listByAppType(String type){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosNodeMachine>>(){
			@Override
			public List<RdosNodeMachine> execute(SqlSession sqlSession) throws Exception {
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.listByAppType(type);
				return null;
			}
		});
	}

	public RdosNodeMachine getByAppTypeAndMachineType(String appType,int machineType){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosNodeMachine>(){
			@Override
			public RdosNodeMachine execute(SqlSession sqlSession) throws Exception {
				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
				rdosNodeMachineMapper.getByAppTypeAndMachineType(appType,machineType);
				return null;
			}
		});
	}
}
