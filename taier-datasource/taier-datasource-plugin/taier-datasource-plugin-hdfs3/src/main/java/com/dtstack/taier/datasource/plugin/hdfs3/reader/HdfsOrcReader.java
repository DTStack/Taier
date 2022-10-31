/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.plugin.hdfs3.reader;

import com.dtstack.taier.datasource.api.dto.HdfsQueryDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Reader;
import org.apache.hadoop.hive.ql.io.orc.RecordReader;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

import java.io.IOException;
import java.util.List;

/**
 * orc reader
 *
 * @author luming
 * @date 2022/3/16
 */
public class HdfsOrcReader extends AbsReader {
    @Override
    protected List<String> parseFile(Path path,
                                     FileSystem fs,
                                     Configuration configuration,
                                     HdfsQueryDTO query) throws IOException {
        //如果前面的文件已经找到指定行列的数据，则后续的文件不再进行读取
        if (isFound) {
            return Lists.newArrayList();
        }

        RecordReader records = null;
        try {
            Reader reader = OrcFile.createReader(fs, path);
            StructObjectInspector inspector = (StructObjectInspector) reader.getObjectInspector();
            records = reader.rows();

            //目前只支持指定行列或只指定列
            Integer colIndex = query.getColIndex();
            Integer rowIndex = query.getRowIndex();
            Integer limit = query.getLimit();
            List<String> values = Lists.newArrayList();

            Object row = null;
            while (records.hasNext()) {
                //文件/文件夹下的总行数超过limit限制则报错
                if (limit != null && currentLine >= limit) {
                    throw new SourceException(
                            "The actual number of data rows exceeds the limit ：" + limit);
                }
                row = records.next(row);
                if (rowIndex != null) {
                    if (currentLine == rowIndex) {
                        List<Object> colValue = inspector.getStructFieldsDataAsList(row);
                        if (colIndex >= colValue.size()) {
                            throw new SourceException(
                                    String.format("max columns : %s, colIndex : %s", colValue.size(), colIndex));
                        }
                        values.add(String.valueOf(colValue.get(colIndex)));
                        isFound = true;
                        return values;
                    }
                } else {
                    List<Object> colValue = inspector.getStructFieldsDataAsList(row);
                    if (colIndex >= colValue.size()) {
                        throw new SourceException(
                                String.format("max columns : %s, colIndex : %s", colValue.size(), colIndex));
                    }
                    values.add(String.valueOf(colValue.get(colIndex)));
                }

                currentLine++;
            }
            return values;
        } catch (Exception e) {
            throw new SourceException("hdfs orc read error. ", e);
        } finally {
            if (records != null) {
                records.close();
            }
        }
    }
}
