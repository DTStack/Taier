package com.dtstack.rdos.engine.execution.flink120.sink.hdfs;

import com.dtstack.rdos.common.util.ClassUtil;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.OutputFormatSinkFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.sinks.StreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;

/**
 * Created by softfly on 17/7/3.
 */
public class HdfsSink implements StreamTableSink<Row> {

    public static final String HDFS_DEFAULT_FS = "defaultFS";
    public static final String HDFS_FILETYPE = "fileType";
    public static final String HDFS_PATH = "path";
    public static final String HDFS_COMPRESS = "compress";

    protected String defaultFS;
    protected String path;
    protected String compress;
    protected String fileType;
    protected String[] fieldNames;
    protected TypeInformation[] fieldTypes;
    protected String tableName;
    protected String[] fullFieldNames;
    protected Class[] fullFieldTypes;

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        OutputFormat<Row> outputFormat = null;

        if(fileType.equalsIgnoreCase("orc")) {
            HdfsOrcOutputFormat.OrcOutputFormatBuilder builder = HdfsOrcOutputFormat.buildOrcOutputFormat();
            builder.setCompress(compress).setDefaultFS(defaultFS);
            builder.setPath(path).setFileName(tableName).setWriteMode("APPEND");
            builder.setColumnNames(fullFieldNames).setInputColumnNames(fieldNames);

            String[] inputColumnTypes = new String[fieldTypes.length];
            for(int i = 0; i < inputColumnTypes.length; ++i) {
                inputColumnTypes[i] = fieldTypes[i].getTypeClass().getSimpleName().toUpperCase();
            }
            builder.setInputColumnTypes(inputColumnTypes);

            String[] columnTypes = new String[fullFieldTypes.length];
            for(int i = 0; i < columnTypes.length; ++i) {
                columnTypes[i] = ClassUtil.getTypeFromClass(fullFieldTypes[i]);
            }
            builder.setColumnTypes(columnTypes);

            outputFormat = builder.finish();
        } else { // 默认当成文本文件处理
        }

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
