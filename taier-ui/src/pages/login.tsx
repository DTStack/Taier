/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { useLayoutEffect,useState } from 'react';
import { Button, Checkbox, Form, Input, message, Modal, Select } from 'antd';

import api from '@/api';
import { formItemLayout } from '@/constant';
import { getCookie,getTenantId } from '@/utils';
import './login.scss';

const { Option } = Select;

interface IFormField {
    username: string;
    password: string;
}

interface ITenantProps {
    tenantId: number;
    tenantName: string;
}

/**
 * For storing the login modal visible
 */
const listener: Record<string, React.Dispatch<React.SetStateAction<boolean>>> = {};

/**
 * Execute this function to open login Modal everywhere
 */
export function showLoginModal() {
    listener.setVisible(true);
}

export default () => {
    const [curTenantId] = useState(getTenantId());
    const [isLogin, setLogin] = useState(() => !!getCookie('token'));
    const [isModalVisible, setVisible] = useState(false);
    const [submitLoading, setLoading] = useState(false);
    const [form] = Form.useForm<IFormField>();
    const [tenantForm] = Form.useForm<{
        change_ten_id: number;
        change_ten_isdefault: boolean;
    }>();
    const [tenants, setTenants] = useState<ITenantProps[]>([]);

    const getTenantList = async () => {
        const res = await api.getTenantList();
        if (res.code === 1) {
            setTenants(res.data);
        }
        return res;
    };

    const handleOk = () => {
        form.validateFields()
            .then((values) => {
                setLoading(true);
                return api.login(values);
            })
            .then((res) => {
                if (res.code === 1) {
                    return getTenantList();
                }
            })
            .then((res) => {
                if (res?.code === 1) {
                    const userId = getCookie('userId');
                    const defaultTenant = localStorage.getItem(`${userId}_default_tenant`);
                    const isValidTenant = (res.data as ITenantProps[]).some(
                        (t) => t.tenantId.toString() === defaultTenant
                    );
                    if (defaultTenant && isValidTenant) {
                        doTenantChange(Number(defaultTenant), true);
                    } else {
                        setLogin(true);
                    }
                }
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const handleCancel = () => {
        setVisible(false);
    };

    const handleTenantSubmit = () => {
        tenantForm.validateFields().then((values) => {
            doTenantChange(values.change_ten_id, values.change_ten_isdefault);
        });
    };

    const doTenantChange = (tenantId: number, isDefault: boolean) => {
        api.switchTenant({ tenantId }).then((res) => {
            if (res.code === 1) {
                if (isDefault) {
                    const userId = getCookie('userId');
                    // 保存租户信息
                    localStorage.setItem(`${userId}_default_tenant`, tenantId.toString());
                }
                setVisible(false);
                window.location.reload();
            } else {
                message.error(res.message);
            }
        });
    };

    useLayoutEffect(() => {
        listener.setVisible = setVisible;

        return () => {
            Reflect.deleteProperty(listener, 'setVisible');
        };
    }, []);

    useLayoutEffect(() => {
        if (isModalVisible && isLogin) {
            getTenantList();
        }
    }, [isModalVisible]);

    const renderLoginForm = () => {
        return (
            <Form<IFormField>
                form={form}
                hidden={isLogin}
                preserve={false}
                wrapperCol={{ span: 24 }}
                autoComplete="off"
                onFinish={handleOk}
            >
                <Form.Item
                    label=""
                    name="username"
                    rules={[
                        {
                            type: 'email',
                            message: '请输入正确格式的邮箱账号',
                        },
                        {
                            required: true,
                            message: '账号不能为空',
                        },
                    ]}
                >
                    <Input className="dt-input-borderless" placeholder="请输入注册账号" bordered={false} />
                </Form.Item>
                <Form.Item
                    label=""
                    name="password"
                    rules={[
                        {
                            required: true,
                            message: '密码不能为空',
                        },
                    ]}
                >
                    <Input.Password className="dt-input-borderless" placeholder="请输入密码" bordered={false} />
                </Form.Item>
                <Form.Item>
                    <Button className="dt-button" loading={submitLoading} block type="primary" htmlType="submit">
                        登录
                    </Button>
                </Form.Item>
            </Form>
        );
    };

    const renderTenantForm = () => {
        return (
            <Form
                form={tenantForm}
                hidden={!isLogin}
                preserve={false}
                {...formItemLayout}
                autoComplete="off"
                initialValues={{
                    change_ten_id: curTenantId === null ? undefined : Number(curTenantId),
                    change_ten_isdefault: !!localStorage.getItem(`${getCookie('userId')}_default_tenant`),
                }}
                onFinish={handleTenantSubmit}
            >
                <Form.Item label="租户名称" name="change_ten_id">
                    <Select style={{ width: '100%' }} showSearch placeholder="请选择租户">
                        {tenants.map((o) => (
                            <Option key={o.tenantId} value={o.tenantId}>
                                {o.tenantName}
                            </Option>
                        ))}
                    </Select>
                </Form.Item>
                <Form.Item
                    name="change_ten_isdefault"
                    valuePropName="checked"
                    wrapperCol={{
                        offset: formItemLayout.labelCol.sm.span,
                        span: 16,
                    }}
                >
                    <Checkbox
                        style={{
                            width: 310,
                            fontSize: 12,
                            color: '#666666',
                            fontWeight: 'normal',
                        }}
                    >
                        是否默认进入该租户
                    </Checkbox>
                </Form.Item>
                <Form.Item
                    wrapperCol={{
                        offset: formItemLayout.labelCol.sm.span,
                        span: 16,
                    }}
                >
                    <Button className="dt-button" loading={submitLoading} block type="primary" htmlType="submit">
                        确认
                    </Button>
                </Form.Item>
            </Form>
        );
    };

    return (
        <Modal
            className="dt-login"
            title={isLogin ? '请选择租户' : '欢迎登录 Taier'}
            visible={isModalVisible}
            footer={null}
            destroyOnClose
            onCancel={handleCancel}
        >
            {renderTenantForm()}
            {renderLoginForm()}
        </Modal>
    );
};
