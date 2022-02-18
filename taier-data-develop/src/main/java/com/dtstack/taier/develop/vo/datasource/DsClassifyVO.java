package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 数据源分类类目模型
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
@ApiModel("数据源分类类目模型")
public class DsClassifyVO implements Serializable {

    @ApiModelProperty("类目主键id")
    private Long classifyId;

    @ApiModelProperty("数据源类目编码")
    private String classifyCode;

    @ApiModelProperty("类目名称")
    private String classifyName;

    @ApiModelProperty("类型栏排序字段")
    private Integer sorted;

    public Long getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Long classifyId) {
        this.classifyId = classifyId;
    }

    public String getClassifyCode() {
        return classifyCode;
    }

    public void setClassifyCode(String classifyCode) {
        this.classifyCode = classifyCode;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public Integer getSorted() {
        return sorted;
    }

    public void setSorted(Integer sorted) {
        this.sorted = sorted;
    }
}
