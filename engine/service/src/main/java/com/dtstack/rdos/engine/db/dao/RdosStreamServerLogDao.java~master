package com.dtstack.rdos.engine.db.dao;

import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.mapper.RdosStreamServerLogMapper;

import org.apache.ibatis.session.SqlSession;

/**
 * Reason:
 * Date: 2017/3/7
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class RdosStreamServerLogDao {

    public void insertLog(final String taskId, final String engineTaskId, final Long actionLogId, final String logInfo){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosStreamServerLogMapper mapper = sqlSession.getMapper(RdosStreamServerLogMapper.class);
                mapper.insertSvrLog(taskId, engineTaskId, actionLogId, logInfo);
                return null;
            }
        });
    }
}
