package com.dtstack.rdos.engine.service.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.db.mapper.RdosEngineStreamJobMapper;

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
	
	
	public void updateTaskStatus(final String taskId,final int status){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.updateTaskStatus(taskId, status);
				return null;
			}
			
		});
	}

	public Integer updateTaskStatusCompareOld(String taskId, Integer status, Integer oldStatus,String taskName) {
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Integer>(){

			@Override
			public Integer execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				return rdosTaskMapper.updateTaskStatusCompareOld(taskId, status, oldStatus, taskName);
			}

		});
	}

	public void updateTaskPluginId(String taskId, long pluginId){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.updateTaskPluginId(taskId, pluginId);
				return null;
			}

		});
	}
	
	public void updateTaskEngineId( String taskId, String engineId, String applicationId){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.updateTaskEngineId(taskId, engineId, applicationId);
				return null;
			}
		});
	}
	
	
	public void updateTaskEngineIdAndStatus(String taskId, String engineId, String applicationId, int status){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.updateTaskEngineIdAndStatus(taskId, engineId, applicationId, status);
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

	public void submitFail(String taskId, Integer status, String s) {

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.submitFail(taskId, status, s);
				return null;
			}

		});
	}

    public RdosEngineStreamJob getByName(String jobName) {
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosEngineStreamJob>(){

			@Override
			public RdosEngineStreamJob execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				return rdosTaskMapper.getByName(jobName);
			}

		});
    }

	public List<String> listNames(String jobName) {
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<String>>(){

			@Override
			public List<String> execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				return rdosTaskMapper.listNames(jobName);
			}

		});
	}

	public List<String> getTaskIdsByStatus(Integer status){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<String>>(){

			@Override
			public List<String> execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				return rdosTaskMapper.getTaskIdsByStatus(status);
			}

		});
	}

	public void updateRetryNum(String jobId, Integer retryNum){
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public List<Object> execute(SqlSession sqlSession) throws Exception {
				RdosEngineStreamJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
				rdosTaskMapper.updateRetryNum(jobId, retryNum);
				return null;
			}

		});
	}
}
