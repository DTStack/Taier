package com.dtstack.engine.dtscript.service.db.dao;

import com.dtstack.engine.dtscript.service.db.callback.MybatisSessionCallback;
import com.dtstack.engine.dtscript.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.engine.dtscript.service.db.dataobject.RdosEngineUniqueSign;
import org.apache.ibatis.session.SqlSession;
import com.dtstack.engine.dtscript.service.db.mapper.RdosEngineUniqueSignMapper;

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
