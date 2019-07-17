package com.dtstack.rdos.engine.service.db.dao;

import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.service.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobStopRecord;
import com.dtstack.rdos.engine.service.db.mapper.RdosEngineJobStopRecordMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @author toutian
 */
public class RdosEngineJobStopRecordDAO {

    public Long insert(RdosEngineJobStopRecord rdosEngineJobStopRecord) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Long>() {

            @Override
            public Long execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobStopRecordMapper rdosEngineJobStopRecordMapper = sqlSession.getMapper(RdosEngineJobStopRecordMapper.class);
                return rdosEngineJobStopRecordMapper.insert(rdosEngineJobStopRecord);
            }
        });
    }

    public List<RdosEngineJobStopRecord> listStopJob() {
        return null;
    }

    public void delete(Long id) {
        MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Object>() {

            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobStopRecordMapper rdosEngineJobStopRecordMapper = sqlSession.getMapper(RdosEngineJobStopRecordMapper.class);
                rdosEngineJobStopRecordMapper.delete(id);
                return null;
            }
        });
    }

    public Integer updateVersion(Long id, Integer version) {
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Integer>() {

            @Override
            public Integer execute(SqlSession sqlSession) throws Exception {
                RdosEngineJobStopRecordMapper rdosEngineJobStopRecordMapper = sqlSession.getMapper(RdosEngineJobStopRecordMapper.class);
                return rdosEngineJobStopRecordMapper.updateVersion(id, version);
            }
        });
    }
}
