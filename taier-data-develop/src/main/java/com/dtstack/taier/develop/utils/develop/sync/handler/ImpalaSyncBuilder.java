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

package com.dtstack.taier.develop.utils.develop.sync.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.enums.develop.SourceDTOType;
import com.dtstack.taier.develop.enums.develop.SyncWriteMode;
import com.dtstack.taier.develop.enums.develop.TableLocationType;
import com.dtstack.taier.develop.utils.develop.sync.template.ImpalaHdfsReader;
import com.dtstack.taier.develop.utils.develop.sync.template.ImpalaHdfsWriter;
import com.dtstack.taier.develop.utils.develop.sync.template.KuduReader;
import com.dtstack.taier.develop.utils.develop.sync.template.KuduWriter;
import com.dtstack.taier.develop.utils.develop.sync.util.ImpalaUtils;
import com.dtstack.taier.pluginapi.pojo.Column;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;


/**
 * impala数据同步构造，用于构造reader和writer参数
 *
 * Date: 2019/12/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Slf4j
@Component
public class ImpalaSyncBuilder implements SyncBuilder {

    @Override
    public void setReaderJson(Map<String, Object> map, Map<String, Object> dataSource,Map<String,Object> kerberos) {
        if (log.isDebugEnabled()) {
            log.debug("set read json DataSourceType: Impala \nsourceMap :{} \n datasourceJson :{}", JSON.toJSONString(map), JSON.toJSONString(dataSource));
        }
        String tableName = (String) map.get("table");
        //优化点 前端可以回传tableLocation数据，减少链接的创建
        JSONObject tableLocation = tableLocation(dataSource, tableName, kerberos);
        log.info("get read tableLocation :{}", tableLocation.toJSONString());
        TableLocationType tableLocationType = TableLocationType.getTableLocationType(tableLocation.getString(TableLocationType.key()));
        if (tableLocationType == null) {
            throw new RdosDefineException("不支持的表存储类型");
        }
        if (tableLocationType == TableLocationType.HIVE) {
//            map.put("type", DataSourceType.HIVE);
//            map.put("partition", map.get(HIVE_PARTITION));
//            map.put("defaultFS", dataSource.get(HDFS_DEFAULTFS));
//            map.put("password", dataSource.get(JDBC_PASSWORD));
//            map.put("username", dataSource.get(JDBC_USERNAME));
//            map.put("jdbcUrl", dataSource.get(JDBC_URL));
//            String hadoopConfig = (String) dataSource.get(HADOOP_CONFIG);
//            if (StringUtils.isNotBlank(hadoopConfig)) {
//                map.put("hadoopConfig", JSONObject.parse(hadoopConfig));
//            }
            map.putAll(tableLocation);
        } else if (tableLocationType == TableLocationType.KUDU) {
            List column = (List) map.get("column");
            for (Object col : column) {
                String name = (String) ((Map<String, Object>) col).get("key");
                ((Map<String, Object>) col).put("name", name);
            }
            map.putAll(tableLocation);
        }

    }

    @Override
    public void setWriterJson(Map<String, Object> map, Map<String, Object> dataSource,Map<String,Object> kerberos) {
        if (log.isDebugEnabled()) {
            log.debug("setWriterJson DataSourceType: Impala \nsourceMap :{} \n datasourceJson :{}", JSON.toJSONString(map), JSON.toJSONString(dataSource));
        }

        String tableName = (String) map.get("table");
        //优化点 前端可以回传tableLocation数据，减少链接的创建
        JSONObject tableLocation = tableLocation(dataSource, tableName, kerberos);
        log.info("get read tableLocation :{}", tableLocation.toJSONString());
        TableLocationType tableLocationType = TableLocationType.getTableLocationType(tableLocation.getString(TableLocationType.key()));
        if (tableLocationType == null) {
            throw new RdosDefineException("不支持的表存储类型");
        }
        if (tableLocationType == TableLocationType.HIVE) {
//            map.put("type", DataSourceType.HIVE);
//            map.put("partition", map.get(HIVE_PARTITION));
//            map.put("defaultFS", dataSource.get(HDFS_DEFAULTFS));
//            map.put("password", dataSource.get(JDBC_PASSWORD));
//            map.put("username", dataSource.get(JDBC_USERNAME));
//            map.put("jdbcUrl", dataSource.get(JDBC_URL));
//            String hadoopConfig = (String) dataSource.get(HADOOP_CONFIG);
//            if (StringUtils.isNotBlank(hadoopConfig)) {
//                map.put("hadoopConfig", JSONObject.parse(hadoopConfig));
//            }
            map.putAll(tableLocation);
        } else if (tableLocationType == TableLocationType.KUDU) {
            List column = (List) map.get("column");
            for (Object col : column) {
                String name = (String) ((Map<String, Object>) col).get("key");
                ((Map<String, Object>) col).put("name", name);
            }
            map.putAll(tableLocation);
        }
    }

    @Override
    public Reader syncReaderBuild(Map<String, Object> sourceMap, List<Long> sourceIds) {

        TableLocationType tableLocationType = TableLocationType.getTableLocationType((String) sourceMap.get(TableLocationType.key()));
        if (tableLocationType == null) {
            throw new RdosDefineException("不支持的表存储类型");
        }
        if (tableLocationType == TableLocationType.HIVE) {
            return objToObject(sourceMap, ImpalaHdfsReader.class);
        } else if (tableLocationType == TableLocationType.KUDU) {
            KuduReader kuduReader = objToObject(sourceMap, KuduReader.class);
            String kuduTableName = (String) sourceMap.get("kuduTableName");
            log.info("syncReaderBuild format impala  kuduTableName :{} ", kuduTableName);
            kuduReader.setTable(kuduTableName);
            return kuduReader;
        }
        return null;
    }

    @Override
    public Writer syncWriterBuild(List<Long> targetIds, Map<String, Object> targetMap, Reader reader) {
        TableLocationType tableLocationType = TableLocationType.getTableLocationType((String) targetMap.get(TableLocationType.key()));
        if (tableLocationType == null) {
            throw new RdosDefineException("不支持的表存储类型");
        }
        if (tableLocationType == TableLocationType.HIVE) {
            Map<String, Object> clone = new HashMap<>(targetMap);

            String writeMode = (String) clone.get("writeMode");
            writeMode = writeMode != null && writeMode.trim().length() != 0 ? SyncWriteMode.tranferHiveMode(writeMode) : SyncWriteMode.HIVE_OVERWRITE.getMode();
            clone.put("writeMode", writeMode);
            //设置hdfs index字段
            List column = (List) clone.get("column");
            List<Column> allColumns = (List<Column>) clone.get("allColumns");
            List<Column> partitionColumns = (List<Column>) clone.get("partitionColumns");
            Map<String, Column> allColumnsMap = allColumns.stream().collect(Collectors.toMap(Column::getName, item -> item));
            for (Object col : column) {
                String name = (String) ((Map<String, Object>) col).get("key");
                ((Map<String, Object>) col).put("index", allColumnsMap.get(name).getIndex());
            }
            //设置 fullColumnNames 和 fullColumnTypes  脏数据记录的时候需要
            //需要去掉分区字段
            Set<String> partitionColumnNameSet = CollectionUtils.isEmpty(partitionColumns) ? new HashSet<>()
                    : partitionColumns.stream().map(pColumn -> pColumn.getName()).collect(Collectors.toSet());

            List<String> fullColumnNames = new ArrayList<>();
            List<String> fullColumnTypes = new ArrayList<>();

            for (Column allColumn : allColumns) {
                if (!partitionColumnNameSet.contains(allColumn.getName())) {
                    fullColumnNames.add(allColumn.getName());
                    fullColumnTypes.add(allColumn.getType());
                }
            }
            clone.put("fullColumnNames", fullColumnNames);
            clone.put("fullColumnTypes", fullColumnTypes);

            String partition = (String) clone.get("partition");
            //fileName 逻辑参考自HiveWriter
            String fileName = StringUtils.isNotEmpty(partition) ? partition : "";
            clone.put("fileName", fileName);

            return objToObject(clone, ImpalaHdfsWriter.class);
        } else if (tableLocationType == TableLocationType.KUDU) {
            KuduWriter kuduWriter = objToObject(targetMap, KuduWriter.class);
            String kuduTableName = (String) targetMap.get("kuduTableName");
            log.info("syncWriterBuild format impala  kuduTableName :{} ", kuduTableName);
            kuduWriter.setTable(kuduTableName);
            return kuduWriter;
        }
        return null;
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.IMPALA;
    }

    public JSONObject tableLocation(Map<String, Object> dataSource, String tableName, Map<String, Object> kerberos) {
        JSONObject result = new JSONObject();
        try {
            ISourceDTO iSourceDTO = SourceDTOType.getSourceDTO(new JSONObject(dataSource), DataSourceType.IMPALA.getVal(), kerberos, Maps.newHashMap());
            //获取表存储文件类型
            String fileType = ImpalaUtils.getTableFileType(iSourceDTO, tableName);
            Assert.isTrue(StringUtils.isNotBlank(fileType), "暂不支持的hive表文件类型");

            if ("KUDU".equals(fileType)) {
                //获取kudu配置信息
                Map<String, String> tableParams = ImpalaUtils.getImpalaKuduTableParams(iSourceDTO, tableName);
                String masterAddress = tableParams.get("kudu.master_addresses");
                String kuduTableName = tableParams.get("kudu.table_name");

                Assert.isTrue(StringUtils.isNotBlank(masterAddress) && StringUtils.isNotBlank(kuduTableName), "impala获取kudu表配置失败");

                result.put(TableLocationType.key(), TableLocationType.KUDU.getValue());
                result.put("masterAddresses", masterAddress);
                result.put("kuduTableName", kuduTableName);
                return result;
            }

            result.put(TableLocationType.key(), TableLocationType.HIVE.getValue());
            //获取impala表信息 用于hdfs同步
            Map<String, Object> map = ImpalaUtils.getImpalaHiveTableDetailInfo(iSourceDTO, tableName);
            String path = (String) map.get("path");
            Assert.isTrue(StringUtils.isNotBlank(path), "无法获取impala hive表location路径");
            result.put("fileType", fileType);
            result.putAll(map);
            return result;
        } catch (Exception e) {
            log.error("tableLocation error ", e);
            if (e instanceof RdosDefineException) {
                throw (RdosDefineException) e;
            }
        }
        throw new RdosDefineException("获取impala表存储类型失败");
    }
}
