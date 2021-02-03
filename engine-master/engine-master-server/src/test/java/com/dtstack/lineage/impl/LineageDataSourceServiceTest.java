package com.dtstack.lineage.impl;

import com.dtstack.schedule.common.enums.DataSourceType;

/**
 * @Author: ZYD
 * Date: 2021/1/27 20:05
 * Description: 测试
 * @since 1.0.0
 */
public class LineageDataSourceServiceTest {


    public static void main(String[] args) {

        LineageDataSourceService sourceService = new LineageDataSourceService();
//        String sourceKey = sourceService.generateSourceKey("{\n" +
//                " \"jdbcUrl\":\"jdbc:phoenix:172.16.8.107,172.16.8.108,172.16.8.109:2181\"\n" +
//                "}", DataSourceType.Phoenix.getVal());
//        String sourceKey = sourceService.generateSourceKey("{\n" +
//                " \"jdbcUrl\":\"jdbc:phoenix:172.16.8.109:2181\"\n" +
//                "}", DataSourceType.Phoenix.getVal());
//
//        String sourceKey = sourceService.generateSourceKey("{\n" +
//                " \"jdbcUrl\":\"172.16.10.104,172.16.10.224,172.16.10.252:2181\"\n" +
//                "}", DataSourceType.HBASE.getVal());
//        String sourceKey = sourceService.generateSourceKey("{\n" +
//                " \"jdbcUrl\":\"172.16.100.175:2181,172.16.101.196:2181,172.16.101.227:2181\"\n" +
//                "}", DataSourceType.HBASE.getVal());
//        String sourceKey = sourceService.generateSourceKey("{\n" +
//                " \"jdbcUrl\":\"172.16.100.175,172.16.101.196,172.16.101.227:2181\"\n" +
//                "}", DataSourceType.HBASE.getVal());
//        String sourceKey = sourceService.generateSourceKey("{\n" +
//        " \"jdbcUrl\":\"jdbc:impala://172.16.8.83:21050/dtstack;AuthMech=3\"\n" +
//        "}", DataSourceType.IMPALA.getVal());

        String sourceKey = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"jdbc:hive2://krbt3:10000/default;principal=hdfs/krbt3@DTSTACK.COM\"\n" +
                "}", DataSourceType.HIVE.getVal());
        System.out.println(sourceKey);
    }

}
