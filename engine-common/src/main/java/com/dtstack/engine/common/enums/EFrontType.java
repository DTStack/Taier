package com.dtstack.engine.common.enums;

/**
 * @author yuebai
 * @date 2020-05-11
 * input: 普通文件输入框
 * radio: 单选框 无子控件渲染
 * group: 类型切换tab框
 * select: 下拉选择控件
 * checkbox: 复选框
 * xml: xml格式的配置
 * other: typeName、md5等前端不展示的控件
 * radio_linkage: 单选框 选择单选框会渲染对应values的下的子控件
 * password: 隐藏显示的输入框
 * custom_control: 自定义控件 可手动添加 可删除
 */
public enum EFrontType {
    INPUT(0, "input"),
    RADIO(1, "radio"),
    GROUP(2, "group"),
    SELECT(3, "select"),
    CHECKBOX(4, "checkbox"),
    XML(5, "xml"),
    OTHER(6, "other"),
    RADIO_LINKAGE(7, "radio_linkage"),
    PASSWORD(8, "password"),
    CUSTOM_CONTROL(9, "custom_control");

    private int code;
    private String name;

    EFrontType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
