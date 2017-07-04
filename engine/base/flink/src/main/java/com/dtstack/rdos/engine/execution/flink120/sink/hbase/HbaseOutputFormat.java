package com.dtstack.rdos.engine.execution.flink120.sink.hbase;

import com.dtstack.rdos.common.util.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.protobuf.generated.FSProtos;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hdfs.util.EnumCounters;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author: jingzhen@dtstack.com
 * date: 2017-6-29
 */
public class HbaseOutputFormat extends RichOutputFormat<Row> {

    private String host;
    private String port;
    private String parent;
    private String[] rowkey;
    private String tableName;
    private Map<String, String> columnNameFamily;
    private String[] columnNames;
    private String[] inputColumnTypes;
    private String[] columnTypes;


    private org.apache.hadoop.conf.Configuration conf;
    private transient Connection conn;
    private transient Table table;

    public final SimpleDateFormat ROWKEY_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public final SimpleDateFormat FIELD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void configure(Configuration parameters) {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.zookeeper.quorum", "172.16.1.151");
        conf.set("zookeeper.znode.parent", "/hbase137");
    }

    @Override
    public void open(int taskNumber, int numTasks) throws IOException {
        conn = ConnectionFactory.createConnection(conf);
        table = conn.getTable(TableName.valueOf(tableName));
    }

    @Override
    public void writeRecord(Row record) throws IOException {

        List<String> list = new ArrayList<>();
        for(int i = 0; i < rowkey.length; ++i) {
            String colName = rowkey[i];
            int j = 0;
            for(; j < columnNames.length; ++j) {
                if(columnNames[j].equals(colName))
                    break;
            }
            if(j != columnNames.length && record.getField(i) != null) {
                Object field = record.getField(j);
                if(field == null ) {
                    list.add("null");
                } else if (field instanceof java.util.Date){
                    java.util.Date d = (java.util.Date)field;
                    list.add(ROWKEY_DATE_FORMAT.format(d));
                } else {
                    list.add(field.toString());
                }
            }
        }

        String key = StringUtils.join(list, "-");

        Put put = new Put(key.getBytes());
        for(int i = 0; i < record.getArity(); ++i) {
            Object field = record.getField(i);
            String cf = columnNameFamily.get(columnNames[i]);
            if(field != null) {
                String inputColumnType = inputColumnTypes[i];
                String columnType = columnTypes[i];

                if(!inputColumnType.equalsIgnoreCase(columnType)) {
                    field = ClassUtil.convertType(field, inputColumnType, columnType);
                }

                String value = null;
                if(columnType.equalsIgnoreCase("DATE") || columnType.equalsIgnoreCase("TIMESTAMP")) {
                    value = FIELD_DATE_FORMAT.format((java.util.Date)field);
                } else {
                    value = columnType.toString();
                }

                put.addColumn(cf.getBytes(), columnNames[i].getBytes(), value.getBytes());

            } else {
                put.addColumn(cf.getBytes(), columnNames[i].getBytes(), null);
            }
        }

        table.put(put);

    }

    @Override
    public void close() throws IOException {
        if(conn != null) {
            conn.close();
            conn = null;
        }
    }

    private HbaseOutputFormat() {}

    public static HbaseOutputFormatBuilder buildHbaseOutputFormat() {
        return new HbaseOutputFormatBuilder();
    }

    public static class HbaseOutputFormatBuilder {

        private HbaseOutputFormat format;

        private HbaseOutputFormatBuilder() {
            format = new HbaseOutputFormat();
        }

        public HbaseOutputFormatBuilder setHost(String host) {
            format.host = host;
            return this;
        }

        public HbaseOutputFormatBuilder setPort(String port) {
            format.port = port;
            return this;
        }

        public HbaseOutputFormatBuilder setParent(String parent) {
            format.parent = parent;
            return this;
        }

        public HbaseOutputFormatBuilder setTable(String tableName) {
            format.tableName = tableName;
            return this;
        }

        public HbaseOutputFormatBuilder setColumnNameFamily(Map<String, String> columnNameFamily) {
            format.columnNameFamily = columnNameFamily;
            return this;
        }

        public HbaseOutputFormatBuilder setRowkey(String[] rowkey) {
            format.rowkey = rowkey;
            return this;
        }

        public HbaseOutputFormatBuilder setColumnNames(String[] columnNames) {
            format.columnNames = columnNames;
            return this;
        }

        public HbaseOutputFormatBuilder setColumnTypes(String[] columnTypes) {
            format.columnTypes = columnTypes;
            return this;
        }

        public HbaseOutputFormatBuilder setInputColumnTypes(String[] inputColumnTypes) {
            format.inputColumnTypes = inputColumnTypes;
            return this;
        }

        public HbaseOutputFormat finish() {
            Preconditions.checkNotNull(format.host, "host should be specified");
            Preconditions.checkNotNull(format.port, "port should be specified");
            Preconditions.checkNotNull(format.tableName, "tableName should be specified");
            return this.format;
        }

    }


}
