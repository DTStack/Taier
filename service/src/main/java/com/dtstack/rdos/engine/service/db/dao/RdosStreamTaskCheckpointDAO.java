package com.dtstack.rdos.engine.service.db.dao;

import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
import com.dtstack.rdos.engine.service.db.mapper.RdosStreamTaskCheckpointMapper;
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

    public void insert(String taskId, String engineTaskId, String checkpointId, Timestamp checkpointTrigger, String checkpointSavepath, Timestamp startTime, Timestamp endTime){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                taskCheckpointMapper.insert(taskId, engineTaskId, checkpointId, checkpointTrigger, checkpointSavepath, startTime, endTime);
                return null;
            }
        });
    }


    public List<RdosStreamTaskCheckpoint> listByTaskIdAndRangeTime(String engineTaskId, Long triggerStart, Long triggerEnd){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosStreamTaskCheckpoint>>(){

            @Override
            public List<RdosStreamTaskCheckpoint> execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                return taskCheckpointMapper.listByTaskIdAndRangeTime(engineTaskId, triggerStart, triggerEnd);
            }
        });
    }

    public List<RdosStreamTaskCheckpoint> getByCheckpointIndexAndCount(int startIndex, int count){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<List<RdosStreamTaskCheckpoint>>(){

            @Override
            public List<RdosStreamTaskCheckpoint> execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                return taskCheckpointMapper.getByCheckpointIndexAndCount(startIndex, count);
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

    public void deleteRecordByCheckpointIDAndTaskEngineID(String taskEngineId, String checkpointID) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                taskCheckpointMapper.deleteByEngineTaskIdAndCheckpointID(taskEngineId, checkpointID);
                return null;
            }
        });
    }
}
