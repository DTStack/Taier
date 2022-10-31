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
import org.apache.hadoop.conf.Configuration;

import java.util.List;

/**
 * hdfs读取操作
 *
 * @author luming
 * @date 2022/3/16
 */
public interface HdfsReader {
    /**
     * 根据不同文件类型读取hdfs数据
     *
     * @param configuration hadoop conf
     * @param hdfsQueryDTO  queryDto
     * @return data，如目录下存在多个文件，则会将多个文件内的数据汇总到最终集合中返回
     */
    List<String> readByType(Configuration configuration, HdfsQueryDTO hdfsQueryDTO);

    /**
     * 一次性读取text类型文件的所有数据
     *
     * @param configuration hadoop conf
     * @param hdfsPath      hdfs文件路径
     * @return data
     */
    String readText(Configuration configuration, String hdfsPath);
}
