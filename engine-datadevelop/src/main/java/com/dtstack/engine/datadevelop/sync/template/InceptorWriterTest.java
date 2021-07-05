package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.loader.dto.Column;
import org.junit.Test;

import java.io.IOException;

/**
 * date: 2021/6/9 9:18 下午
 * author: zhaiyue
 */

public class InceptorWriterTest {

    InceptorWriter inceptorWriter = new InceptorWriter();

    @Test
    public void toWriterJsonTest() throws IOException {
        String ss = "{\"sourceId\":1575,\"column\":[{\"part\":false,\"comment\":\"\",\"type\":\"string\",\"key\":\"id\"}],\"writeMode\":\"replace\",\"source\":{\"active\":1,\"createUserId\":179,\"dataDesc\":\"\",\"dataJson\":\"eyJqZGJjLnBhc3N3b3JkIjoiIiwiamRiYy51c2VybmFtZSI6IiIsIm9wZW5LZXJiZXJvcyI6ZmFsc2UsImhpdmUubWV0YXN0b3JlLnVyaXMiOiJ0aHJpZnQ6Ly8xNzIuMTYuMjEuMTc3OjkwODMiLCJqZGJjLnVybCI6ImpkYmM6aGl2ZTI6Ly8xNzIuMTYuMjEuMTc3OjEwMDAwL2RldiIsIm9wZW5IYWRvb3BDb25maWciOnRydWUsImhhZG9vcENvbmZpZyI6IntcbiAgICBcIkhBRE9PUF9VU0VSX05BTUVcIjogXCJoaXZlXCIsXG4gICAgXCJkZnMuY2xpZW50LmZhaWxvdmVyLnByb3h5LnByb3ZpZGVyLm5hbWVzZXJ2aWNlMVwiOiBcIm9yZy5hcGFjaGUuaGFkb29wLmhkZnMuc2VydmVyLm5hbWVub2RlLmhhLkNvbmZpZ3VyZWRGYWlsb3ZlclByb3h5UHJvdmlkZXJcIixcbiAgICBcImRmcy5uYW1lc2VydmljZXNcIjogXCJuYW1lc2VydmljZTFcIixcbiAgICBcImRmcy5oYS5uYW1lbm9kZXMubmFtZXNlcnZpY2UxXCI6IFwibm4xLG5uMlwiLFxuICAgIFwiZGZzLm5hbWVub2RlLnJwYy1hZGRyZXNzLm5hbWVzZXJ2aWNlMS5ubjFcIjogXCIxNzIuMTYuMjEuMTgyOjgwMjBcIixcbiAgICBcImRmcy5uYW1lbm9kZS5ycGMtYWRkcmVzcy5uYW1lc2VydmljZTEubm4yXCI6IFwiMTcyLjE2LjIyLjIzNDo4MDIwXCJcbn0iLCJkZWZhdWx0RlMiOiJoZGZzOi8vbmFtZXNlcnZpY2UxIiwicHJpbmNpcGFsTGlzdCI6W119\",\"dataName\":\"inceptor_1\",\"gmtCreate\":1622086194000,\"gmtModified\":1622519520000,\"id\":1575,\"isDefault\":0,\"isDeleted\":0,\"linkState\":1,\"modifyUserId\":5,\"projectId\":1039,\"tenantId\":3,\"type\":52},\"type\":\"INCEPTOR\",\"password\":\"\",\"partition\":\"pt=20200529\",\"hiveMetastoreUris\":\"thrift://172.16.21.177:9083\",\"hadoopConfig\":{\"dfs.ha.namenodes.nameservice1\":\"nn1,nn2\",\"HADOOP_USER_NAME\":\"hive\",\"dfs.namenode.rpc-address.nameservice1.nn1\":\"172.16.21.182:8020\",\"dfs.namenode.rpc-address.nameservice1.nn2\":\"172.16.22.234:8020\",\"dfs.client.failover.proxy.provider.nameservice1\":\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\"dfs.nameservices\":\"nameservice1\"},\"name\":\"inceptor_1\",\"jdbcUrl\":\"jdbc:hive2://172.16.21.177:10000/dev\",\"defaultFS\":\"hdfs://nameservice1\",\"havePartition\":true,\"extralConfig\":\"\",\"table\":\"zy_parquet_part\",\"sourceIds\":[1575],\"dataSourceType\":52,\"username\":\"\"}";
        JSONObject jsonObject = JSONObject.parseObject(ss);
        jsonObject.put("table", "");
        inceptorWriter = PublicUtil.objectToObject(jsonObject, InceptorWriter.class);
        inceptorWriter.toWriterJson();
    }

    @Test
    public void toWriterJsonStringTest() {
        inceptorWriter.toWriterJsonString();
    }

    @Test
    public void isValidTest() {
        inceptorWriter.isValid();
    }

    @Test
    public void getErrMsgTest() {
        inceptorWriter.getErrMsg();
    }

    @Test
    public void checkFormatTest() {
        JSONObject jsonObject = new JSONObject();
        JSONObject parameter = new JSONObject();
        parameter.put("path", "1/2/3");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(new Column());
        parameter.put("column", jsonArray);
        jsonObject.put("parameter", parameter);
        inceptorWriter.checkFormat(jsonObject);
    }

}

