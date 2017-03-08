package com.dtstack.rdos.engine.entrance.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.entrance.db.mapper.RdosActionLogMapper;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosActionLogDAO {

	  public void updateActionStatus(final String actionLogId,final int status){
		  MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback(){

			@Override
			public void execute(SqlSession sqlSession) throws Exception {
				// TODO Auto-generated method stub
				RdosActionLogMapper rdosActionLogMapper = sqlSession.getMapper(RdosActionLogMapper.class);
				rdosActionLogMapper.updateActionStatus(actionLogId, status);
			}
		  }); 
	  }
}
