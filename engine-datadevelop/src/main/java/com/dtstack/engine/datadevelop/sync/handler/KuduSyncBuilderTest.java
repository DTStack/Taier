package com.dtstack.batch.sync.handler;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class KuduSyncBuilderTest {

    private final KuduSyncBuilder kuduSyncBuilder = new KuduSyncBuilder();
    private Map<String, Object> map = Maps.newHashMap();

    @Before
    public void setUp () {
        map.put("hostPorts", "testPorts");
        map.put("others", null);
    }

    @Test
    public void setReaderJsonTest() {
        kuduSyncBuilder.setReaderJson(map, Maps.newHashMap(), null);
    }

    @Test
    public void setWriterJson() {
        kuduSyncBuilder.setWriterJson(map, Maps.newHashMap(), null);
    }

    @Test
    public void syncReaderBuild() {
        kuduSyncBuilder.setReaderJson(map, Maps.newHashMap(), null);
        kuduSyncBuilder.syncReaderBuild(map, null);
    }

    @Test
    public void syncWriterBuild() {
        kuduSyncBuilder.setWriterJson(map, Maps.newHashMap(), null);
        kuduSyncBuilder.syncWriterBuild(null, map, null);
    }

    @Test
    public void DataSourceTypeTest(){
        kuduSyncBuilder.getDataSourceType();
    }
}
