import * as React from 'react';

import {
    Input, Select, Form, Checkbox, Modal
} from 'antd'

import { MY_APPS, ANALYTICS_ENGINE_ROLE } from 'main/consts';
import { isDisabledRole } from 'main/views/admin/user/form';
import { formItemLayout } from '../../../../consts';

const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group;

class FormAddUser extends React.Component<any, any> {
    render () {
        const { form, roles, onSearch, userList, initialData, user, myRoles } = this.props;
        const getFieldDecorator = form.getFieldDecorator;

        const userOptions = userList && userList
            .map((item: any) =>
                <Option
                    key={item.userId}
                    value={`${item.userId}`}
                    name={item.userName}
                    optionFilterProp="name"
                    {...{ name: item.userName, optionFilterProp: 'name' }}
                >
                    {item.userName}
                </Option>
            )

        let roleOptions: any = [];
        let initialValue: any = [];
        if (roles) {
            roles.forEach((role: any) => {
                // 判断哪些角色禁用
                const disabled = isDisabledRole(MY_APPS.ANALYTICS_ENGINE, role.roleValue, user, myRoles);
                if (role.roleValue === ANALYTICS_ENGINE_ROLE.VISITOR) {
                    initialValue.push(role.id)
                }
                roleOptions.push({ label: role.roleName, value: role.id, disabled })
            })
        }

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="数据库"
                >
                    {getFieldDecorator('databaseId', {
                        rules: [{
                            required: true, message: '数据库为必填项！'
                        }],
                        initialValue: initialData && initialData.id
                    })(
                        <Input type="hidden" />
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
                            required: true, message: '用户不可为空！'
                        }]
                    })(
                        <Select
                            mode="multiple"
                            showSearch
                            showArrow={true}
                            {...{ showArrow: true }}
                            style={{ width: '100%' }}
                            notFoundContent="当前用户不存在"
                            placeholder="请搜索并选择用户"
                            optionFilterProp="name"
                            onSearch={onSearch}
                        >
                            {userOptions}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="角色设置"
                >
                    {getFieldDecorator('roleIds', {
                        rules: [],
                        initialValue: initialValue
                    })(
                        <CheckboxGroup
                            options={roleOptions}
                        />
                    )}
                </FormItem>
            </Form>
        )
    }
}

const FormWrapper = Form.create<any>()(FormAddUser);

class AddUser extends React.Component<any, any> {
    _formInstance: any;

    addUser = () => {
        const { onSubmit } = this.props;
        const form = this._formInstance.props.form;
        form.validateFields((err: any, values: any) => {
            if (!err) {
                if (onSubmit) {
                    onSubmit(values);
                    setTimeout(this.onCancel, 0);
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
                    wrappedComponentRef={(e: any) => { this._formInstance = e }}
                    {...this.props}
                />
            </Modal>
        )
    }
}

export default AddUser
