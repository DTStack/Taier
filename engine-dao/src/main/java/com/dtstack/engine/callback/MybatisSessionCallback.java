package com.dtstack.engine.callback;

import org.apache.ibatis.session.SqlSession;

/**
 * 
 *
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface MybatisSessionCallback<T> {

	T execute(SqlSession sqlSession) throws Exception;
}
