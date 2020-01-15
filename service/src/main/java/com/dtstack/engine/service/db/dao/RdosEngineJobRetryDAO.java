package com.dtstack.engine.service.db.dao;

import com.dtstack.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobRetry;
import com.dtstack.engine.service.db.mapper.RdosEngineJobRetryMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @author toutian
 */
public class RdosEngineJobRetryDAO {

	public void insert(RdosEngineJobRetry rdosEngineBatchJobRetry) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineJobRetryMapper mapper = sqlSession.getMapper(RdosEngineJobRetryMapper.class);
                mapper.insert(rdosEngineBatchJobRetry);
                return null;
            }
        });
	}

    public RdosEngineJobRetry getJobRetryByJobId(String jobId, int retrynum) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosEngineJobRetry>(){

            @Override
            public RdosEngineJobRetry execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobRetryMapper mapper = sqlSession.getMapper(RdosEngineJobRetryMapper.class);
                return mapper.getJobRetryByJobId(jobId, retrynum);
            }
        });
    }

    public String getRetryTaskParams(String jobId, int retrynum) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<String>(){
            @Override
            public String execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobRetryMapper mapper = sqlSession.getMapper(RdosEngineJobRetryMapper.class);
                return mapper.getRetryTaskParams(jobId, retrynum);
            }
        });
    }

    public void removeByJobId(String jobId) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobRetryMapper mapper = sqlSession.getMapper(RdosEngineJobRetryMapper.class);
                mapper.removeByJobId(jobId);
                return null;
            }
        });
    }

    public void updateEngineLog(long id, String engineLog){
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobRetryMapper mapper = sqlSession.getMapper(RdosEngineJobRetryMapper.class);
                mapper.updateEngineLog(id, engineLog);
                return null;
            }
        });
    }
}
