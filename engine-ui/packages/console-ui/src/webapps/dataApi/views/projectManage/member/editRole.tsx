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

const FormWrapper = Form.create<any>()(EditRoleForm)

export default FormWrapper
