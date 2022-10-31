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

package com.dtstack.taier.datasource.plugin.hdfs.downloader.tableDownload;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.datasource.plugin.common.utils.ListUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcInputFormat;
import org.apache.hadoop.hive.ql.io.orc.OrcSerde;
import org.apache.hadoop.hive.ql.io.orc.OrcSplit;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 下载hive表 - 存储结构为ORC
 * Date: 2020/6/3
 * Company: www.dtstack.com
 *
 * @author wangchuan
 */
@Slf4j
public class HiveORCDownload implements IDownloader {
    private static final int SPLIT_NUM = 1;

    private OrcSerde orcSerde;
    private InputFormat inputFormat;
    private JobConf conf;
    private RecordReader recordReader;

    private Object key;
    private Object value;
    private StructObjectInspector inspector;
    private List<? extends StructField> fields;

    private final String tableLocation;
    private final List<String> columnNames;
    private final Configuration configuration;

    private InputSplit[] splits;
    private int splitIndex = 0;

    private InputSplit currentSplit;

    private final List<String> partitionColumns;

    private final Map<String, Object> kerberosConfig;

    /**
     * 需要查询字段的索引
     */
    private final List<Integer> needIndex;

    /**
     * 所有分区
     */
    private final List<String> partitions;

    public HiveORCDownload(Configuration configuration, String tableLocation, List<String> columnNames,
                           List<String> partitionColumns, List<Integer> needIndex,
                           List<String> partitions, Map<String, Object> kerberosConfig){
        this.configuration = configuration;
        this.tableLocation = tableLocation;
        this.columnNames = columnNames;
        this.partitionColumns = partitionColumns;
        this.needIndex = needIndex;
        this.partitions = partitions;
        this.kerberosConfig = kerberosConfig;
    }

    @Override
    public boolean configure() throws Exception {

        this.orcSerde = new OrcSerde();
        this.inputFormat = new OrcInputFormat();
        conf = new JobConf(configuration);

        Path targetFilePath = new Path(tableLocation);
        FileSystem fileSystem = FileSystem.get(configuration);
        // 判断表路径是否存在
        if (!fileSystem.exists(targetFilePath)) {
            log.warn("Table path: {} does not exist", tableLocation);
            return false;
        }
        FileInputFormat.setInputPaths(conf, targetFilePath);
        splits = inputFormat.getSplits(conf, SPLIT_NUM);
        if(ArrayUtils.isNotEmpty(splits)){
            boolean isInit = initRecordReader();
            if (isInit) {
                key = recordReader.createKey();
                value = recordReader.createValue();
                Properties p = new Properties();
                p.setProperty("columns", StringUtil.join(columnNames,","));
                orcSerde.initialize(conf, p);
                this.inspector = (StructObjectInspector) orcSerde.getObjectInspector();
                fields = inspector.getAllStructFieldRefs();
            }
        }
        return true;
    }

    @Override
    public List<String> getMetaInfo(){
        List<String> metaInfo = new ArrayList<>(columnNames);
        if(CollectionUtils.isNotEmpty(partitionColumns)){
            metaInfo.addAll(partitionColumns);
        }
        return metaInfo;
    }

    @Override
    public List<String> readNext() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<List<String>>) ()->{
                    try {
                        return readNextWithKerberos();
                    } catch (Exception e){
                        throw new SourceException(String.format("Abnormal reading file,%s", e.getMessage()), e);
                    }
                });
    }

    public List<String> readNextWithKerberos() {
        List<String> row = new ArrayList<>();

        // 分区字段的值
        List<String> partitions = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(partitionColumns)){
            String path = ((OrcSplit)currentSplit).getPath().toString();
            List<String> partData = HdfsOperator.parsePartitionDataFromUrl(path,partitionColumns);
            partitions.addAll(partData);
        }

        // needIndex不为空表示获取指定字段
        if (CollectionUtils.isNotEmpty(needIndex)) {
            for (Integer index : needIndex) {
                // 表示该字段为分区字段
                if (index > columnNames.size() - 1 && CollectionUtils.isNotEmpty(partitions)) {
                    // 分区字段的索引
                    int partIndex = index - columnNames.size();
                    if (partIndex < partitions.size()) {
                        row.add(partitions.get(partIndex));
                    } else {
                        row.add(null);
                    }
                } else if (index < columnNames.size()) {
                    row.add(getFieldByIndex(index));
                } else {
                    row.add(null);
                }
            }
            // needIndex为空表示获取所有字段
        } else {
            for (int index = 0; index < columnNames.size(); index++) {
                row.add(getFieldByIndex(index));
            }
            if(CollectionUtils.isNotEmpty(partitionColumns)){
                row.addAll(partitions);
            }
        }

        return row;
    }

    // 根据index获取字段值
    private String getFieldByIndex(Integer index) {
        if (index > fields.size() -1) {
            return null;
        }
        StructField field = fields.get(index);
        Object data = inspector.getStructFieldData(value, field);
        // 处理 Map 类型
        if (data instanceof Map) {
            return convertMap((Map) data);
        }
        return Objects.isNull(data) ? null : data.toString();
    }

    /**
     * 转换 Map 类型数据
     *
     * @param data 数据
     * @return 转换后的 String
     */
    private String convertMap(Map data) {
        Map<String, Object> result = new HashMap<>();
        data.keySet().stream().forEach(key -> {
            Object value = data.get(key);
            result.put(key.toString(), Objects.isNull(value) ? null : value.toString());
        });
        return JSON.toJSONString(result);
    }

    private boolean initRecordReader() throws IOException {
        if(splitIndex > splits.length - 1){
            return false;
        }
        OrcSplit orcSplit = (OrcSplit)splits[splitIndex];
        currentSplit = splits[splitIndex];
        splitIndex++;

        if(recordReader != null){
            close();
        }

        // 如果路径不存在，重新进行初始化 recordReader orcSplit.getPath().toString() 可以拿到当前逻辑切片的 hdfs 文件路径
        if (!isPartitionExists(orcSplit.getPath().toString())) {
            return initRecordReader();
        }

        recordReader = inputFormat.getRecordReader(orcSplit, conf, Reporter.NULL);
        return true;
    }

    public boolean nextRecord() throws IOException {
        if(recordReader.next(key, value)){
            return true;
        }
        for (int i = splitIndex; i < splits.length; i++) {
            if (initRecordReader() && recordReader.next(key, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean reachedEnd() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<Boolean>) ()->{
                    try {
                        return recordReader == null || !nextRecord();
                    } catch (Exception e){
                        throw new SourceException(String.format("Download file is abnormal,%s", e.getMessage()), e);
                    }
                });
    }

    @Override
    public boolean close() throws IOException {
        if(recordReader != null){
            recordReader.close();
        }
        return true;
    }

    /**
     * 判断分区是否存在
     *
     * @param path hdfs 文件路径
     * @return 分区是否存在
     */
    private boolean isPartitionExists(String path) {
        // 如果 partitions 为 null，表示非分区表，返回 true
        if (Objects.isNull(partitions)) {
            return true;
        }
        // 如果为空标识是分区表，但是无分区信息，返回 false
        if (CollectionUtils.isEmpty(partitions)) {
            return false;
        }
        String curPathPartition = getCurPathPartition(path);
        if (StringUtils.isBlank(curPathPartition)) {
            return false;
        }
        return ListUtil.containsIgnoreCase(partitions, curPathPartition);
    }

    /**
     * 获取当前路径的分区路径
     *
     * @return 分区
     */
    private String getCurPathPartition(String path) {
        StringBuilder curPart = new StringBuilder();
        for (String part : path.split("/")) {
            if(part.contains("=")){
                curPart.append(part).append("/");
            }
        }
        String curPartString = curPart.toString();
        if (StringUtils.isNotBlank(curPartString)) {
            return curPartString.substring(0, curPartString.length() - 1);
        }
        return curPartString;
    }
}