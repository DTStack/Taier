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

import { useContext, useEffect, useMemo } from 'react';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import { Form,Input, message, Modal, Radio, Select } from 'antd';

import { CATALOGUE_TYPE, formItemLayout, TASK_TYPE_ENUM, UDF_TYPE_NAMES, UDF_TYPE_VALUES } from '@/constant';
import context from '@/context';
import type { IFunctionProps } from '@/interface';
import resourceManagerTree from '@/services/resourceManagerService';
import FolderPicker from '../../components/folderPicker';

const FormItem = Form.Item;

interface IFnModalProps {
    visible?: boolean;
    onClose?: () => void;
    data?: Partial<IFunctionProps>;
    onAddFunction?: (data: IFormFieldProps) => Promise<boolean>;
    onEditFunction?: (data: Partial<IFunctionProps>) => Promise<boolean>;
}

interface IFormFieldProps {
    taskType: TASK_TYPE_ENUM;
    udfType?: UDF_TYPE_VALUES;
    name?: string;
    className?: string;
    resourceId?: number;
    purpose?: string;
    commandFormate: string;
    paramDesc?: string;
    nodePid?: number;
}

const TASK_TYPE_OPTIONS = [TASK_TYPE_ENUM.SPARK_SQL, TASK_TYPE_ENUM.SQL, TASK_TYPE_ENUM.HIVE_SQL];

export default function FnModal({ data, visible, onClose, onAddFunction, onEditFunction }: IFnModalProps) {
    const { supportJobTypes } = useContext(context);
    const [form] = Form.useForm<IFormFieldProps>();

    const handleSubmit = () => {
        form.validateFields().then((values) => {
            if (data?.id !== undefined) {
                onEditFunction?.({ ...data, ...values }).then((res) => {
                    if (res) {
                        message.success('编辑成功');
                        onClose?.();
                    }
                });
            } else {
                onAddFunction?.({ ...values }).then((res) => {
                    if (res) {
                        message.success('创建成功');
                        onClose?.();
                    }
                });
            }
        });
    };

    const checkNotDir = (_: any, value: number) => {
        return resourceManagerTree.checkNotDir(value);
    };

    useEffect(() => {
        if (visible) {
            if (data) {
                form.setFieldsValue({
                    taskType: data?.taskType,
                    udfType: data?.taskType === TASK_TYPE_ENUM.SQL ? data?.udfType : undefined,
                    name: data?.name,
                    className: data?.className,
                    resourceId: data?.resources,
                    purpose: data?.purpose,
                    commandFormate: data?.commandFormate,
                    paramDesc: data?.paramDesc,
                    nodePid: data?.nodePid,
                });
            } else {
                form.resetFields();
            }
        }
    }, [visible, data]);

    const isEdit = useMemo(() => !!data?.id, [data]);
    const initialValues = useMemo<Partial<IFormFieldProps>>(
        () => ({
            taskType: TASK_TYPE_ENUM.SPARK_SQL,
        }),
        []
    );

    return (
        <Modal
            title={`${isEdit ? '编辑' : '新建'}自定义函数`}
            visible={visible}
            destroyOnClose
            onCancel={onClose}
            onOk={handleSubmit}
        >
            {isEdit && (
                <div className="task_offline_message">
                    <ExclamationCircleOutlined style={{ marginRight: 7 }} />
                    替换资源时，如果资源的新文件与现有文件名称保持一致，那么替换后关联函数对应任务可立即生效，否则关联函数对应任务需重新提交才可生效。
                </div>
            )}
            <Form<IFormFieldProps>
                {...formItemLayout}
                form={form}
                autoComplete="off"
                preserve={false}
                initialValues={initialValues}
            >
                <FormItem
                    label="函数类型"
                    name="taskType"
                    rules={[
                        {
                            required: true,
                            message: '函数类型不可为空！',
                        },
                    ]}
                >
                    <Select
                        disabled={isEdit}
                        getPopupContainer={() => document.getElementById('molecule')!}
                        options={TASK_TYPE_OPTIONS.map((o) => ({
                            label: supportJobTypes.find((t) => t.key === o)?.value || '未知',
                            value: o,
                        }))}
                    />
                </FormItem>
                <FormItem noStyle dependencies={['taskType']}>
                    {({ getFieldValue }) =>
                        getFieldValue('taskType') === TASK_TYPE_ENUM.SQL && (
                            <FormItem
                                name="udfType"
                                label="UDF类型"
                                rules={[
                                    {
                                        required: true,
                                        message: '请选择UDF类型',
                                    },
                                ]}
                                initialValue={UDF_TYPE_VALUES.UDF}
                            >
                                <Radio.Group disabled={isEdit}>
                                    {Object.entries(UDF_TYPE_NAMES).map(([key, value]) => (
                                        <Radio key={key} value={Number(key)}>
                                            {value}
                                        </Radio>
                                    ))}
                                </Radio.Group>
                            </FormItem>
                        )
                    }
                </FormItem>
                <FormItem
                    label="函数名称"
                    name="name"
                    rules={[
                        {
                            required: true,
                            message: '函数名称不可为空！',
                        },
                        {
                            pattern: /^[a-zA-Z0-9_]+$/,
                            message: '函数名称只能由字母、数字、下划线组成!',
                        },
                        {
                            max: 20,
                            message: '函数名称不得超过20个字符！',
                        },
                    ]}
                >
                    <Input placeholder="请输入函数名称" disabled={isEdit} />
                </FormItem>
                <FormItem
                    label="类名"
                    name="className"
                    rules={[
                        {
                            required: true,
                            message: '类名不能为空',
                        },
                        {
                            pattern: /^[a-zA-Z]+[0-9a-zA-Z_]*(\.[a-zA-Z]+[0-9a-zA-Z_]*)*$/,
                            message: '请输入有效的类名!',
                        },
                    ]}
                >
                    <Input placeholder="请输入类名" />
                </FormItem>
                <FormItem {...formItemLayout} label="资源" required>
                    <FormItem
                        noStyle
                        name="resourceId"
                        rules={[
                            {
                                required: true,
                                message: '请选择关联资源',
                            },
                            {
                                validator: checkNotDir,
                            },
                        ]}
                    >
                        <FolderPicker dataType={CATALOGUE_TYPE.RESOURCE} showFile />
                    </FormItem>
                </FormItem>
                <FormItem label="用途" name="purpose">
                    <Input placeholder="用途" />
                </FormItem>
                <FormItem
                    label="命令格式"
                    name="commandFormate"
                    rules={[
                        {
                            required: true,
                            message: '请输入命令格式',
                        },
                        {
                            max: 128,
                            message: '描述请控制在128个字符以内！',
                        },
                    ]}
                >
                    <Input placeholder="命令格式" />
                </FormItem>
                <FormItem
                    label="参数说明"
                    name="paramDesc"
                    rules={[
                        {
                            max: 200,
                            message: '描述请控制在200个字符以内！',
                        },
                    ]}
                >
                    <Input.TextArea rows={4} placeholder="请输入函数的参数说明" />
                </FormItem>
                <FormItem
                    name="nodePid"
                    label="选择存储位置"
                    rules={[
                        {
                            required: true,
                            message: '存储位置必选！',
                        },
                    ]}
                >
                    <FolderPicker showFile={false} dataType={CATALOGUE_TYPE.FUNCTION} />
                </FormItem>
            </Form>
        </Modal>
    );
}
