package com.dtstack.rdos.engine.entrance.db.callback;

import org.apache.ibatis.session.SqlSession;


/**
 * 
 * @author sishu.yss
 *
 */
public interface MybatisSessionCallback {

	void execute(SqlSession sqlSession) throws Exception;
}
