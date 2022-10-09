package com.dtstack.taier.datasource.plugin.hdfs3.reader;

import com.dtstack.taier.datasource.api.dto.HdfsQueryDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.schema.GroupType;

import java.io.IOException;
import java.util.List;

/**
 * parquetReader
 *
 * @author luming
 * @date 2022/3/16
 */
public class HdfsParquetReader extends AbsReader {
    @Override
    protected List<String> parseFile(Path path,
                                     FileSystem fs,
                                     Configuration configuration,
                                     HdfsQueryDTO query) throws IOException {
        //如果前面的文件已经找到指定行列的数据，则后续的文件不再进行读取
        if (isFound) {
            return Lists.newArrayList();
        }

        try (ParquetReader<Group> reader =
                     ParquetReader.builder(new GroupReadSupport(), path).withConf(configuration).build()) {
            GroupType groupType;
            SimpleGroup group;
            Integer colIndex = query.getColIndex();
            Integer rowIndex = query.getRowIndex();
            Integer limit = query.getLimit();
            List<String> values = Lists.newArrayList();

            //目前只支持指定行列或只指定列
            while ((group = (SimpleGroup) reader.read()) != null) {
                if (limit != null && currentLine >= limit) {
                    throw new SourceException(
                            "The actual number of data rows exceeds the limit ：" + limit);
                }

                if (rowIndex != null) {
                    if (currentLine == rowIndex) {
                        groupType = group.getType();
                        List<String> colValue = Lists.newArrayList();
                        for (int i = 0; i < groupType.getFieldCount(); i++) {
                            String value = group.getValueToString(i, 0);
                            colValue.add(value);
                        }if (colIndex >= colValue.size()) {
                            throw new SourceException(
                                    String.format("max columns : %s, colIndex : %s", colValue.size(), colIndex));
                        }
                        values.add(colValue.get(colIndex));
                        isFound = true;
                        return values;
                    }
                } else {
                    groupType = group.getType();
                    List<String> colValue = Lists.newArrayList();
                    for (int i = 0; i < groupType.getFieldCount(); i++) {
                        String value = group.getValueToString(i, 0);
                        colValue.add(value);
                    }if (colIndex >= colValue.size()) {
                        throw new SourceException(
                                String.format("max columns : %s, colIndex : %s", colValue.size(), colIndex));
                    }
                    values.add(String.valueOf(colValue.get(colIndex)));
                }

                currentLine++;
            }
            return values;
        }
    }
}
