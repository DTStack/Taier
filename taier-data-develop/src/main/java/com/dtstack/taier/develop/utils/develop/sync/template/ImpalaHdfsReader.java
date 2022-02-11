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

package com.dtstack.taier.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.pluginapi.pojo.Column;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Date: 2019/12/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class ImpalaHdfsReader extends ImpalaHdfsBase implements Reader {

    @Override
    public JSONObject toReaderJson() {
        HDFSReader hdfsReader = new HDFSReader();
        hdfsReader.setHadoopConfig(hadoopConfig);
        hdfsReader.setFieldDelimiter(fieldDelimiter);
        //前端传入的column参数没有index hdfs读取需要此参数
        Map<String, Column> allColumnsMap = allColumns.stream().collect(Collectors.toMap(Column::getName, item -> item));

        for (Object col : column) {
            String name = (String) ((Map<String, Object>) col).get("key");
            ((Map<String, Object>) col).put("index", allColumnsMap.get(name).getIndex());
        }

        hdfsReader.setColumn(column);
        hdfsReader.setDefaultFS(defaultFS);
        hdfsReader.setEncoding(encoding);
        hdfsReader.setExtralConfig(super.getExtralConfig());
        hdfsReader.setFileType(fileType);

        if(StringUtils.isNotEmpty(partition)) {
            hdfsReader.setPath(path + "/" + partition);
        } else {
            hdfsReader.setPath(path);
        }
        if (MapUtils.isNotEmpty(sftpConf)) {
            hdfsReader.setSftpConf(sftpConf);
        }
        if (StringUtils.isNotEmpty(remoteDir)) {
            hdfsReader.setRemoteDir(remoteDir);
        }

        hdfsReader.setPath(hdfsReader.getPath().trim());
        return hdfsReader.toReaderJson();
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
