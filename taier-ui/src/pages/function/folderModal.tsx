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

import React, { useMemo, useRef, useState } from 'react';
import type { FormInstance } from 'antd';
import { Button, Form,Input, Modal } from 'antd';
import assign from 'object-assign';

import type { CATALOGUE_TYPE } from '@/constant';
import { formItemLayout } from '@/constant';
import type { CatalogueDataProps } from '@/interface';
import FolderPicker from '../../components/folderPicker';

const FormItem = Form.Item;

interface IFormFieldProps {
    dt_nodeName: string;
    nodePid: number;
}

interface IFolderFormProps {
    form: FormInstance<IFormFieldProps>;
    dataType: CATALOGUE_TYPE;
    defaultData: IFolderModalProps['defaultData'];
    treeData: IFolderModalProps['treeData'];
}

interface IFolderModalProps {
    isModalShow: boolean;
    /**
     * 如果不存在该数据，则说明是根目录新建
     * 如果存在该数据，且存在 id，name 则为编辑
     * 如果存在该数据，但不存在 id 而存在 parentId 则表示从某一个目录新建
     */
    defaultData: Partial<Pick<CatalogueDataProps, 'id' | 'parentId' | 'name'>> | undefined;
    /**
     * 后端传过来的当前类别下的所有目录数据
     */
    treeData: CatalogueDataProps;
    dataType: CATALOGUE_TYPE;
    toggleCreateFolder: () => void;
    addOfflineCatalogue: (values: { nodeName: string; nodePid: number }) => Promise<boolean>;
    editOfflineCatalogue: (values: { nodeName: string; nodePid: number; id: number; type: string }) => Promise<boolean>;
}

function FolderForm({ form, defaultData, dataType, treeData }: IFolderFormProps) {
    const handleSelectTreeChange = (value: number) => {
        form.setFieldsValue({ nodePid: value });
    };

    // 没有默认数据
    const isCreateNormal = typeof defaultData === 'undefined';

    return (
        <Form form={form} preserve={false}>
            {/* 这里不能直接叫nodeName refer: https://github.com/facebook/react/issues/6284 */}
            <FormItem
                key="dt_nodeName"
                label="目录名称"
                {...formItemLayout}
                name="dt_nodeName"
                rules={[
                    {
                        max: 20,
                        message: '目录名称不得超过20个字符！',
                    },
                    {
                        required: true,
                        message: '文件夹名称不能为空',
                    },
                ]}
                initialValue={isCreateNormal ? undefined : defaultData.name}
            >
                <Input type="text" placeholder="文件夹名称" />
            </FormItem>
            <FormItem key="nodePid" label="选择目录位置" {...formItemLayout}>
                <FormItem
                    noStyle
                    name="nodePid"
                    rules={[
                        {
                            required: true,
                            message: '请选择目录位置',
                        },
                    ]}
                    initialValue={isCreateNormal ? treeData.id : defaultData.parentId}
                >
                    <Input type="hidden" />
                </FormItem>
                <FolderPicker
                    showFile={false}
                    dataType={dataType}
                    defaultValue={isCreateNormal ? treeData.id : defaultData.parentId}
                    onChange={handleSelectTreeChange}
                />
            </FormItem>
        </Form>
    );
}

let dtcount = 0;
export default function FolderModal({
    isModalShow,
    defaultData,
    treeData,
    dataType,
    toggleCreateFolder,
    addOfflineCatalogue,
    editOfflineCatalogue,
}: IFolderModalProps) {
    const [form] = Form.useForm<IFormFieldProps>();
    const [loading, setLoading] = useState(false);
    const wrapper = useRef<HTMLDivElement>(null);

    const isCreate = useMemo(() => {
        if (!defaultData) return true;
        return !defaultData.name;
    }, [defaultData]);

    const handleSubmit = () => {
        setLoading(true);
        form.validateFields().then((values) => {
            const params = {
                nodeName: values.dt_nodeName,
                nodePid: values.nodePid,
            };
            if (isCreate) {
                addOfflineCatalogue(params)
                    .then((success) => {
                        if (success) {
                            closeModal();
                            form.resetFields();
                        }
                    })
                    .finally(() => {
                        setLoading(false);
                    });
            } else {
                editOfflineCatalogue(
                    assign(params, {
                        id: defaultData!.id!,
                        type: 'folder', // 文件夹编辑，新增参数固定为folder
                    })
                )
                    .then((success) => {
                        if (success) {
                            closeModal();
                            form.resetFields();
                        }
                    })
                    .finally(() => {
                        setLoading(false);
                    });
            }
        });
    };

    const closeModal = () => {
        toggleCreateFolder();
        dtcount += 1;
    };

    const handleCancel = () => {
        closeModal();
    };

    return (
        <div ref={wrapper}>
            <Modal
                destroyOnClose
                title={!isCreate ? '编辑文件夹' : '新建文件夹'}
                visible={isModalShow}
                key={dtcount}
                footer={[
                    <Button key="back" size="large" onClick={handleCancel}>
                        取消
                    </Button>,
                    <Button key="submit" type="primary" size="large" onClick={handleSubmit} loading={loading}>
                        确认
                    </Button>,
                ]}
                onCancel={handleCancel}
                getContainer={() => wrapper.current!}
            >
                <FolderForm form={form} treeData={treeData} dataType={dataType} defaultData={defaultData} />
            </Modal>
        </div>
    );
}
