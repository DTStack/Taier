package com.dtstack.rdos.engine.execution.flink120.sink.hdfs;

import com.google.common.base.Preconditions;

/**
 * Created by softfly on 17/7/4.
 */
public class HdfsOutputFormatBuilder {
    protected HdfsOutputFormat format;

    public HdfsOutputFormatBuilder(String fileType) {
        if(fileType == null || fileType.equalsIgnoreCase("TEXT")) {
            format = new HdfsTextOutputFormat();
        } else if(fileType.equalsIgnoreCase("ORC")) {
            format = new HdfsOrcOutputFormat();
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    public HdfsOutputFormatBuilder setColumnNames(String[] columnNames) {
        format.columnNames = columnNames;
        return this;
    }

    public HdfsOutputFormatBuilder setColumnTypes(String[] columnTypes) {
        format.columnTypes = columnTypes;
        return this;
    }

    public HdfsOutputFormatBuilder setDefaultFS(String defaultFS) {
        format.defaultFS = defaultFS;
        return this;
    }

    public HdfsOutputFormatBuilder setPath(String path) {
        format.path = path;
        return this;
    }

    public HdfsOutputFormatBuilder setFileName(String fileName) {
        format.fileName = fileName;
        return this;
    }

    public HdfsOutputFormatBuilder setWriteMode(String writeMode) {
        format.writeMode = writeMode;
        return this;
    }

    public HdfsOutputFormatBuilder setCompress(String compress) {
        format.compress = compress;
        return this;
    }

    public HdfsOutputFormatBuilder setInputColumnNames(String[] inputColumnNames) {
        format.inputColumnNames = inputColumnNames;
        return this;
    }

    public HdfsOutputFormatBuilder setInputColumnTypes(String[] inputColumnTypes) {
        format.inputColumnTypes = inputColumnTypes;
        return this;
    }

    public HdfsOutputFormatBuilder setDelimiter(String delimiter) {
        format.delimiter = delimiter;
        return this;
    }

    public HdfsOutputFormat finish() {
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
