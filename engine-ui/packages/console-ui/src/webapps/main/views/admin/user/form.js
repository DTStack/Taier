import React, { Component } from 'react'
import { debounce } from 'lodash'

import {
    Select, Form, Checkbox
} from 'antd'

import utils from 'utils'

import {
    MY_APPS,
    RDOS_ROLE,
    APP_ROLE,
    formItemLayout
} from '../../../consts';

const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group;

// 过滤项目所有者，租户所有者，访客三种无效的授权对象
export const isDisabledRole = (app, value, loginUser, myRoles = {}) => {
    switch (app) {
        case MY_APPS.RDOS:
        case MY_APPS.STREAM: {
            if (loginUser.isTenantAdmin || myRoles.isProjectOwner) { // 租户管理员和项目拥有者
                return (value === RDOS_ROLE.PROJECT_OWNER ||
                value === RDOS_ROLE.TENANT_OWVER)
            } else if (myRoles.isProjectAdmin) { // 项目管理员
                return value === RDOS_ROLE.PROJECT_OWNER ||
                value === RDOS_ROLE.TENANT_OWVER ||
                value === RDOS_ROLE.PROJECT_ADMIN
            } else {
                return true;
            }
        }
        case MY_APPS.API:
        case MY_APPS.LABEL:
        case MY_APPS.ANALYTICS_ENGINE:
        case MY_APPS.DATA_QUALITY: {
            if (loginUser.isTenantAdmin) { // 租户管理员
                return value === APP_ROLE.TENANT_OWVER
            } else if (myRoles.isProjectAdmin) { // 产品管理员
                return (value === APP_ROLE.TENANT_OWVER || value === APP_ROLE.ADMIN)
            } else {
                return true;
            }
        }
        default: {
            return false;
        }
    }
}

class UserRoleForm extends Component {
    onSeachChange = (value) => {
        this.props.form.setFieldsValue({
            targetUserIds: value
        })
    }

    onSearch = (value) => {
        const query = utils.trim(value);
        this.props.onSearchUsers(query);
    }

    debounceSearch = debounce(this.onSearch, 300, { 'maxWait': 2000 })

    render () {
        const { roles, form, notProjectUsers, app, user, myRoles } = this.props;
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

        const roleOptions = [];
        const initialValue = [];

        if (roles) {
            roles.forEach(role => {
                const disabled = isDisabledRole(app, role.roleValue, user, myRoles)
                const isRdosOrStream = MY_APPS.RDOS == app || MY_APPS.STREAM == app

                if (role.roleValue == APP_ROLE.VISITOR && !isRdosOrStream) {
                    initialValue.push(role.id)
                } else if (role.roleValue == RDOS_ROLE.VISITOR && isRdosOrStream) {
                    initialValue.push(role.id)
                }

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
                            required: true, message: '用户不可为空！'
                        }]
                    })(
                        <Select
                            mode="multiple"
                            showSearch
                            showArrow={true}
                            style={{ width: '100%' }}
                            notFoundContent="当前用户不存在"
                            placeholder="请搜索并选择用户"
                            filterOption={(inputValue, option) => {
                                const val = utils.trim(inputValue);
                                return option.props.name.indexOf(val) > -1
                            }}
                            onChange={this.onSeachChange}
                            onSearch={this.debounceSearch}
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
                        initialValue
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

const FormWrapper = Form.create()(UserRoleForm)

export default FormWrapper
