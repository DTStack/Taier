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

import com.dtstack.taier.develop.utils.develop.common.enums.StoredType;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public abstract class HDFSBase extends BaseSource{

    protected String defaultFS;
    protected String path = "";
    protected String fileType = StoredType.ORC.getValue();
    protected String encoding = "utf-8";
    protected String fieldDelimiter = "\001";
    protected Map<String,Object> hadoopConfig;
    protected List column;
    protected String remoteDir;
    protected Map<String, Object> sftpConf;


    public String getDefaultFS() {
        return defaultFS;
    }

    public void setDefaultFS(String defaultFS) {
        this.defaultFS = defaultFS;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public Map<String, Object> getHadoopConfig() {
        return hadoopConfig;
    }

    public void setHadoopConfig(Map<String, Object> hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
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
