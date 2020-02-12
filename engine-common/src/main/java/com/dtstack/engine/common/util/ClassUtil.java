package com.dtstack.engine.common.util;

import com.dtstack.engine.common.exception.RdosDefineException;

import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class ClassUtil {

    public static Class<?> stringConvetClass(String str) {
        switch (str.toLowerCase()) {
            case "boolean":
                return Boolean.class;
            case "int":
                return Integer.class;

            case "bigint":
            case "long":
                return Long.class;

            case "tinyint":
            case "byte":
                return Byte.class;

            case "short":
            case "smallint":
                return Short.class;

            case "char":
            case "varchar":
            case "string":
                return String.class;

            case "float":
                return Float.class;

            case "double":
                return Double.class;

            case "date":
                return Date.class;

            case "timestamp":
                return Timestamp.class;

        }

        throw new RdosDefineException("not support for type " + str);
    }

    public static Object convertType(Object field, String fromType, String toType) {
        fromType = fromType.toUpperCase();
        toType = toType.toUpperCase();
        String rowData = field.toString();

        switch(toType) {
            case "TINYINT":
                return Byte.valueOf(rowData);
            case "SMALLINT":
                return Short.valueOf(rowData);
            case "INT":
                return Integer.valueOf(rowData);
            case "BIGINT":
                return Long.valueOf(rowData);
            case "FLOAT":
                return Float.valueOf(rowData);
            case "DOUBLE":
                return Double.valueOf(rowData);
            case "STRING":
                return rowData;
            case "BOOLEAN":
                return Boolean.valueOf(rowData);
            case "DATE":
                return DateUtil.columnToDate(field);
            case "TIMESTAMP":
                Date d = DateUtil.columnToDate(field);
                return new Timestamp(d.getTime());
            default:
                throw new RuntimeException("Can't convert from " + fromType + " to " + toType);
        }

    }

    public static String getTypeFromClass(Class<?> clz) {

        if(clz == Byte.class){
            return "TINYINT";
        }
        else if(clz == Short.class){
            return "SMALLINT";
        }
        else if(clz == Integer.class){
            return "INT";
        }
        else if(clz == Long.class){
            return "BIGINT";
        }
        else if(clz == String.class){
            return "STRING";
        }
        else if(clz == Float.class){
            return "FLOAT";
        }
        else if(clz == Double.class){
            return "DOUBLE";
        }
        else if(clz == Date.class){
            return "DATE";
        }
        else if(clz == Timestamp.class){
            return "TIMESTAMP";
        }
        else if(clz == Boolean.class){
            return "BOOLEAN";
        }
        throw new IllegalArgumentException("Unsupported data type: " + clz.getName());

    }

}
