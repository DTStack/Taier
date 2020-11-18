package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.handler.HiveUglySqlHandler;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author chener
 * @Classname HiveSqlBaseTest
 * @Description hive sql 测试基类
 * @Date 2020/9/18 20:15
 * @Created chener@dtstack.com
 */
public class HiveSqlBaseTest extends BaseSqlTest{
    public SqlParserImpl getHiveSqlParser(){
        SqlParserImpl sqlParser = new AstNodeParser(new HiveUglySqlHandler());
        return sqlParser;
    }

    public void printTables(List<Table> tableList){
        if (CollectionUtils.isNotEmpty(tableList)){
            for (Table table:tableList){
                System.out.println(table.getDb()+"."+table.getName());
            }
        }
    }
}
