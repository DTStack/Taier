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

package com.dtstack.engine.rdbs.greenplum;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

/**
 * Date: 2020/11/14
 * Company: www.dtstack.com
 * @author xiuzhu
 */


public class GreenPlumConnFactoryTest {

    @InjectMocks
    GreenPlumConnFactory greenPlumConnFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuildSqlList() {
        String testSql = "use testDatabase; select * from testTable;";
        List<String> strings = greenPlumConnFactory.buildSqlList(testSql);
        Assert.assertNotNull(strings);
        Assert.assertEquals(strings.size(), 2);
    }

    @Test
    public void testGetCreateProcedureHeader() {
        String procName = "testCase";
        String createProcedureHeader = greenPlumConnFactory.getCreateProcedureHeader(procName);
        Assert.assertEquals("CREATE FUNCTION \"testCase\"() RETURNS void AS $body$ ", createProcedureHeader);
    }

    @Test
    public void testGetCallProc() {
        String procName = "testCase";
        String callProc = greenPlumConnFactory.getCallProc(procName);
        Assert.assertEquals("select \"testCase\"()", callProc);
    }

    @Test
    public void testGetDropProc() {
        String procName = "testCase";
        String dropProc = greenPlumConnFactory.getDropProc(procName);
        Assert.assertEquals("drop function \"testCase\"()", dropProc);
    }

}
