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

import com.dtstack.taiga.develop.utils.develop.common.enums.StoredType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
@Data
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
}
