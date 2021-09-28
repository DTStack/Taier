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

package com.dtstack.engine.master.vo;

import com.dtstack.engine.domain.KerberosConfig;
import java.sql.Timestamp;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@ApiModel
public class KerberosConfigVO extends KerberosConfig {
    private String keytabPath;

    private Map hdfsConfig;

    private Timestamp kerberosFileTimestamp;

    /**
     * keytab 名称 平台使用
     */
    private String principalFile;

    public static KerberosConfigVO toVO(KerberosConfig config) {
        KerberosConfigVO kerberosConfigVO = new KerberosConfigVO();
        BeanUtils.copyProperties(config, kerberosConfigVO);
        kerberosConfigVO.setPrincipalFile(config.getName());
        return kerberosConfigVO;
    }

    public String getPrincipalFile() {
        return principalFile;
    }

    public void setPrincipalFile(String principalFile) {
        this.principalFile = principalFile;
    }

    public String getKeytabPath() {
        return keytabPath;
    }

    public void setKeytabPath(String keytabPath) {
        this.keytabPath = keytabPath;
    }

    public Map getHdfsConfig() {
        return hdfsConfig;
    }

    public void setHdfsConfig(Map hdfsConfig) {
        this.hdfsConfig = hdfsConfig;
    }

    public Timestamp getKerberosFileTimestamp() {
        return kerberosFileTimestamp;
    }

    public void setKerberosFileTimestamp(Timestamp kerberosFileTimestamp) {
        this.kerberosFileTimestamp = kerberosFileTimestamp;
    }
}

