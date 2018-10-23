import React, { Component } from 'react';

import {
    Input, Button, Card, Radio,
    Select, Form, Checkbox, Modal,
} from 'antd'

const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group;

import { formItemLayout } from '../../../../consts';

class FormAddUser extends Component {

    render() {

        const { form, roles, onSearch, userList, initialData } = this.props;
        const getFieldDecorator = form.getFieldDecorator;

        const userOptions = userList && userList
            .map(item =>
                <Option
                    key={item.userId}
                    value={`${item.userId}`}
                    name={item.userName}
                    optionFilterProp="name"
                >
                    {item.userName}
                </Option>
            )
        
        let roleOptions = [];
        let initialValue=[];
        if (roles) {
            roles.forEach(role => {
                roleOptions.push({ label: role.roleName, value: role.id })
            })
        }

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="数据库"
                    hasFeedback
                >
                    {getFieldDecorator('databaseId', {
                        rules: [{
                            required: true, message: '数据库为必填项！',
                        }],
                        initialValue: initialData && initialData.id,
                    })(
                        <Input type="hidden" />,
                    )}
                    <span>{initialData && initialData.name}</span>
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="请选择用户"
                    hasFeedback
                >
                    {getFieldDecorator('targetUserIds', {
                        rules: [{
                            required: true, message: '用户不可为空！',
                        }],
                    })(
                        <Select
                            mode="multiple"
                            showSearch
                            showArrow={true}
                            style={{ width: '100%' }}
                            notFoundContent="当前用户不存在"
                            placeholder="请搜索并选择用户"
                            optionFilterProp="name"
                            onSearch={onSearch}
                        >
                            {userOptions}
                        </Select>,
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="角色设置"
                >
                    {getFieldDecorator('roleIds', {
                        rules: [],
                        initialValue: initialValue,
                    })(
                        <CheckboxGroup
                            options={roleOptions}
                            onChange={this.roleChange}
                        />,
                    )}
                </FormItem>
            </Form>
        )
    }
}

const FormWrapper = Form.create()(FormAddUser);

class AddUser extends Component {
    
    addUser = () => {
        const { onSubmit } = this.props;
        const form = this._formInstance.props.form;
        form.validateFields((err, values) => {
            if (!err) {
                if (onSubmit) {
                    onSubmit(values);
                }
            }
        });
    }

    onCancel = () => {
        const form = this._formInstance.props.form;
        form.resetFields();
        this.props.onCancel();
    }

    render () {
        return (
            <Modal
                title="添加用户"
                visible={this.props.visible}
                onOk={this.addUser}
                onCancel={this.onCancel}
            >
                <FormWrapper
                    wrappedComponentRef={(e) => { this._formInstance = e }}
                    {...this.props}
                />
            </Modal>
        )
    }
}

export default AddUser