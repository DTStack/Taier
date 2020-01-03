package com.dtstack.engine.callback;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class MybatisSessionCallbackMethod {
	
	private static final Logger logger = LoggerFactory.getLogger(MybatisSessionCallbackMethod.class);

	private static SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
	
	public static <M> M doCallback(MybatisSessionCallback<M> mybatisSessionCallback){
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		try{
			return mybatisSessionCallback.execute(sqlSession);
		}catch(Exception e){
			logger.error("MybatisSessionCallbackMethod error:{}", e);
			throw new RdosException(ErrorCode.UNKNOWN_ERROR,e);
		}finally{
			if(sqlSession!=null){sqlSession.close();}
		}
	}
}
