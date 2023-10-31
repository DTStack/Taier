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
import molecule from '@dtinsight/molecule/esm';
import { connect } from '@dtinsight/molecule/esm/react';
import { Button, Form,Input } from 'antd';

import { CATALOGUE_TYPE, formItemLayout, tailFormItemLayout } from '@/constant';
import type { CatalogueDataProps } from '@/interface';
import FolderPicker from '../../components/folderPicker';
import './editFolder.scss';

interface IEditFolderProps extends molecule.model.IEditor {
    onSubmitFolder?: (values: IFormFieldProps) => Promise<boolean>;
    /**
     * Current editing catalogue
     */
    record?: CatalogueDataProps;
    tabId: string | number;
}

interface IFormFieldProps {
    /**
     * To replace nodeName for while
     */
    dt_nodeName?: string;
    /**
     * React issues refer: https://github.com/facebook/react/issues/6284
     */
    nodeName?: string;
    nodePid: number;
}

const FormItem = Form.Item;

export default connect(molecule.editor, ({ onSubmitFolder, current, record, tabId }: IEditFolderProps) => {
    const [form] = Form.useForm<IFormFieldProps>();
    const [loading, setLoading] = useState(false);

    const handleSubmit = (values: IFormFieldProps) => {
        setLoading(true);
        onSubmitFolder?.({ nodeName: values.dt_nodeName, nodePid: values.nodePid }).then((success) => {
            setLoading(success);
        });
    };

    useEffect(() => {
        const currentTab = current?.tab;

        if (currentTab) {
            if (currentTab.data.id) {
                // restoring the data into form
                form.setFieldsValue({
                    dt_nodeName: currentTab.data.dt_nodeName,
                    nodePid: currentTab.data.nodePid,
                });
            }
            return () => {
                // storing current data into tab
                molecule.editor.updateTab({
                    ...currentTab,
                    data: {
                        ...currentTab.data,
                        ...form.getFieldsValue(),
                        id: record?.id ?? tabId,
                    },
                });
            };
        }
    }, []);

    return (
        <molecule.component.Scrollbar>
            <Form form={form} onFinish={handleSubmit} className="mo-open-task">
                <FormItem
                    {...formItemLayout}
                    label="目录名称"
                    name="dt_nodeName"
                    rules={[
                        {
                            max: 64,
                            message: '目录名称不得超过64个字符！',
                        },
                        {
                            required: true,
                            message: '文件夹名称不能为空',
                        },
                    ]}
                >
                    <Input autoComplete={'off'} />
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="选择目录位置"
                    name="nodePid"
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <FolderPicker showFile={false} dataType={CATALOGUE_TYPE.TASK} />
                </FormItem>
                <FormItem {...tailFormItemLayout}>
                    <Button type="primary" htmlType="submit" loading={loading}>
                        确认
                    </Button>
                </FormItem>
            </Form>
        </molecule.component.Scrollbar>
    );
});
