/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.base.filesystem.manager;

import com.dtstack.engine.common.sftp.SftpConfig;

/**
 * Date: 2020/7/20
 * Company: www.dtstack.com
 * <p>
 * 参考 @com.dtstack.engine.common.util.SFTPHandler
 *
 * @author maqi
 */
public class SftpFileManage extends com.dtstack.engine.common.sftp.SftpFileManage implements IFileManage {

    public static final String PREFIX = "sftp://";

    public SftpFileManage(SftpConfig sftpConfig) {
        super(sftpConfig);
    }


    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public boolean canHandle(String remotePath) {
        return remotePath.contains(PREFIX);
    }
}
