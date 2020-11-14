package com.dtstack.engine.rdbs.mysql;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * Date: 2020/11/14
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public class MysqlConnFactoryTest {

    @InjectMocks
    MysqlConnFactory mysqlConnFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCreateProcedureHeader() {
        String procName = "testCase";
        String createProcedureHeader = mysqlConnFactory.getCreateProcedureHeader(procName);
        Assert.assertEquals("create procedure testCase() \n", createProcedureHeader);
    }

    @Test
    public void testGetCallProc() {
        String procName = "testCase";
        String callProc = mysqlConnFactory.getCallProc(procName);
        Assert.assertEquals("call testCase()", callProc);
    }

    @Test
    public void testGetDropProc() {
        String procName = "testCase";
        String dropProc = mysqlConnFactory.getDropProc(procName);
        Assert.assertEquals("DROP PROCEDURE testCase", dropProc);
    }

}
