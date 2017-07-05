package com.dtstack.rdos.engine.execution.flink120.sink.hbase;

import com.dtstack.rdos.common.util.ClassUtil;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.OutputFormatSinkFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.sinks.StreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sishu.yss on 2017/5/23.
 */
public abstract class HbaseSink implements StreamTableSink<Row> {

    public static final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "port";
    public static final String HBASE_ZOOKEEPER_QUORUM = "host";
    public static final String ZOOKEEPER_ZNODE_PARENT = "parent";
    public static final String HBASE_COLUMN_FAMILY = "columnFamily";
    public static final String HBASE_ROWKEY = "rowkey";

    protected String[] fieldNames;
    protected TypeInformation[] fieldTypes;
    protected String host;
    protected String port;
    protected String parent;
    protected String tableName;
    protected String[] fullFieldNames;
    protected Class[] fullFieldTypes;
    protected String columnFamily;
    protected String rowkey;

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        HbaseOutputFormat.HbaseOutputFormatBuilder builder = HbaseOutputFormat.buildHbaseOutputFormat();
        builder.setHost(this.host).setPort(this.port).setTable(this.tableName);

        if(this.parent != null)
            builder.setParent(this.parent);

        Map<String, String> map = new HashMap<>();
        String[] cf = columnFamily.split("\\s+");
        for(int i = 0; i < cf.length; ++i) {
            int mid = cf[i].indexOf("[");
            String cfName = cf[i].substring(0, mid);
            int end = cf[i].indexOf("]");
            String[] cfMember = cf[i].substring(mid + 1, end).split(":");
            for(String member : cfMember) {
                map.put(member, cfName);
            }
        }
        builder.setColumnNameFamily(map);

        String[] inputColumnTypes = new String[fieldTypes.length];
        for(int i = 0; i < inputColumnTypes.length; ++i) {
            inputColumnTypes[i] = fieldTypes[i].getTypeClass().getSimpleName().toUpperCase();
        }
        builder.setInputColumnTypes(inputColumnTypes);

        String[] columnTypes = new String[fieldTypes.length];
        for(int i = 0; i < fieldNames.length; ++i) {
            int j = 0;
            for(; j < fullFieldNames.length; ++j) {
                if(fullFieldNames[j].equalsIgnoreCase(fieldNames[i]))
                    break;
            }
            columnTypes[i] = ClassUtil.getTypeFromClass(fullFieldTypes[j]);
        }

        builder.setColumnTypes(columnTypes);
        builder.setRowkey(rowkey.split(":"));
        builder.setColumnNames(fieldNames);
        HbaseOutputFormat outputFormat = builder.finish();
        RichSinkFunction richSinkFunction = new OutputFormatSinkFunction(outputFormat);
        dataStream.addSink(richSinkFunction);
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
