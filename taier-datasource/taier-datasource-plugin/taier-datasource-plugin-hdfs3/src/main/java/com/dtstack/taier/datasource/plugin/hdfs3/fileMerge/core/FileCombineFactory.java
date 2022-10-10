package com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.core;

import com.dtstack.taier.datasource.api.enums.FileFormat;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class FileCombineFactory {

    public static CombineServer getCombine(Path sourcePath, Configuration configuration, FileFormat fileFormat) throws IOException {

        if (Objects.nonNull(fileFormat)) {
            switch (fileFormat) {
                case ORC:
                    return new OrcCombineServer();
                case PARQUET:
                    return new ParquetCombineServer();
                case TEXT:
                    return new TextCombineServer();
                default:
                    throw new SourceException("we just support orc ,parquet ,text file merge");
            }
        } else {
            FileSystem fs = FileSystem.get(configuration);
            FileStatus[] fileStatuses;
            try {
                fileStatuses = fs.listStatus(sourcePath);
            } catch (Exception e) {
                throw new SourceException(String.format("get path [" + sourcePath + "] status error：%s", e.getMessage()), e);
            }
            //找到第一个文件
            FileStatus fileStatus = Arrays.stream(fileStatuses)
                    .filter(FileStatus::isFile)
                    .findFirst()
                    .orElseThrow(() -> new SourceException("not find any file to combine on " + sourcePath));
            return getTypeBySelf(fileStatus, fs);
        }
    }

    public static CombineServer getTypeBySelf(FileStatus fileStatuses, FileSystem fileSystem) throws IOException {
        FSDataInputStream in = fileSystem.open(fileStatuses.getPath());

        if (isParquet(in, fileStatuses)) {
            return new ParquetCombineServer();
        }

        if (isOrc(in, fileStatuses)) {
            return new OrcCombineServer();
        }

        if (isText(fileStatuses)) {
            return new TextCombineServer();
        }

        throw new SourceException("wo just support orc ,parquet ,text file merge");

    }


    public static boolean isOrc(FSDataInputStream inputStream, FileStatus fileStatus) throws IOException {
        byte[] bytes = new byte[3];
        inputStream.read(fileStatus.getLen() - 4, bytes, 0, 3);
        return "ORC".equals(new String(bytes));
    }

    public static boolean isParquet(FSDataInputStream inputStream, FileStatus fileStatus) throws IOException {
        byte[] bytes = new byte[4];
        inputStream.read(0, bytes, 0, 4);
        if ("PAR1".equals(new String(bytes))) {
            bytes = new byte[4];
            inputStream.read(fileStatus.getLen() - 4, bytes, 0, 4);
            return "PAR1".equals(new String(bytes));
        }
        return false;
    }

    public static boolean isText(FileStatus fileStatus) {

        String pathType = fileStatus.getPath().getName().substring(fileStatus.getPath().getName().lastIndexOf(".") + 1);
        return "text".equalsIgnoreCase(pathType);
    }

}
