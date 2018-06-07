import React, { Component } from 'react'
import { debounce } from 'lodash'

import {
    Input, Button, Card, Radio,
    Select, Form, Checkbox,
} from 'antd'


import {
    MY_APPS,
    RDOS_ROLE,
    APP_ROLE,
    formItemLayout,
} from '../../../consts';

const FormItem = Form.Item
const Option = Select.Option
const RadioGroup = Radio.Group
const CheckboxGroup = Checkbox.Group;


// 过滤项目所有者，租户所有者，访客三种无效的授权对象
export const isDisabledRole = (app, value, loginUser) => {
    switch(app) {
        case MY_APPS.RDOS: {
            if (loginUser.isTenantAdmin) {
                return (value === RDOS_ROLE.PROJECT_OWNER ||
                value === RDOS_ROLE.TENANT_OWVER)
            } else {
                return value === RDOS_ROLE.PROJECT_OWNER ||
                value === RDOS_ROLE.TENANT_OWVER ||
                value === RDOS_ROLE.VISITOR
            }
        }
        case MY_APPS.API: 
        case MY_APPS.LABEL:
        case MY_APPS.DATA_QUALITY: {
            if (loginUser.isTenantAdmin) {
                return value === RDOS_ROLE.TENANT_OWVER 
            } else {
                return value === APP_ROLE.ADMIN ||
                value === APP_ROLE.TENANT_OWVER ||
                value === APP_ROLE.VISITOR
            }
        }
        default: {
            return false;
        }
    }
}

class UserRoleForm extends Component {

    debounceSearch = debounce(this.props.onSearchUsers, 300, { 'maxWait': 2000 })

    render() {
        const { roles, form, notProjectUsers, app, user } = this.props;
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

        let roleOptions = [];
        if (roles) {
            roles.forEach(role => {
                const disabled = isDisabledRole(app, role.roleValue, user)
                roleOptions.push({ label: role.roleName, value: role.id, disabled })
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
                            showSearch
                            showArrow={true}
                            style={{ width: '100%' }}
                            notFoundContent="当前用户不存在"
                            placeholder="请搜索并选择用户"
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
                        initialValue: [],
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
