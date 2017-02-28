package com.dtstack.rdos.engine.execution.flink.sink;

import com.dtstack.rdos.engine.execution.exception.RdosException;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.OutputFormatSinkFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.sinks.StreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import java.sql.Types;

/**
 * 数据库连接配置相关
 * Date: 2017/2/27
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public abstract class DBSink implements StreamTableSink<Row> {

    protected String driverName;

    protected String dbURL;

    protected String userName;

    protected String password;

    /**batch 刷新间隔 ms, 默认是1s*/
    protected int batchInterval = 1000;

    protected int[] sqlTypes;

    protected String tableName;

    protected String sql;

    protected String[] fieldNames;

    private TypeInformation[] fieldTypes;

    public RichSinkFunction createJdbcSinkFunc(){

        if(driverName == null || dbURL == null || userName == null || sql == null
                || password == null || sqlTypes == null || tableName == null){
            throw new RdosException("any of params in(driverName, dbURL, userName, sql, password, types, tableName) " +
                    " must not be null. please check it!!!");
        }

        JDBCOutputFormat.JDBCOutputFormatBuilder jdbcFormatBuild = JDBCOutputFormat.buildJDBCOutputFormat();
        jdbcFormatBuild.setDBUrl(dbURL);
        jdbcFormatBuild.setDrivername(driverName);
        jdbcFormatBuild.setUsername(userName);
        jdbcFormatBuild.setPassword(password);
        jdbcFormatBuild.setQuery(sql);
        jdbcFormatBuild.setBatchInterval(batchInterval);//默认5s
        jdbcFormatBuild.setSqlTypes(sqlTypes);
        JDBCOutputFormat outputFormat = jdbcFormatBuild.finish();

        OutputFormatSinkFunction outputFormatSinkFunc = new OutputFormatSinkFunction(outputFormat);
        return outputFormatSinkFunc;
    }

    /**
     * 暂时没找到通过TypeInformation 转换对应的sql.Types
     * 现在通过指定的class类型转换.
     * FIXME 后续有添加新的类型的时候需要修改
     * @param fieldTypeArray
     */
    protected void buildSqlTypes(Class<?>[] fieldTypeArray){

        int[] tmpFieldsType = new int[fieldTypeArray.length];
        for(int i=0; i<fieldTypeArray.length; i++){
            Class fieldType = fieldTypeArray[i];
            if(fieldType == Long.class){
                tmpFieldsType[i] = Types.BIGINT;
            }else if(fieldType == Byte.class){
                tmpFieldsType[i] = Types.TINYINT;
            }else if(fieldType == Short.class){
                tmpFieldsType[i] = Types.SMALLINT;
            }else if(fieldType == String.class){
                tmpFieldsType[i] = Types.CHAR;
            }else if(fieldType == Float.class || fieldType == Double.class){
                tmpFieldsType[i] = Types.DOUBLE;
            }else{
                throw new RdosException("no support field type for sql. the input type:" + fieldType.getName());
            }
        }

        this.sqlTypes = tmpFieldsType;
    }

    /**
     * 设置提交更新的频率---默认是1s,单位是ms
     * @param batchInterval
     */
    public void setBatchInterval(int batchInterval) {
        this.batchInterval = batchInterval;
    }

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        RichSinkFunction richSinkFunction = createJdbcSinkFunc();
        dataStream.addSink(richSinkFunction);
    }

    @Override
    public TypeInformation<Row> getOutputType() {
        return new RowTypeInfo(getFieldTypes());
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    @Override
    public TypeInformation<?>[] getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        this.fieldNames = Preconditions.checkNotNull(fieldNames, "fieldNames");
        this.fieldTypes = Preconditions.checkNotNull(fieldTypes, "fieldTypes");
        return this;
    }

    public void buildSql(String tableName, String[] fields){
        throw new RdosException("you need to achieve this method in your own class.");
    }
}
