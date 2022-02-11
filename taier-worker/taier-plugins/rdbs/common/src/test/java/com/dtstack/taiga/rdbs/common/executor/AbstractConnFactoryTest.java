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

package com.dtstack.taiga.rdbs.common.executor;

import com.dtstack.taiga.rdbs.common.TestConnFactory;
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
@PrepareForTest({DriverManager.class, TestConnFactory.class, AbstractConnFactory.class})
public class AbstractConnFactoryTest {

    @InjectMocks
    TestConnFactory testConnFactory;

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
}
