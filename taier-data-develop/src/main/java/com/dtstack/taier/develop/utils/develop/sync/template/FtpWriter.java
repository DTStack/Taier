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
import com.dtstack.taier.common.enums.SftpAuthType;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;

import java.util.Optional;

public class FtpWriter extends FtpBase implements Writer {

    private String writeMode = "overwrite";

    private String path;

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);

        if (auth != null && Integer.valueOf(auth).equals(SftpAuthType.RSA.getType())) {
            //免登录 私钥路径
            parameter.put("privateKeyPath", rsaPath);
        } else {
            parameter.put("password", this.getPassword());
        }
        parameter.put("protocol", this.getProtocol());
        parameter.put("path", Optional.ofNullable(this.getPath()).orElse(""));
        parameter.put("host", this.getHost());
        parameter.put("port", this.getPort());
        parameter.put("ftpFileName", this.getFtpFileName());
        parameter.put("username", this.getUsername());
        parameter.put("fieldDelimiter", this.fieldDelimiter);
        parameter.put("writeMode", this.getWriteMode());
        parameter.put("connectPattern", this.getConnectPattern());
        parameter.put("column", ColumnUtil.getColumns(this.column,PluginName.FTP_W));
        parameter.put("encoding", this.getEncoding());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        JSONObject writer = new JSONObject(true);

        writer.put("name", PluginName.FTP_W);
        writer.put("parameter", parameter);

        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
