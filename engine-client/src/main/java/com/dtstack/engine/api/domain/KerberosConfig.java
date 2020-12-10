package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class KerberosConfig extends BaseEntity {

    private Long clusterId;

    private String name;

    private Integer openKerberos;

    /**
     * 集群文件夹的sftp路径
     */
    @ApiModelProperty(notes = "集群文件夹的sftp路径")
    private String remotePath;

    @ApiModelProperty(notes = "kerberos认证用户名")
    private String principal;

    @ApiModelProperty(notes = "keytab中所有用户名")
    private String principals;

    private Integer componentType;

    /**
     * krb5 的文件名称
     */
    @ApiModelProperty(notes = "krb5 的文件名称")
    private String krbName;


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
