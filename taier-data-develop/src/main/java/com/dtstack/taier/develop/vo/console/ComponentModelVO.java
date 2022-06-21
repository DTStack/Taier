package com.dtstack.taier.develop.vo.console;

import com.dtstack.taier.common.util.Pair;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("组件模版信息")
public class ComponentModelVO {

    @ApiModelProperty(notes = "组件名称")
    private String name;

    @ApiModelProperty(notes = "是否允许多版本")
    private boolean allowCoexistence;

    @ApiModelProperty(notes = "组件依赖前置组件")
    private List<Integer> dependOn;

    @ApiModelProperty(notes = "组件可选择版本")
    private List<Pair<String, List<Pair>>> versionDictionary;

    @ApiModelProperty(notes = "组件所属类型")
    private Integer owner;

    @ApiModelProperty(notes = "组件所属code")
    private Integer componentCode;

    @ApiModelProperty(notes = "是否允许kerberos")
    private boolean allowKerberos;

    @ApiModelProperty(notes = "是否允许参数上传")
    private boolean allowParamUpload;

    public List<Pair<String, List<Pair>>> getVersionDictionary() {
        return versionDictionary;
    }

    public void setVersionDictionary(List<Pair<String, List<Pair>>> versionDictionary) {
        this.versionDictionary = versionDictionary;
    }

    public boolean isAllowKerberos() {
        return allowKerberos;
    }

    public void setAllowKerberos(boolean allowKerberos) {
        this.allowKerberos = allowKerberos;
    }

    public boolean isAllowParamUpload() {
        return allowParamUpload;
    }

    public void setAllowParamUpload(boolean allowParamUpload) {
        this.allowParamUpload = allowParamUpload;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(Integer componentCode) {
        this.componentCode = componentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAllowCoexistence() {
        return allowCoexistence;
    }

    public void setAllowCoexistence(boolean allowCoexistence) {
        this.allowCoexistence = allowCoexistence;
    }

    public List<Integer> getDependOn() {
        return dependOn;
    }

    public void setDependOn(List<Integer> dependOn) {
        this.dependOn = dependOn;
    }
}
