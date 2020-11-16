package com.dtstack.engine.rdbs.oracle;

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

public class OracleConnFactoryTest {

    @InjectMocks
    OracleConnFactory oracleConnFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCreateProcedureHeader() {
        String procName = "testCase";
        String createProcedureHeader = oracleConnFactory.getCreateProcedureHeader(procName);
        Assert.assertEquals("create  procedure \"testCase\" Authid Current_User as\n", createProcedureHeader);
    }

    @Test
    public void testBuildSqlList() {
        String testSql = "use testDatabase; select * from testTable;";
        List<String> strings = oracleConnFactory.buildSqlList(testSql);
        Assert.assertNotNull(strings);
        Assert.assertEquals(strings.size(), 2);
    }

}
