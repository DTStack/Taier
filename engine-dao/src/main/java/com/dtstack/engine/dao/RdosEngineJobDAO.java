package com.dtstack.engine.dao;

import com.dtstack.engine.callback.MybatisSessionCallbackMethod;
import com.dtstack.engine.domain.RdosEngineJob;
import org.apache.ibatis.session.SqlSession;
import com.dtstack.engine.callback.MybatisSessionCallback;
import com.dtstack.engine.mapper.RdosEngineJobMapper;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 *
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosEngineJobDAO {

	public RdosEngineJob getRdosTaskByTaskId(final String jobId){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosEngineJob>(){

			@Override
			public RdosEngineJob execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				return rdosTaskMapper.getRdosJobByJobId(jobId);
			}

		});
	}

	public List<RdosEngineJob> getRdosTaskByTaskIds(final List<String> jobIds){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineJob>>(){

			@Override
			public List<RdosEngineJob> execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				return rdosTaskMapper.getRdosJobByJobIds(jobIds);
			}

		});
	}

	public void updateJobStatus(final String jobId,final int status){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				rdosTaskMapper.updateJobStatus(jobId, status);
				return null;
			}

		});
	}

	public void updateTaskStatusNotStopped(final String jobId,final int status, final List<Integer> stopStatuses){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				rdosTaskMapper.updateTaskStatusNotStopped(jobId, status, stopStatuses);
				return null;
			}

		});
	}

	public Integer updateTaskStatusCompareOld(String jobId, Integer status, Integer oldStatus,String jobName) {
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Integer>(){

			@Override
			public Integer execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				return rdosTaskMapper.updateTaskStatusCompareOld(jobId, status,oldStatus, jobName);
			}

		});
	}

	public void updateJobEngineIdAndStatus(final String jobId, final String engineJobId, final int status,String applactionId){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				rdosTaskMapper.updateJobEngineIdAndStatus(jobId, engineJobId, status, applactionId);
				return null;
			}

		});
	}

	public void updateJobSubmitFailed(final String jobId, final String engineJobId, final int status,String applactionId){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				rdosTaskMapper.updateJobSubmitFailed(jobId, engineJobId, status, applactionId);
				return null;
			}

		});
	}

	public void submitFail(final String jobId,final int status, String logInfo){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				rdosTaskMapper.jobFail(jobId, status, logInfo);
				return null;
			}

		});
	}

	public void updateJobPluginId(final String jobId, long pluginId){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				rdosTaskMapper.updateJobPluginId(jobId, pluginId);
				return null;
			}

		});
	}

	public void updateJobEngineId(final String jobId,final String engineId,String applactionId){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				rdosTaskMapper.updateJobEngineId(jobId, engineId,applactionId);
				return null;
			}
		});
	}

	public void updateRetryTaskParams(final String jobId, final String retryTaskParams){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				rdosTaskMapper.updateRetryTaskParams(jobId, retryTaskParams);
				return null;
			}
		});
	}


	public void updateJobStatusAndExecTime(final String jobId, final int status){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				rdosTaskMapper.updateJobStatusAndExecTime(jobId, status);
				return null;
			}
		});
	}

    public void updateEngineLog(final String jobId, final String engineLog){
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineJobMapper mapper = sqlSession.getMapper(RdosEngineJobMapper.class);
                mapper.updateEngineLog(jobId, engineLog);
                return null;
            }
        });
    }


	public void updateSubmitLog(String jobId, String submitLog) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineJobMapper mapper = sqlSession.getMapper(RdosEngineJobMapper.class);
                mapper.updateSubmitLog(jobId, submitLog);
                return null;
            }
        });
	}


	public void insert(RdosEngineJob rdosEngineBatchJob) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineJobMapper mapper = sqlSession.getMapper(RdosEngineJobMapper.class);
                mapper.insert(rdosEngineBatchJob);
                return null;
            }
        });
	}

    public RdosEngineJob getByName(String jobName) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosEngineJob>(){

            @Override
            public RdosEngineJob execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobMapper mapper = sqlSession.getMapper(RdosEngineJobMapper.class);
                return mapper.getByName(jobName);
            }
        });
    }

    public List<String> listNames(String jobName) {
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<String>>(){

			@Override
			public List<String> execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper mapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				return mapper.listNames(jobName);
			}
		});
    }

	public void updateRetryNum(final String jobId, final Integer retryNum){
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper mapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				mapper.updateRetryNum(jobId, retryNum);
				return null;
			}
		});
	}

	public Integer resetExecTime(final String jobId){
	    return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Integer>() {
            @Override
            public Integer execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobMapper mapper = sqlSession.getMapper(RdosEngineJobMapper.class);
                return mapper.resetExecTime(jobId);
            }
        });
    }
	
	public List<String> getTaskIdsByStatus(Integer status, Integer computeType){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<String>>(){

			@Override
			public List<String> execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper mapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				return mapper.getTaskIdsByStatus(status, computeType);
			}

		});
	}

	public List<RdosEngineJob> listJobStatus(Timestamp timeStamp, Integer computeType) {
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineJob>>() {
			@Override
			public List<RdosEngineJob> execute(SqlSession sqlSession) throws Exception {
				RdosEngineJobMapper mapper = sqlSession.getMapper(RdosEngineJobMapper.class);
				return mapper.listJobStatus(timeStamp, computeType);
			}
		});
	}
}
