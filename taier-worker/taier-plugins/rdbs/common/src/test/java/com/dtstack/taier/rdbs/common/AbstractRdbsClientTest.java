/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.rdbs.common;

import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.base.resource.EngineResourceInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.rdbs.common.executor.RdbsExeQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, TestConnFactory.class, TestRdbsClient.class, AbstractRdbsClient.class})
public class AbstractRdbsClientTest {

    @Mock
    TestConnFactory testConnFactory;

    @InjectMocks
    TestRdbsClient testRdbsClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() throws Exception {
        MemberModifier.field(TestConnFactory.class, "driverName").set(testConnFactory, "com.mysql.jdbc.Driver");
        MemberModifier.field(TestConnFactory.class, "isFirstLoaded").set(testConnFactory, new AtomicBoolean(true));

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

        AbstractRdbsClient abstractRdbsClient = PowerMockito.mock(AbstractRdbsClient.class, Mockito.CALLS_REAL_METHODS);
        when(abstractRdbsClient.getConnFactory()).thenReturn(testConnFactory);
        //MemberModifier.field(AbstractRdbsClient.class, "connFactory").set(abstractRdbsClient, testConnFactory);

        abstractRdbsClient.init(props);
    }

    @Test
    public void testProcessSubmitJobWithType() throws Exception {
        JobClient jobClient = new JobClient();
        jobClient.setJobType(EJobType.MR);
        AbstractRdbsClient abstractRdbsClient = PowerMockito.mock(AbstractRdbsClient.class, Mockito.CALLS_REAL_METHODS);

        Boolean isMr = true;
        try {
            JobResult jobResult = abstractRdbsClient.processSubmitJobWithType(jobClient);
            Assert.assertNotNull(jobResult);
        } catch (Exception e) {
            isMr = true;
        }
        Assert.assertTrue(isMr);

        jobClient.setJobType(EJobType.SQL);

        RdbsExeQueue rdbsExeQueue = PowerMockito.mock(RdbsExeQueue.class);
        when(rdbsExeQueue.submit(any(JobClient.class))).thenReturn("test");
        MemberModifier.field(AbstractRdbsClient.class, "exeQueue").set(abstractRdbsClient, rdbsExeQueue);
        JobResult jobResult = abstractRdbsClient.processSubmitJobWithType(jobClient);
        Assert.assertNotNull(jobResult);
    }

    @Test
    public void testCancelJob() throws Exception {
        RdbsExeQueue rdbsExeQueue = PowerMockito.mock(RdbsExeQueue.class);
        when(rdbsExeQueue.cancelJob(any(String.class))).thenReturn(true);
        MemberModifier.field(TestRdbsClient.class, "exeQueue").set(testRdbsClient, rdbsExeQueue);
        JobIdentifier jobIdentifier = JobIdentifier.createInstance("test", "test", "test");
        JobResult jobResult = testRdbsClient.cancelJob(jobIdentifier);
        Assert.assertNotNull(jobResult);
    }

    @Test
    public void testGetJobStatus() throws Exception {
        RdbsExeQueue rdbsExeQueue = PowerMockito.mock(RdbsExeQueue.class);
        when(rdbsExeQueue.getJobStatus(any(String.class))).thenReturn(TaskStatus.RUNNING);
        MemberModifier.field(TestRdbsClient.class, "exeQueue").set(testRdbsClient, rdbsExeQueue);
        JobIdentifier jobIdentifier = JobIdentifier.createInstance("test", "test", "test");
        TaskStatus status = testRdbsClient.getJobStatus(jobIdentifier);
        Assert.assertEquals(status, TaskStatus.RUNNING);
    }

    @Test
    public void testGetJobLog() throws Exception {
        RdbsExeQueue rdbsExeQueue = PowerMockito.mock(RdbsExeQueue.class);
        when(rdbsExeQueue.getJobLog(any(String.class))).thenReturn("job log");
        MemberModifier.field(TestRdbsClient.class, "exeQueue").set(testRdbsClient, rdbsExeQueue);
        JobIdentifier jobIdentifier = JobIdentifier.createInstance("test", "test", "test");
        String jobLog = testRdbsClient.getJobLog(jobIdentifier);
        Assert.assertEquals(jobLog, "job log");
    }

    @Test
    public void testJudgeSlots() throws Exception {
        JobClient jobClient = new JobClient();
        JudgeResult result = testRdbsClient.judgeSlots(jobClient);
        Assert.assertEquals(JudgeResult.JudgeType.NOT_OK, result.getResult());

        EngineResourceInfo info = PowerMockito.mock(EngineResourceInfo.class);
        when(info.judgeSlots(any(JobClient.class))).thenReturn(JudgeResult.ok());
        MemberModifier.field(TestRdbsClient.class, "resourceInfo").set(testRdbsClient, info);
        JudgeResult judgeResult = testRdbsClient.judgeSlots(jobClient);
        Assert.assertEquals(JudgeResult.JudgeType.OK, judgeResult.getResult());
    }

    @Test
    public void testTestConnect() throws Exception {
        ComponentTestResult result = testRdbsClient.testConnect(null);
        Assert.assertFalse(result.getResult());

        TestConnFactory connFactory = PowerMockito.mock(TestConnFactory.class);
        PowerMockito.doAnswer(new Answer() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return "test";
            }
        }).when(connFactory).init(any(Properties.class));

        MemberModifier.field(TestRdbsClient.class, "connFactory").set(testRdbsClient, connFactory);
        String pluginInfo = "{\"test\": \"test\"}";
        ComponentTestResult okResult = testRdbsClient.testConnect(pluginInfo);
        Assert.assertTrue(okResult.getResult());
    }

    @Test
    public void testExecuteQuery() throws Exception {

        TestConnFactory connFactory = PowerMockito.mock(TestConnFactory.class);
        Connection connection = PowerMockito.mock(Connection.class);
        Statement stmt = PowerMockito.mock(Statement.class);
        when(stmt.execute(any(String.class))).thenReturn(true);

        ResultSet res = PowerMockito.mock(ResultSet.class);
        ResultSetMetaData metaData = PowerMockito.mock(ResultSetMetaData.class);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnName(any(int.class))).thenReturn("name");
        when(res.getMetaData()).thenReturn(metaData);

        when(stmt.getResultSet()).thenReturn(res);

        when(connection.createStatement()).thenReturn(stmt);
        when(connFactory.getConn()).thenReturn(connection);
        MemberModifier.field(TestRdbsClient.class, "connFactory").set(testRdbsClient, connFactory);

        String sql = "select * from tables";
        String database = "default";
        List<List<Object>> execRes = testRdbsClient.executeQuery(sql, database);
        Assert.assertNotNull(execRes);
    }
}
