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


//	public void insert(String ip, long port, int machineType, MachineAppType machineAppType, String deployInfo){
//		final NodeMachine nodeMachine = new NodeMachine(ip, port, machineType, machineAppType.getType(), deployInfo);
//
//		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
//
//			@Override
//			public Object execute(SqlSession sqlSession) throws Exception {
//				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
//				rdosNodeMachineMapper.insert(nodeMachine);
//				return null;
//			}
//		});
//	}
//
//
//	public void insert(String localAddress, int machineType, MachineAppType machineAppType, String deployInfo){
//		String[] args = localAddress.split(":");
//		insert(args[0], Integer.parseInt(args[1]), machineType, machineAppType, deployInfo);
//	}
//
//
//	public void updateMachineType(String localAddress,int machineType){
//		String[] args = localAddress.split(":");
//		updateMachineType(args[0],Integer.parseInt(args[1]),machineType);
//	}
//
//	public void disableMachineNode(String localAddress,int machineType){
//		String[] args = localAddress.split(":");
//		disableMachineNode(args[0],Integer.parseInt(args[1]),machineType);
//	}
//
//
//    public void ableMachineNode(String localAddress,int machineType){
//        String[] args = localAddress.split(":");
//        ableMachineNode(args[0],Integer.parseInt(args[1]),machineType);
//    }
//
//	public void ableMachineNode(String ip,long port,int machineType){
//		final NodeMachine nodeMachine = new NodeMachine(ip, port, machineType,
//				MachineAppType.ENGINE.getType(), "");
//
//		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
//			@Override
//			public Object execute(SqlSession sqlSession) throws Exception {
//				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
//				rdosNodeMachineMapper.ableMachineNode(nodeMachine);
//				return null;
//			}
//		});
//	}
//
//	public void disableMachineNode(String ip,long port,int machineType){
//		final NodeMachine nodeMachine = new NodeMachine(ip, port, machineType,
//				MachineAppType.ENGINE.getType(), "");
//
//		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
//			@Override
//			public Object execute(SqlSession sqlSession) throws Exception {
//				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
//				rdosNodeMachineMapper.disableMachineNode(nodeMachine);
//				return null;
//			}
//		});
//	}
//
//
//	public void updateMachineType(String ip,long port,int machineType){
//		final NodeMachine nodeMachine = new NodeMachine(ip, port, machineType,
//				MachineAppType.ENGINE.getType(), "");
//
//		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
//			@Override
//			public Object execute(SqlSession sqlSession) throws Exception {
//				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
//				rdosNodeMachineMapper.updateMachineType(nodeMachine);
//				return null;
//			}
//		});
//	}
//
//	public void updateOneTypeMachineToSlave(String type) {
//		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
//			@Override
//			public Object execute(SqlSession sqlSession) throws Exception {
//				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
//				rdosNodeMachineMapper.updateOneTypeMachineToSlave(type);
//				return null;
//			}
//		});
//	}
//
//    public void updateMachineToMaster(String ip, String appType) {
//        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
//            @Override
//            public Object execute(SqlSession sqlSession) throws Exception {
//                RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
//                rdosNodeMachineMapper.updateMachineToMaster(ip, appType);
//                return null;
//            }
//        });
//    }
//
//    public List<NodeMachine> listByAppType(String type){
//		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<NodeMachine>>(){
//			@Override
//			public List<NodeMachine> execute(SqlSession sqlSession) throws Exception {
//				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
//				rdosNodeMachineMapper.listByAppType(type);
//				return null;
//			}
//		});
//	}
//
//	public NodeMachine getByAppTypeAndMachineType(String appType, int machineType){
//		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<NodeMachine>(){
//			@Override
//			public NodeMachine execute(SqlSession sqlSession) throws Exception {
//				RdosNodeMachineMapper rdosNodeMachineMapper = sqlSession.getMapper(RdosNodeMachineMapper.class);
//				rdosNodeMachineMapper.getByAppTypeAndMachineType(appType,machineType);
//				return null;
//			}
//		});
//	}
}
