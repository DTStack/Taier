import React, { Component } from 'react'
import { debounce } from 'lodash'

import {
    Input, Button, Card, Radio,
    Select, Form, Checkbox,
} from 'antd'

import {
    formItemLayout,
    RDOS_PROJECT_ROLE,
    DQ_PROJECT_ROLE,
} from 'main/consts'

const FormItem = Form.Item
const Option = Select.Option
const RadioGroup = Radio.Group
const CheckboxGroup = Checkbox.Group;

class UserRoleForm extends Component {

    debounceSearch = debounce(this.props.onSearchUsers, 500, { 'maxWait': 2000 })

    render() {
        const { roles, form, notProjectUsers } = this.props;
        const getFieldDecorator = form.getFieldDecorator;

        const userOptions = notProjectUsers && notProjectUsers
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

        let roleOptions = [], defaultRoles = [];
        if (roles) {
            roles.forEach(role => {
                // 过滤项目所有者，租户所有者无效的授权对象，禁用访客默认授权角色
                // TODO 由于现在后端项目角色值每个应用对应的不一致，暂时用中文做对比
                if (role.roleName !== '租户所有者' &&
                role.roleName !== '项目所有者') {
                    const option = { label: role.roleName, value: role.id }
                    if (role.roleName === '访客') {
                        defaultRoles.push(role.id)
                        option.disabled = true;
                    }
                    roleOptions.push(option)
                }
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
                            required: true, message: '用户不可为空！',
                        }],
                    })(
                        <Select
                            mode="multiple"
                            style={{ width: '100%' }}
                            notFoundContent=""
                            placeholder="请选择用户"
                            onSearch={this.debounceSearch}
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
                        initialValue: defaultRoles,
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

const FormWrapper = Form.create()(UserRoleForm)

export default FormWrapper
