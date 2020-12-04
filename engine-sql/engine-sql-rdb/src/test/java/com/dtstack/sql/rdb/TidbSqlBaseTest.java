package com.dtstack.sql.rdb;

import com.dtstack.sql.SqlParserImpl;
import com.dtstack.sql.handler.LibraUglySqlHandler;

/**
 * @author chener
 * @Classname TidbSqlBaseTest
 * @Description TODO
 * @Date 2020/12/2 15:51
 * @Created chener@dtstack.com
 */
public class TidbSqlBaseTest extends BaseSqlTest{

    public SqlParserImpl getTidbParser(){
        SqlParserImpl sqlParser = new CalciteNodeParser(new LibraUglySqlHandler());;
        return sqlParser;
    }
}
