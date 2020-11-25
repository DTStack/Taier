package com.dtstack.engine.rdbs.hive;

import com.dtstack.engine.api.pojo.ComponentTestResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import scala.concurrent.java8.FuturesConvertersImpl;

import java.io.File;
import java.sql.DriverManager;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, HiveConnFactory.class, HiveClient.class})
public class HiveClientTest {

    @InjectMocks
    HiveClient hiveClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        hiveClient.getConnFactory();
    }

    @Test
    public void testTestConnect() throws Exception {
        ComponentTestResult result = hiveClient.testConnect(null);
        Assert.assertFalse(result.getResult());

        HiveConnFactory hiveConnFactory = PowerMockito.mock(HiveConnFactory.class);
        PowerMockito.doAnswer(new Answer() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return "test";
            }
        }).when(hiveConnFactory).init(any(Properties.class));

        MemberModifier.field(HiveClient.class, "connFactory").set(hiveClient, hiveConnFactory);
        String pluginInfo = "{\"test\": \"test\"}";
        ComponentTestResult okResult = hiveClient.testConnect(pluginInfo);
        Assert.assertTrue(okResult.getResult());
    }

    @Test
    public void testExecuteQuery() {

    }

}
