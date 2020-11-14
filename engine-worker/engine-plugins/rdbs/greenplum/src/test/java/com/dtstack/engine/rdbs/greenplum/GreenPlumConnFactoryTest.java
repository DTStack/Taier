package com.dtstack.engine.rdbs.greenplum;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

/**
 * Date: 2020/11/14
 * Company: www.dtstack.com
 * @author xiuzhu
 */


public class GreenPlumConnFactoryTest {

    @InjectMocks
    GreenPlumConnFactory greenPlumConnFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuildSqlList() {
        String testSql = "use testDatabase; select * from testTable;";
        List<String> strings = greenPlumConnFactory.buildSqlList(testSql);
        Assert.assertNotNull(strings);
        Assert.assertEquals(strings.size(), 2);
    }

    @Test
    public void testGetCreateProcedureHeader() {
        String procName = "testCase";
        String createProcedureHeader = greenPlumConnFactory.getCreateProcedureHeader(procName);
        Assert.assertEquals("CREATE FUNCTION \"testCase\"() RETURNS void AS $body$ ", createProcedureHeader);
    }

    @Test
    public void testGetCallProc() {
        String procName = "testCase";
        String callProc = greenPlumConnFactory.getCallProc(procName);
        Assert.assertEquals("select \"testCase\"()", callProc);
    }

    @Test
    public void testGetDropProc() {
        String procName = "testCase";
        String dropProc = greenPlumConnFactory.getDropProc(procName);
        Assert.assertEquals("drop function \"testCase\"()", dropProc);
    }

}
