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

import { DATA_SOURCE_ENUM, DATA_SOURCE_VERSION } from "@/constant";
import { Form, Input, InputNumber, Radio, Select } from "antd";
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
            name="message"
            label="测试信息"
            rules={[{ required: false, message: '请输入测试信息' }]}
        >
            <Input.TextArea rows={3} placeholder={`测试信息将在连接建立后发送一次给主机，用作连接测试；内容为空时不进行发送`} style={{ width: '100%' }} />
        </FormItem>
        <FormItem
            name="codecType"
            label="返回类型"
            rules={[{ required: false, message: '请选择返回类型' }]}
            initialValue='text'
        >
            <Select
                getPopupContainer={(triggerNode: any) => triggerNode}
                disabled
                showSearch
                placeholder="请选择返回类型"
                className="right-select"
                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
            >
                <Option value="text">text</Option>
            </Select>
        </FormItem>
        <FormItem
            name="retry"
            label="重试次数"
            initialValue={3}
            rules={[{ required: false, message: '请输入重试次数' }]}
        >
            <InputNumber style={{ width: '50%' }} min={1} step={1} max={5} addonAfter='次，每次间隔2分钟' />
        </FormItem>
        <FormItem
            name="collectPoint"
            label="采集起点"
        >
            <Radio value="taskRun" disabled checked>从任务运行时开始</Radio>
        </FormItem>
    </React.Fragment>
}