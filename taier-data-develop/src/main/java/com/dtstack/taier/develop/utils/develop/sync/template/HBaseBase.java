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

import java.util.List;
import java.util.Map;

/**
 * @author jingzhen
 */
public abstract class HBaseBase extends BaseSource {
    protected Map<String, Object> hbaseConfig;
    protected List<JSONObject> column;
    protected String encoding = "utf-8";
    protected String mode = "normal";
    protected String table;
    protected String remoteDir;
    protected Map<String, Object> sftpConf;

    public Map<String, Object> getHbaseConfig() {
        return hbaseConfig;
    }

    public void setHbaseConfig(Map<String, Object> hbaseConfig) {
        this.hbaseConfig = hbaseConfig;
    }

    public List<JSONObject> getColumn() {
        return column;
    }

    public void setColumn(List<JSONObject> column) {
        this.column = column;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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
}