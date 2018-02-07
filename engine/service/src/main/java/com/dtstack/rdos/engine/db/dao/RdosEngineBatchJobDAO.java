package com.dtstack.rdos.engine.db.dao;

import org.apache.ibatis.session.SqlSession;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.db.mapper.RdosEngineBatchJobMapper;

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
	
	public void updateJobStatus(final String jobId,final int stauts){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobStatus(jobId, stauts);
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
	
	
	public void updateTaskEngineIdAndStatus(final String jobId, final String engineId, final int status){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobEngineIdAndStatus(jobId, engineId, status);
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
}
