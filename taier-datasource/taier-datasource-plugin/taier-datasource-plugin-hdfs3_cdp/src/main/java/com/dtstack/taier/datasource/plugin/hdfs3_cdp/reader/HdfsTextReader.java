package com.dtstack.taier.datasource.plugin.hdfs3_cdp.reader;

import com.dtstack.taier.datasource.api.dto.HdfsQueryDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * text reader
 *
 * @author luming
 * @date 2022/3/16
 */
public class HdfsTextReader extends AbsReader {
    @Override
    protected List<String> parseFile(Path path,
                                     FileSystem fs,
                                     Configuration configuration,
                                     HdfsQueryDTO query) throws IOException {
        //如果前面的文件已经找到指定行列的数据，则后续的文件不再进行读取
        if (isFound) {
            return Lists.newArrayList();
        }

        try (FSDataInputStream is = fs.open(path);
             BufferedReader d = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            Integer colIndex = query.getColIndex();
            Integer rowIndex = query.getRowIndex();
            Integer limit = query.getLimit();
            String lineStr;
            List<String> values = Lists.newArrayList();

            //目前只支持指定行列或只指定列
            while ((lineStr = d.readLine()) != null) {
                //读取行数超出limit限制抛出异常
                if (limit != null && currentLine >= limit) {
                    throw new SourceException(
                            "The actual number of data rows exceeds the limit ：" + limit);
                }

                if (rowIndex != null) {
                    if (currentLine == rowIndex) {
                        String[] colValue = lineStr.split(query.getSeparator());
                        if (colIndex >= colValue.length) {
                            throw new SourceException(
                                    String.format("max columns : %s, colIndex : %s", colValue.length, colIndex));
                        }
                        values.add(colValue[colIndex]);
                        isFound = true;
                        return values;
                    }
                } else {
                    String[] colValue = lineStr.split(query.getSeparator());
                    if (colIndex >= colValue.length) {
                        throw new SourceException(
                                String.format("max columns : %s, colIndex : %s", colValue.length, colIndex));
                    }
                    values.add(colValue[colIndex]);
                }
                currentLine++;
            }
            return values;
        }
    }


    @Override
    public String readText(Configuration configuration, String hdfsPath) {
        StringBuilder sb = new StringBuilder();

        try (FileSystem fs = FileSystem.get(configuration)) {
            FileStatus fstat = fs.getFileStatus(new Path(hdfsPath));
            if (fstat.isFile()) {
                try (FSDataInputStream is = fs.open(new Path(hdfsPath));
                     BufferedReader d = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = d.readLine()) != null) {
                        sb.append(line);
                    }
                }
            }
        } catch (Exception e) {
            throw new SourceException("read text error: ", e);
        }

        return sb.toString();
    }
}
