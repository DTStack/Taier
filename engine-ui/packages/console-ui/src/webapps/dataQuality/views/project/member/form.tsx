import * as React from 'react'

import {
    Select, Form, Checkbox
} from 'antd'

import { formItemLayout, PROJECT_ROLE } from '../../../consts'

const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group;

class MemberForm extends React.Component<any, any> {
    roleChange: any;
    render () {
        const { roles, form, notProjectUsers } = this.props;
        const getFieldDecorator = form.getFieldDecorator;

        const userOptions = notProjectUsers && notProjectUsers
            .map((item: any) => {
                const nameFix = {
                    name: item.userName
                }
                return (
                    <Option
                        key={item.userId}
                            // name={item.userName}
                        value={`${item.userId}`}
                        {...nameFix}
                    >
                        {item.userName}
                    </Option>
                )
            }
            )

        let roleOptions: any = [];
        let defaultRoles: any = [];
        if (roles) {
            roles.forEach((role: any) => {
                // 置灰项目所有者，租户所有者两种授权对象
                const option: any = { label: role.roleName, value: role.id }
                if (role.roleValue === PROJECT_ROLE.PROJECT_OWNER ||
                    role.roleValue === PROJECT_ROLE.TENANT_OWVER ||
                    role.roleValue === PROJECT_ROLE.VISITOR) {
                    option.disabled = true;
                }
                if (role.roleValue === PROJECT_ROLE.VISITOR) {
                    defaultRoles.push(role.id)
                }
                roleOptions.push(option)
            })
        }

        return (
            <Form>
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
                            style={{ width: '100%' }}
                            placeholder="请选择用户"
                            optionFilterProp="name"
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
                        initialValue: defaultRoles
                    })(
                        <CheckboxGroup
                            options={roleOptions}
                            onChange={this.roleChange}
                        />
                    )}
                </FormItem>
            </Form>
        )
    }
}

const FormWrapper = Form.create<any>()(MemberForm)

export default FormWrapper
