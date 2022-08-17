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

package com.dtstack.taier.common.util;

import org.junit.Assert;
import org.junit.Test;

public class SqlRegexUtilTest {

    @Test
    public void testIsSelect() {
        // check can execute a query sql so assert true
        String sql = "SEleCT\n \nway, \nrom  \n" +
                "from\n default.test\n;";
        Assert.assertTrue(SqlRegexUtil.isSelect(sql));

        // check can execute a query sql so assert true
        sql = "select way, rom from default.test;";
        Assert.assertTrue(SqlRegexUtil.isSelect(sql));

        // check can execute a query sql so assert true
        sql = "SELECT way, rom from default.test;";
        Assert.assertTrue(SqlRegexUtil.isSelect(sql));

        // check can execute a query sql so assert true
        sql = "SEleCT\r\nway, rom from default.test;";
        Assert.assertTrue(SqlRegexUtil.isSelect(sql));

        sql = "SEleCTway, rom from default.test;";
        // check not a query sql so assert false
        Assert.assertFalse(SqlRegexUtil.isSelect(sql));
    }


    @Test
    public void isExplainSql() {
        // check can execute a explain sql so assert true
        String sql = "ExPlain SEleCT\n \nway, \nrom  \n" +
                "from\n default.test\n;";
        Assert.assertTrue(SqlRegexUtil.isExplainSql(sql));

        // check can execute a explain sql so assert true
        sql = "explain select way, rom from taier;";
        Assert.assertTrue(SqlRegexUtil.isExplainSql(sql));

        // check can execute a explain sql so assert true
        sql = "EXPLAIN SEleCT way, rom from taier;";
        Assert.assertTrue(SqlRegexUtil.isExplainSql(sql));

        // check can execute a explain sql so assert true
        sql = "explain\r\n SEleCT way, rom from taier;";
        Assert.assertTrue(SqlRegexUtil.isExplainSql(sql));

        // check not a explain sql so assert false
        sql = "ExPlainSEleCT from default.test;";
        Assert.assertFalse(SqlRegexUtil.isExplainSql(sql));
    }
}