// @ts-nocheck
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

import React, { useState } from 'react';
import { MinusCircleOutlined, UpOutlined } from '@ant-design/icons';
import { Button, Card, Checkbox, Form, Input, InputNumber, message, Select } from 'antd';

import stream from '@/api';
import Editor from '@/components/editor';
import {
    fieldDelimiter,
    intervalTime,
    restfulDecode,
    restfulFields,
    restfulParam,
    strategy,
} from '@/components/helpDoc/docs';
import { NEST_KEYS, RESTFUL_METHOD, RESTFUL_PROPTOCOL, RESTFUL_RESP_MODE, RESTFUL_STRATEGY } from '@/constant';
import { checkUrl } from '../../helper';
import EditTable from '../editTable';

const FormItem = Form.Item;
const Option = Select.Option;
const tabList = [
    { key: '0', tab: '第一次请求' },
    { key: '1', tab: '第二次请求' },
];
const editorOptions: any = {
    mode: 'resetfulPreview',
    lineNumbers: false,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true,
};

export default (props: { collectionData: any }) => {
    const { collectionData } = props;
    const { sourceMap } = collectionData;
    const { requestMode, decode, param, body, url, requestInterval } = sourceMap;

    const [failedValiData, setFailedValiData] = useState<any>({});
    const [previewLoading, setPreviewLoading] = useState(false);
    const [restfulPreviewVisable, setRestfulPreviewVisable] = useState(false);
    const [restfulPreviewData, setRestfulPreviewData] = useState<any>({});
    const [previewTabKey, setPreviewTabKey] = useState('0');

    const loadRestFulPreview = async () => {
        if (!checkUrl(url) || !requestInterval) {
            message.error(!checkUrl(url) ? '请输入正确的URL' : '请输入请求间隔');
            return;
        }
        setPreviewLoading(true);
        const res = await stream.getRestfulDataPreview(sourceMap);
        if (!(res && res?.success)) {
            setPreviewLoading(false);
            return;
        }
        setPreviewLoading(false);
        setRestfulPreviewVisable(true);
        setRestfulPreviewData(res?.data);
    };
    const validateParams = (dataSource: any, requireFields: any) => {
        if (!requireFields.length) return true;
        for (let i = 0; i <= dataSource.length - 1; i++) {
            const data = dataSource[i];
            for (const field of requireFields) {
                if (!data[field]) {
                    setFailedValiData({
                        [i]: {
                            [field]: 'error',
                        },
                    });
                    return false;
                }
            }
        }
        setFailedValiData({});
        return true;
    };

    const tableValueChange = (e: any, field: any, index: any, onTableCellChange: any) => {
        const data = { field, value: e.target?.value };
        if (e.target?.checked && !e.target?.value) data.value = e.target.checked;
        onTableCellChange(data, index);
    };
    const validateUrl = (rule: any, val: any) => {
        if (!val) {
            return Promise.reject(new Error('请输入URL！'));
        }
        if (val.includes(' ')) {
            const msg = '不支持输入空格';
            return Promise.reject(new Error(msg));
        }
        if (checkUrl(val)) {
            return Promise.resolve();
        } else {
            return Promise.reject(new Error('请检查您的URL链接格式！'));
        }
    };
    const renderSelect = (dataSource: any[]) => (
        <Select>
            {dataSource.map(({ text, value }) => (
                <Option value={value} key={value}>
                    {text}
                </Option>
            ))}
        </Select>
    );

    const paramOrBody = requestMode === 'get' ? 'param' : 'body';
    const isNesting = (param || body)?.some(({ nest }: any) => nest);
    const isJson = decode === 'json';
    return (
        <React.Fragment>
            <FormItem name="protocol" label="协议" style={{ textAlign: 'left' }}>
                {renderSelect(RESTFUL_PROPTOCOL)}
            </FormItem>
            <FormItem name="requestMode" label="请求方式" style={{ textAlign: 'left' }}>
                {renderSelect(RESTFUL_METHOD)}
            </FormItem>
            <FormItem
                name="url"
                label="URL"
                style={{ textAlign: 'left' }}
                rules={[{ validator: validateUrl }]}
                validateTrigger="onBlur"
            >
                <Input placeholder="http(s)://host:port" />
            </FormItem>
            <FormItem name="header" label="Header 参数" style={{ textAlign: 'left' }}>
                <EditTable
                    columns={[
                        {
                            title: 'Key',
                            dataIndex: 'key',
                            width: '30%',
                            render: (text: any, record: any, index: any, onTableCellChange: any) => (
                                <Input
                                    value={text}
                                    onChange={(e) => {
                                        tableValueChange(e, 'key', index, onTableCellChange);
                                    }}
                                />
                            ),
                        },
                        {
                            title: 'Value',
                            dataIndex: 'value',
                            width: '30%',
                            render: (text: any, record: any, index: any, onTableCellChange: any) => (
                                <Input
                                    value={text}
                                    onChange={(e) => {
                                        tableValueChange(e, 'value', index, onTableCellChange);
                                    }}
                                />
                            ),
                        },
                        {
                            title: 'Description',
                            dataIndex: 'description',
                            width: '40%',
                            render: (text: any, record: any, index: any, onTableCellChange: any, operations: any) => (
                                <div style={{ position: 'relative' }}>
                                    <Input
                                        value={text}
                                        onChange={(e) => {
                                            tableValueChange(e, 'description', index, onTableCellChange);
                                        }}
                                    />
                                    <MinusCircleOutlined className="c-icon-delete" onClick={operations} />
                                </div>
                            ),
                        },
                    ]}
                />
            </FormItem>
            <FormItem
                name={paramOrBody}
                label={paramOrBody === 'param' ? 'Param参数' : 'Body参数'}
                style={{ textAlign: 'left' }}
                tooltip={restfulParam}
            >
                <EditTable
                    validator={validateParams}
                    columns={[
                        {
                            title: 'Key',
                            dataIndex: 'key',
                            width: '20%',
                            required: true,
                            render: (text: any, record: any, index: any, onTableCellChange: any) => (
                                <FormItem
                                    validateStatus={failedValiData[index]?.key || 'success'}
                                    help={failedValiData[index]?.key && '请输入key'}
                                >
                                    <Input
                                        value={text}
                                        onChange={(e) => {
                                            tableValueChange(e, 'key', index, onTableCellChange);
                                        }}
                                    />
                                </FormItem>
                            ),
                        },
                        {
                            title: 'Value',
                            dataIndex: 'value',
                            width: '20%',
                            required: true,
                            render: (text: any, record: any, index: any, onTableCellChange: any) => (
                                <FormItem
                                    validateStatus={failedValiData[index]?.value || 'success'}
                                    help={failedValiData[index]?.value && '请输入value'}
                                >
                                    <Input
                                        value={text}
                                        onChange={(e) => {
                                            tableValueChange(e, 'value', index, onTableCellChange);
                                        }}
                                    />
                                </FormItem>
                            ),
                        },
                        {
                            title: 'NextValue',
                            dataIndex: 'nextValue',
                            width: '20%',
                            render: (text: any, record: any, index: any, onTableCellChange: any) => (
                                <Input
                                    value={text}
                                    onChange={(e) => {
                                        tableValueChange(e, 'nextValue', index, onTableCellChange);
                                    }}
                                />
                            ),
                        },
                        {
                            title: '嵌套',
                            dataIndex: 'nest',
                            width: '10%',
                            render: (text: any, record: any, index: any, onTableCellChange: any) => (
                                <Checkbox
                                    checked={text}
                                    onChange={(e: any) => {
                                        tableValueChange(e, 'nest', index, onTableCellChange);
                                    }}
                                />
                            ),
                        },
                        {
                            title: 'Format',
                            dataIndex: 'format',
                            width: '30%',
                            render: (text: any, record: any, index: any, onTableCellChange: any, operations: any) => (
                                <div style={{ position: 'relative' }}>
                                    <Input
                                        value={text}
                                        onChange={(e) => {
                                            tableValueChange(e, 'format', index, onTableCellChange);
                                        }}
                                    />
                                    <MinusCircleOutlined className="c-icon-delete" onClick={operations} />
                                </div>
                            ),
                        },
                    ]}
                />
            </FormItem>
            {isNesting && (
                <FormItem
                    name="fieldDelimiter"
                    label="嵌套切分键"
                    style={{ textAlign: 'left' }}
                    rules={[{ required: true, message: '请选择嵌套切分键' }]}
                    tooltip={fieldDelimiter}
                >
                    {renderSelect(NEST_KEYS)}
                </FormItem>
            )}
            <FormItem name="strategy" label="异常策略" style={{ textAlign: 'left' }} tooltip={strategy}>
                <EditTable
                    columns={[
                        {
                            title: 'Key',
                            dataIndex: 'key',
                            width: '30%',
                            render: (text: any, record: any, index: any, onTableCellChange: any) => (
                                <Input
                                    value={text}
                                    onChange={(e) => {
                                        tableValueChange(e, 'key', index, onTableCellChange);
                                    }}
                                />
                            ),
                        },
                        {
                            title: 'Value',
                            dataIndex: 'value',
                            width: '30%',
                            render: (text: any, record: any, index: any, onTableCellChange: any) => (
                                <Input
                                    value={text}
                                    onChange={(e) => {
                                        tableValueChange(e, 'value', index, onTableCellChange);
                                    }}
                                />
                            ),
                        },
                        {
                            title: 'Strategy',
                            dataIndex: 'handle',
                            width: '40%',
                            render: (text: any, record: any, index: any, onTableCellChange: any, operations: any) => (
                                <div style={{ position: 'relative' }}>
                                    <Select
                                        value={text}
                                        placeholder="请选择Strategy"
                                        onChange={(value) => {
                                            onTableCellChange({ field: 'handle', value }, index);
                                        }}
                                    >
                                        {RESTFUL_STRATEGY.map(({ text, value }: any) => (
                                            <Option key={value} value={value}>
                                                {text}
                                            </Option>
                                        ))}
                                    </Select>
                                    <MinusCircleOutlined className="c-icon-delete" onClick={operations} />
                                </div>
                            ),
                        },
                    ]}
                />
            </FormItem>
            <FormItem name="decode" label="返回类型" style={{ textAlign: 'left' }} tooltip={restfulDecode}>
                {renderSelect(RESTFUL_RESP_MODE)}
            </FormItem>
            {isJson && (
                <FormItem name="fields" label="指定解析字段" style={{ textAlign: 'left' }} tooltip={restfulFields}>
                    <Input placeholder="请填写JSON中指定解析字段，多个字段用英文逗号隔开，默认不做解析" />
                </FormItem>
            )}
            <FormItem
                name="requestInterval"
                label="请求间隔"
                style={{ textAlign: 'left' }}
                rules={[{ required: true, message: '请输入请求时间间隔' }]}
                tooltip={intervalTime}
            >
                <InputNumber style={{ width: '96%' }} min={1} step={1} max={999999} precision={0} addonAfter="s" />
            </FormItem>
            <FormItem key="startLocation" label="采集起点" style={{ textAlign: 'left' }}>
                从任务运行时开始
            </FormItem>
            <FormItem key="preview" label="数据预览">
                <Button ghost loading={previewLoading} onClick={loadRestFulPreview}>
                    数据预览
                </Button>
            </FormItem>
            {restfulPreviewVisable && (
                <FormItem key="previewTab" label=" " className="c-restful-preview">
                    <Card
                        key="previewCard"
                        tabBarExtraContent={
                            <a
                                onClick={() => {
                                    setRestfulPreviewVisable(false);
                                }}
                            >
                                <UpOutlined />
                            </a>
                        }
                        tabList={tabList}
                        activeTabKey={previewTabKey}
                        onTabChange={(key: any) => {
                            setPreviewTabKey(key);
                        }}
                    >
                        <Editor
                            sync
                            value={restfulPreviewData[previewTabKey] || ''}
                            style={{ height: '100%', maxHeight: '400px', minHeight: '0px' }}
                            options={editorOptions}
                        />
                    </Card>
                </FormItem>
            )}
        </React.Fragment>
    );
};
