package com.dtstack.rdos.engine.service.db.dao;

import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.db.mapper.RdosEngineJobCacheMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosEngineJobCacheDAO {

    public void insertJob(String jobId, String engineType, Integer computeType, int stage, String jobInfo){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                mapper.insert(jobId, engineType, computeType, stage, jobInfo);
                return null;
            }
        });
    }

    public void updateJobStage(String jobId, int stage){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                mapper.updateStage(jobId, stage);
                return null;
            }
        });
    }

    public void deleteJob(String jobId){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                mapper.delete(jobId);
                return null;
            }
        });
    }

    public RdosEngineJobCache getJobById(String jobId){

        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosEngineJobCache>(){

            @Override
            public RdosEngineJobCache execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.getOne(jobId);
            }
        });
    }

    /**
     * 根据阶段信息加载: stage 1:处于master-queue阶段， 2: 处于worker-exe-queue阶段
     * @param stage
     * @return
     */
    public List<RdosEngineJobCache> getJobForPriorityQueue(int stage){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineJobCache>>(){

            @Override
            public List<RdosEngineJobCache> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.listByStage(stage);
            }
        });
    }
}
