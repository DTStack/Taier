package com.dtstack.rdos.engine.service.db.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.mapper.RdosEngineBatchJobMapper;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosEngineBatchJobDAO {
	
	public RdosEngineBatchJob getRdosTaskByTaskId(final String jobId){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosEngineBatchJob>(){

			@Override
			public RdosEngineBatchJob execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				return rdosTaskMapper.getRdosJobByJobId(jobId);
			}
			
		});
	}

	public List<RdosEngineBatchJob> getRdosTaskByTaskIds(final List<String> jobIds){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineBatchJob>>(){

			@Override
			public List<RdosEngineBatchJob> execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				return rdosTaskMapper.getRdosJobByJobIds(jobIds);
			}

		});
	}
	
	public void updateJobStatus(final String jobId,final int status){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobStatus(jobId, status);
				return null;
			}
			
		});
	}

	public void updateJobEngineIdAndStatus(final String jobId, final String engineJobId, final int status){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobEngineIdAndStatus(jobId, engineJobId, status);
				return null;
			}

		});
	}

	public void submitFail(final String jobId,final int status, String logInfo){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.jobFail(jobId, status, logInfo);
				return null;
			}

		});
	}

	public void updateJobPluginId(final String jobId, long pluginId){

		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobPluginId(jobId, pluginId);
				return null;
			}

		});
	}
	
	public void updateJobEngineId(final String jobId,final String engineId){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobEngineId(jobId, engineId);
				return null;
			}
		});
	}
	
	
	public void updateJobStatusAndExecTime(final String jobId, final int status){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobStatusAndExecTime(jobId, status);
				return null;
			}
		});
	}
	
    public void updateEngineLog(final String jobId, final String engineLog){
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
                mapper.updateEngineLog(jobId, engineLog);
                return null;
            }
        });
    }


	public void updateSubmitLog(String jobId, String submitLog) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
                mapper.updateSubmitLog(jobId, submitLog);
                return null;
            }
        });
	}


	public void insert(RdosEngineBatchJob rdosEngineBatchJob) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
                mapper.insert(rdosEngineBatchJob);
                return null;
            }
        });
	}

	public List<RdosEngineBatchJob> listStatusByIds(List<String> jobIds){
		return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineBatchJob>>(){
			@Override
			public List<RdosEngineBatchJob> execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				return mapper.listStatusByIds(jobIds);
			}
		});
	}

	public void updateJobStatusAndLog(String jobId, Integer taskStatus, String logInfo, String engineLog, Timestamp gmtModified){
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				mapper.updateJobStatusAndLog(jobId,taskStatus,logInfo,engineLog,gmtModified);
				return null;
			}
		});
	}

	public void batchInsert(Collection<RdosEngineBatchJob> engineJobs){
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				mapper.batchInsert(engineJobs);
				return null;
			}
		});
	}

	public void update(RdosEngineBatchJob engineBatchJob){
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				mapper.update(engineBatchJob);
				return null;
			}
		});
	}

	public void finishJobWithStatus(String jobId, Integer taskStatus){
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				mapper.finishJobWithStatus(jobId,taskStatus);
				return null;
			}
		});
	}

	public void updateUnsubmitJobStatus(String fillDataJobNameLike,Integer status, Long projectId){
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				mapper.updateUnsubmitJobStatus(fillDataJobNameLike,status,projectId);
				return null;
			}
		});
	}
}
