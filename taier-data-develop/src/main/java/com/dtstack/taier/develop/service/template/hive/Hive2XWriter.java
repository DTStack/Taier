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

package com.dtstack.taier.develop.service.template.hive;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.service.template.PluginName;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:18 2019-07-04
 */
public class Hive2XWriter extends HiveWriterBase {

    private String msg = "";
    protected List connection;
    private List<String> fullColumnName = new ArrayList<>();
    private List<String> fullColumnType = new ArrayList<>();
    protected String remoteDir;
    protected Map<String, Object> sftpConf;
    protected String encoding = "utf-8";
//    @Override
    public void checkFormat(JSONObject data) {
        data = data.getJSONObject("parameter");
        if (StringUtils.isEmpty(data.getString("jdbcUrl"))) {
            throw new RdosDefineException("jdbcUrl 不能为空");
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List getConnection() {
        return connection;
    }

    public void setConnection(List connection) {
        this.connection = connection;
    }

    public List<String> getFullColumnName() {
        return fullColumnName;
    }

    public void setFullColumnName(List<String> fullColumnName) {
        this.fullColumnName = fullColumnName;
    }

    public List<String> getFullColumnType() {
        return fullColumnType;
    }

    public void setFullColumnType(List<String> fullColumnType) {
        this.fullColumnType = fullColumnType;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public Map<String, Object> getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(Map<String, Object> sftpConf) {
        this.sftpConf = sftpConf;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String pluginName() {
        if (CollectionUtils.isNotEmpty(connection)) {
            return PluginName.HDFS_W;
        }else {
            return PluginName.HIVE_W;
        }
    }

}
