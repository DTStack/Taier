package com.dtstack.rdos.engine.entrance.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.entrance.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.entrance.db.mapper.RdosActionLogMapper;

public class RdosActionLogDAO {

	  public void updateActionStatus(final String actionLogId,final byte status){
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
