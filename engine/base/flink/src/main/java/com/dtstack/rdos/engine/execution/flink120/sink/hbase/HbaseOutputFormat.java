package com.dtstack.rdos.engine.execution.flink120.sink.hbase;

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


    private org.apache.hadoop.conf.Configuration conf;
    private transient Connection conn;
    private transient Table table;

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

        String rowkey = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            rowkey = base64en.encode(md5.digest(record.toString().getBytes("utf-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Put put = new Put(rowkey.getBytes());

        put.addColumn("cf".getBytes(), "q1".getBytes(), record.getField(0).toString().getBytes());
        put.addColumn("cf".getBytes(), "q2".getBytes(), record.getField(1).toString().getBytes());

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

        public HbaseOutputFormat finish() {
            Preconditions.checkNotNull(format.host, "host should be specified");
            Preconditions.checkNotNull(format.port, "port should be specified");
            Preconditions.checkNotNull(format.tableName, "tableName should be specified");
            return this.format;
        }

    }


}
