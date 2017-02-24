package com.dtstack.rdos.engine.entrance.db.callback;

import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月21日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class MyBatisConnectionFactory {
 
    private static SqlSessionFactory sqlSessionFactory;
    
    private static Logger looger = LoggerFactory.getLogger(MyBatisConnectionFactory.class);
 
    static {
        try {
            String resource = "mybatis/config.xml";
            Reader reader = Resources.getResourceAsReader(resource);
 
            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            }
        }
        catch (Exception e) {
        	looger.error("MyBatisConnectionFactory error:{}",ExceptionUtil.getErrorMessage(e));
        }
    }
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}