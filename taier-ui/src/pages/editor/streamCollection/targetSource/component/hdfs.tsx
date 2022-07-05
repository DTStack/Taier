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

import { splitCharacter } from "@/components/helpDoc/docs";
import { Form, Input, Radio, Select } from "antd";
import React from "react";

const FormItem = Form.Item;
const Option = Select.Option;

export default (props: { collectionData: any; }) => {
    const { collectionData } = props;
    const { targetMap = {} } = collectionData;
    const isText = targetMap.fileType == 'text';
    return (<React.Fragment>
        <FormItem
            label="路径"
            name="path"
            rules={[{ required: true }]}
        >
            <Input placeholder="例如: /app/batch" />
        </FormItem>
        <FormItem
            label="写入分区目录名称"
            name="fileName"
        >
            <Input placeholder="写入分区目录名称为空，则默认在当前路径下写入" />
        </FormItem>
        <FormItem
            label="文件类型"
            name="fileType"
            rules={[{ required: true }]}
        >
            <Radio.Group>
                <Radio value="orc">
                    orc
                </Radio>
                <Radio value="text">
                    text
                </Radio>
                <Radio value="parquet">
                    parquet
                </Radio>
            </Radio.Group>
        </FormItem>
        {isText && <React.Fragment><FormItem
            label="列分隔符"
            name="fieldDelimiter"
            tooltip={splitCharacter}
        >
            <Input placeholder="例如: 目标为hive则 分隔符为\001" />
        </FormItem>
            <FormItem
                label="编码"
                name="encoding"
                rules={[{ required: true }]}
            >
                <Select>
                    <Option value="utf-8">utf-8</Option>
                    <Option value="gbk">gbk</Option>
                </Select>
            </FormItem>
        </React.Fragment>}
        <FormItem
            label="写入模式"
            className="txt-left"
            name="writeMode"
            rules={[{ required: true }]}
        >
            <Radio.Group>
                <Radio value="NONCONFLICT" disabled style={{ float: 'left' }}>
                    覆盖（Insert Overwrite）
                </Radio>
                <Radio value="APPEND" style={{ float: 'left' }}>
                    追加（Insert Into）
                </Radio>
            </Radio.Group>
        </FormItem>
    </React.Fragment>)
}