package com.dtstack.rdos.engine.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.db.mapper.RdosEngineStreamJobMapper;

import java.util.List;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosEngineStreamJobDAO {
	
	public RdosEngineStreamJob getRdosTaskByTaskId(final String taskId){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosEngineStreamJob>(){

			@Override
			public RdosEngineStreamJob execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				return rdosTaskMapper.getRdosTaskByTaskId(taskId);
			}

		});
	}

	public List<RdosEngineStreamJob> getRdosTaskByTaskIds(final List<String> taskIds){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineStreamJob>>(){

			@Override
			public List<RdosEngineStreamJob> execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				return rdosTaskMapper.getRdosTaskByTaskIds(taskIds);
			}

		});
	}
	
	
	public void updateTaskStatus(final String taskId,final int stauts){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.updateTaskStatus(taskId, stauts);
				return null;
			}
			
		});
	}

	public void updateTaskPluginId(final String taskId, long pluginId){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.updateTaskPluginId(taskId, pluginId);
				return null;
			}

		});
	}
	
	public void updateTaskEngineId(final String taskId,final String engineId){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.updateTaskEngineId(taskId, engineId);
				return null;
			}
		});
	}
	
	
	public void updateTaskEngineIdAndStatus(final String taskId, final String engineId, final int status){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.updateTaskEngineIdAndStatus(taskId, engineId, status);
				return null;
			}
		});
	}
	
    public void updateEngineLog(final String taskId, final String engineLog){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineStreamJobMapper mapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
                mapper.updateEngineLog(taskId, engineLog);
                return null;
            }
        });
    }


	public void updateSubmitLog(String taskId, String submitLog) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineStreamJobMapper mapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
                mapper.updateSubmitLog(taskId, submitLog);
                return null;
            }
        });
	}


	public void insert(RdosEngineStreamJob rdosEngineStreamJob) {

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineStreamJobMapper mapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
                mapper.insert(rdosEngineStreamJob);
                return null;
            }
        });
	}
}
