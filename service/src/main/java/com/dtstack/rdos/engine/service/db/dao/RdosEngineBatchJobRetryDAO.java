package com.dtstack.rdos.engine.service.db.dao;

import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJobRetry;
import com.dtstack.rdos.engine.service.db.mapper.RdosEngineBatchJobRetryMapper;
import org.apache.ibatis.session.SqlSession;

/**
 * @author toutian
 */
public class RdosEngineBatchJobRetryDAO {

	public void insert(RdosEngineBatchJobRetry rdosEngineBatchJobRetry) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineBatchJobRetryMapper mapper = sqlSession.getMapper(RdosEngineBatchJobRetryMapper.class);
                mapper.insert(rdosEngineBatchJobRetry);
                return null;
            }
        });
	}

    public RdosEngineBatchJobRetry getJobRetryByJobId(String jobId) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosEngineBatchJobRetry>(){

            @Override
            public RdosEngineBatchJobRetry execute(SqlSession sqlSession) throws Exception {
                RdosEngineBatchJobRetryMapper mapper = sqlSession.getMapper(RdosEngineBatchJobRetryMapper.class);
                return mapper.getJobRetryByJobId(jobId);
            }
        });
    }

}
