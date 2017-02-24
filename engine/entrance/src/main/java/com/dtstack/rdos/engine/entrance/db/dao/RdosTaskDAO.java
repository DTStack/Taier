package com.dtstack.rdos.engine.entrance.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.entrance.db.mapper.RdosTaskMapper;

public class RdosTaskDAO {
	
	public void updateTaskStatus(final String taskId,final byte stauts){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public void execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosTaskMapper rdosTaskMapper = sqlSession.getMapper(RdosTaskMapper.class);
				rdosTaskMapper.updateTaskStatus(taskId, stauts);
			}
			
		});
	}
	
	public void updateTaskEngineId(final String taskId,final String engineId){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public void execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosTaskMapper rdosTaskMapper = sqlSession.getMapper(RdosTaskMapper.class);
				rdosTaskMapper.updateTaskEngineId(taskId, engineId);
			}
		});
	}
}
