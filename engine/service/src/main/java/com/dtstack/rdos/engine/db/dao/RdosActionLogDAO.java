package com.dtstack.rdos.engine.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.mapper.RdosStreamActionLogMapper;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosActionLogDAO {

	  public void updateActionStatus(final Long actionLogId,final int status){
		  MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public Object execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosStreamActionLogMapper rdosActionLogMapper = sqlSession.getMapper(RdosStreamActionLogMapper.class);
				rdosActionLogMapper.updateActionStatus(actionLogId, status);
				return null;
			}
		  }); 
	  }
}
