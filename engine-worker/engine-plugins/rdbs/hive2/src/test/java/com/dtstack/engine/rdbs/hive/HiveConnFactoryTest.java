package com.dtstack.engine.rdbs.hive;

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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, HiveConnFactory.class})
public class HiveConnFactoryTest {

    @InjectMocks
    HiveConnFactory hiveConnFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testInit() throws Exception {
        MemberModifier.field(HiveConnFactory.class, "driverName").set(hiveConnFactory, "org.apache.hive.jdbc.HiveDriver");
        MemberModifier.field(HiveConnFactory.class, "isFirstLoaded").set(hiveConnFactory, new AtomicBoolean(true));
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

        hiveConnFactory.init(props);
    }

    @Test
    public void testBuildSqlList() throws IllegalAccessException {
        String testSql = "use testDatabase; select * from testTable;";
        MemberModifier.field(HiveConnFactory.class, "hiveSubType").set(hiveConnFactory, "INCEPTOR");
        List<String> strings = hiveConnFactory.buildSqlList(testSql);
        Assert.assertNotNull(strings);
        Assert.assertEquals(strings.size(), 1);
    }

    @Test
    public void testGetConnByTaskParams() throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {

        MemberModifier.field(HiveConnFactory.class, "username").set(hiveConnFactory, "user");
        MemberModifier.field(HiveConnFactory.class, "password").set(hiveConnFactory, "pwd");
        MemberModifier.field(HiveConnFactory.class, "jdbcUrl").set(hiveConnFactory, "jdbc:hive2://dtstack:10001/default");
        PowerMockito.mockStatic(DriverManager.class);
        when(DriverManager.getConnection(any(String.class), any(Properties.class))).thenReturn(PowerMockito.mock(Connection.class));

        String taskParams = "hiveconf:mapreduce.job.name=test\nhiveconf:mapreduce.job.name=test";
        String jobName = "testJob";
        Connection conn = hiveConnFactory.getConnByTaskParams(taskParams, jobName);
        Assert.assertNotNull(conn);
    }

    @Test
    public void testSupportTransaction() {
        Assert.assertFalse(hiveConnFactory.supportTransaction());
    }

    @Test
    public void testSupportProcedure() {
        String sql = "select * from tables";
        Assert.assertFalse(hiveConnFactory.supportProcedure(sql));
    }

}
