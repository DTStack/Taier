package com.dtstack.rdos.base.flink;

import com.dtstack.rdos.engine.execution.flink120.sink.hbase.HbaseOutputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by softfly on 17/6/29.
 */
public class TestHbaseOutputFormat {

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        Collection<Row> rows = new ArrayList<>();
        Row row = new Row(2);
        row.setField(0,"xxxx");
        row.setField(1,"yyyy");
        rows.add(row);
        DataSet<Row> ds = env.fromCollection(rows);

        OutputFormat<Row> outputFormat =  HbaseOutputFormat.buildHbaseOutputFormat()
                .setHost("172.16.1.151")
                .setPort("2181")
                .setParent("/hbase137")
                .setTable("test")
                .finish();

        ds.output(outputFormat);

        env.execute();
    }

}
