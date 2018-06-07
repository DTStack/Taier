import React, { Component } from 'react'
import { debounce } from 'lodash'

import {
    Input, Button, Card, Radio,
    Select, Form, Checkbox,
 } from 'antd'

import { formItemLayout, PROJECT_ROLE } from '../../../comm/const'
 
const FormItem = Form.Item
const Option = Select.Option
const RadioGroup = Radio.Group
const CheckboxGroup = Checkbox.Group;


class MemberForm extends Component {


    debounceSearch = debounce(this.props.onSearchUsers, 300, { 'maxWait': 2000 })

    render() {
        const { roles, form, notProjectUsers } = this.props;
        const getFieldDecorator = form.getFieldDecorator;

        const userOptions = notProjectUsers && notProjectUsers
        .map(item => 
            <Option 
                key={item.userId}
                name={item.userName}
                value={`${item.userId}`}
            >
                {item.userName}
            </Option>
        )

        let roleOptions = [], defaultRoles = [];
        if (roles) {
            roles.forEach(role => {
                // 过滤项目所有者，租户所有者，访客三种无效的授权对象
                if (role.roleValue !== PROJECT_ROLE.PROJECT_OWNER &&
                    role.roleValue !== PROJECT_ROLE.TENANT_OWVER) {
                    const option = { label: role.roleName, value: role.id }

                    if (role.roleValue === PROJECT_ROLE.VISITOR) {
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
                            placeholder="请选择用户"
                            optionFilterProp="name"
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

const FormWrapper = Form.create()(MemberForm)

export default FormWrapper