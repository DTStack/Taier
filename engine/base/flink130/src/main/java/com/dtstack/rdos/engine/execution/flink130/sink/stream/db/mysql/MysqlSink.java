package com.dtstack.rdos.engine.execution.flink130.sink.stream.db.mysql;

import com.dtstack.rdos.engine.execution.base.operator.stream.StreamCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink130.sink.stream.IStreamSinkGener;
import com.dtstack.rdos.engine.execution.flink130.sink.stream.db.DBSink;
import com.google.common.base.Preconditions;

import java.util.Properties;

/**
 * 构建mysql sink需要的信息
 * Date: 2017/2/27
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class MysqlSink extends DBSink implements IStreamSinkGener<MysqlSink>{

    public MysqlSink(){
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

        fieldsStr = fieldsStr.replaceFirst(",", "");
        placeholder = placeholder.replaceFirst(",", "");

        sqlTmp = sqlTmp.replace("${fields}", fieldsStr).replace("${placeholder}", placeholder);
        this.sql = sqlTmp;
    }


    @Override
    public MysqlSink genStreamSink(StreamCreateResultOperator operator) {
        Properties properties = operator.getProperties();
        String tmpDbURL = Preconditions.checkNotNull(properties.getProperty(SQL_DB_URL_KEY),
                "dbURL must not be null");
        String tmpUserName = Preconditions.checkNotNull(properties.getProperty(SQL_DB_USERNAME_KEY),
                "userName must not be null");
        String tmpPassword = Preconditions.checkNotNull(properties.getProperty(SQL_DB_password_KEY),
                "password must not be null");
        String tmpTableName = Preconditions.checkNotNull(properties.getProperty(SQL_DB_tableName_KEY),
                "tableName must not be null");

        Object tmpSqlBatchSize = properties.get(SQL_BATCH_SIZE_KEY);
        if(tmpSqlBatchSize != null){
            setBatchInterval((Integer) tmpSqlBatchSize);
        }

        Object tmpSinkParallelism = properties.get(SQL_DB_SINK_PARALLELISM_KEY);
        if(tmpSinkParallelism != null){
            setParallelism((Integer) tmpSinkParallelism);
        }

        this.driverName = "com.mysql.jdbc.Driver";
        this.dbURL = tmpDbURL;
        this.userName = tmpUserName;
        this.password = tmpPassword;
        this.tableName = tmpTableName;
        buildSql(tableName, operator.getFields());
        buildSqlTypes(operator.getFieldTypes());
        return this;
    }

}
