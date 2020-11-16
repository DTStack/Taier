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
import java.util.List;
import java.util.Properties;

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

}
