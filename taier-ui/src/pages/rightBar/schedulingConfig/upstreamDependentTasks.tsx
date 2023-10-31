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

import { useEffect, useState } from 'react';
import { Empty, Form, Modal, Select, Spin } from 'antd';
import type { FormInstance } from 'antd/lib/form/Form';
import { debounce } from 'lodash';

import api from '@/api';
import { formItemLayout } from '@/constant';
import { getCookie } from '@/utils';

const FormItem = Form.Item;
const { Option } = Select;

interface IUpstreamTaskProps {
    form: FormInstance;
    taskId: number;
    submitData: (task: ITaskSearchResultProps) => void;
    onCancel: () => void;
    visible: boolean;
}

/**
 * 任务搜索结果类型
 */
export interface ITaskSearchResultProps {
    taskId: number;
    taskName: string;
    tenantId: number;
    tenantName: string;
}

interface ITenantProps {
    tenantName: string;
    tenantId: number;
}

export default function UpstreamDependentTasks({
    form,
    taskId: currentTaskId,
    submitData,
    visible,
    onCancel,
}: IUpstreamTaskProps) {
    const [tenants, setTenants] = useState<ITenantProps[]>([]);
    const [tasks, setTasks] = useState<ITaskSearchResultProps[]>([]);
    const [fetching, setFetching] = useState(false);

    const changeTenant = () => {
        form.setFieldsValue({
            taskId: undefined,
        });
    };

    const handleSearch = (value: string) => {
        setFetching(true);
        api.allProductGlobalSearch({
            taskName: value,
            selectTenantId: form.getFieldValue('tenantId'),
            taskId: currentTaskId,
        })
            .then((res) => {
                if (res.code === 1) {
                    setTasks(res.data || []);
                }
            })
            .finally(() => {
                setFetching(false);
            });
    };

    const handleSubmit = () => {
        form.validateFields().then(({ taskId }) => {
            const task = tasks.find((t) => t.taskId === taskId);
            if (task) {
                submitData(task);
            }
        });
    };

    useEffect(() => {
        api.getTenantList().then((res) => {
            if (res.code === 1) {
                setTenants(res.data);
            }
        });
        // Get the default value
        handleSearch('');
    }, []);

    return (
        <Modal visible={visible} title="添加上游依赖任务" onOk={handleSubmit} onCancel={onCancel}>
            <Form form={form}>
                <FormItem
                    {...formItemLayout}
                    label="所属租户"
                    name="tenantId"
                    rules={[{ required: true, message: '请选择所属租户!' }]}
                    initialValue={Number(getCookie('tenantId'))}
                >
                    <Select<number> onChange={changeTenant}>
                        {tenants.map((tenantItem) => {
                            return (
                                <Option key={tenantItem.tenantId} value={tenantItem.tenantId}>
                                    {tenantItem.tenantName}
                                </Option>
                            );
                        })}
                    </Select>
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="任务"
                    required
                    name="taskId"
                    rules={[{ required: true, message: '请选择任务!' }]}
                >
                    <Select
                        showSearch
                        placeholder="请输入任务名称搜索"
                        style={{ width: '100%' }}
                        defaultActiveFirstOption={false}
                        showArrow={false}
                        filterOption={false}
                        onSearch={debounce(handleSearch, 500, { maxWait: 2000 })}
                        notFoundContent={fetching ? <Spin spinning /> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
                    >
                        {tasks.map((task) => (
                            <Option key={task.taskId} value={task.taskId}>
                                {task.taskName}
                            </Option>
                        ))}
                    </Select>
                </FormItem>
            </Form>
        </Modal>
    );
}
