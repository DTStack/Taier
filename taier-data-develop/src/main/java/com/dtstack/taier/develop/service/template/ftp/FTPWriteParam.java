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

package com.dtstack.taier.develop.service.template.ftp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.StringUtils;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.service.template.PluginName;

import java.util.List;

import static com.dtstack.taier.develop.service.template.BaseReaderPlugin.dealExtralConfig;
import static com.dtstack.taier.develop.service.template.ftp.FTPFileReaderParam.*;

/**
 * @author bnyte
 * @since 1.3.1
 **/
public class FTPWriteParam extends FTPParam implements Writer {

    private String extralConfig;

    protected List<Long> sourceIds;

    private String writeMode;

    private String fileType;

    @Override
    public JSONObject toWriterJson() {
        JSONObject param = new JSONObject();
        try {
            param = JSON.parseObject(JSON.toJSONString(this));
        } catch (Exception e) {
            param = JSON.parseObject(JSON.toJSON(this).toString());
        }


        dealExtralConfig(param);
        JSONObject res = new JSONObject();
        res.put("name", pluginName());
        res.put("parameter", param);
        return res;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }

    public List<Long> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }

    public String getExtralConfig() {
        return extralConfig;
    }

    public void setExtralConfig(String extralConfig) {
        this.extralConfig = extralConfig;
    }

    public String pluginName() {
        return PluginName.FTP_W;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public String getFileType() {
        if (StringUtils.hasText(fileType)) {
            return this.fileType;
        }
        String filePath = getPath();
        if (!StringUtils.hasText(filePath)) {
            throw new TaierDefineException("ftp reader source cannot be empty of file path");
        }
        int fileSuffixSeparateIndex = filePath.lastIndexOf(".");
        if (fileSuffixSeparateIndex == -1) {
            throw new TaierDefineException("ftp read source file type not supported");
        }
        String fileSuffix = filePath.substring(fileSuffixSeparateIndex).toLowerCase();
        if (EXCEL_FILE_TYPES.contains(fileSuffix)) {
            this.fileType = "excel";
            return this.fileType;
        }

        if (CSV.contains(fileSuffix)) {
            this.fileType = "csv";
            return this.fileType;
        }

        if (TXT.contains(fileSuffix)) {
            this.fileType = "txt";
            return this.fileType;
        }

        throw new TaierDefineException("unsupported file type of the" + fileSuffix);
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
