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

import React from 'react';
import { Button, Form, Input, Select } from 'antd';
import molecule from '@dtinsight/molecule/esm';
import { Scrollable } from '@dtinsight/molecule/esm/components';
import { WrappedFormUtils } from 'antd/lib/form/Form';
import FormItem from 'antd/lib/form/FormItem';
import { TASK_TYPE } from '../../comm/const';

import FolderPicker from '../../components/folderPicker';

const Option = Select.Option;

const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    },
};
const tailFormItemLayout = {
    wrapperCol: {
        xs: {
            span: 24,
            offset: 0,
        },
        sm: {
            span: 16,
            offset: 6,
        },
    },
};

interface OpenProps {
    onSubmit?: (values: any) => Promise<boolean>;
    record?: any;
    form: WrappedFormUtils<any>;
    current?: any;
    tabId?: string | number;
}

const taskType = [
    {
        value: TASK_TYPE.SQL,
        text: 'SparkSql',
    },
    {
        value: TASK_TYPE.SYNC,
        text: '数据同步',
    },
];

class Open extends React.PureComponent<OpenProps, {}> {
    state = {
        loading: false,
    };

    handleSubmit = (e: any) => {
        e.preventDefault();
        const taskTreeRootId =
            molecule.folderTree.getState().folderTree?.data?.[0].data.id;

        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                this.setState(
                    {
                        loading: true,
                    },
                    () => {
                        const params = {
                            ...values,
                            nodePid: values.nodePid || taskTreeRootId,
                        };
                        this.props.onSubmit?.(params).then((loading) => {
                            this.setState({
                                loading,
                            });
                        });
                    }
                );
            }
        });
    };

    componentDidMount() {
        const { record, current, tabId } = this.props;
        const {
            tab: { data },
        } = current;
        if (data.id === undefined) {
            this.updateTabData({
                id: record?.id ?? tabId, // 存入id标识tab中是否有数据
                name: record?.name,
                taskType: record?.taskType,
                nodePid: record?.parentId ?? data.value,
                taskDesc: record?.taskDesc,
            });
        }
        this.syncTabData2Form();
    }

    syncTabData2Form = () => {
        const {
            form,
            current: {
                tab: { data },
            },
        } = this.props;
        const { name, taskType, nodePid, taskDesc } = data;
        form.setFieldsValue({
            name,
            taskType,
            nodePid,
            taskDesc,
        });
    };

    updateTabData = (values: any) => {
        const { current } = this.props;
        molecule.editor.updateTab({
            ...current.tab,
            data: {
                ...current.tab.data,
                ...values,
            },
        });
    };

    handleFolderPickerChange = (value: string) => {
        this.updateTabData({ nodePid: value });
    };

    render() {
        const { record } = this.props;
        const { getFieldDecorator } = this.props.form;
        const { loading } = this.state;

        return (
            <Scrollable>
                <Form onSubmit={this.handleSubmit} className="mo-open-task">
                    <FormItem {...formItemLayout} label="任务名称">
                        {getFieldDecorator('name', {
                            rules: [
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
                            ],
                        })(
                            <Input
                                autoComplete={'off'}
                                onChange={(e: any) => {
                                    this.updateTabData({
                                        name: e.target.value,
                                    });
                                }}
                            />
                        )}
                    </FormItem>
                    <FormItem {...formItemLayout} label="任务类型">
                        {getFieldDecorator('taskType', {
                            rules: [
                                {
                                    required: true,
                                    message: `请选择任务类型`,
                                },
                            ],
                        })(
                            <Select
                                disabled={!!record}
                                onChange={(value: any) => {
                                    this.updateTabData({ taskType: value });
                                }}
                            >
                                {taskType.map((type) => (
                                    <Option key={type.value} value={type.value}>
                                        {type.text}
                                    </Option>
                                ))}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem {...formItemLayout} label="存储位置">
                        {getFieldDecorator('nodePid', {
                            rules: [
                                {
                                    required: true,
                                    message: '存储位置必选！',
                                },
                            ],
                        })(
                            <FolderPicker
                                showFile={false}
                                dataType="task"
                                onChange={this.handleFolderPickerChange}
                            />
                        )}
                    </FormItem>
                    <FormItem {...formItemLayout} label="描述" hasFeedback>
                        {getFieldDecorator('taskDesc', {
                            rules: [
                                {
                                    max: 200,
                                    message: '描述请控制在200个字符以内！',
                                },
                            ],
                        })(
                            <Input.TextArea
                                disabled={false}
                                rows={4}
                                onChange={(e: any) => {
                                    this.updateTabData({
                                        taskDesc: e.target.value,
                                    });
                                }}
                            />
                        )}
                    </FormItem>
                    <FormItem {...tailFormItemLayout}>
                        <Button
                            type="primary"
                            htmlType="submit"
                            loading={loading}
                        >
                            确认
                        </Button>
                    </FormItem>
                </Form>
            </Scrollable>
        );
    }
}

export default Form.create<OpenProps>({ name: 'open' })(Open);
