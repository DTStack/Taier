package com.dtstack.rdos.engine.db.dao;

import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.dataobject.GenerateUniqueSign;
import org.apache.ibatis.session.SqlSession;
import com.dtstack.rdos.engine.db.mapper.GenerateUniqueSignMapper;

/**
 * Created by sishu.yss on 2018/3/8.
 */
public class GenerateUniqueSignDAO {

    public Long generate(GenerateUniqueSign generateUniqueSign){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Long>(){

            @Override
            public Long execute(SqlSession sqlSession) throws Exception {
                GenerateUniqueSignMapper generateUniqueSignMapper = sqlSession.getMapper(GenerateUniqueSignMapper.class);
                return generateUniqueSignMapper.insert(generateUniqueSign);
            }
        });
    }
}
