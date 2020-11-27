package com.dtstack.engine.rdbs.common.executor;

import com.dtstack.engine.rdbs.common.AbstractRdbsClientTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, AbstractConnFactoryTest.TestConnFactory.class})
public class AbstractConnFactoryTest {

    @InjectMocks
    AbstractConnFactoryTest.TestConnFactory testConnFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() throws Exception {
        MemberModifier.field(AbstractConnFactoryTest.TestConnFactory.class, "driverName").set(testConnFactory, "com.mysql.jdbc.Driver");
        MemberModifier.field(AbstractConnFactoryTest.TestConnFactory.class, "isFirstLoaded").set(testConnFactory, new AtomicBoolean(true));

        PowerMockito.mockStatic(DriverManager.class);
        Connection conn = PowerMockito.mock(Connection.class);
        Statement stmt = PowerMockito.mock(Statement.class);
        when(stmt.execute(any(String.class))).thenReturn(true);
        when(conn.createStatement()).thenReturn(stmt);
        when(DriverManager.getConnection(any(String.class), any(String.class), any(String.class))).thenReturn(conn);

        Properties props = new Properties();
        props.put("jdbcUrl", "jdbcUrl");
        props.put("username", "username");
        props.put("password", "password");
        testConnFactory.init(props);
    }

    @Test
    public void testSupportProcedure() {
        String sql = "begin \n select * from stu;\n end";
        boolean res = testConnFactory.supportProcedure(sql);
        Assert.assertTrue(res);
    }

    @Test
    public void testGetCallProc() {
        String procName = "testCase";
        String callProc = testConnFactory.getCallProc(procName);
        Assert.assertEquals("call \"testCase\"()", callProc);
    }

    @Test
    public void testGetDropProc() {
        String procName = "testCase";
        String dropProc = testConnFactory.getDropProc(procName);
        Assert.assertEquals("DROP PROCEDURE \"testCase\"", dropProc);
    }

    static class TestConnFactory extends AbstractConnFactory {

        public TestConnFactory() {
            driverName = "com.mysql.jdbc.Driver";
            testSql = "select 1111";
        }

        @Override
        public String getCreateProcedureHeader(String procName) {
            return String.format("create procedure %s() \n", procName);
        }
    }
}
