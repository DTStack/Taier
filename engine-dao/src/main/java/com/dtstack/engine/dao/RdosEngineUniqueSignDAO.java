package com.dtstack.engine.dao;

import com.dtstack.engine.callback.MybatisSessionCallback;
import com.dtstack.engine.callback.MybatisSessionCallbackMethod;
import com.dtstack.engine.domain.RdosEngineUniqueSign;
import org.apache.ibatis.session.SqlSession;
import com.dtstack.engine.mapper.RdosEngineUniqueSignMapper;

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
