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


import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.common.template.Reader;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author jingzhen
 */
public class HiveReader extends HiveBase implements Reader {

    @Override
    public JSONObject toReaderJson() {
        try {
            inferHdfsParams();
            HDFSReader hdfsReader = new HDFSReader();
            hdfsReader.setHadoopConfig(hadoopConfig);
            hdfsReader.setFileType(fileType);
            hdfsReader.setFieldDelimiter(fieldDelimiter);
            hdfsReader.setColumn(column);
            hdfsReader.setDefaultFS(defaultFS);
            hdfsReader.setEncoding(encoding);
            hdfsReader.setExtralConfig(super.getExtralConfig());
            hdfsReader.setSourceIds(getSourceIds());
            if(StringUtils.isNotEmpty(partition)) {
                hdfsReader.setPartition(partition );
                hdfsReader.setFileName(partition);
            }

            if(StringUtils.isNotEmpty(partition)) {
                hdfsReader.setPartition(partition);
            }

            if (MapUtils.isNotEmpty(sftpConf)) {
                hdfsReader.setSftpConf(sftpConf);
            }
            if (StringUtils.isNotEmpty(remoteDir)) {
                hdfsReader.setRemoteDir(remoteDir);
            }

            if(StringUtils.isNotEmpty(table)) {
                hdfsReader.setTable(table);
            }

            if(StringUtils.isNotEmpty(jdbcUrl)) {
                hdfsReader.setJdbcUrl(jdbcUrl);
            }

            if(StringUtils.isNotEmpty(username)) {
                hdfsReader.setUsername(username);
            }

            if(StringUtils.isNotEmpty(password)) {
                hdfsReader.setPassword(password);
            }


            hdfsReader.setPath(path == null ? "" : path.trim());

            return hdfsReader.toReaderJson();
        } catch (Exception ex) {
            throw new RdosDefineException(ex.getCause().getMessage());
        }
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
