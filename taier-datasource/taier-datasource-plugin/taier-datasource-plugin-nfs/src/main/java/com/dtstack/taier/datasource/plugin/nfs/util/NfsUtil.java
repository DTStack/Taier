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

package com.dtstack.taier.datasource.plugin.nfs.util;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.NfsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.emc.ecs.nfsclient.nfs.NfsSetAttributes;
import com.emc.ecs.nfsclient.nfs.nfs3.Nfs3;
import com.emc.ecs.nfsclient.rpc.CredentialUnix;

/**
 * nfs util
 *
 * @author ：wangchuan
 * date：Created in 下午8:14 2022/3/17
 * company: www.dtstack.com
 */
public class NfsUtil {

    private static final long MODE = 510L;

    public static Nfs3 getClient(ISourceDTO sourceDTO) {
        try {
            NfsSourceDTO nfsSourceDTO = (NfsSourceDTO) sourceDTO;
            NfsSetAttributes nfsSetAttr = new NfsSetAttributes();
            nfsSetAttr.setMode(MODE);
            return new Nfs3(nfsSourceDTO.getServer(), nfsSourceDTO.getPath(), new CredentialUnix(-2, -2, null), 3);
        } catch (Exception e) {
            throw new SourceException(String.format("get nfs client error: %s", e.getMessage()), e);
        }
    }
}
