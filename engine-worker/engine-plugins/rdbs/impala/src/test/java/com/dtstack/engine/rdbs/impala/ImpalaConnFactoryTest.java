package com.dtstack.engine.rdbs.impala;

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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, ImpalaConnFactory.class})
public class ImpalaConnFactoryTest {

    @InjectMocks
    ImpalaConnFactory impalaConnFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() throws Exception {
        MemberModifier.field(ImpalaConnFactory.class, "driverName").set(impalaConnFactory, "com.cloudera.impala.jdbc41.Driver");
        MemberModifier.field(ImpalaConnFactory.class, "isFirstLoaded").set(impalaConnFactory, new AtomicBoolean(true));
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

        impalaConnFactory.init(props);
    }

    @Test
    public void testBuildSqlList() {
        String testSql = "use testDatabase; select * from testTable;";
        List<String> strings = impalaConnFactory.buildSqlList(testSql);
        Assert.assertNotNull(strings);
        Assert.assertEquals(strings.size(), 2);
    }

    @Test
    public void testSupportTransaction() {
        Assert.assertFalse(impalaConnFactory.supportTransaction());
    }

    @Test
    public void testSupportProcedure() {
        String sql = "select * from tables";
        Assert.assertFalse(impalaConnFactory.supportProcedure(sql));
    }

}
