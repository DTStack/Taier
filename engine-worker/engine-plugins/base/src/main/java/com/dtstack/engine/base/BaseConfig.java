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

    private String remoteDir;

    private String principalFile;

    private String krbName;

    private String principalPath;

    private String principalName;

    public SftpConfig getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(SftpConfig sftpConf) {
        this.sftpConf = sftpConf;
    }

    private Timestamp kerberosFileTimestamp;

    public Timestamp getKerberosFileTimestamp() {
        return kerberosFileTimestamp;
    }

    public void setKerberosFileTimestamp(Timestamp kerberosFileTimestamp) {
        this.kerberosFileTimestamp = kerberosFileTimestamp;
    }

    public String getPrincipalPath() {
        return principalPath;
    }

    public void setPrincipalPath(String principalPath) {
        this.principalPath = principalPath;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getKrbName() {
        return krbName;
    }

    public void setKrbName(String krbName) {
        this.krbName = krbName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseConfig that = (BaseConfig) o;

        if (openKerberos != that.openKerberos) return false;
        if (sftpConf != null ? !sftpConf.equals(that.sftpConf) : that.sftpConf != null) return false;
        if (remoteDir != null ? !remoteDir.equals(that.remoteDir) : that.remoteDir != null) return false;
        if (principalFile != null ? !principalFile.equals(that.principalFile) : that.principalFile != null)
            return false;
        if (krbName != null ? !krbName.equals(that.krbName) : that.krbName != null) return false;
        if (principalPath != null ? !principalPath.equals(that.principalPath) : that.principalPath != null)
            return false;
        if (principalName != null ? !principalName.equals(that.principalName) : that.principalName != null)
            return false;
        return kerberosFileTimestamp != null ? kerberosFileTimestamp.equals(that.kerberosFileTimestamp) : that.kerberosFileTimestamp == null;
    }

    @Override
    public int hashCode() {
        int result = sftpConf != null ? sftpConf.hashCode() : 0;
        result = 31 * result + (openKerberos ? 1 : 0);
        result = 31 * result + (remoteDir != null ? remoteDir.hashCode() : 0);
        result = 31 * result + (principalFile != null ? principalFile.hashCode() : 0);
        result = 31 * result + (krbName != null ? krbName.hashCode() : 0);
        result = 31 * result + (principalPath != null ? principalPath.hashCode() : 0);
        result = 31 * result + (principalName != null ? principalName.hashCode() : 0);
        result = 31 * result + (kerberosFileTimestamp != null ? kerberosFileTimestamp.hashCode() : 0);
        return result;
    }
}
