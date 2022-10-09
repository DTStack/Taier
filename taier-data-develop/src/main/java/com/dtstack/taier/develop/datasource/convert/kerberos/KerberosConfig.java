package com.dtstack.taier.develop.datasource.convert.kerberos;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author ：nanqi
 * date：Created in 下午5:15 2021/7/29
 * company: www.dtstack.com
 */
public class KerberosConfig implements Serializable {
    private Long clusterId;

    private String name;

    private Integer openKerberos;

    private String remotePath;

    private String principal;

    private String principalFile;

    /**
     * Kerberos 文件上传时间
     */
    private Timestamp kerberosFileTimestamp;

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPrincipalFile() {
        return principalFile;
    }

    public void setPrincipalFile(String principalFile) {
        this.principalFile = principalFile;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOpenKerberos() {
        return openKerberos;
    }

    public void setOpenKerberos(Integer openKerberos) {
        this.openKerberos = openKerberos;
    }

    public Long getClusterId() {

        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Timestamp getKerberosFileTimestamp() {
        return kerberosFileTimestamp;
    }

    public void setKerberosFileTimestamp(Timestamp kerberosFileTimestamp) {
        this.kerberosFileTimestamp = kerberosFileTimestamp;
    }
}
