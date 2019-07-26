import * as React from 'react'

import {
    Form, Checkbox
} from 'antd'

import { isDisabledRole } from './form'

const FormItem = Form.Item
const CheckboxGroup = Checkbox.Group;

const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
    }
}

class EditRoleForm extends React.Component<any, any> {
    componentDidMount () {
        this.setFields(this.props.user)
    }

    /* eslint-disable */
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        if (this.props.user !== nextProps.user) {
            this.setFields(nextProps.user)
        }
    }
    /* eslint-disable */

    setFields = (user) => {
        const selectedRoles = user && user.roles
            ? user.roles.map(role => role.id) : [];

        this.props.form.setFieldsValue({ roleIds: selectedRoles });
    }

    render () {
        const { roles, form, app, loginUser, myRoles } = this.props;
        const getFieldDecorator = form.getFieldDecorator;

        const roleOptions = [];

        if (roles) {
            roles.forEach(role => {
                const disabled = isDisabledRole(app, role.roleValue, loginUser, myRoles)

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
                        rules: [{
                            required: true,
                            message: '用户角色不可为空！'
                        }],
                        initialValue: []
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
