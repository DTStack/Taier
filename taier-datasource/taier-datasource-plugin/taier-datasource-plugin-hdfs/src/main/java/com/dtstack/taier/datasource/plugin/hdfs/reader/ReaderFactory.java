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
