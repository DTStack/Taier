package com.dtstack.taiga.dao.domain;


import com.baomidou.mybatisplus.annotation.TableName;

@TableName("console_kerberos")
public class KerberosConfig extends BaseEntity{

    private Long clusterId;

    private String name;

    private Integer openKerberos;


    private String remotePath;


    private String principal;


    private String principals;

    private Integer componentType;

    /**
     * krb5 的文件名称
     */

    private String krbName;

    private String mergeKrbContent;
    /**
     * 因为kerberos文件可能在组件保存之前上传,因此只能添加版本来区分
     */
    private String componentVersion;

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public String getMergeKrbContent() {
        return mergeKrbContent;
    }

    public void setMergeKrbContent(String mergeKrbContent) {
        this.mergeKrbContent = mergeKrbContent;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public String getKrbName() {
        return krbName;
    }

    public void setKrbName(String krbName) {
        this.krbName = krbName;
    }

    public Integer getComponentType() {
        return componentType;
    }

    public void setComponentType(Integer componentType) {
        this.componentType = componentType;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
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
}
