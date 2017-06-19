package com.dtstack.rdos.engine.execution.flink130.sink.hbase;

import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import java.io.IOException;

/**
 * Created by sishu.yss on 2017/5/23.
 */
public class HbaseOutputFormat extends RichOutputFormat<Row> {

    @Override
    public void configure(Configuration parameters) {

    }

    @Override
    public void open(int taskNumber, int numTasks) throws IOException {

    }

    @Override
    public void writeRecord(Row record) throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
