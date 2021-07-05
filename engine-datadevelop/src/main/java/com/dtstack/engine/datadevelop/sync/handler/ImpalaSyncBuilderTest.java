package com.dtstack.batch.sync.handler;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.batch.mockcontainer.ImpalaSyncBuilderMock;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@MockWith(ImpalaSyncBuilderMock.class)
public class ImpalaSyncBuilderTest {

    private final ImpalaSyncBuilder impalaSyncBuilder = new ImpalaSyncBuilder();

    @Test
    public void setReaderJsonTest() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("table", "kudu");
        map.put("partition", "partition");
        Map<String, Object> column = Maps.newHashMap();
        column.put("key", "name");
        List<Map<String, Object>> columns = Lists.newArrayList(column);
        map.put("column", columns);
        Map<String, Object> dataSource = Maps.newHashMap();
        dataSource.put("jdbcUrl", "jdbc:impala://172.16.101.17:21050/dev");
        dataSource.put("username", "test");
        dataSource.put("password", "test");
        dataSource.put("defaultFS", "hdfs://sdsds");
        dataSource.put("hadoopConfig", "{}");
        Map<String,Object> kerberos = Maps.newHashMap();
        impalaSyncBuilder.setReaderJson(map, dataSource, kerberos);
        map.put("table", "hive");
        impalaSyncBuilder.setReaderJson(map, dataSource, kerberos);
    }

    @Test
    public void setWriterJsonTest() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("table", "kudu");
        map.put("partition", "partition");
        Map<String, Object> column = Maps.newHashMap();
        column.put("key", "name");
        List<Map<String, Object>> columns = Lists.newArrayList(column);
        map.put("column", columns);
        Map<String, Object> dataSource = Maps.newHashMap();
        dataSource.put("jdbcUrl", "jdbc:impala://172.16.101.17:21050/dev");
        dataSource.put("username", "test");
        dataSource.put("password", "test");
        dataSource.put("defaultFS", "hdfs://sdsdsd");
        dataSource.put("hadoopConfig", "{}");
        Map<String,Object> kerberos = Maps.newHashMap();
        impalaSyncBuilder.setWriterJson(map, dataSource, kerberos);
        map.put("table", "hive");
        impalaSyncBuilder.setWriterJson(map, dataSource, kerberos);
    }

    @Test
    public void syncReaderBuildTest() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("tableLocationType", "kudu");
        impalaSyncBuilder.syncReaderBuild(map, Lists.newArrayList(1L));
        map.put("tableLocationType", "hive");
        impalaSyncBuilder.syncReaderBuild(map, Lists.newArrayList(1L));
    }

    @Test
    public void syncWriterBuildTest() {
        Map<String, Object> map = Maps.newHashMap();
        Map<String, Object> column = Maps.newHashMap();
        column.put("key", "name");
        List<Map<String, Object>> columns = Lists.newArrayList(column);
        map.put("column", columns);
        Column allColumn = new Column();
        allColumn.setName("name");
        allColumn.setIndex(1);
        List<Column> allColumns = Lists.newArrayList(allColumn);
        map.put("allColumns", allColumns);
        Column partitionColumn = new Column();
        partitionColumn.setName("name2");
        List<Column> partitionColumns = Lists.newArrayList(partitionColumn);
        map.put("partitionColumns", partitionColumns);
        map.put("tableLocationType", "kudu");
        map.put("writeMode", "");
        map.put("partition", "partition");
        impalaSyncBuilder.syncWriterBuild(Lists.newArrayList(1L), map, null);
        map.put("tableLocationType", "hive");
        impalaSyncBuilder.syncWriterBuild(Lists.newArrayList(1L), map, null);
    }

    @Test
    public void getDataSourceTypeTest() {
        impalaSyncBuilder.getDataSourceType();
    }

}
