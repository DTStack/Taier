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

import React, { useEffect, useState } from 'react';
import { Form,message, Modal, Select } from 'antd';

import api from '@/api';
import { formItemLayout } from '@/constant';
import './index.scss';

const { Option } = Select;
const FormItem = Form.Item;
const { confirm } = Modal;

interface IResourceManageModalProps {
    title?: React.ReactNode;
    visible: boolean;
    isBindTenant?: boolean;
    /**
     * 资源队列默认值
     */
    queueName?: string;
    clusterId: number;
    tenantId?: number;
    onCancel?: () => void;
    onOk?: () => void;
}

export default ({
    title,
    visible,
    queueName,
    clusterId,
    tenantId,
    isBindTenant = false,
    onCancel,
    onOk,
}: IResourceManageModalProps) => {
    const [form] = Form.useForm();
    const [isLoading, setLoading] = useState(false);
    const [queueList, setQueueList] = useState<string[]>([]);

    const getQueueList = () => {
        api.getClusterResources({
            clusterId,
        }).then((res) => {
            if (res.code === 1) {
                setQueueList(res.data.queues?.map((i: any) => i.queueName) || []);
            }
        });
    };

    const getServiceParam = () => {
        form.validateFields()
            .then((values) => {
                setLoading(true);
                api.switchQueue({
                    queueName: values.queueName,
                    tenantId,
                    clusterId,
                })
                    .then((res) => {
                        if (res.code === 1) {
                            message.success('提交成功');
                            onOk?.();
                        }
                    })
                    .finally(() => {
                        setLoading(false);
                    });
            })
            .catch(() => {});
    };

    const handleCancel = () => {
        confirm({
            title: '是否保存配置？',
            content: '若不保存编辑内容，再次打开时会进行重置',
            okText: '保存',
            cancelText: '返回',
            onOk() {
                getServiceParam();
            },
            onCancel() {
                onCancel?.();
            },
        });
    };

    useEffect(() => {
        if (visible) {
            getQueueList();
        }
    }, [visible]);

    return (
        <Modal
            title={title}
            visible={visible}
            onOk={() => getServiceParam()}
            onCancel={handleCancel}
            destroyOnClose
            width="600px"
            confirmLoading={isLoading}
            className={isBindTenant ? 'no-padding-modal' : ''}
        >
            <Form form={form} preserve={false}>
                <FormItem
                    label="资源队列"
                    tooltip="指Yarn上分配的资源队列，若下拉列表中无全部队列，请前往“多集群管理”页面的具体集群中刷新集群"
                    {...formItemLayout}
                    name="queueName"
                    rules={[
                        {
                            required: true,
                            message: '资源队列不可为空！',
                        },
                    ]}
                    initialValue={queueName || undefined}
                >
                    <Select allowClear placeholder="请选择资源队列">
                        {queueList.map((item) => {
                            return (
                                <Option key={item} value={item}>
                                    {item}
                                </Option>
                            );
                        })}
                    </Select>
                </FormItem>
            </Form>
        </Modal>
    );
};
