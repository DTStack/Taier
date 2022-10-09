package com.dtstack.taier.datasource.plugin.oracle;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 13:50 2020/8/20
 * @Description：Oracle 字段转化测试
 */
public class OracleDaAdapterTest {

    @Test
    public void mapColumnTypeJdbc2Java() {
        Assert.assertEquals(OracleDbAdapter.JavaType.TYPE_VARCHAR.getFlinkSqlType(), OracleDbAdapter.mapColumnTypeJdbc2Java(Types.CHAR, 0, 0));
        Assert.assertEquals(OracleDbAdapter.JavaType.TYPE_VARCHAR.getFlinkSqlType(), OracleDbAdapter.mapColumnTypeJdbc2Java(Types.NCLOB, 0, 0));

        Assert.assertEquals(OracleDbAdapter.JavaType.TYPE_TIMESTAMP.getFlinkSqlType(), OracleDbAdapter.mapColumnTypeJdbc2Java(Types.TIME, 0, 0));
        Assert.assertEquals(OracleDbAdapter.JavaType.TYPE_TIMESTAMP.getFlinkSqlType(), OracleDbAdapter.mapColumnTypeJdbc2Java(Types.TIMESTAMP, 0, 0));
        Assert.assertEquals(OracleDbAdapter.JavaType.TYPE_TIMESTAMP.getFlinkSqlType(), OracleDbAdapter.mapColumnTypeJdbc2Java(Types.TIMESTAMP, 0, 1));

        Assert.assertEquals(OracleDbAdapter.JavaType.TYPE_INT.getFlinkSqlType(), OracleDbAdapter.mapColumnTypeJdbc2Java(Types.TINYINT, 0, 0));
    }
}
