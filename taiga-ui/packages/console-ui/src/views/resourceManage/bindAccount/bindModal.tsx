/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from 'react';
import { get } from 'lodash';
import { Modal, Form, Select, Alert, Button, Input } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import { formItemLayout } from '../../../consts';

const Option = Select.Option;

interface IProps extends FormComponentProps {
    title: string;
    data: object;
    userList: any[];
    visible: boolean;
    engineText: string;
    onCancel: (e: any) => any;
    onOk: (e: any) => any;
    onUnbind: (e: any) => any;
}

class BindAccountModal extends React.Component<IProps, any> {
    constructor (props: any) {
        super(props);
        this.state = {}
    }

    onSubmit = (callback: (values: any) => {}) => {
        const { form, data, userList } = this.props;
        const isEdit = data !== null && data !== undefined;
        const validFields = isEdit ? ['username', 'id'] : ['bindUserId'];
        form.validateFields(validFields.concat(['name', 'password']), (err, user) => {
            if (!err) {
                const selectedUser = userList.find(u => u.userId == user.bindUserId);
                if (selectedUser) {
                    user.username = selectedUser.userName;
                    user.email = selectedUser.userName;
                }
                // 此处主要是由于后端字段不一致的原因所致
                if (user.id) {
                    user.bindUserId = user.id;
                }

                if (callback) {
                    callback(user);
                    setTimeout(() => form.resetFields(), 0);
                }
            }
        })
    }

    onOk = () => {
        const { onOk } = this.props;
        this.onSubmit(onOk)
    }

    onUnbind = () => {
        const { onUnbind } = this.props;
        this.onSubmit(onUnbind)
    }

    onCancel = (e: any) => {
        const { onCancel, form } = this.props;
        if (onCancel) {
            setTimeout(() => form.resetFields(), 0);
            onCancel(e);
        }
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, onCancel, title, data, userList } = this.props;
        const isEdit = data !== null && data !== undefined;
        const footer = (
            <div style={{ height: '30px' }}>
                { data
                    ? <span className="left">
                        <Button onClick={this.onUnbind}>解除绑定</Button>
                    </span>
                    : null
                }
                <span className="right">
                    <Button onClick={this.onCancel} style={{ marginRight: 10 }}>取消</Button>
                    <Button type="primary" onClick={this.onOk}>确定</Button>
                </span>
            </div>
        )

        return (
            <Modal
                closable
                title={title}
                visible={visible}
                footer={footer}
                onCancel={onCancel}
                className={'no-padding-modal'}
            >
                <React.Fragment>
                    <Alert
                        message="每个产品账号可绑定一个数据库用户，任务提交运行时后台将使用绑定的数据库用户执行。"
                        style={{ marginBottom: 24 }}
                        type="info"
                        showIcon
                    />
                    <Form>
                        { !isEdit
                            ? <Form.Item
                                key="bindUserId"
                                label="产品账号"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('bindUserId', {
                                    rules: [{
                                        required: true,
                                        message: '产品账号不可为空！'
                                    }],
                                    initialValue: undefined
                                })(
                                    <Select
                                        allowClear
                                        showSearch
                                        placeholder='请选择产品账号'
                                        optionFilterProp="title"
                                    >
                                        {userList && userList.map((user: any) => {
                                            const uid = `${user.userId}`;
                                            const uname = user.userName;
                                            return <Option key={uid} title={uname} value={uid}>{uname}</Option>
                                        })}
                                    </Select>
                                )}
                            </Form.Item>
                            : (
                                <React.Fragment>
                                    <Form.Item
                                        key="username"
                                        label="产品账号"
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('username', {
                                            rules: [{
                                                required: true,
                                                message: '产品账号不可为空！'
                                            }],
                                            initialValue: get(data, 'username', '')
                                        })(
                                            <Input disabled={isEdit}/>
                                        )}
                                    </Form.Item>
                                    <Form.Item
                                        key="id"
                                        label="产品账号"
                                        style={{ display: 'none' }}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('id', {
                                            initialValue: get(data, 'id', undefined)
                                        })(
                                            <Input />
                                        )}
                                    </Form.Item>
                                </React.Fragment>
                            )
                        }
                        <Form.Item
                            label="数据库账号"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('name', {
                                rules: [{
                                    required: true,
                                    message: '数据库账号不可为空！'
                                }, {
                                    pattern: /^[^\s]*$/,
                                    message: '数据库账号不可输入空格!'
                                }, {
                                    max: 128,
                                    message: '数据库账号不得超过128个字符!'
                                }],
                                initialValue: get(data, 'name', '')
                            })(
                                <Input placeholder="请输入数据库账号" />
                            )}
                        </Form.Item>
                        <Form.Item
                            label="数据库密码"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('password', {
                                rules: [{
                                    pattern: /^[^\s]*$/,
                                    message: '数据库密码不可输入空格!'
                                }, {
                                    max: 128,
                                    message: '数据库密码不得超过128个字符!'
                                }],
                                initialValue: ``
                            })(
                                <Input type="password" placeholder="请输入数据库密码" />
                            )}
                        </Form.Item>
                    </Form>
                </React.Fragment>
            </Modal>
        )
    }
}
export default Form.create<IProps>()(BindAccountModal);
