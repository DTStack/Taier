package com.dtstack.engine.lineage.impl;

import com.dtstack.engine.lineage.bo.UrlInfo;
import com.dtstack.engine.lineage.util.JdbcUrlUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: ZYD
 * Date: 2021/4/9 13:37
 * Description: 测试jdbcUrl
 * @since 1.0.0
 */
public class JdbcUrlTest {



    @Test
    public void test1(){

        UrlInfo urlInfo = JdbcUrlUtil.getUrlInfo("jdbc:postgresql://172.16.101.246:5432/");
        Assert.assertEquals("5432",urlInfo.getPort().toString());
    }

    @Test
    public void test2(){

        UrlInfo urlInfo = JdbcUrlUtil.getUrlInfo("jdbc:impala://172.16.100.109:21050/");
        Assert.assertEquals("21050",urlInfo.getPort().toString());
    }

}
