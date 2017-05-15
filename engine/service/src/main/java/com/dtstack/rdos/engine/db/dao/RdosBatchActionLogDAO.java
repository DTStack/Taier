package com.dtstack.rdos.engine.db.dao;

import com.dtstack.rdos.engine.db.dataobject.RdosBatchActionLog;
import com.dtstack.rdos.engine.db.mapper.RdosBatchActionLogMapper;
import org.apache.ibatis.session.SqlSession;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosBatchActionLogDAO {

	public void updateActionStatus(final Long actionLogId,final int status){
		MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){
			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				RdosBatchActionLogMapper rdosActionLogMapper = sqlSession.getMapper(RdosBatchActionLogMapper.class);
				rdosActionLogMapper.updateActionStatus(actionLogId, status);
				return null;
			}
		});
	}

	public RdosBatchActionLog findActionLogById(final Long actionLogId){
        return  (RdosBatchActionLog)MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){
            @Override
            public Object execute(SqlSession sqlSession) throws Exception {
                RdosBatchActionLogMapper rdosActionLogMapper = sqlSession.getMapper(RdosBatchActionLogMapper.class);
                return rdosActionLogMapper.findActionLogById(actionLogId);
            }
        });
    }


}
