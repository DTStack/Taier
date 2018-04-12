import React, { Component } from 'react'

import {
    Input, Button, Card,
    Select, Form, Checkbox,
 } from 'antd'

import { 
    formItemLayout, 
    RDOS_PROJECT_ROLE, 
    DQ_PROJECT_ROLE,
    MY_APPS,
} from 'main/consts'
 
const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group;

// 过滤项目所有者，租户所有者，访客三种无效的授权对象
const isDisabled = (app, value) => {
    switch(app) {
        case MY_APPS.RDOS: {
            return value === RDOS_PROJECT_ROLE.PROJECT_OWNER ||
            value === RDOS_PROJECT_ROLE.TENANT_OWVER ||
            value === RDOS_PROJECT_ROLE.VISITOR
        }
        case MY_APPS.DATA_QUALITY: {
            return value === DQ_PROJECT_ROLE.ADMIN ||
            value === DQ_PROJECT_ROLE.VISITOR
        }
        default: {
            return false;
        }
    }

}

class EditRoleForm extends Component {

    render() {
        const { roles, form, user, app } = this.props;
        const getFieldDecorator = form.getFieldDecorator;

        const selectedRoles = user && user.roles 
        ? user.roles.map(role => role.id) : [];

        let roleOptions = [];
        if (roles) {
            roles.forEach(role => {
                const disabled = isDisabled(app, role.roleValue)
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
                        initialValue: selectedRoles,
                    })(
                        <CheckboxGroup options={roleOptions} />,
                    )}
                </FormItem>
            </Form>
        )
    }

}

const FormWrapper = Form.create()(EditRoleForm)

export default FormWrapper