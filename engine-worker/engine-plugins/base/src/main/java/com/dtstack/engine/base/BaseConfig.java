package com.dtstack.engine.base;

import com.dtstack.engine.common.sftp.SftpConfig;

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
