package com.dtstack.engine.rdbs.inceptor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class InceptorConnFactoryTest {

    private InceptorConnFactory connFactory;

    @Before
    public void init() {
        connFactory = new InceptorConnFactory();
    }
    @Test
    public void testSupportProcedure() {
        Assert.assertFalse(connFactory.supportProcedure(""));
    }

    @Test
    public void testBuildSqlList() {
        String sql = "SELECT * FROM foo; SELECT * FROM bar;";
        List<String> sqlList = connFactory.buildSqlList(sql);
        Assert.assertEquals(sqlList.get(0).trim(), "SELECT * FROM foo");
        Assert.assertEquals(sqlList.get(1).trim(), "SELECT * FROM bar");
    }
}