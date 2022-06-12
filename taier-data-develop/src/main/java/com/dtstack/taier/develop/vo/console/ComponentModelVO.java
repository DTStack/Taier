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

    @ApiModelProperty(notes = "组件可选择版本")
    private List<Pair<String, List<Pair>>> versionDictionary;

    @ApiModelProperty(notes = "组件依赖前置组件")
    private List<Integer> dependOn;

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

    public List<Pair<String, List<Pair>>> getVersionDictionary() {
        return versionDictionary;
    }

    public void setVersionDictionary(List<Pair<String, List<Pair>>> versionDictionary) {
        this.versionDictionary = versionDictionary;
    }

    public List<Integer> getDependOn() {
        return dependOn;
    }

    public void setDependOn(List<Integer> dependOn) {
        this.dependOn = dependOn;
    }
}
