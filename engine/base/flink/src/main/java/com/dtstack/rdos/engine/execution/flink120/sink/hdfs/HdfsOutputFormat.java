package com.dtstack.rdos.engine.execution.flink120.sink.hdfs;

import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by softfly on 17/7/4.
 */
public abstract class HdfsOutputFormat extends RichOutputFormat<Row> {

    protected static final int NEWLINE = 10;
    protected String charsetName = "UTF-8";
    protected transient Charset charset;
    protected String writeMode;
    protected transient boolean overwrite;
    protected String compress;
    protected String defaultFS;
    protected String path;
    protected String fileName;
    protected String delimiter;

    protected String[] columnNames;
    protected String[] columnTypes;
    protected String[] inputColumnNames;
    protected String[] inputColumnTypes;

    protected transient String outputFilePath;
    protected transient FileOutputFormat outputFormat;
    protected transient JobConf conf;
    protected transient Map<String, String> columnNameTypeMap;
    protected transient Map<String, Integer> columnNameIndexMap;
    protected transient RecordWriter recordWriter;

    @Override
    public void configure(Configuration configuration) {
        if (this.writeMode == null || this.writeMode.length() == 0 || this.writeMode.equalsIgnoreCase("APPEND"))
            this.overwrite = true;
        else if (this.writeMode.equalsIgnoreCase("NONCONFLICT"))
            this.overwrite = false;
        else
            throw new IllegalArgumentException("Unsupported WriteMode");

        this.outputFilePath = defaultFS + path + "/" + fileName;

        if(!overwrite) {
            org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
            conf.set("fs.default.name", defaultFS);

            FileSystem fs = null;
            try {
                fs = FileSystem.get(conf);
                if(fs.exists(new Path(outputFilePath)))
                    throw new RuntimeException("nonConflict, you know that.");
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }

        }

        conf = new JobConf();

        columnNameTypeMap = new HashMap<>();
        columnNameIndexMap = new HashMap<>();
        for(int i = 0; i < columnNames.length; ++i) {
            columnNameTypeMap.put(columnNames[i], columnTypes[i]);
            columnNameIndexMap.put(columnNames[i], i);
        }

    }

    public abstract void writeRecord(Row row) throws IOException;

    @Override
    public void close() throws IOException {
        RecordWriter rw = this.recordWriter;
        if(rw != null) {
            rw.close(Reporter.NULL);
            this.recordWriter = null;
        }
    }


}
