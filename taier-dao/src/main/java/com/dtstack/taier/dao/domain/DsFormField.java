package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author 全阅
 * @Description: 数据源表单属性
 * @Date: 2021/3/10
 */

@TableName("datasource_form_field")
public class DsFormField extends BaseModel<DsFormField> {

    /**
     * 表单属性名称
     */
    @TableField("name")
    private String name;

    /**
     * 属性前label名称
     */
    @TableField("label")
    private String label;

    /**
     * 属性格式
     */
    @TableField("widget")
    private String widget;

    /**
     * 是否必填
     */
    @TableField("required")
    private Integer required;

    /**
     * 是否为隐藏
     */
    @TableField("invisible")
    private Integer invisible;

    /**
     * 表单属性中默认值
     */
    @TableField("default_value")
    private String defaultValue;

    /**
     * 输入框placeHold, 默认为空'
     */
    @TableField("place_hold")
    private String placeHold;

    /**
     * 请求数据Api接口地址，一般用于关联下拉框类型，如果不需要请求则为空
     */
    @TableField("request_api")
    private String requestApi;

    /**
     * 是否为数据源需要展示的连接信息字段。0-否; 1-是
     */
    @TableField("is_link")
    private Integer isLink;

    /**
     * 校验返回信息文案
     */
    @TableField("valid_info")
    private String validInfo;

    /**
     * 输入框后问号的提示信息
     */
    @TableField("tooltip")
    private String tooltip;

    /**
     * 前端表单属性style参数
     */
    @TableField("style")
    private String style;

    /**
     * 正则校验表达式
     */
    @TableField("regex")
    private String regex;

    /**
     * 对应数据源版本信息
     */
    @TableField("type_version")
    private String typeVersion;

    /**
     * select组件的下拉内容
     */
    @TableField("options")
    private String options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getWidget() {
        return widget;
    }

    public void setWidget(String widget) {
        this.widget = widget;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public Integer getInvisible() {
        return invisible;
    }

    public void setInvisible(Integer invisible) {
        this.invisible = invisible;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getPlaceHold() {
        return placeHold;
    }

    public void setPlaceHold(String placeHold) {
        this.placeHold = placeHold;
    }

    public String getRequestApi() {
        return requestApi;
    }

    public void setRequestApi(String requestApi) {
        this.requestApi = requestApi;
    }

    public Integer getIsLink() {
        return isLink;
    }

    public void setIsLink(Integer isLink) {
        this.isLink = isLink;
    }

    public String getValidInfo() {
        return validInfo;
    }

    public void setValidInfo(String validInfo) {
        this.validInfo = validInfo;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getTypeVersion() {
        return typeVersion;
    }

    public void setTypeVersion(String typeVersion) {
        this.typeVersion = typeVersion;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
