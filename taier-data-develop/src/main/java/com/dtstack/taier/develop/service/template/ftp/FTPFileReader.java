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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;
import com.dtstack.taier.develop.service.template.PluginName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @since 1.3.1
 */
public class FTPFileReader extends FTPReaderParam {

    private static final Logger logger = LoggerFactory.getLogger(FTPFileReader.class);

    /**E
     * file type of reader source file
     */
    private String fileType;


    @Override
    public String pluginName() {
        return PluginName.FTP_R;
    }

    @Override
    public void checkFormat(JSONObject data) {
        logger.info("data info --> {}", data);
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
