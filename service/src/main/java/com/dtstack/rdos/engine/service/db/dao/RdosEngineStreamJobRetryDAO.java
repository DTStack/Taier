package com.dtstack.rdos.engine.service.db.dao;

import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.db.mapper.RdosEngineStreamJobMapper;
import org.apache.ibatis.session.SqlSession;

/**
 * @author toutian
 */
public class RdosEngineStreamJobRetryDAO {
	
	public void insert(RdosEngineStreamJob rdosEngineStreamJob) {

        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineStreamJobMapper mapper = sqlSession.getMapper(RdosEngineStreamJobMapper.class);
                mapper.insert(rdosEngineStreamJob);
                return null;
            }
        });
	}

}
