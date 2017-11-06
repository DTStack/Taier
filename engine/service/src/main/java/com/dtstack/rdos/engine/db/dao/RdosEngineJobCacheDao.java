package com.dtstack.rdos.engine.db.dao;

import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.db.mapper.RdosEngineJobCacheMapper;
import org.apache.ibatis.session.SqlSession;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class RdosEngineJobCacheDao {

    public void insertJob(String jobId, String jobInfo){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                mapper.insert(jobId, jobInfo);
                return null;
            }
        });
    }

    public void deleteJob(String jobId){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                mapper.delete(jobId);
                return null;
            }
        });
    }

    public RdosEngineJobCache getJobById(String jobId){

        return (RdosEngineJobCache) MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.getOne(jobId);
            }
        });
    }
}
