package com.dtstack.sql.flink;

import com.dtstack.sql.flink.api.FlinkTableLineage;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author chener
 * @Classname FlinkBloodTest
 * @Description
 * @Date 2020/10/21 17:27
 * @Created chener@dtstack.com
 */
public class FlinkBloodTest {
    @Test
    public void test(){
        String sql = "-- name sdsd123f\n" +
                "-- type FlinkSQL\n" +
                "-- author admin@dtstack.com\n" +
                "-- create time 2020-10-15 19:37:43\n" +
                "-- desc \n" +
                "CREATE TABLE MySource(\n" +
                "    id int,\n" +
                "    content string\n" +
                " )WITH(\n" +
                "    type ='kafka10',\n" +
                "    bootstrapServers ='172.16.100.242:9092',\n" +
                "    zookeeperQuorum ='172.16.100.242:2181',\n" +
                "    offsetReset ='latest',\n" +
                "    topic ='shifang1234',\n" +
                "    charsetName ='utf-8',\n" +
                "    timezone='Asia/Shanghai',\n" +
                "    updateMode ='append',\n" +
                "    enableKeyPartitions ='false',\n" +
                "    topicIsPattern ='false',\n" +
                "    parallelism ='1'\n" +
                " );\n" +
                "\n" +
                "CREATE TABLE MyResult(\n" +
                "    id int,\n" +
                "    content string,\n" +
                "    dt int\n" +
                " )WITH(\n" +
                "    type ='impala',\n" +
                "    url ='jdbc:impala://172.16.101.13:21050/default',\n" +
                "    userName ='hxb',\n" +
                "    password = '******',\n" +
                "    authMech ='3',\n" +
                "    tableName ='daythree',\n" +
                "    updateMode ='append',\n" +
                "    storeType ='text',\n" +
                "    parallelism ='1',\n" +
                "    batchSize ='100',\n" +
                "    batchWaitInterval ='1000',\n" +
                "    enablePartition ='true',\n" +
                "    partitionFields ='dt'\n" +
                " );\n" +
                "\n" +
                "INSERT \n" +
                "INTO\n" +
                "    MyResult\n" +
                "    SELECT\n" +
                "        id,\n" +
                "        content,\n" +
                "        18 AS dt \n" +
                "    FROM\n" +
                "        MySource;";

        List<FlinkTableLineage> blood = BloodParser.getBlood(sql);
        Assert.assertEquals(1,blood.size());
    }
}
