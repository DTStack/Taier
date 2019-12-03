import * as React from 'react'

import {
    Form, Checkbox
} from 'antd'

import { formItemLayout, API_PRO_ROLES } from '../../../consts'

const FormItem = Form.Item
const CheckboxGroup = Checkbox.Group;

class EditRoleForm extends React.Component<any, any> {
    render () {
        const { roles, form, user } = this.props;
        const getFieldDecorator = form.getFieldDecorator;
        const selectedRoles = user && user.roles
            ? user.roles.map((role: any) => role && role.id) : [];
        let roleOptions: any = [];
        if (roles) {
            roles.forEach((role: any) => {
                const disabled = role.roleValue === API_PRO_ROLES.TENANT_OWVER ||
                    role.roleValue === API_PRO_ROLES.PRO_OWNER
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

const FormWrapper = Form.create<any>()(EditRoleForm)

export default FormWrapper
