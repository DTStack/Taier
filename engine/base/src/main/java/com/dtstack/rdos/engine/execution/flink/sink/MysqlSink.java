package com.dtstack.rdos.engine.execution.flink.sink;

import com.dtstack.rdos.engine.execution.base.operator.CreateResultOperator;
import com.dtstack.rdos.engine.execution.flink.sink.DBSink;
import com.google.common.base.Preconditions;

import java.util.Properties;

/**
 * 构建mysql sink需要的信息
 * Date: 2017/2/27
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class MysqlSink extends DBSink {

    public MysqlSink(CreateResultOperator operator){
        Properties properties = operator.getProperties();
        String tmpDbURL = Preconditions.checkNotNull(properties.getProperty("dbURL"),
                "dbURL must not be null");
        String tmpUserName = Preconditions.checkNotNull(properties.getProperty("userName"),
                "userName must not be null");
        String tmpPassword = Preconditions.checkNotNull(properties.getProperty("password"),
                "password must not be null");
        String tmpTableName = Preconditions.checkNotNull(properties.getProperty("tableName"),
                "tableName must not be null");

        init(tmpDbURL, tmpUserName, tmpPassword, tmpTableName, operator.getFields(), operator.getFieldTypes());
    }

    public void init(String dburl, String userName, String pwd, String tableName, String[] fields, Class<?>[] fieldTypeArray){
        this.driverName = "mysql.driver";
        this.dbURL = dburl;
        this.userName = userName;
        this.password = pwd;
        this.tableName = tableName;
        buildSql(tableName, fields);
        buildSqlTypes(fieldTypeArray);
    }

    @Override
    public void buildSql(String tableName, String[] fields){
        String sqlTmp = "replace into " + tableName + " (${fields}) values (${placeholder})";
        String fieldsStr = "";
        String placeholder = "";

        for(String fieldName : fields){
            fieldsStr += "," + fieldName;
            placeholder += ",?";
        }

        fieldsStr.replaceFirst(",", "");
        placeholder.replaceFirst(",", "");

        sqlTmp = sqlTmp.replace("${fields}", fieldsStr).replace("${placeholder}", placeholder);
        this.sql = sqlTmp;
    }


}
