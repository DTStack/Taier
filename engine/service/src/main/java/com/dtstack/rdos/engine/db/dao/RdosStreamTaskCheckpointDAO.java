package com.dtstack.rdos.engine.db.dao;

import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.mapper.RdosStreamTaskCheckpointMapper;
import org.apache.ibatis.session.SqlSession;

/**
 * Reason:
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class RdosStreamTaskCheckpointDAO {

    public void insert(String taskId, String engineTaskId, String checkpointInfo, Long startTime, Long endTime){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosStreamTaskCheckpointMapper taskCheckpointMapper = sqlSession.getMapper(RdosStreamTaskCheckpointMapper.class);
                taskCheckpointMapper.insert(taskId, engineTaskId, checkpointInfo, startTime, endTime);
                return null;
            }
        });
    }
}
