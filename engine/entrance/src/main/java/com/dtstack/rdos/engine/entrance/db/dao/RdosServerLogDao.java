package com.dtstack.rdos.engine.entrance.db.dao;

import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.entrance.db.dataobject.RdosServerLog;
import com.dtstack.rdos.engine.entrance.db.mapper.RdosServerLogMapper;
import com.dtstack.rdos.engine.entrance.db.mapper.RdosTaskMapper;
import org.apache.ibatis.session.SqlSession;

/**
 * Reason:
 * Date: 2017/3/7
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class RdosServerLogDao {

    public void insertLog(final String taskId, final String logInfo){

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

            @Override
            public void execute(SqlSession sqlSession) throws Exception {
                RdosServerLogMapper mapper = sqlSession.getMapper(RdosServerLogMapper.class);
                mapper.insertSvrLog(taskId, logInfo);
            }

        });
    }
}
