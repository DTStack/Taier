package com.dtstack.engine.dao;

import com.dtstack.engine.callback.MybatisSessionCallback;
import com.dtstack.engine.callback.MybatisSessionCallbackMethod;
import com.dtstack.engine.domain.RdosEngineJobCache;
import com.dtstack.engine.mapper.RdosEngineJobCacheMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosEngineJobCacheDAO {

    public void insertJob(String jobId, String engineType, Integer computeType,
                          int stage, String jobInfo, String nodeAddress, String jobName, Long jobPriority, String jobResource){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                mapper.insert(jobId, engineType, computeType, stage, jobInfo, nodeAddress, jobName, jobPriority, jobResource);
                return null;
            }
        });
    }

    public void updateJobStage(String jobId, int stage, String nodeAddress, Long jobPriority){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                mapper.updateStage(jobId, stage, nodeAddress, jobPriority);
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
    public List<RdosEngineJobCache> getJobForPriorityQueue(Long id, String nodeAddress, Integer stage, String engineType){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineJobCache>>(){

            @Override
            public List<RdosEngineJobCache> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.listByStage(id,nodeAddress, stage, engineType);
            }
        });
    }

    public List<RdosEngineJobCache> getJobByIds(List<String> jobIds) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineJobCache>>(){

            @Override
            public List<RdosEngineJobCache> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.getByJobIds(jobIds);
            }
        });
    }

    public List<String> listNames(int computeType, String jobName) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<String>>(){

            @Override
            public List<String> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.listNames(computeType, jobName);
            }
        });
    }

    public int countGroupQueueJob(String jobResource, Integer stage, String localAddress){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Integer>(){

            @Override
            public Integer execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.countByStage(jobResource, stage, localAddress);
            }
        });
    }
}
