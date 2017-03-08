package com.dtstack.rdos.engine.entrance.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.entrance.db.mapper.RdosTaskMapper;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosTaskDAO {
	
	public void updateTaskStatus(final String taskId,final int stauts){
		
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
