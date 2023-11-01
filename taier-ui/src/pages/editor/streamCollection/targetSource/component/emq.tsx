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
import { Checkbox, Form, Input, Select } from 'antd';

import { isCleanSession } from '@/components/helpDoc/docs';
import { QOS_TYPE } from '@/constant';

const FormItem = Form.Item;
const Option = Select.Option;

export default (props: { collectionData: any }) => {
    const { collectionData } = props;
    const { isEdit } = collectionData;
    return (
        <React.Fragment>
            <FormItem
                name="topic"
                label="Topic"
                rules={[
                    {
                        required: true,
                        message: '请输入topic',
                    },
                    {
                        pattern: /[\w/#+]+/i,
                        message: 'Topic仅支持英文、数字、+、/、#进行输入',
                    },
                ]}
            >
                <Input disabled={isEdit} placeholder="请输入topic" />
            </FormItem>
            <FormItem
                label="清除Session"
                name="isCleanSession"
                rules={[{ required: true, message: '请选择是否清除Session' }]}
                valuePropName="checked"
                tooltip={isCleanSession}
            >
                <Checkbox>清除</Checkbox>
            </FormItem>
            <FormItem label="服务质量(qos)" name="qos" rules={[{ required: true, message: '请选择服务质量' }]}>
                <Select disabled={isEdit} getPopupContainer={(triggerNode: any) => triggerNode}>
                    <Option value={QOS_TYPE.EXACTLY_ONCE}>精准一次（EXACTLY_ONCE)</Option>
                    <Option value={QOS_TYPE.AT_LEAST_ONCE}>至少一次 (AT_LEAST_ONCE)</Option>
                    <Option value={QOS_TYPE.AT_MOST_ONCE}>至多一次 (AT_MOST_ONCE)</Option>
                </Select>
            </FormItem>
        </React.Fragment>
    );
};
