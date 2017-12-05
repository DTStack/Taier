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
		return (RdosEngineBatchJob)MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				return rdosTaskMapper.getRdosJobByJobId(jobId);
			}
			
		});
	}
	
	
	public void updateJobStatus(final String jobId,final int stauts){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobStatus(jobId, stauts);
				return null;
			}
			
		});
	}
	
	public void updateJobEngineId(final String jobId,final String engineId){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobEngineId(jobId, engineId);
				return null;
			}
		});
	}
	
	
	public void updateTaskEngineIdAndStatus(final String jobId, final String engineId, final int status){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosEngineBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
				rdosTaskMapper.updateJobEngineIdAndStatus(jobId, engineId, status);
				return null;
			}
		});
	}
	
    public void updateEngineLog(final String jobId, final String engineLog){
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
                mapper.updateEngineLog(jobId, engineLog);
                return null;
            }
        });
    }
}
