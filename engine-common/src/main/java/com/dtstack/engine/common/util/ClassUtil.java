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

            default:
        }

        throw new RdosDefineException("not support for type " + str);
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
