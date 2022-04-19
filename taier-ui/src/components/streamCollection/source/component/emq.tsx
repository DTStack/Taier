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

import { isCleanSession } from "@/components/helpDoc/docs";
import { DATA_SOURCE_ENUM, DATA_SOURCE_VERSION, QOS_TYPE } from "@/constant";
import { Checkbox, Form, Input, Radio, Select, Table } from "antd";
import React from "react";

const FormItem = Form.Item;
const Option = Select.Option;

export default (props: { collectionData: any; sourceList: any[] }) => {
    const { collectionData, sourceList } = props;
    const { isEdit, sourceMap } = collectionData;
    const { type } = sourceMap;

    const sourceDataOptions = sourceList?.map?.((o: any) => {
        return <Option key={o.id} value={o.id}>{o.name}{DATA_SOURCE_VERSION[o.type as DATA_SOURCE_ENUM] && ` (${DATA_SOURCE_VERSION[o.type as DATA_SOURCE_ENUM]})`}</Option>
    })
    return <React.Fragment>
        <FormItem
            name="sourceId"
            label="数据源"
            rules={[{ required: true, message: '请选择数据源' }]}
        >
            <Select
                getPopupContainer={(triggerNode: any) => triggerNode}
                disabled={isEdit && type === DATA_SOURCE_ENUM.EMQ}
                showSearch
                placeholder="请选择数据源"
                className="right-select"
                filterOption={(input: any, option: any) => option.props.children.toString().toLowerCase().indexOf(input.toLowerCase()) >= 0}
            >
                {sourceDataOptions}
            </Select>
        </FormItem>
        <FormItem
            name="topic"
            label="Topic"
            rules={[{
                required: true, message: '请输入topic'
            }, {
                pattern: /[\w/#+]+/i, message: 'Topic仅支持英文、数字、+、/、#进行输入'
            }]}
        >
            <Input placeholder="请输入Topic，若不存在，将直接创建新的Topic" />
        </FormItem>
        <FormItem
            name="codec"
            label="解码器类型(codec)"
            rules={[{ required: true, message: '请填选择解码器类型' }]}
        >
            <Radio.Group
                disabled={isEdit}
            >
                <Radio key="plain" value="plain">plain</Radio>
                <Radio key="json" value="json">json</Radio>
            </Radio.Group>
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
        <FormItem
            label="服务质量(qos)"
            name="qos"
            rules={[{ required: true, message: '请选择服务质量' }]}
        >
            <Select disabled={isEdit} getPopupContainer={(triggerNode: any) => triggerNode}>
                <Option value={QOS_TYPE.EXACTLY_ONCE}>精准一次（EXACTLY_ONCE)</Option>
                <Option value={QOS_TYPE.AT_LEAST_ONCE}>至少一次 (AT_LEAST_ONCE)</Option>
                <Option value={QOS_TYPE.AT_MOST_ONCE}>至多一次 (AT_MOST_ONCE)</Option>
            </Select>
        </FormItem>
    </React.Fragment>
}