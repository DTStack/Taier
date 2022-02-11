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

package com.dtstack.taiga.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.common.template.Writer;
import com.dtstack.taiga.develop.enums.develop.SyncWriteMode;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author jingzhen
 */
public class HiveWriter extends HiveBase implements Writer {

    private String msg = "";
    private String[] parts;

    @Override
    public JSONObject toWriterJson() {
        try {
            inferHdfsParams();

            JSONObject connection = new JSONObject(true);
            connection.put("jdbcUrl", this.getJdbcUrl());
            connection.put("table", StringUtils.isNotBlank(this.getTable()) ? Lists.newArrayList(this.getTable()) : Lists.newArrayList());

            HDFSWriter hdfsWriter = new HDFSWriter();

            if(writeMode != null && writeMode.trim().length() != 0) {
                writeMode = SyncWriteMode.tranferHiveMode(writeMode);
            } else {
                writeMode = SyncWriteMode.HIVE_OVERWRITE.getMode();
            }

            hdfsWriter.setWriteMode(writeMode);

            hdfsWriter.setColumn(column);
            hdfsWriter.setFileName(fileName);
            hdfsWriter.setDefaultFS(defaultFS);
            hdfsWriter.setEncoding(encoding);
            hdfsWriter.setFieldDelimiter(fieldDelimiter);
            hdfsWriter.setFileType(fileType);
            hdfsWriter.setHadoopConfig(hadoopConfig);
            hdfsWriter.setPath(path == null ? "" : path.trim());
            hdfsWriter.setExtralConfig(super.getExtralConfig());
            hdfsWriter.setSourceIds(sourceIds);

            if(StringUtils.isNotEmpty(partition)) {
                hdfsWriter.setFileName(partition);
                hdfsWriter.setPartition(partition);
            } else {
                hdfsWriter.setFileName("");
            }

            hdfsWriter.setFullColumnName(fullColumnNames);
            hdfsWriter.setFullColumnType(fullColumnTypes);

            if(StringUtils.isNotEmpty(table)) {
                hdfsWriter.setTable(table);
            }

            if(StringUtils.isNotEmpty(jdbcUrl)) {
                hdfsWriter.setJdbcUrl(jdbcUrl);
            }

            if(StringUtils.isNotEmpty(username)) {
                hdfsWriter.setUsername(username);
            }

            if(StringUtils.isNotEmpty(password)) {
                hdfsWriter.setPassword(password);
            }

            if (MapUtils.isNotEmpty(sftpConf)) {
                hdfsWriter.setSftpConf(sftpConf);
            }
            if (StringUtils.isNotEmpty(remoteDir)) {
                hdfsWriter.setRemoteDir(remoteDir);
            }

            return hdfsWriter.toWriterJson();
        } catch(Exception ex) {
            throw new RdosDefineException(ex.getCause().getMessage(), ex);
        }
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    private String generateQuotedPartitions() {
        List<String> quotedParts = new ArrayList<>();
        for(int i = 0; i < parts.length; ++i) {
            String[] pair = parts[i].split("=");
            if(pair.length != 2) {
                msg = "分区格式错误[" + parts[i] + "]";
                return null;
            }
            if(!partitionedBy.get(i).equals(pair[0].trim())) {
                msg = "错误的分区列[" + pair[0] + "]";
                return null;
            }
            quotedParts.add(pair[0] + "='" + pair[1] + "'");
        }
        return StringUtils.join(quotedParts, ",");
    }

    public boolean isValid() {

        // 确定hdfs参数
        inferHdfsParams();

        // 分区表
        if(isPartitioned) {
            if(StringUtils.isBlank(partition)) {
                msg = "写入分区表时必须指定分区";
                return false;
            }

            parts = partition.split("/");
            if(parts.length != partitionedBy.size()) {
                msg = "填入的分区层数与hive表定义的不一致";
                return false;
            }

            String partText = generateQuotedPartitions();
            if(StringUtils.isBlank(partText)) {
                return false;
            }

        }

        // 非分区表
        if(!isPartitioned && StringUtils.isNotBlank(partition)) {
            msg = "写入非分区表不需要指定分区";
            return false;
        }

        return true;
    }

    public String getErrMsg() {
        return msg;
    }

    @Override
    public void checkFormat(JSONObject data) {
        JSONObject parameter = data.getJSONObject("parameter");

        if (StringUtils.isEmpty(parameter.getString("path"))){
            throw new RdosDefineException("path 不能为空");
        }

        JSONArray column = parameter.getJSONArray("column");
        if(column == null || column.isEmpty()){
            throw new RdosDefineException("column 不能为空");
        }

        for (Object o : column) {
            if (o instanceof String){
                throw new RdosDefineException("column 必须为对象数组 : [{\"name\":\"id\",\"type\":\"int\"}]");
            }
        }
    }
}
