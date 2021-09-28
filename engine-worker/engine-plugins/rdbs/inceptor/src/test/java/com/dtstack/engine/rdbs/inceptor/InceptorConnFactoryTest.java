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

package com.dtstack.engine.rdbs.inceptor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class InceptorConnFactoryTest {

    private InceptorConnFactory connFactory;

    @Before
    public void init() {
        connFactory = new InceptorConnFactory();
    }
    @Test
    public void testSupportProcedure() {
        Assert.assertFalse(connFactory.supportProcedure(""));
    }

    @Test
    public void testBuildSqlList() {
        String sql = "SELECT * FROM foo; SELECT * FROM bar;";
        List<String> sqlList = connFactory.buildSqlList(sql);
        Assert.assertEquals(sqlList.get(0).trim(), "SELECT * FROM foo");
        Assert.assertEquals(sqlList.get(1).trim(), "SELECT * FROM bar");
    }
}