package com.dtstack.lineage.impl;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.schedule.common.enums.DataSourceType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: ZYD
 * Date: 2021/1/27 20:05
 * Description: 测试
 * @since 1.0.0
 */
public class LineageDataSourceServiceTest extends AbstractTest {


    @Autowired
    private LineageDataSourceService sourceService;

    @Test
    public void testGenerateSourceKey(){

        String sourceKey = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"jdbc:phoenix:172.16.8.107,172.16.8.108,172.16.8.109:2181\"\n" +
                "}", DataSourceType.Phoenix.getVal());
        Assert.assertEquals("172.16.8.107#2181_172.16.8.108#2181_172.16.8.109#2181",sourceKey);
        String sourceKey2 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"jdbc:phoenix:172.16.8.109:2181\"\n" +
                "}", DataSourceType.Phoenix.getVal());
        Assert.assertEquals("172.16.8.109#2181",sourceKey2);
        String sourceKey3 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"172.16.10.104,172.16.10.224,172.16.10.252:2181\"\n" +
                "}", DataSourceType.HBASE.getVal());
        Assert.assertEquals("172.16.10.104#2181_172.16.10.224#2181_172.16.10.252#2181",sourceKey3);

        String sourceKey4 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"172.16.100.175:2181,172.16.101.196:2181,172.16.101.227:2181\"\n" +
                "}", DataSourceType.HBASE.getVal());
        Assert.assertEquals("172.16.100.175#2181_172.16.101.196#2181_172.16.101.227#2181",sourceKey4);

        String sourceKey5 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"172.16.100.175,172.16.101.196,172.16.101.227:2181\"\n" +
                "}", DataSourceType.HBASE.getVal());
        Assert.assertEquals("172.16.100.175#2181_172.16.101.196#2181_172.16.101.227#2181",sourceKey5);

        String sourceKey6 = sourceService.generateSourceKey("{\n" +
        " \"jdbcUrl\":\"jdbc:impala://172.16.8.83:21050/dtstack;AuthMech=3\"\n" +
        "}", DataSourceType.IMPALA.getVal());
        Assert.assertEquals("172.16.8.83#21050",sourceKey6);

        String sourceKey7 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"jdbc:hive2://krbt3:10000/default;principal=hdfs/krbt3@DTSTACK.COM\"\n" +
                "}", DataSourceType.HIVE.getVal());
        Assert.assertEquals("krbt3#10000",sourceKey7);

    }


    @Test
    public void testGenerateSourceKey2(){

        String sourceKey = sourceService.generateSourceKey("{\"password\":\"Abc12345\",\"jdbcUrl\":\"jdbc:sqlserver://172.16.101.246:1433;database=shihu\",\"username\":\"sa\"}"
                , DataSourceType.HIVE.getVal());
        Assert.assertEquals("172.16.101.246#1433",sourceKey);

    }

    @Test
    public void testGenerateSourceKey3(){

        String sourceKey = sourceService.generateSourceKey("{\"password\":\"\",\"jdbcUrl\":\"jdbc:impala://172.16.100.109:21050/\",\"username\":\"\"}"
                , DataSourceType.HIVE.getVal());
        Assert.assertEquals("172.16.100.109#21050",sourceKey);

    }

}
