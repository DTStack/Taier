import React, { Component } from "react";

import {
    Input,
    Form,
    Radio,
} from "antd";

const RadioGroup = Radio.Group;

const FormItem = Form.Item;

import { formItemLayout } from "../../../../consts";

const DATAMAP_TYPE = {
    PRE_SUM: 1,
    TIME_SEQUENCE: 2,
    FILTER: 3,
}

class DataMapForm extends Component {

    state = {
        datamapType: DATAMAP_TYPE.PRE_SUM,
    };

    onDataMapTypeChange = (e) => {
        console.log('radio checked', e.target.value);
        this.setState({
            datamapType: e.target.value,
        });
    }

    dynamicRender = () => {
        const { datamapType } = this.state;
        const { isCreate, form, data } = this.props;
        const { getFieldDecorator } = form;

        switch(datamapType) {
            case DATAMAP_TYPE.TIME_SEQUENCE: {
                return (
                    <FormItem {...formItemLayout} label="主表查询" hasFeedback>
                        {getFieldDecorator("datamapType", {
                            rules: [
                                {
                                    required: true,
                                    message: "密码不可为空！"
                                },
                                {
                                    min: 6,
                                    message: "密码长度应该不低于6个字符"
                                },
                                {
                                    validator: this.checkConfirm
                                }
                            ],
                            initialValue: data ? data.datamapType : datamapType,
                        })(
                            <Input placehoder="支持对字段进行SUM、AVG、MAX、MIN、COUNT函数的预聚合处理" type="textarea" />
                        )}
                    </FormItem>
                )
            }
            case DATAMAP_TYPE.FILTER: {
                return (
                    <FormItem {...formItemLayout} label="主表查询" hasFeedback>
                        {getFieldDecorator("query", {
                            rules: [
                                {
                                    required: true,
                                    message: "密码不可为空！"
                                },
                                {
                                    min: 6,
                                    message: "密码长度应该不低于6个字符"
                                },
                                {
                                    validator: this.checkConfirm
                                }
                            ],
                            initialValue: data ? data.query : '',
                        })(
                            <Input placehoder="支持对字段进行SUM、AVG、MAX、MIN、COUNT函数的预聚合处理" type="textarea" />
                        )}
                    </FormItem>
                )
            }
            case DATAMAP_TYPE.PRE_SUM:
            default: {
                return (
                    <FormItem {...formItemLayout} label="主表查询" hasFeedback>
                        {getFieldDecorator("datamapType", {
                            rules: [
                                {
                                    required: true,
                                    message: "密码不可为空！"
                                },
                                {
                                    min: 6,
                                    message: "密码长度应该不低于6个字符"
                                },
                                {
                                    validator: this.checkConfirm
                                }
                            ],
                            initialValue: data ? data.datamapType : datamapType,
                        })(
                            <Input placeholder="支持对字段进行SUM、AVG、MAX、MIN、COUNT函数的预聚合处理" type="textarea" />
                        )}
                    </FormItem>
                )
            }
        }
    }

    render() {
        const { datamapType } = this.state;
        const { isCreate, form, data } = this.props;
        const { getFieldDecorator } = form;

        return (
            <Form style={{marginTop: '24px'}}>
                <FormItem {...formItemLayout} label="主表" hasFeedback>
                    {getFieldDecorator("table", {
                        rules: [
                            {
                                required: true,
                                message: "数据库名称不可为空！"
                            },
                            {
                                max: 20,
                                message: "数据库名称不得超过20个字符！"
                            },
                            {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message:
                                    "数据库名称只能由字母与数字、下划线组成"
                            }
                        ],
                        initialValue: data ? data.table : ""
                    })(<Input autoComplete="off" />)}
                </FormItem>
                <FormItem {...formItemLayout} label="DataMap名称" hasFeedback>
                    {getFieldDecorator("datamapName", {
                        rules: [
                            {
                                required: true,
                                message: "数据库名称不可为空！"
                            },
                            {
                                max: 20,
                                message: "数据库名称不得超过20个字符！"
                            },
                            {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message:
                                    "数据库名称只能由字母与数字、下划线组成"
                            }
                        ],
                        initialValue: data ? data.datamapName : ""
                    })(<Input autoComplete="off" />)}
                </FormItem>
                <FormItem {...formItemLayout} label="DataMap类型" hasFeedback>
                    {getFieldDecorator("datamapType", {
                        rules: [
                            {
                                required: true,
                                message: "密码不可为空！"
                            },
                            {
                                min: 6,
                                message: "密码长度应该不低于6个字符"
                            },
                            {
                                validator: this.checkConfirm
                            }
                        ],
                        initialValue: data ? data.datamapType : datamapType,
                    })(
                        <RadioGroup onChange={this.onDataMapTypeChange}>
                            <Radio value={DATAMAP_TYPE.PRE_SUM}>预聚合</Radio>
                            <Radio value={DATAMAP_TYPE.TIME_SEQUENCE}>时间序列</Radio>
                            <Radio value={DATAMAP_TYPE.FILTER}>布隆过滤器</Radio>
                        </RadioGroup>
                    )}
                </FormItem>
                {this.dynamicRender()}
            </Form>
        );
    }
}

const FormWrapper = Form.create()(DataMapForm);

export default FormWrapper;
