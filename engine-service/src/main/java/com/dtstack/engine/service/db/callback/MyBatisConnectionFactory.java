package com.dtstack.engine.service.db.callback;

import com.dtstack.engine.common.config.ConfigParse;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月21日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class MyBatisConnectionFactory {

    private static  String resource = "mybatis/config.xml";

    private static SqlSessionFactory sqlSessionFactory;

    private static Logger logger = LoggerFactory.getLogger(MyBatisConnectionFactory.class);

    private static ObjectMapper objectMapper = new ObjectMapper();
 
    static {
        try {
            Map<String,String> db = ConfigParse.getDB();
            Properties properties = objectMapper.readValue(objectMapper.writeValueAsBytes(db),Properties.class);
            InputStream input = MyBatisConnectionFactory.class.getClassLoader().getResourceAsStream(resource);
            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(input,properties);
            }
        }catch (Exception e) {
            logger.error("load db error:", e);
            throw new RuntimeException(e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}