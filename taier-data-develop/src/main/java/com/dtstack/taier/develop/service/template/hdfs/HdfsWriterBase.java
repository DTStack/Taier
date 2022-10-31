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

package com.dtstack.taier.develop.service.template.hdfs;



import com.dtstack.taier.develop.enums.develop.FileType;
import com.dtstack.taier.develop.service.template.BaseWriterPlugin;

import java.util.List;
import java.util.Map;

/**
 * @author: toutian
 * create: 2019/04/17
 */
public abstract class HdfsWriterBase extends BaseWriterPlugin {


    protected String defaultFS;
    protected String path = "";
    protected String fileType = FileType.ORCFILE.getVal();
    protected String charsetName = "utf-8";
    protected String fieldDelimiter = "\001";
    protected Map<String,Object> hadoopConfig;
    protected long interval;
    protected List<String> column;

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
    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public List<String> getColumn() {
        return column;
    }

    public void setColumn(List<String> column) {
        this.column = column;
    }

    public Map<String,Object> getHadoopConfig() {
        return hadoopConfig;
    }

    public void setHadoopConfig(Map<String,Object> hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
