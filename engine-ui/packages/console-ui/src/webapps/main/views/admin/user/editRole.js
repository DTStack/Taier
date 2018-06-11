import React, { Component } from 'react'

import {
    Input, Button, Card,
    Select, Form, Checkbox,
 } from 'antd'

import { 
    RDOS_ROLE, 
    APP_ROLE,
    MY_APPS,
} from 'main/consts'

import { isDisabledRole } from './form'

const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group;

const formItemLayout={
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
    },
}


class EditRoleForm extends Component {

    componentDidMount() {
        this.setFields(this.props.user)
    }

    componentWillReceiveProps(nextProps) {
        if (this.props.user !== nextProps.user) {
            this.setFields(nextProps.user)
        }
    }

    setFields = (user) => {
        const selectedRoles = user && user.roles 
        ? user.roles.map(role => role.id) : [];
        this.props.form.setFieldsValue({ roleIds: selectedRoles });
    }

    render() {
        const { roles, form, app, loginUser } = this.props;
        const getFieldDecorator = form.getFieldDecorator;

        let roleOptions = [];
        if (roles) {
            roles.forEach(role => {
                const disabled = isDisabledRole(app, role.roleValue, loginUser)
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
                        initialValue: [],
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