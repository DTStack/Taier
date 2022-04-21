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

import { HELP_DOC_URL } from "@/constant";
import { Checkbox, Form, Input, Select } from "antd";
import { debounce } from "lodash";
import React from "react";

interface IProps {
    defaultData: any,
    taskChange: (args: any) => void
    formItemLayout: any
}

const Option = Select.Option;

export default function WrappedCataForm({ defaultData, taskChange, formItemLayout }: IProps) {
    const [form] = Form.useForm()
    const onChange = (val: any, item: string) => {
        const { getFieldsValue } = form;
        let params = {};
        if (item === 'failRetry') {
            if (val) {
                params = {
                    failRetry: 1,
                    retryIntervalUnit: 1,
                    maxRetryNum: 3,
                    retryInterval: 1
                };
            } else {
                params = {
                    failRetry: 0
                };
            }
            params = Object.assign(params, {
                submitExpiredUnit: defaultData?.submitExpiredUnit,
                submitExpired: defaultData?.submitExpired
            })
        } else {
            const res = {
                ...defaultData,
                ...getFieldsValue(),
                [item]: val
            }
            params = {
                ...res,
                failRetry: res.failRetry ? 1 : 0
            };
        }
        taskChange(params);
    }
    const changeItem = debounce(onChange, 500, { 'maxWait': 2000 });
    const { failRetry = 0, retryIntervalUnit, submitExpiredUnit } = defaultData || {};
    const retryIntervalUnitElm = (
        <Select key={failRetry} defaultValue={retryIntervalUnit === undefined ? 1 : retryIntervalUnit} onChange={(val: any) => changeItem(val, 'retryIntervalUnit')} >
            <Option value={1}>分钟</Option>
            <Option value={0}>秒</Option>
        </Select>
    )

    const submitExpiredUnitElm = (
        <Select key={failRetry} defaultValue={submitExpiredUnit === undefined ? 1 : submitExpiredUnit} onChange={(val: any) => changeItem(val, 'submitExpiredUnit')} >
            <Option value={1}>分钟</Option>
            <Option value={0}>秒</Option>
        </Select>
    )
    return (
        <Form
            key={failRetry}
            form={form}
            {...formItemLayout}
            initialValues={{
                failRetry: failRetry === 1,
                maxRetryNum: defaultData?.maxRetryNum || 3,
                retryInterval: defaultData?.retryInterval || 1,
                submitExpired: defaultData?.submitExpired || 3,
            }}
        >
            <Form.Item
                label={'出错重试'}
                name='failRetry'
                tooltip='开启时任务失败会自动重试'
            >
                <Checkbox onChange={(e: any) => changeItem(e.target.checked, 'failRetry')} checked={failRetry === 1}>是</Checkbox>
            </Form.Item>
            {failRetry === 1 && <React.Fragment>
                <Form.Item
                    label="重试次数"
                    name='maxRetryNum'
                    rules={[{
                        pattern: /^([1-9]|10)$/,
                        message: '重试次数范围在[1,10]'
                    }]}
                >
                    <Input addonAfter={'次'} onChange={(e: any) => changeItem(e.target.value, 'maxRetryNum')} />
                </Form.Item>
                <Form.Item
                    label="重试间隔"
                    name='retryInterval'
                    rules={[{
                        pattern: /^([1-9]\d{0,4})$/,
                        message: '重试间隔范围在[1,99999]'
                    }]}
                >
                    <Input addonAfter={retryIntervalUnitElm} onChange={(e: any) => changeItem(e.target.value, 'retryInterval')} />
                </Form.Item>
            </React.Fragment>}
            <Form.Item
                label="等待超时"
                name='submitExpired'
                rules={[{
                    pattern: /^([1-9]\d{0,4})$/,
                    message: '等待超时范围在[1,99999]'
                }]}
                tooltip={<div>
                    等待超时后任务自动取消，时长建议超过总重试时间，详情可参考
                    <a
                        target="blank"
                        style={{ marginLeft: 5 }}
                        href={HELP_DOC_URL.JOB_SETTING}
                    >
                        帮助文档
                    </a>
                </div>}
            >
                <Input addonAfter={submitExpiredUnitElm} onChange={(e: any) => changeItem(e.target.value, 'submitExpired')} />
            </Form.Item>
        </Form>
    )
}