package com.dtstack.taier.common.util;

import org.junit.Assert;
import org.junit.Test;

public class SqlRegexUtilTest {

    @Test
    public void testIsSelect() {
        String sql = "SEleCT\n \nway, \nrom  \n" +
                "from\n default.test\n;";
        Assert.assertTrue(SqlRegexUtil.isSelect(sql));
    }


    @Test
    public void isExplainSql() {
        String sql = "ExPlain SEleCT\n \nway, \nrom  \n" +
                "from\n default.test\n;";
        Assert.assertTrue(SqlRegexUtil.isExplainSql(sql));
    }
}