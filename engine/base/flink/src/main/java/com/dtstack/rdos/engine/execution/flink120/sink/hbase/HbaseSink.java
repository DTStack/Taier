package com.dtstack.rdos.engine.execution.flink120.sink.hbase;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.sinks.StreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;

/**
 * Created by sishu.yss on 2017/5/23.
 */
public abstract class HbaseSink implements StreamTableSink<Row> {
    @Override
    public void emitDataStream(DataStream<Row> dataStream) {

    }

    @Override
    public TypeInformation<Row> getOutputType() {
        return null;
    }

    @Override
    public String[] getFieldNames() {
        return new String[0];
    }

    @Override
    public TypeInformation<?>[] getFieldTypes() {
        return new TypeInformation<?>[0];
    }

    @Override
    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        return null;
    }
}
