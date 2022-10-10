package com.dtstack.taier.datasource.plugin.hdfs.fileMerge.meta;

import org.apache.parquet.schema.MessageType;

/**
 * parquet文件元信息
 */
public class ParquetMetaData extends FileMetaData {
    //新的parquet文件的schema
    private MessageType schema;

    public MessageType getSchema() {
        return schema;
    }

    public void setSchema(MessageType schema) {
        this.schema = schema;
    }
}
