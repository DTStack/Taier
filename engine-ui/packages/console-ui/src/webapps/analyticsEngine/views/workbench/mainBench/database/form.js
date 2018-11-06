import React, { Component } from "react";

import {
    Input,
    Form,
} from "antd";

const FormItem = Form.Item;

import { formItemLayout } from "../../../../consts";

class DatabaseForm extends Component {

    state = {
        confirmDirty: false
    };

    handleConfirmBlur = e => {
        const value = e.target.value;
        this.setState({ confirmDirty: this.state.confirmDirty || !!value });
    };

    checkPassword = (rule, value, callback) => {
        const form = this.props.form;
        if (value && value !== form.getFieldValue("dbPwd")) {
            callback("两次输入的密码不一致!");
        } else {
            callback();
        }
    };

    checkConfirm = (rule, value, callback) => {
        const form = this.props.form;
        if (value && this.state.confirmDirty) {
            form.validateFields(["confirmPassword"], { force: true });
        }
        callback();
    };

    render() {
        const { isCreate, databaseData, form } = this.props;
        const { getFieldDecorator } = form;

        return (
            <Form style={{marginTop: '24px'}}>
                <FormItem {...formItemLayout} label="数据库标识" hasFeedback={isCreate}>
                    {getFieldDecorator("name", {
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
                        initialValue: databaseData ? databaseData.name : ""
                    })(<Input autoComplete="off" disabled={!isCreate} />)}
                </FormItem>
                <FormItem {...formItemLayout} label="用户名" hasFeedback={isCreate}>
                    {getFieldDecorator("dbUserName", {
                        rules: [
                            {
                                required: true,
                                message: "用户名不可为空！"
                            },
                            {
                                max: 20,
                                message: "用户名控制在20个字符以内！"
                            },
                            {
                                pattern: /^[A-Za-z0-9_@]+$/,
                                message: "名称只能由字母与数字、@, 下划线组成"
                            }
                        ],
                        initialValue: databaseData ? databaseData.dbUserName : ""
                    })(<Input disabled={!isCreate}/>)}
                </FormItem>
                {
                    !isCreate && 
                    <FormItem {...formItemLayout} label="旧密码" hasFeedback>
                    {getFieldDecorator("oldPwd", {
                        rules: [
                            {
                                required: true,
                                message: "旧密码不可为空！"
                            },
                            {
                                min: 6,
                                message: "旧密码长度应该不低于6个字符"
                            },
                        ],
                        initialValue: '',
                    })(<Input type="password" />)}
                </FormItem>
                }
                <FormItem {...formItemLayout} label="密码" hasFeedback>
                    {getFieldDecorator("dbPwd", {
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
                        initialValue: '',
                    })(<Input type="password" />)}
                </FormItem>
                <FormItem {...formItemLayout} label="确认密码" hasFeedback>
                    {getFieldDecorator("confirmPassword", {
                        rules: [
                            {
                                required: true,
                                message: "确认密码不可为空！"
                            },
                            {
                                min: 6,
                                message: "密码长度应该不低于6个字符"
                            },
                            {
                                validator: this.checkPassword
                            }
                        ],
                        initialValue: '',
                    })(<Input type="password" onBlur={this.handleConfirmBlur}/>)}
                </FormItem>
            </Form>
        );
    }
}

const FormWrapper = Form.create()(DatabaseForm);

export default FormWrapper;
