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

package com.dtstack.taier.datasource.plugin.hdfs3;

import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import com.dtstack.taier.datasource.api.dto.source.Hdfs3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.commons.lang3.StringUtils;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:53 2020/2/27
 * @Description：HDFS 连接工厂
 */
public class HdfsConnFactory {

    public Boolean testConn(ISourceDTO iSource) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) iSource;
        if (StringUtils.isBlank(hdfsSourceDTO.getDefaultFS())) {
            throw new SourceException("defaultFS incorrect format");
        }

        return HdfsOperator.checkConnection(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
    }
}
