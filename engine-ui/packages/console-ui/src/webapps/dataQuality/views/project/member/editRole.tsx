import * as React from 'react'

import {
    Form, Checkbox
} from 'antd'

import { formItemLayout, PROJECT_ROLE } from '../../../consts'

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
                // 置灰项目所有者，租户所有者两种授权对象
                const option: any = { label: role.roleName, value: role.id }
                if (role.roleValue === PROJECT_ROLE.PROJECT_OWNER ||
                    role.roleValue === PROJECT_ROLE.TENANT_OWVER) {
                    option.disabled = true;
                }
                roleOptions.push(option)
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
