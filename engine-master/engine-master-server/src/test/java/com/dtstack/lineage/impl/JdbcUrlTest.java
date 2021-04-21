package com.dtstack.lineage.impl;

import com.dtstack.lineage.bo.UrlInfo;
import com.dtstack.lineage.util.JdbcUrlUtil;
import com.dtstack.schedule.common.enums.DataSourceType;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    @Test
    public void test3(){

        LineageDataSourceService dataSourceService = new LineageDataSourceService();
        String sourceKey = dataSourceService.generateSourceKey(
                "{\"password\":\"\",\"jdbcUrl\":\"jdbc:impala://eng-cdh3:21050;AuthMech=1;KrbRealm=DTSTACK.COM;KrbHostFQDN=eng-cdh3;KrbServiceName=impala\",\"username\":\"\"}",
                DataSourceType.IMPALA.getVal());
        System.out.println(sourceKey);
        Assert.assertEquals("eng-cdh3#21050",sourceKey);
    }

    @Test
    public void test4(){

        LineageDataSourceService dataSourceService = new LineageDataSourceService();
        String sourceKey = dataSourceService.generateSourceKey(
                "{\"password\":\"\",\"jdbcUrl\":\"jdbc:impala://eng-cdh3:21050;AuthMech=1;KrbServiceName=impala;KrbHostFQDN=eng-cdh3\",\"username\":\"\"}",
                DataSourceType.IMPALA.getVal());
        System.out.println(sourceKey);
        Assert.assertEquals("eng-cdh3#21050",sourceKey);
    }

    @Test
    public void test5(){

        LineageDataSourceService dataSourceService = new LineageDataSourceService();
        String sourceKey = dataSourceService.generateSourceKey(
                "{\"password\":\"\",\"jdbcUrl\":\"jdbc:impala://eng-cdh3:21050;AuthMech=1;KrbRealm=DTSTACK.COM;KrbHostFQDN=eng-cdh3;KrbServiceName=impala\",\"username\":\"\"}",
                DataSourceType.IMPALA.getVal());
        System.out.println(sourceKey);
        Assert.assertEquals("eng-cdh3#21050",sourceKey);
    }
}
