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
import { Form, FormInstance, Input, Select, Table } from "antd";
import React from "react";
import './assetPanel.scss';

const FormItem = Form.Item;

interface IAssetPanelProps {
    panelColumn: any;
    handleInputChange: Function;
    assetTableOptionTypes: any[];
    dataBaseOptionTypes: any[];
    getDataBaseList: () => void;
    embedElm?: JSX.Element;
    pIndex?: number;
    hideTargetCol?: boolean;
}

export const AssetPanel = ({
    pIndex,
    handleInputChange,
    getDataBaseList,
    dataBaseOptionTypes,
    assetTableOptionTypes,
    embedElm,
    panelColumn,
    hideTargetCol
}: IAssetPanelProps) => {
    // 初始化表格
    const initColumn = () => {
        const columns = [
            {
                title: '字段',
                dataIndex: 'column',
                key: 'column',
                width: '100px',
                ellipsis: true
            }, {
                title: '类型',
                dataIndex: 'type',
                key: 'type',
                width: '60px',
                ellipsis: true
            }
        ];
        const targetCol = [
            {
                title: '别名',
                dataIndex: 'targetCol',
                key: 'targetCol',
                render: (text: string, record: any, index: number) => {
                    return (
                        <Input placeholder="请输入别名" value={text} onChange={(e: any) => onInputChange('targetCol', e.target.value, index)} />
                    )
                }
            }
        ]
        return hideTargetCol ? columns : [...columns, ...targetCol];
    }
    // 输入框内容修改
    const onInputChange = (type: string, value: any, subIndex?: any) => {
        if (pIndex !== undefined) {
            if (subIndex !== undefined) {
                handleInputChange(type, pIndex, subIndex, value);
                return;
            }
            handleInputChange(type, pIndex, value);
        } else {
            if (subIndex !== undefined) {
                handleInputChange(type, subIndex, value);
                return;
            }
            handleInputChange(type, value);
        }
    }
    const filterOption = (input: any, option: any) => option.props.children
        .toLowerCase()
        .indexOf(input.toLowerCase()) >= 0
    return <React.Fragment>
        <FormItem
            label='数据库'
            tooltip='指「数据资产/数据模型/标准化建表/」中创建的数据库'
            name='dbId'
            rules={[{ required: true, message: '请选择数据库' }]}
        >
            <Select
                className="right-select"
                placeholder="请选择数据库"
                onChange={(value: any) => onInputChange('dbId', value)}
                showSearch
                filterOption={filterOption}
                onFocus={getDataBaseList}
            >
                {dataBaseOptionTypes}
            </Select>
        </FormItem>
        <FormItem
            label='表'
            tooltip={<span>指「数据资产/数据模型/标准化建表」中数据库下的Flink表，详情可参考 <a rel="noopener noreferrer" target="_blank" href={HELP_DOC_URL.ASSET_MANAGE}>元数据管理</a></span>}
            name='tableId'
            rules={[{ required: true, message: '请选择数据表' }]}
        >
            <Select
                className="right-select"
                placeholder="请选择数据表"
                onChange={(value: any) => onInputChange('tableId', value)}
                showSearch
                filterOption={filterOption}
            >
                {assetTableOptionTypes}
            </Select>
        </FormItem>
        {embedElm}
        <FormItem
            label="字段"
            required
            name='columns'
        >
            <Table
                className="dt-pagination-lower dt-table-border asset-panel-table"
                rowKey="column"
                size="middle"
                dataSource={pIndex !== undefined ? panelColumn[pIndex]?.columns : panelColumn?.columns}
                columns={initColumn()}
                pagination={false}
            />
        </FormItem>
    </React.Fragment>
}