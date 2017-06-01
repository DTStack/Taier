package com.dtstack.rdos.engine.db.callback;

import java.io.File;
import java.io.FileInputStream;
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

    private static  String resource = System.getProperty("user.dir") + "/conf/mybatis/config.xml";

    private static SqlSessionFactory sqlSessionFactory;

    private static Logger logger = LoggerFactory.getLogger(MyBatisConnectionFactory.class);
 
    static {
        try {
            FileInputStream input = new FileInputStream(new File(resource));
 
            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(input);
            }
        }
        catch (Exception e) {
            logger.error("MyBatisConnectionFactory error:{}",ExceptionUtil.getErrorMessage(e));
        }
    }
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}