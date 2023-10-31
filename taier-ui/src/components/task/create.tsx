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

import { useContext, useEffect, useMemo, useState } from 'react';
import molecule from '@dtinsight/molecule/esm';
import { connect } from '@dtinsight/molecule/esm/react';
import { Button, Empty,Form, Input, Select, Spin } from 'antd';

import api from '@/api';
import {
    CATALOGUE_TYPE,
    CREATE_MODEL_TYPE,
    DATA_SYNC_MODE,
    FLINK_VERSIONS,
    PythonVersionKind,
    TASK_TYPE_ENUM,
} from '@/constant';
import Context from '@/context';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import { taskRenderService } from '@/services';
import FolderPicker from '../folderPicker';
import './create.scss';

const FormItem = Form.Item;

interface ICreateProps extends molecule.model.IEditor {
    onSubmit?: (values: ICreateTaskFormFieldProps) => Promise<boolean>;
    /**
     * Only in editing
     */
    record?: CatalogueDataProps | IOfflineTaskProps;
    /**
     * 是否请求远程接口展示数据，如果设置成 false，则会拿 record 中的数据
     */
    isRequest?: boolean;
    /**
     * 是否渲染存储位置
     */
    isRenderPosition?: boolean;
}

export interface ICreateTaskFormFieldProps {
    name: string;
    taskType: TASK_TYPE_ENUM;
    nodePid: number;
    taskDesc: string;
    sourceMap?: {
        syncModel: DATA_SYNC_MODE;
    };
    createModel?: Valueof<typeof CREATE_MODEL_TYPE>;
    sqlText?: string;
    resourceIdList?: [number];
    mainClass?: string;
    exeArgs?: string;
    pythonVersion?: PythonVersionKind;
    componentVersion: Valueof<typeof FLINK_VERSIONS>;
}

const Create = connect(
    molecule.editor,
    ({ onSubmit, record, current, isRequest = true, isRenderPosition = true }: ICreateProps) => {
        const { supportJobTypes } = useContext(Context);
        const [form] = Form.useForm<ICreateTaskFormFieldProps>();
        const [loading, setLoading] = useState(false);
        const [pageLoading, setPageLoading] = useState(false);

        const getCurrentTaskInfo = () => {
            if (record) {
                if (isRequest) {
                    setPageLoading(true);
                    api.getOfflineTaskByID({ id: record.id })
                        .then((res) => {
                            if (res.code === 1) {
                                // 如果发现 syncModel 字段放到旧版本字段的位置上了，则给一个提示
                                const isTruncate =
                                    res.data.sourceMap?.syncModel === undefined && res.data.syncModel !== undefined;
                                form.setFields([
                                    {
                                        name: 'syncModel',
                                        touched: false,
                                        validating: false,
                                        errors: [isTruncate ? '由于版本更新，需要重新设置该字段' : ''],
                                        value: res.data.sourceMap?.syncModel,
                                    },
                                ]);

                                // 设置所有类型都需要的字段
                                form.setFieldsValue({
                                    name: res.data.name,
                                    taskDesc: res.data.taskDesc,
                                    sqlText: res.data.sqlText,
                                    taskType: res.data.taskType,
                                });

                                // 获取当前类型在新建的时候所需要的字段
                                const formFields = taskRenderService
                                    .getState()
                                    .supportTaskList.find((i) => i.key === res.data.taskType);

                                if (formFields) {
                                    formFields.taskProperties.formField?.forEach((field) => {
                                        // 特殊处理
                                        if (field === 'syncModel') {
                                            form.setFieldsValue({
                                                sourceMap: {
                                                    syncModel: res.data.sourceMap?.syncModel,
                                                },
                                            });
                                        } else if (field === 'pythonVersion') {
                                            try {
                                                const pyVersion: string | undefined = JSON.parse(res.data.exeArgs)?.[
                                                    '--app-type'
                                                ];

                                                const version = pyVersion?.endsWith('3')
                                                    ? PythonVersionKind.py3
                                                    : PythonVersionKind.py2;
                                                form.setFieldsValue({
                                                    pythonVersion: version,
                                                });
                                            } catch {}
                                        } else {
                                            const mapping: Record<string, string> = {
                                                datasource: 'datasourceId',
                                            };
                                            form.setFieldsValue({
                                                [mapping[field]]: res.data[mapping[field]],
                                            });
                                        }
                                    });
                                }
                            }
                        })
                        .finally(() => {
                            setPageLoading(false);
                        });
                } else {
                    form.setFieldsValue({
                        ...(record as ICreateTaskFormFieldProps),
                    });
                }
            }
        };

        const handleSubmit = (values: ICreateTaskFormFieldProps) => {
            setLoading(true);
            onSubmit?.({ ...values }).then((success) => {
                setLoading(success);
            });
        };

        const handleValuesChanged = (_: Partial<ICreateTaskFormFieldProps>, values: ICreateTaskFormFieldProps) => {
            if (current?.tab) {
                const { id } = current.tab;
                // Insert form values into tab for preventing losing the values when switch tabs
                molecule.editor.updateTab({
                    id,
                    data: { ...current.tab.data, ...values },
                    status: 'edited',
                });
            }
        };

        useEffect(() => {
            getCurrentTaskInfo();
        }, []);

        const initialValues = useMemo(() => {
            if (current?.tab) {
                const { data } = current.tab;
                const { nodePid, ...restData } = data;
                return {
                    nodePid: nodePid?.toString().split('-')[0],
                    ...restData,
                };
            }
            return undefined;
        }, []);

        return (
            <div className="taier__create__container">
                <div className="taier__create__wrapper">
                    <Spin spinning={pageLoading}>
                        <Form<ICreateTaskFormFieldProps>
                            form={form}
                            onFinish={handleSubmit}
                            onValuesChange={handleValuesChanged}
                            initialValues={initialValues}
                            autoComplete="off"
                            className="taier__create__form"
                            layout="vertical"
                        >
                            <FormItem
                                label="任务名称"
                                name="name"
                                rules={[
                                    {
                                        required: true,
                                        message: `任务名称不可为空！`,
                                    },
                                    {
                                        max: 128,
                                        message: `任务名称不得超过128个字符！`,
                                    },
                                    {
                                        pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/,
                                        message: `任务名称只能由字母、数字、中文、下划线组成!`,
                                    },
                                ]}
                            >
                                <Input placeholder="请输入任务名称" />
                            </FormItem>
                            <FormItem
                                label="任务类型"
                                name="taskType"
                                rules={[
                                    {
                                        required: true,
                                        message: `请选择任务类型`,
                                    },
                                ]}
                            >
                                <Select<string>
                                    placeholder="请选择任务类型"
                                    disabled={!!record}
                                    showSearch
                                    getPopupContainer={(node) => node.parentNode}
                                    optionFilterProp="label"
                                    notFoundContent={<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
                                    options={supportJobTypes.map((t) => ({
                                        label: t.value,
                                        value: t.key,
                                    }))}
                                />
                            </FormItem>
                            <FormItem noStyle dependencies={['taskType']}>
                                {(currentForm) =>
                                    taskRenderService.renderCreateForm(
                                        currentForm.getFieldValue('taskType'),
                                        record,
                                        currentForm
                                    )
                                }
                            </FormItem>
                            {isRenderPosition && (
                                <FormItem
                                    label="存储位置"
                                    name="nodePid"
                                    rules={[
                                        {
                                            required: true,
                                            message: '存储位置必选！',
                                        },
                                    ]}
                                    initialValue={molecule.folderTree.getState().folderTree?.data?.[0].id}
                                >
                                    <FolderPicker showFile={false} dataType={CATALOGUE_TYPE.TASK} />
                                </FormItem>
                            )}
                            <FormItem
                                label="描述"
                                name="taskDesc"
                                rules={[
                                    {
                                        max: 200,
                                        message: '描述请控制在200个字符以内！',
                                    },
                                ]}
                            >
                                <Input.TextArea placeholder="请输入描述" disabled={false} rows={4} />
                            </FormItem>
                            <FormItem name="sqlText" hidden />
                            <FormItem>
                                <Button type="primary" htmlType="submit" loading={loading}>
                                    确认
                                </Button>
                            </FormItem>
                        </Form>
                    </Spin>
                </div>
            </div>
        );
    }
);

export default Create as (params: ICreateProps) => JSX.Element;
