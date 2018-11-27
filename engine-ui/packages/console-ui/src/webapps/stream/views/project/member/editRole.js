import React, { Component } from 'react'

import {
    Input, Button, Card,
    Select, Form, Checkbox
} from 'antd'

import { formItemLayout, PROJECT_ROLE } from '../../../comm/const'

const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group;

class EditRoleForm extends Component {
    render () {
        const { roles, form, user } = this.props;
        const getFieldDecorator = form.getFieldDecorator;

        const selectedRoles = user && user.roles
            ? user.roles.map(role => role.id) : [];

        let roleOptions = [];
        if (roles) {
            roles.forEach(role => {
                // 过滤项目所有者，租户所有者，访客三种无效的授权对象
                const disabled = role.roleValue === PROJECT_ROLE.PROJECT_OWNER ||
                    role.roleValue === PROJECT_ROLE.TENANT_OWVER ||
                    role.roleValue === PROJECT_ROLE.VISITOR
                roleOptions.push({ label: role.roleName, value: role.id, disabled })
            })
        }

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="请选择用户角色"
                >
                    {getFieldDecorator('roleIds', {
                        rules: [],
                        initialValue: selectedRoles
                    })(
                        <CheckboxGroup options={roleOptions} />
                    )}
                </FormItem>
            </Form>
        )
    }
}

const FormWrapper = Form.create()(EditRoleForm)

export default FormWrapper
