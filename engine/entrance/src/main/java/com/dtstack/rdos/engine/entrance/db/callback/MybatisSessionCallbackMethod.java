package com.dtstack.rdos.engine.entrance.db.callback;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;

public class MybatisSessionCallbackMethod {
	
	private static final Logger logger = LoggerFactory.getLogger(MybatisSessionCallbackMethod.class);

	private static SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
	
	public static void doCallback(MybatisSessionCallback mybatisSessionCallback){
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		try{
			mybatisSessionCallback.execute(sqlSession);
		}catch(Exception e){
			logger.error("MybatisSessionCallbackMethod error:{}",ExceptionUtil.getErrorMessage(e));
		}finally{
			if(sqlSession!=null)sqlSession.close();
		}
	}
}
