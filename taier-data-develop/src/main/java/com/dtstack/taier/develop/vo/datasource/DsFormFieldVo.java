package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据源表单属性视图类
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
@ApiModel("数据源表单属性")
@Data
public class DsFormFieldVo implements Serializable {

    /**
     * 表单属性名称
     */
    @ApiModelProperty("表单属性名称")
    private String name;

    /**
     * 属性前label名称
     */
    @ApiModelProperty("属性前label名称")
    private String label;

    /**
     * 属性格式
     */
    @ApiModelProperty("属性格式")
    private String widget;

    /**
     * 是否必填
     */
    @ApiModelProperty("是否必填 0-非必填 1-必填")
    private Integer required;

    /**
     * 是否为隐藏
     */
    @ApiModelProperty("是否为隐藏 0-否 1-隐藏")
    private Integer invisible;

    /**
     * 表单属性中默认值
     */
    @ApiModelProperty("表单属性中默认值")
    private String defaultValue;

    /**
     * 输入框placeHold, 默认为空'
     */
    @ApiModelProperty("输入框placeHold, 默认为空")
    private String placeHold;

    /**
     * 请求数据Api接口地址，一般用于关联下拉框类型，如果不需要请求则为空
     */
    @ApiModelProperty("请求数据Api接口地址，一般用于关联下拉框类型，如果不需要请求则为空")
    private String requestApi;

    /**
     * 是否为数据源需要展示的连接信息字段。0-否; 1-是
     */
    @ApiModelProperty("是否为数据源需要展示的连接信息字段。0-否; 1-是")
    private Integer isLink;

    /**
     * 校验返回信息文案
     */
    @ApiModelProperty("校验返回信息文案")
    private String validInfo;

    /**
     * 输入框后问号的提示信息
     */
    @ApiModelProperty("输入框后问号的提示信息")
    private String tooltip;

    /**
     * 前端表单属性style参数
     */
    @ApiModelProperty("前端表单属性style参数")
    private String style;

    /**
     * 正则校验表达式
     */
    @ApiModelProperty("正则校验表达式")
    private String regex;

    @ApiModelProperty("select组件下拉内容")
    private List<Map> options;

}
