package com.dtstack.taier.datasource.plugin.hdfs.reader;

import com.dtstack.taier.datasource.api.enums.FileFormat;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.commons.lang3.StringUtils;

/**
 * 工厂类
 *
 * @author luming
 * @date 2022/3/16
 */
public class ReaderFactory {
    public static HdfsReader getInstance(String type) {
        if (StringUtils.isBlank(type)) {
            throw new SourceException("read type can't be blank");
        }
        if (FileFormat.ORC.getVal().equalsIgnoreCase(type)) {
            return new HdfsOrcReader();
        } else if (FileFormat.PARQUET.getVal().equalsIgnoreCase(type)) {
            return new HdfsParquetReader();
        } else if (FileFormat.TEXT.getVal().equalsIgnoreCase(type)) {
            return new HdfsTextReader();
        } else {
            throw new SourceException(
                    "can't match hdfs read type ，valid values are 'text/parquet/orc'");
        }
    }
}
