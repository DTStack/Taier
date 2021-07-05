package com.dtstack.batch.sync.format.writer;

import org.junit.Test;

public class HiveWriterFormatTest {

    private final HiveWriterFormat hiveWriterFormat = new HiveWriterFormat();

    @Test
    public void test(){
        hiveWriterFormat.formatToString("BIT");
        hiveWriterFormat.formatToString("TINYINT");
        hiveWriterFormat.formatToString("SMALLINT");
        hiveWriterFormat.formatToString("SMALLSERIAL");
        hiveWriterFormat.formatToString("INT");
        hiveWriterFormat.formatToString("MEDIUMINT");
        hiveWriterFormat.formatToString("INTEGER");
        hiveWriterFormat.formatToString("YEAR");
        hiveWriterFormat.formatToString("INT2");
        hiveWriterFormat.formatToString("INT4");
        hiveWriterFormat.formatToString("INT8");
        hiveWriterFormat.formatToString("UINT8");
        hiveWriterFormat.formatToString("UINT16");
        hiveWriterFormat.formatToString("UINT32");
        hiveWriterFormat.formatToString("INT16");
        hiveWriterFormat.formatToString("INT32");
        hiveWriterFormat.formatToString("SERIAL");
        hiveWriterFormat.formatToString("BIGINT");
        hiveWriterFormat.formatToString("UINT64");
        hiveWriterFormat.formatToString("INT64");
        hiveWriterFormat.formatToString("REAL");
        hiveWriterFormat.formatToString("FLOAT");
        hiveWriterFormat.formatToString("FLOAT2");
        hiveWriterFormat.formatToString("FLOAT4");
        hiveWriterFormat.formatToString("FLOAT8");
        hiveWriterFormat.formatToString("FLOAT32");
        hiveWriterFormat.formatToString("DOUBLE");
        hiveWriterFormat.formatToString("BINARY_DOUBLE");
        hiveWriterFormat.formatToString("NUMERIC");
        hiveWriterFormat.formatToString("NUMBER");
        hiveWriterFormat.formatToString("DECIMAL");
        hiveWriterFormat.formatToString("STRING");
        hiveWriterFormat.formatToString("VARCHAR");
        hiveWriterFormat.formatToString("VARCHAR2");
        hiveWriterFormat.formatToString("CHAR");
        hiveWriterFormat.formatToString("CHARACTER");
        hiveWriterFormat.formatToString("NCHAR");
        hiveWriterFormat.formatToString("TINYTEXT");
        hiveWriterFormat.formatToString("TEXT");
        hiveWriterFormat.formatToString("MEDIUMTEXT");
        hiveWriterFormat.formatToString("LONGTEXT");
        hiveWriterFormat.formatToString("LONGVARCHAR");
        hiveWriterFormat.formatToString("LONGNVARCHAR");
        hiveWriterFormat.formatToString("NVARCHAR");
        hiveWriterFormat.formatToString("NVARCHAR2");
        hiveWriterFormat.formatToString("CLOB");
        hiveWriterFormat.formatToString("TIMESTAMP");

        hiveWriterFormat.formatToString("number(10,2)");
        hiveWriterFormat.formatToString("int");
        hiveWriterFormat.formatToString("BINARY");
        hiveWriterFormat.formatToString("BOOLEAN");
        hiveWriterFormat.formatToString("DATE");
        hiveWriterFormat.formatToString("BIGSERIAL");
        hiveWriterFormat.formatToString("SMALLDATETIME");
    }
}
