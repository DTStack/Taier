package com.dtstack.taier.datasource.plugin.hdfs.fileMerge;

import org.apache.commons.lang.StringUtils;

/**
 * @author jiangbo
 * @explanation
 * @date 2019/4/3
 */
public enum ECompressType {


    /**
     * text file
     */
    TEXT_NONE("NONE", "text", "", 1.0F),
    TEXT_GZIP("GZIP", "text", ".gz", 0.7091F),
    TEXT_BZIP2("BZIP2", "text", ".bz2", 0.6666F),

    /**
     * orc file
     */
    ORC_NONE("NONE", "orc", "", 1.0F),
    ORC_SNAPPY("SNAPPY", "orc", ".snappy", 0.233F),
    ORC_GZIP("GZIP", "orc", ".gz", 1.0F),
    ORC_BZIP("BZIP", "orc", ".bz", 1.0F),
    ORC_LZ4("LZ4", "orc", ".lz4", 1.0F),
    ORC_ZLIB("ZLIB", "orc", ".zltb", 1.0F),
    LZO("LZO", "orc", ".lzo", 1.0F),
    ZSTD("ZSTD", "orc", ".zstd", 1.0F),


    /**
     * parquet file
     */
    PARQUET_UNCOMPRESSED("UNCOMPRESSED", "parquet", "", 1.0F),
    PARQUET_SNAPPY("SNAPPY", "parquet", ".snappy", 0.274F),
    PARQUET_GZIP("GZIP", "parquet", ".gz", 1.0F),
    PARQUET_LZO("LZO", "parquet", ".lzo", 1.0F),
    PARQUET_BROTLI("BROTLI", "parquet", ".brotli", 1.0F),
    PARQUET_LZ4("LZ4", "parquet", ".lz4", 1.0F),
    PARQUET_ZSTD("ZSTD", "parquet", ".zstd", 1.0F);

    //压缩类型
    private final String type;

    //文件类型
    private final String fileType;

    //后缀名
    private final String suffix;

    //压缩比 压缩后文件大小/压缩前文件大小 目前仅tetx格式是测试过的
    private final float deviation;

    ECompressType(String type, String fileType, String suffix, float deviation) {
        this.type = type;
        this.fileType = fileType;
        this.suffix = suffix;
        this.deviation = deviation;
    }

    public static ECompressType getByTypeAndFileType(String type, String fileType) {
        if (StringUtils.isBlank(type)) {
            return null;
        }

        for (ECompressType value : ECompressType.values()) {
            if (value.getType().equalsIgnoreCase(type) && value.getFileType().equalsIgnoreCase(fileType)) {
                return value;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getFileType() {
        return fileType;
    }

    public String getSuffix() {
        return suffix;
    }

    public float getDeviation() {
        return deviation;
    }
}
