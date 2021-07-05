package com.dtstack.batch.sync.format.writer;

import org.junit.Test;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/2/25 3:15 下午
 */
public class LibraWriterFormatTest {

    private final LibraWriterFormat libraWriterFormat = new LibraWriterFormat();

    @Test
    public void testFormatToString() {
        libraWriterFormat.formatToString("BIT");
        libraWriterFormat.formatToString("TINYINT");
        libraWriterFormat.formatToString("SMALLINT");
        libraWriterFormat.formatToString("INT");
        libraWriterFormat.formatToString("MEDIUMINT");
        libraWriterFormat.formatToString("INTEGER");
        libraWriterFormat.formatToString("YEAR");
        libraWriterFormat.formatToString("INT2");
        libraWriterFormat.formatToString("INT4");
        libraWriterFormat.formatToString("INT8");
        libraWriterFormat.formatToString("BIGINT");
        libraWriterFormat.formatToString("REAL");
        libraWriterFormat.formatToString("FLOAT");
        libraWriterFormat.formatToString("FLOAT2");
        libraWriterFormat.formatToString("FLOAT4");
        libraWriterFormat.formatToString("FLOAT8");
        libraWriterFormat.formatToString("DOUBLE");
        libraWriterFormat.formatToString("BINARY_DOUBLE");
        libraWriterFormat.formatToString("NUMERIC");
        libraWriterFormat.formatToString("NUMBER");
        libraWriterFormat.formatToString("DECIMAL");
        libraWriterFormat.formatToString("STRING");
        libraWriterFormat.formatToString("VARCHAR");
        libraWriterFormat.formatToString("VARCHAR2");
        libraWriterFormat.formatToString("CHAR");
        libraWriterFormat.formatToString("CHARACTER");
        libraWriterFormat.formatToString("NCHAR");
        libraWriterFormat.formatToString("TINYTEXT");
        libraWriterFormat.formatToString("TEXT");
        libraWriterFormat.formatToString("MEDIUMTEXT");
        libraWriterFormat.formatToString("LONGTEXT");
        libraWriterFormat.formatToString("LONGVARCHAR");
        libraWriterFormat.formatToString("LONGNVARCHAR");
        libraWriterFormat.formatToString("NVARCHAR");
        libraWriterFormat.formatToString("NVARCHAR2");
        libraWriterFormat.formatToString("CLOB");
        libraWriterFormat.formatToString("TIMESTAMP");
        libraWriterFormat.formatToString("DATE");
        libraWriterFormat.formatToString("TINYINT");
        libraWriterFormat.formatToString("SMALLINT");
        libraWriterFormat.formatToString("INT8");
        libraWriterFormat.formatToString("BIGINT");
        libraWriterFormat.formatToString("FLOAT8");
        libraWriterFormat.formatToString("BINARY_DOUBLE");
        libraWriterFormat.formatToString("DECIMAL");
        libraWriterFormat.formatToString("BINARY");
        libraWriterFormat.formatToString("BOOLEAN");
        libraWriterFormat.formatToString("SMALLDATETIME");

        try {
            libraWriterFormat.formatToString("SMALLSERIAL");
        } catch (IllegalArgumentException e) {

        }
    }
}