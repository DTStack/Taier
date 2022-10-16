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

package com.dtstack.taier.base;

import com.dtstack.taier.pluginapi.sftp.SftpConfig;

import java.sql.Timestamp;

/**
 * @author yuebai
 * @date 2020-06-15
 */
public class BaseConfig {

    private SftpConfig sftpConf;

    private boolean openKerberos;

    /** remote file path eg. sftp */
    private String remoteDir;

    /** keytab file name */
    private String principalFile;

    private String krbName;

    private String principal;

    private Timestamp kerberosFileTimestamp;

    private String mergeKrbContent;

    private String hadoopUserName;

    private String dtProxyUserName;

    public String getHadoopUserName() {
        return hadoopUserName;
    }

    public void setHadoopUserName(String hadoopUserName) {
        this.hadoopUserName = hadoopUserName;
    }

    public String getDtProxyUserName() {
        return dtProxyUserName;
    }

    public void setDtProxyUserName(String dtProxyUserName) {
        this.dtProxyUserName = dtProxyUserName;
    }

    public String getMergeKrbContent() {
        return mergeKrbContent;
    }

    public void setMergeKrbContent(String mergeKrbContent) {
        this.mergeKrbContent = mergeKrbContent;
    }

    public SftpConfig getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(SftpConfig sftpConf) {
        this.sftpConf = sftpConf;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getKrbName() {
        return krbName;
    }

    public void setKrbName(String krbName) {
        this.krbName = krbName;
    }

    public Timestamp getKerberosFileTimestamp() {
        return kerberosFileTimestamp;
    }

    public void setKerberosFileTimestamp(Timestamp kerberosFileTimestamp) {
        this.kerberosFileTimestamp = kerberosFileTimestamp;
    }

    public boolean isOpenKerberos() {
        return openKerberos;
    }

    public void setOpenKerberos(boolean openKerberos) {
        this.openKerberos = openKerberos;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public String getPrincipalFile() {
        return principalFile;
    }

    public void setPrincipalFile(String principalFile) {
        this.principalFile = principalFile;
    }
}
