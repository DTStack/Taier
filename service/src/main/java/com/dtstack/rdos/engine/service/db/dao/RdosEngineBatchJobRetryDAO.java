package com.dtstack.rdos.engine.service.db.dao;

import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.mapper.RdosEngineBatchJobMapper;
import org.apache.ibatis.session.SqlSession;

/**
 * @author toutian
 */
public class RdosEngineBatchJobRetryDAO {

	public void insert(RdosEngineBatchJob rdosEngineBatchJob) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>(){

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
            	RdosEngineBatchJobMapper mapper = sqlSession.getMapper(RdosEngineBatchJobMapper.class);
                mapper.insert(rdosEngineBatchJob);
                return null;
            }
        });
	}

}
