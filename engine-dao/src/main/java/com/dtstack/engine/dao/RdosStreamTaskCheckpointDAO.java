package com.dtstack.engine.dao;

import com.dtstack.engine.callback.MybatisSessionCallback;
import com.dtstack.engine.callback.MybatisSessionCallbackMethod;
import com.dtstack.engine.domain.RdosStreamTaskCheckpoint;
import com.dtstack.engine.mapper.RdosStreamTaskCheckpointMapper;
import org.apache.ibatis.session.SqlSession;

import java.sql.Timestamp;
import java.util.List;

/**
 * Reason:
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosStreamTaskCheckpointDAO {

    public void insert(String taskId, String engineTaskId, String checkpointId, Timestamp checkpointTrigger, String checkpointSavepath, String checkpointCounts){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                taskCheckpointMapper.insert(taskId, engineTaskId, checkpointId, checkpointTrigger, checkpointSavepath, checkpointCounts);
                return null;
            }
        });
    }

    public List<RdosStreamTaskCheckpoint> listByTaskIdAndRangeTime(String taskId, Long triggerStart, Long triggerEnd){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosStreamTaskCheckpoint>>(){

            @Override
            public List<RdosStreamTaskCheckpoint> execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                return taskCheckpointMapper.listByTaskIdAndRangeTime(taskId, triggerStart, triggerEnd);
            }
        });
    }

    public List<RdosStreamTaskCheckpoint> getByTaskEngineIDAndCheckpointIndexAndCount(String taskEngineID, int startIndex, int count){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosStreamTaskCheckpoint>>(){

            @Override
            public List<RdosStreamTaskCheckpoint> execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                return taskCheckpointMapper.getByTaskEngineIDAndCheckpointIndexAndCount(taskEngineID, startIndex, count);
            }
        });
    }


    public RdosStreamTaskCheckpoint getByTaskIdAndEngineTaskId(String taskId, String engineTaskId){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosStreamTaskCheckpoint>(){

            @Override
            public RdosStreamTaskCheckpoint execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                return taskCheckpointMapper.getByTaskIdAndEngineTaskId(taskId, engineTaskId);
            }
        });
    }

    public void batchDeleteByEngineTaskIdAndCheckpointID(String taskEngineId, String checkpointID) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                taskCheckpointMapper.batchDeleteByEngineTaskIdAndCheckpointID(taskEngineId, checkpointID);
                return null;
            }
        });
    }

    public void cleanAllCheckpointByTaskEngineId(String taskEngineId) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                taskCheckpointMapper.cleanAllCheckpointByTaskEngineId(taskEngineId);
                return null;
            }
        });
    }

    public Integer update(String taskId, String checkpoint){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Integer>() {
            @Override
            public Integer execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                return taskCheckpointMapper.updateCheckpoint(taskId, checkpoint);
            }
        });
    }

    public RdosStreamTaskCheckpoint getByTaskId(String taskId){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosStreamTaskCheckpoint>() {
            @Override
            public RdosStreamTaskCheckpoint execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                return taskCheckpointMapper.getByTaskId(taskId);
            }
        });
    }

    public Integer deleteByTaskId(String taskId){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Integer>() {
            @Override
            public Integer execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                return taskCheckpointMapper.deleteByTaskId(taskId);
            }
        });
    }
}
