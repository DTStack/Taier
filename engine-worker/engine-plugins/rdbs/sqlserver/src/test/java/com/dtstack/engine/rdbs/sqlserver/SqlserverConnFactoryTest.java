package com.dtstack.engine.rdbs.sqlserver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class SqlserverConnFactoryTest {

    @InjectMocks
    SqlserverConnFactory sqlserverConnFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCreateProcedureHeader() {
        String procName = "testCase";
        String createProcedureHeader = sqlserverConnFactory.getCreateProcedureHeader(procName);
        Assert.assertEquals("create procedure \"testCase\" as\n", createProcedureHeader);
    }

    @Test
    public void testGetCallProc() {
        String procName = "testCase";
        String callProc = sqlserverConnFactory.getCallProc(procName);
        Assert.assertEquals("execute \"testCase\"", callProc);
    }
}
