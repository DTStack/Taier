package com.dtstack.rdos.engine.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.dataobject.RdosStreamTask;
import com.dtstack.rdos.engine.db.mapper.RdosStreamTaskMapper;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosStreamTaskDAO {
	
	public RdosStreamTask getRdosTaskByTaskId(final String taskId){
		return (RdosStreamTask)MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosStreamTaskMapper rdosTaskMapper = sqlSession.getMapper(RdosStreamTaskMapper.class);
				return rdosTaskMapper.getRdosTaskByTaskId(taskId);
			}
			
		});
	}
	
	
	public void updateTaskStatus(final String taskId,final int stauts){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosStreamTaskMapper rdosTaskMapper = sqlSession.getMapper(RdosStreamTaskMapper.class);
				rdosTaskMapper.updateTaskStatus(taskId, stauts);
				return null;
			}
			
		});
	}
	
	public void updateTaskEngineId(final String taskId,final String engineId){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosStreamTaskMapper rdosTaskMapper = sqlSession.getMapper(RdosStreamTaskMapper.class);
				rdosTaskMapper.updateTaskEngineId(taskId, engineId);
				return null;
			}
		});
	}
	
	
	public void updateTaskEngineIdAndStatus(final String taskId, final String engineId, final int status){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosStreamTaskMapper rdosTaskMapper = sqlSession.getMapper(RdosStreamTaskMapper.class);
				rdosTaskMapper.updateTaskEngineIdAndStatus(taskId, engineId, status);
				return null;
			}
		});
	}
}
