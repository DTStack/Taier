package com.dtstack.engine.rdbs.postgresql;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class PostgreSQLConnFactoryTest {

    @InjectMocks
    PostgreSQLConnFactory postgreSQLConnFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCreateProcedureHeader() {
        String procName = "testCase";
        String createProcedureHeader = postgreSQLConnFactory.getCreateProcedureHeader(procName);
        Assert.assertEquals("CREATE FUNCTION \"testCase\"() RETURNS void AS $body$ ", createProcedureHeader);
    }

    @Test
    public void testGetCallProc() {
        String procName = "testCase";
        String callProc = postgreSQLConnFactory.getCallProc(procName);
        Assert.assertEquals("select \"testCase\"()", callProc);
    }

    @Test
    public void testGetDropProc() {
        String procName = "testCase";
        String dropProc = postgreSQLConnFactory.getDropProc(procName);
        Assert.assertEquals("drop function \"testCase\"()", dropProc);
    }

}
