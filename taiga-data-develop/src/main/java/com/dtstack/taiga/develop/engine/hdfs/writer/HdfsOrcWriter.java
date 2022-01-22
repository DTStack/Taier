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

package com.dtstack.taiga.develop.engine.hdfs.writer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.HDFSImportColumn;
import com.dtstack.dtcenter.loader.dto.HdfsWriterDTO;
import com.dtstack.dtcenter.loader.dto.source.HdfsSourceDTO;
import com.dtstack.dtcenter.loader.enums.FileFormat;
import com.dtstack.taiga.develop.domain.BatchTableColumn;
import com.dtstack.taiga.develop.engine.core.domain.ImportColum;
import com.dtstack.taiga.develop.engine.rdbms.common.HadoopConf;
import com.dtstack.taiga.develop.engine.rdbms.common.HdfsOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 将指定的本地文件读取,并写入到hdfs
 * Date: 2017/9/11
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class HdfsOrcWriter {

    private static final Logger logger = LoggerFactory.getLogger(HdfsOrcWriter.class);

    public static int writeByPos(Long dtuicTenantId, String hdfsDirPath, String fromLineDelimiter, String fromFileName,
                                 String oriCharSet, int startLine, boolean topLineIsTitle, List<BatchTableColumn> columnsList, List<ImportColum> keyList) throws IOException {
        Map<String, Object> conf = HadoopConf.getConfiguration(dtuicTenantId);
        Map<String, Object> kerberosConf = HadoopConf.getHadoopKerberosConf(dtuicTenantId);

        HdfsSourceDTO hdfsSourceDTO = HdfsSourceDTO.builder()
                .defaultFS(conf.getOrDefault("fs.defaultFS","").toString())
                .kerberosConfig(kerberosConf)
                .config(JSONObject.toJSONString(conf)).build();

        List<ColumnMetaDTO> columnMetaDTOS = columnsList.stream().map(c -> {
            ColumnMetaDTO metaDTO = new ColumnMetaDTO();
            metaDTO.setKey(c.getColumnName());
            metaDTO.setType(c.getColumnType());
            metaDTO.setComment(c.getColumnDesc());
            return metaDTO;
        }).collect(Collectors.toList());

        List<HDFSImportColumn> hdfsKeyList = keyList.stream().map(c -> {
            HDFSImportColumn column = new HDFSImportColumn();
            column.setKey(c.getKey());
            column.setDateFormat(c.getDateFormat());
            column.setFormat(c.getFormat());
            return column;
        }).collect(Collectors.toList());
        try {
            HdfsWriterDTO writerDTO = new HdfsWriterDTO();
            writerDTO.setColumnsList(columnMetaDTOS);
            writerDTO.setKeyList(hdfsKeyList);
            writerDTO.setFromFileName(fromFileName);
            writerDTO.setHdfsDirPath(hdfsDirPath);
            writerDTO.setFromLineDelimiter(fromLineDelimiter);
            writerDTO.setToLineDelimiter(null);
            writerDTO.setOriCharSet(oriCharSet);
            writerDTO.setStartLine(startLine);
            writerDTO.setTopLineIsTitle(topLineIsTitle);
            writerDTO.setFileFormat(FileFormat.ORC.getVal());
            return HdfsOperator.getHdfsFileClient(conf, kerberosConf).writeByPos(hdfsSourceDTO,writerDTO);
        } catch (Exception e) {
            logger.error("",e);
        }
        return 0;
    }

    public static int writeByName(Long dtuicTenantId, String hdfsDirPath, String fromLineDelimiter, String fromFileName,
                                  String oriCharSet, int startLine, boolean topLineIsTitle, List<BatchTableColumn> columnsList,
                                  List<ImportColum> keyList) throws IOException {

        Map<String, Object> conf = HadoopConf.getConfiguration(dtuicTenantId);
        Map<String, Object> kerberosConf = HadoopConf.getHadoopKerberosConf(dtuicTenantId);

        HdfsSourceDTO hdfsSourceDTO = HdfsSourceDTO.builder()
                .defaultFS(conf.getOrDefault("fs.defaultFS","").toString())
                .kerberosConfig(kerberosConf)
                .config(JSONObject.toJSONString(conf)).build();

        List<ColumnMetaDTO> columnMetaDTOS = columnsList.stream().map(c -> {
            ColumnMetaDTO metaDTO = new ColumnMetaDTO();
            metaDTO.setKey(c.getColumnName());
            metaDTO.setType(c.getColumnType());
            metaDTO.setComment(c.getColumnDesc());
            return metaDTO;
        }).collect(Collectors.toList());

        List<HDFSImportColumn> hdfsKeyList = keyList.stream().map(c -> {
            HDFSImportColumn column = new HDFSImportColumn();
            column.setKey(c.getKey());
            column.setDateFormat(c.getDateFormat());
            column.setFormat(c.getFormat());
            return column;
        }).collect(Collectors.toList());
        buildTypeInfo(columnsList);
        try {
            HdfsWriterDTO writerDTO = new HdfsWriterDTO();
            writerDTO.setColumnsList(columnMetaDTOS);
            writerDTO.setKeyList(hdfsKeyList);
            writerDTO.setFromFileName(fromFileName);
            writerDTO.setHdfsDirPath(hdfsDirPath);
            writerDTO.setFromLineDelimiter(fromLineDelimiter);
            writerDTO.setToLineDelimiter(null);
            writerDTO.setOriCharSet(oriCharSet);
            writerDTO.setStartLine(startLine);
            writerDTO.setTopLineIsTitle(topLineIsTitle);
            writerDTO.setFileFormat(FileFormat.ORC.getVal());
            return HdfsOperator.getHdfsFileClient(conf, kerberosConf).writeByName(hdfsSourceDTO,writerDTO);
        } catch (Exception e) {
            logger.error("",e);
        }
        return 0;
    }

    /**
     * FIXME columnsList 必须是经过index排序过的
     * hive orc type 格式 struct<columnName:columnType,....>
     *
     * @param columnsList
     * @return
     */
    public static String buildTypeInfo(List<BatchTableColumn> columnsList) {
        StringBuffer sb = new StringBuffer("");
        for (BatchTableColumn columns : columnsList) {
            sb.append(columns.getColumnName())
                    .append(":")
                    .append(columns.getColumnType())
                    .append(",");
        }

        String typeInfo = sb.toString();
        if (typeInfo.endsWith(",")) {
            typeInfo = typeInfo.substring(0, typeInfo.length() - 1);
        }

        typeInfo = "struct<" + typeInfo + ">";
        return typeInfo;
    }

}