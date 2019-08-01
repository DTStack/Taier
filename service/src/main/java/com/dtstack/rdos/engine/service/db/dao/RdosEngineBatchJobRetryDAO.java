package com.dtstack.rdos.engine.service.db.dao;

import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJobRetry;
import com.dtstack.rdos.engine.service.db.mapper.RdosEngineBatchJobRetryMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

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

    public List<RdosEngineBatchJobRetry> getJobRetryByJobId(String jobId) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineBatchJobRetry>>(){

            @Override
            public List<RdosEngineBatchJobRetry> execute(SqlSession sqlSession) throws Exception {
                RdosEngineBatchJobRetryMapper mapper = sqlSession.getMapper(RdosEngineBatchJobRetryMapper.class);
                return mapper.getJobRetryByJobId(jobId);
            }
        });
    }

    public String getRetryTaskParams(String jobId, int retrynum) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<String>(){
            @Override
            public String execute(SqlSession sqlSession) throws Exception {
                RdosEngineBatchJobRetryMapper mapper = sqlSession.getMapper(RdosEngineBatchJobRetryMapper.class);
                return mapper.getRetryTaskParams(jobId, retrynum);
            }
        });
    }

    public void removeByJobId(String jobId) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineBatchJobRetryMapper mapper = sqlSession.getMapper(RdosEngineBatchJobRetryMapper.class);
                mapper.removeByJobId(jobId);
                return null;
            }
        });
    }
}
