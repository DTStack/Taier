package com.dtstack.batch.sync.util;

import com.dtstack.batch.sync.job.PluginName;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ColumnUtilTest {

    @Test
    public void getColumnsTest() {
        List<Object> columns = Lists.newArrayList();
        String pluginName = PluginName.AWS_S3_R;
        ColumnUtil.getColumns(columns, pluginName);
        columns.add("column");
        ColumnUtil.getColumns(columns, pluginName);
        columns.clear();
        Map<String, Object> column1 = Maps.newHashMap();
        column1.put("key", 1);
        column1.put("type", 1);
        column1.put("index", 1);
        column1.put("isPart", true);
        columns.add(column1);
        Map<String, Object> column2 = Maps.newHashMap();
        column2.put("key", "1");
        column2.put("type", 1);
        column2.put("index", 1);
        column2.put("isPart", true);
        columns.add(column1);
        columns.add(column2);
        ColumnUtil.getColumns(columns, pluginName);
    }
}
