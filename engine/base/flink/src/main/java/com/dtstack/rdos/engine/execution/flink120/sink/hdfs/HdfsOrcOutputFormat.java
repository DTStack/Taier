package com.dtstack.rdos.engine.execution.flink120.sink.hdfs;

import com.dtstack.rdos.common.util.ClassUtil;
import com.dtstack.rdos.common.util.DateUtil;
import com.google.common.base.Preconditions;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcSerde;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by softfly on 17/7/3.
 */
public class HdfsOrcOutputFormat extends RichOutputFormat<Row> {

    private static final long serialVersionUID = 1L;
    private static final int NEWLINE = 10;
    private String charsetName = "UTF-8";
    private transient Charset charset;
    private String writeMode;
    private transient boolean overwrite;
    private String compress;
    private String defaultFS;
    private String path;
    private String fileName;

    private transient String outputFilePath;
    private transient RecordWriter recordWriter;
    private transient OrcSerde orcSerde;
    private transient StructObjectInspector inspector;
    private String[] columnNames;
    private String[] columnTypes;
    private String[] inputColumnNames;
    private String[] inputColumnTypes;
    private transient List<ObjectInspector> columnTypeList;
    private transient FileOutputFormat outputFormat;
    private transient JobConf conf;
    private transient Map<String, String> columnNameTypeMap;
    private transient Map<String, Integer> columnNameIndexMap;

    private HdfsOrcOutputFormat(){
        // do nothing at all
    }

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

        this.orcSerde = new OrcSerde();
        this.outputFormat = new org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat();

        this.conf = new JobConf();

        if(compress != null && compress.length() != 0 && !compress.equalsIgnoreCase("NONE")) {
            if(compress.equalsIgnoreCase("SNAPPY")) {
                this.outputFormat.setOutputCompressorClass(conf, SnappyCodec.class);
            } else {
                throw new IllegalArgumentException("Unsupported compress format: " + compress);
            }
        }

        columnNameTypeMap = new HashMap<>();
        columnNameIndexMap = new HashMap<>();
        for(int i = 0; i < columnNames.length; ++i) {
            columnNameTypeMap.put(columnNames[i], columnTypes[i]);
            columnNameIndexMap.put(columnNames[i], i);
        }

        this.columnTypeList = new ArrayList<>();
        for(String columnType : columnTypes) {
            this.columnTypeList.add(HdfsUtil.columnTypeToObjectInspetor(columnType));
        }
        this.inspector = ObjectInspectorFactory
                .getStandardStructObjectInspector(Arrays.asList(this.columnNames), this.columnTypeList);
    }

    @Override
    public void open(int taskNumber, int numTasks) throws IOException {
        if(taskNumber >= 0 && numTasks >= 1) {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = formatter.format(currentTime);

            String pathStr = outputFilePath + "/" + taskNumber + "." + dateString + "." + UUID.randomUUID();
            System.out.println("pathStr=" + pathStr);

            this.recordWriter = this.outputFormat.getRecordWriter(null, conf, pathStr, Reporter.NULL);

        } else {
            throw new IllegalArgumentException("TaskNumber: " + taskNumber + ", numTasks: " + numTasks);
        }
    }

    @Override
    public void writeRecord(Row row) throws IOException {

        Object[] record = new Object[columnNames.length];
        for(int i = 0; i < row.getArity(); ++i) {
            Object column = row.getField(i);

            String columnName = inputColumnNames[i];
            String fromType = inputColumnTypes[i];
            String toType = columnNameTypeMap.get(columnName);

            if(toType == null) {
                continue;
            }

            if(!fromType.equalsIgnoreCase(toType)) {
                column = ClassUtil.convertType(column, fromType, toType);
            }

            String rowData = column.toString();
            Object field = null;
            switch(toType.toUpperCase()) {
                case "TINYINT":
                    field = Byte.valueOf(rowData);
                    break;
                case "SMALLINT":
                    field = Short.valueOf(rowData);
                    break;
                case "INT":
                    field = Integer.valueOf(rowData);
                    break;
                case "BIGINT":
                    field = Long.valueOf(rowData);
                    break;
                case "FLOAT":
                    field = Float.valueOf(rowData);
;                   break;
                case "DOUBLE":
                    field = Double.valueOf(rowData);
                    break;
                case "STRING":
                case "VARCHAR":
                case "CHAR":
                    field = rowData;
                    break;
                case "BOOLEAN":
                    field = Boolean.valueOf(rowData);
                    break;
                case "DATE":
                    field = DateUtil.columnToDate(column);
                    break;
                case "TIMESTAMP":
                    //recordList.add(new java.sql.Timestamp(column.asDate().getTime()));
                    java.sql.Date d = DateUtil.columnToDate(column);
                    field = new java.sql.Timestamp(d.getTime());
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            record[columnNameIndexMap.get(columnName)] = field;

        }

        this.recordWriter.write(NullWritable.get(), this.orcSerde.serialize(Arrays.asList(record), this.inspector));
    }


    @Override
    public void close() throws IOException {
        RecordWriter rw = this.recordWriter;
        if(rw != null) {
            rw.close(Reporter.NULL);
            this.recordWriter = null;
        }
    }

    public static OrcOutputFormatBuilder buildOrcOutputFormat() {
        return new OrcOutputFormatBuilder();
    }

    public static class OrcOutputFormatBuilder {

        protected HdfsOrcOutputFormat format;

        protected OrcOutputFormatBuilder() {
            this.format = new HdfsOrcOutputFormat();
        }

        public OrcOutputFormatBuilder setColumnNames(String[] columnNames) {
            format.columnNames = columnNames;
            return this;
        }

        public OrcOutputFormatBuilder setColumnTypes(String[] columnTypes) {
            format.columnTypes = columnTypes;
            return this;
        }

        public OrcOutputFormatBuilder setDefaultFS(String defaultFS) {
            format.defaultFS = defaultFS;
            return this;
        }

        public OrcOutputFormatBuilder setPath(String path) {
            format.path = path;
            return this;
        }

        public OrcOutputFormatBuilder setFileName(String fileName) {
            format.fileName = fileName;
            return this;
        }

        public OrcOutputFormatBuilder setWriteMode(String writeMode) {
            format.writeMode = writeMode;
            return this;
        }

        public OrcOutputFormatBuilder setCompress(String compress) {
            format.compress = compress;
            return this;
        }

        public OrcOutputFormatBuilder setInputColumnNames(String[] inputColumnNames) {
            format.inputColumnNames = inputColumnNames;
            return this;
        }

        public OrcOutputFormatBuilder setInputColumnTypes(String[] inputColumnTypes) {
            format.inputColumnTypes = inputColumnTypes;
            return this;
        }

        public HdfsOrcOutputFormat finish() {

            Preconditions.checkNotNull(format.defaultFS);
            Preconditions.checkNotNull(format.path);
            Preconditions.checkNotNull(format.columnNames);
            Preconditions.checkNotNull(format.columnTypes);
            Preconditions.checkNotNull(format.inputColumnNames);
            Preconditions.checkNotNull(format.inputColumnTypes);

            Preconditions.checkArgument(format.inputColumnNames.length == format.inputColumnTypes.length);
            Preconditions.checkArgument(format.columnNames.length == format.columnTypes.length);

            return this.format;
        }

    }

}
