package com.dtstack.rdos.engine.db.dao;

import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineUniqueSign;
import org.apache.ibatis.session.SqlSession;
import com.dtstack.rdos.engine.db.mapper.RdosEngineUniqueSignMapper;

/**
 * Created by sishu.yss on 2018/3/8.
 */
public class RdosEngineUniqueSignDAO {

    public Long generate(RdosEngineUniqueSign generateUniqueSign){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Long>(){

            @Override
            public Long execute(SqlSession sqlSession) throws Exception {
                RdosEngineUniqueSignMapper generateUniqueSignMapper = sqlSession.getMapper(RdosEngineUniqueSignMapper.class);
                return generateUniqueSignMapper.insert(generateUniqueSign);
            }
        });
    }
}
