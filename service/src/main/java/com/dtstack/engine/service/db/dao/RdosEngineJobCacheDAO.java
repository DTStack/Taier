package com.dtstack.engine.service.db.dao;

import com.dtstack.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.engine.service.db.mapper.RdosEngineJobCacheMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosEngineJobCacheDAO {

    public void insertJob(String jobId, String engineType, Integer computeType,
                          int stage, String jobInfo, String nodeAddress, String jobName, Long jobPriority, String groupName){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                mapper.insert(jobId, engineType, computeType, stage, jobInfo, nodeAddress, jobName, jobPriority, groupName);
                return null;
            }
        });
    }

    public void updateJobStage(String jobId, int stage, String nodeAddress, Long jobPriority, String groupName){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                mapper.updateStage(jobId, stage, nodeAddress, jobPriority, groupName);
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
    public List<RdosEngineJobCache> getJobForPriorityQueue(Long id, String nodeAddress, Integer stage, String engineType, String groupName){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineJobCache>>(){

            @Override
            public List<RdosEngineJobCache> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.listByStage(id,nodeAddress, stage, engineType, groupName);
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

    public List<String> listNames(String jobName) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<String>>(){

            @Override
            public List<String> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.listNames(jobName);
            }
        });
    }

    public List<String> getAllNodeAddress() {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<String>>(){

            @Override
            public List<String> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.getAllNodeAddress();
            }
        });
    }

    public int countByStage(String engineType, String groupName, List<Integer> stages, String nodeAddress) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Integer>(){

            @Override
            public Integer execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.countByStage(engineType, groupName, stages, nodeAddress);
            }
        });
    }

    public Long maxPriorityByStage(String engineType, String groupName, Integer stage, String nodeAddress) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Long>(){

            @Override
            public Long execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.maxPriorityByStage(engineType, groupName, stage, nodeAddress);
            }
        });
    }

    public Long countByJobResource(String engineType, String groupName, String nodeAddress) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Long>(){

            @Override
            public Long execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.countByJobResource(engineType, groupName, nodeAddress);
            }
        });
    }

    public List<RdosEngineJobCache> listByJobResource(String engineType, String groupName, String nodeAddress) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosEngineJobCache>>(){

            @Override
            public List<RdosEngineJobCache> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.listByJobResource(engineType, groupName, nodeAddress);
            }
        });
    }

    public List<String> getJobResources() {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<String>>(){

            @Override
            public List<String> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.getJobResources();
            }
        });
    }

    public List<Map<String,Object>> groupByJobResource(String nodeAddress) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<Map<String,Object>>>(){

            @Override
            public List<Map<String,Object>> execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobCacheMapper mapper = sqlSession.getMapper(RdosEngineJobCacheMapper.class);
                return mapper.groupByJobResource(nodeAddress);
            }
        });
    }
}
