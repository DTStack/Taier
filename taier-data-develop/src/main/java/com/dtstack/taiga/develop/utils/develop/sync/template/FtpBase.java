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


import lombok.Data;

import java.util.List;

@Data
public class FtpBase extends BaseSource{

    protected String protocol;
    protected String host;
    protected String username;
    protected String password;
    protected String fieldDelimiter = "\001";
    protected String encoding;
    protected Integer port;
    protected List column;
    protected String connectPattern;
    protected String rsaPath;
    protected String auth;

    /**
     * 写入文件名 可以多个 以逗号分割 离线不处理
     */
    private String ftpFileName;

}

