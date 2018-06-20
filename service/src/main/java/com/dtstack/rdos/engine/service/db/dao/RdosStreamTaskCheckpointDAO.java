package com.dtstack.rdos.engine.service.db.dao;

import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.mapper.RdosStreamTaskCheckpointMapper;
import org.apache.ibatis.session.SqlSession;

import java.sql.Timestamp;

/**
 * Reason:
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosStreamTaskCheckpointDAO {

    public void insert(String taskId, String engineTaskId, String checkpointInfo, Timestamp startTime, Timestamp endTime){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                taskCheckpointMapper.insert(taskId, engineTaskId, checkpointInfo, startTime, endTime);
                return null;
            }
        });
    }
}
