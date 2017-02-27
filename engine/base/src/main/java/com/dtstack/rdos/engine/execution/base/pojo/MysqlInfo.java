package com.dtstack.rdos.engine.execution.base.pojo;

import com.dtstack.rdos.engine.execution.exception.RdosException;

import java.sql.Types;

/**
 * Reason:
 * Date: 2017/2/27
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class MysqlInfo extends JdbcInfo {

    public MysqlInfo(String dburl, String userName, String pwd, String tableName, String[] fields, Class<?>[] fieldTypeArray){
        this.dbURL = dburl;
        this.userName = userName;
        this.password = pwd;
        this.driverName = "mysql.driver";
        this.tableName = tableName;
        buildSql(tableName, fields);
        buildSqlTypes(fieldTypeArray);
    }

    private void buildSql(String tableName, String[] fields){
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

    private void buildSqlTypes(Class<?>[] fieldTypeArray){

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

        this.types = tmpFieldsType;
    }
}
