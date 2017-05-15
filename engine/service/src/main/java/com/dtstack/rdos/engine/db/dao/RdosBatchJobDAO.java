package com.dtstack.rdos.engine.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.dataobject.RdosBatchJob;
import com.dtstack.rdos.engine.db.mapper.RdosBatchJobMapper;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosBatchJobDAO {
	
	public RdosBatchJob getRdosTaskByTaskId(final String jobId){
		return (RdosBatchJob)MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosBatchJobMapper.class);
				return rdosTaskMapper.getRdosJobByJobId(jobId);
			}
			
		});
	}
	
	
	public void updateJobStatus(final String jobId,final int stauts){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosBatchJobMapper.class);
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
				RdosBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosBatchJobMapper.class);
				rdosTaskMapper.updateJobEngineId(jobId, engineId);
				return null;
			}
		});
	}
	
	
	public void updateTaskEngineIdAndStatus(final String jobId, final String engineId, final int status){
		
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosBatchJobMapper rdosTaskMapper = sqlSession.getMapper(RdosBatchJobMapper.class);
				rdosTaskMapper.updateJobEngineIdAndStatus(jobId, engineId, status);
				return null;
			}
		});
	}
}
