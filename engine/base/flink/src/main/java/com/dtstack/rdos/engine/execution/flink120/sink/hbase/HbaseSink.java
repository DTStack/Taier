package com.dtstack.rdos.engine.execution.flink120.sink.hbase;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.sinks.StreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;

/**
 * Created by sishu.yss on 2017/5/23.
 */
public abstract class HbaseSink implements StreamTableSink<Row> {

    public static final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "port";
    public static final String HBASE_ZOOKEEPER_QUORUM = "host";
    public static final String ZOOKEEPER_ZNODE_PARENT = "zookeeper.znode.parent";
    public static final String HBASE_TABLENAME_KEY = "tableName";

    protected String[] fieldNames;
    protected TypeInformation[] fieldTypes;
    protected String host;
    protected String port;
    protected String parent;
    protected String tableName;

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        //RichSinkFunction richSinkFunction = new RichSinkFunction();
        HbaseOutputFormat.HbaseOutputFormatBuilder builder = HbaseOutputFormat.buildHbaseOutputFormat();
        //builder.setHost(this.host).setPort(this.port).setTable()
    }

    @Override
    public TypeInformation<Row> getOutputType() {
        return new RowTypeInfo(getFieldTypes());
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    @Override
    public TypeInformation<?>[] getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        this.fieldNames = fieldNames;
        this.fieldTypes = fieldTypes;
        return this;
    }
}
