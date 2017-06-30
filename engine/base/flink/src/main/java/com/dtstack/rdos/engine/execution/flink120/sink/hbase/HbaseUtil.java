package com.dtstack.rdos.engine.execution.flink120.sink.hbase;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

/**
 * Created by softfly on 17/6/30.
 */
public class HbaseUtil {

    private HbaseUtil() {}

    public static TypeInformation columnTypeToTypeInformation(String type) {
        type = type.toUpperCase();

        switch(type) {
            case "TINYINT":
                return BasicTypeInfo.getInfoFor(ByteWritable.class);
            case "SMALLINT":
                return BasicTypeInfo.SHORT_TYPE_INFO;
            case "INT":
                return BasicTypeInfo.getInfoFor(IntWritable.class);
            case "BIGINT":
                return BasicTypeInfo.LONG_TYPE_INFO;
            case "FLOAT":
                return BasicTypeInfo.FLOAT_TYPE_INFO;
            case "DOUBLE":
                return BasicTypeInfo.DOUBLE_TYPE_INFO;
            case "TIMESTAMP":
            case "DATE":
                return BasicTypeInfo.DATE_TYPE_INFO;
            case "STRING":
            case "VARCHAR":
            case "CHAR":
                return BasicTypeInfo.getInfoFor(Text.class);
            case "BOOLEAN":
                return BasicTypeInfo.BOOLEAN_TYPE_INFO;
            default:
                throw new IllegalArgumentException("Unsupported type");
        }

    }

}
