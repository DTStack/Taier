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
import { FileTypes } from '@dtinsight/molecule/esm/model';
import { Button, Form, Input, Modal, Radio, Select, Space, Upload } from 'antd';
import type { RcFile } from 'antd/lib/upload';

import api from '@/api';
import { CATALOGUE_TYPE, formItemLayout, RESOURCE_TYPE } from '@/constant';
import { IComputeType } from '@/interface';
import { catalogueService } from '@/services';
import resourceManagerTree from '@/services/resourceManagerService';
import { resourceNameMapping } from '@/utils/enums';
import FolderPicker from '../../components/folderPicker';

const FormItem = Form.Item;
const { Option } = Select;

export interface IFormFieldProps {
    /**
     * Only when editing
     */
    id?: number;
    originFileName?: string;
    /**
     * Only when adding
     */
    resourceName?: string;
    resourceType?: RESOURCE_TYPE;
    file?: RcFile;
    nodePid?: number;
    resourceDesc?: string;
    /**
     * 计算类型
     */
    computeType?: IComputeType;
}

interface IResModalProps {
    visible?: boolean;
    /**
     * 是否替换资源
     */
    isCoverUpload?: boolean;
    /**
     * 初始值设置
     */
    defaultValue?: IFormFieldProps;
    onClose?: () => void;
    onReplaceResource?: (values: IFormFieldProps) => Promise<boolean>;
    onAddResource?: (values: IFormFieldProps) => Promise<boolean>;
}

export default function ResModal({
    visible,
    isCoverUpload,
    defaultValue,
    onClose,
    onReplaceResource,
    onAddResource,
}: IResModalProps) {
    const [form] = Form.useForm<IFormFieldProps>();
    const [confirmLoading, setLoading] = useState(false);

    const handleSubmit = () => {
        form.validateFields().then((values) => {
            const params = { ...values };
            params.resourceDesc = values.resourceDesc || '';
            setLoading(true);
            if (isCoverUpload) {
                onReplaceResource?.(values)
                    .then((res) => {
                        if (res) {
                            onClose?.();
                            form.resetFields();
                        }
                    })
                    .finally(() => {
                        setLoading(false);
                    });
            } else {
                onAddResource?.(values)
                    .then((res) => {
                        if (res) {
                            onClose?.();
                            form.resetFields();
                        }
                    })
                    .finally(() => {
                        setLoading(false);
                    });
            }
        });
    };

    const handleFormValueChange = (changed: Partial<IFormFieldProps>) => {
        if ('id' in changed) {
            const node = resourceManagerTree.get(changed.id!);
            if (node?.fileType === FileTypes.File) {
                api.getOfflineRes({
                    resourceId: node.data.id,
                }).then((res) => {
                    if (res.code === 1) {
                        form.setFieldsValue({
                            originFileName: res.data.originFileName,
                            resourceType: res.data.resourceType,
                            computeType: res.data.computeType,
                        });
                    }
                });
            } else {
                form.resetFields(['originFileName', 'resourceType', 'computeType']);
            }
        }
    };

    /**
     * @description 检查所选是否为文件夹
     */
    const checkNotDir = (_: any, value: number) => {
        return resourceManagerTree.checkNotDir(value);
    };

    const validateFileType = (_: any, value: RcFile) => {
        if (!value) {
            return Promise.resolve();
        }
        const { resourceType: fileType } = form.getFieldsValue();
        const fileSuffix = resourceNameMapping(fileType);
        if (fileType === RESOURCE_TYPE.OTHER) {
            return Promise.resolve();
        }
        const suffix = value.name.split('.').slice(1).pop();
        if (fileSuffix.toLocaleLowerCase() !== suffix) {
            return Promise.reject(new Error(`资源文件只能是${fileSuffix}文件!`));
        }
        return Promise.resolve();
    };

    const renderFormItem = () => {
        if (!isCoverUpload) {
            return (
                <>
                    <FormItem
                        label="资源名称"
                        name="resourceName"
                        rules={[
                            {
                                required: true,
                                message: '资源名称不可为空!',
                            },
                            {
                                pattern: /^[A-Za-z0-9_-]+$/,
                                message: '资源名称只能由字母、数字、下划线组成!',
                            },
                            {
                                max: 20,
                                message: '资源名称不得超过20个字符!',
                            },
                        ]}
                    >
                        <Input placeholder="请输入资源名称" />
                    </FormItem>
                    <FormItem
                        label="资源类型"
                        name="resourceType"
                        rules={[
                            {
                                required: true,
                                message: '资源类型不可为空!',
                            },
                        ]}
                        initialValue={RESOURCE_TYPE.JAR}
                    >
                        <Select onChange={() => form.resetFields(['file'])}>
                            <Option value={RESOURCE_TYPE.JAR} key={RESOURCE_TYPE.JAR}>
                                {resourceNameMapping(RESOURCE_TYPE.JAR)}
                            </Option>
                            <Option value={RESOURCE_TYPE.PY} key={RESOURCE_TYPE.PY}>
                                {resourceNameMapping(RESOURCE_TYPE.PY)}
                            </Option>
                            <Option value={RESOURCE_TYPE.EGG} key={RESOURCE_TYPE.EGG}>
                                {resourceNameMapping(RESOURCE_TYPE.EGG)}
                            </Option>
                            <Option value={RESOURCE_TYPE.ZIP} key={RESOURCE_TYPE.ZIP}>
                                {resourceNameMapping(RESOURCE_TYPE.ZIP)}
                            </Option>
                            <Option value={RESOURCE_TYPE.OTHER} key={RESOURCE_TYPE.OTHER}>
                                {resourceNameMapping(RESOURCE_TYPE.OTHER)}
                            </Option>
                        </Select>
                    </FormItem>
                    <FormItem
                        label="上传"
                        required
                        shouldUpdate={(pre, cur) => pre.resourceType !== cur.resourceType || pre.file !== cur.file}
                    >
                        {({ getFieldValue }) => (
                            <>
                                <FormItem
                                    noStyle
                                    name="file"
                                    rules={[
                                        {
                                            required: true,
                                            message: '请选择上传文件',
                                        },
                                        {
                                            validator: validateFileType,
                                        },
                                    ]}
                                    valuePropName="file"
                                    getValueFromEvent={(e) => e.file}
                                >
                                    <Upload
                                        accept={
                                            getFieldValue('resourceType') !== RESOURCE_TYPE.OTHER
                                                ? `.${resourceNameMapping(getFieldValue('resourceType'))}`
                                                : undefined
                                        }
                                        beforeUpload={() => false}
                                        showUploadList={false}
                                    >
                                        <Button>选择文件</Button>
                                    </Upload>
                                </FormItem>
                                <span className="ml-5px">{getFieldValue('file')?.name}</span>
                            </>
                        )}
                    </FormItem>
                    <FormItem
                        name="computeType"
                        label="计算类型"
                        required
                        initialValue={IComputeType.STREAM}
                        tooltip="设置资源上传的计算组件类型"
                    >
                        <Radio.Group>
                            <Space>
                                <Radio value={IComputeType.STREAM}>STFP</Radio>
                                <Radio value={IComputeType.BATCH}>HDFS</Radio>
                            </Space>
                        </Radio.Group>
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
                        initialValue={catalogueService.getRootFolder(CATALOGUE_TYPE.RESOURCE)?.data?.id}
                    >
                        <FolderPicker dataType={CATALOGUE_TYPE.RESOURCE} showFile={false} />
                    </FormItem>
                    <FormItem
                        label="描述"
                        name="resourceDesc"
                        rules={[
                            {
                                max: 200,
                                message: '描述请控制在200个字符以内！',
                            },
                        ]}
                    >
                        <Input.TextArea rows={4} />
                    </FormItem>
                </>
            );
        }

        return (
            <>
                <FormItem
                    label="选择目标替换资源"
                    name="id"
                    rules={[
                        {
                            required: true,
                            message: '替换资源为必选！',
                        },
                        {
                            validator: checkNotDir,
                        },
                    ]}
                    initialValue={catalogueService.getRootFolder(CATALOGUE_TYPE.RESOURCE)?.data?.id}
                >
                    <FolderPicker dataType={CATALOGUE_TYPE.RESOURCE} showFile />
                </FormItem>
                <FormItem label="文件名" name="originFileName">
                    <Input disabled readOnly />
                </FormItem>
                <FormItem
                    label="资源类型"
                    name="resourceType"
                    rules={[
                        {
                            required: true,
                            message: '资源类型不可为空!',
                        },
                    ]}
                    initialValue={RESOURCE_TYPE.JAR}
                >
                    <Select onChange={() => form.resetFields(['file'])}>
                        <Option value={RESOURCE_TYPE.JAR} key={RESOURCE_TYPE.JAR}>
                            {resourceNameMapping(RESOURCE_TYPE.JAR)}
                        </Option>
                        <Option value={RESOURCE_TYPE.PY} key={RESOURCE_TYPE.PY}>
                            {resourceNameMapping(RESOURCE_TYPE.PY)}
                        </Option>
                        <Option value={RESOURCE_TYPE.EGG} key={RESOURCE_TYPE.EGG}>
                            {resourceNameMapping(RESOURCE_TYPE.EGG)}
                        </Option>
                        <Option value={RESOURCE_TYPE.ZIP} key={RESOURCE_TYPE.ZIP}>
                            {resourceNameMapping(RESOURCE_TYPE.ZIP)}
                        </Option>
                        <Option value={RESOURCE_TYPE.OTHER} key={RESOURCE_TYPE.OTHER}>
                            {resourceNameMapping(RESOURCE_TYPE.OTHER)}
                        </Option>
                    </Select>
                </FormItem>
                <FormItem name="computeType" label="计算类型" required tooltip="设置资源上传的计算组件类型">
                    <Radio.Group disabled>
                        <Space>
                            <Radio value={IComputeType.STREAM}>STFP</Radio>
                            <Radio value={IComputeType.BATCH}>HDFS</Radio>
                        </Space>
                    </Radio.Group>
                </FormItem>
                <FormItem
                    label="上传"
                    required
                    shouldUpdate={(pre, cur) => pre.resourceType !== cur.resourceType || pre.file !== cur.file}
                >
                    {({ getFieldValue }) => (
                        <>
                            <FormItem
                                noStyle
                                name="file"
                                rules={[
                                    {
                                        required: true,
                                        message: '请选择上传文件',
                                    },
                                    {
                                        validator: validateFileType,
                                    },
                                ]}
                                valuePropName="file"
                                getValueFromEvent={(e) => e.file}
                            >
                                <Upload
                                    accept={
                                        getFieldValue('resourceType') !== RESOURCE_TYPE.OTHER
                                            ? `.${resourceNameMapping(getFieldValue('resourceType'))}`
                                            : undefined
                                    }
                                    beforeUpload={() => false}
                                    showUploadList={false}
                                >
                                    <Button>选择文件</Button>
                                </Upload>
                            </FormItem>
                            <span className="ml-5px">{getFieldValue('file')?.name}</span>
                        </>
                    )}
                </FormItem>
                <FormItem
                    label="描述"
                    name="resourceDesc"
                    rules={[
                        {
                            max: 200,
                            message: '描述请控制在200个字符以内！',
                        },
                    ]}
                >
                    <Input.TextArea rows={4} />
                </FormItem>
            </>
        );
    };

    useEffect(() => {
        if (visible) {
            form.setFieldsValue({
                ...defaultValue,
            });
        }
    }, [visible, defaultValue]);

    return (
        <Modal
            title={isCoverUpload ? '替换资源' : '上传资源'}
            confirmLoading={confirmLoading}
            visible={visible}
            onCancel={onClose}
            onOk={handleSubmit}
            destroyOnClose
        >
            <Form
                preserve={false}
                form={form}
                onValuesChange={handleFormValueChange}
                autoComplete="off"
                {...formItemLayout}
            >
                {renderFormItem()}
            </Form>
        </Modal>
    );
}
