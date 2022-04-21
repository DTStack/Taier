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

import * as React from 'react';
import { Table } from 'antd';
import { PlusCircleOutlined } from "@ant-design/icons"
import { cloneDeep, debounce } from 'lodash'

const initialState = {
    dataSource: []
}

type IState = typeof initialState
interface IProps{
    value?: any;
    onChange?: (value: any) => void;
    validator?: (value: any, requireFields: any) => void;
    columns: any;
}

class EditTable extends React.Component<IProps, IState> {
    state=initialState
    requireFields: [] = [];

    componentDidMount () {
        const { value, columns } = this.props
        this.setState({
            dataSource: value ?? []
        })
        this.requireFields = columns.map(({ dataIndex, required }: any) => required ? dataIndex : '').filter(Boolean)
    }

    debounceChange = debounce(() => {
        const { onChange, validator } = this.props
        const { dataSource } = this.state
        onChange && onChange(dataSource)
        validator && validator(dataSource, this.requireFields)
    }, 500)

    addRow = () => {
        const { columns } = this.props
        const newRow: any = {}
        columns.map(({ dataIndex }: any) => {
            newRow[dataIndex] = undefined
        })
        const { dataSource } = this.state
        this.setState({
            dataSource: (dataSource || []).concat([newRow] as any)
        })
    }

    deleteParams = (target: any) => {
        const { dataSource } = this.state
        const newDataSource = dataSource.filter((data, index) => index !== target)
        this.setState({
            dataSource: newDataSource
        }, this.debounceChange)
    }
    renderRequiredTitle = (title: any) => <span className='ant-form-item-required'>{title}</span>

    onTableCellChange = (data: any, index: any) => {
        const { field, value } = data
        const { dataSource } = this.state
        const newDataSource: any = cloneDeep(dataSource)
        newDataSource[index][field] = value
        this.setState({
            dataSource: newDataSource
        }, this.debounceChange)
    }

    initColumns = () => {
        const { columns } = this.props
        return columns.map((col: any) => {
            const { render, title, required } = col
            let newTitle = required ? this.renderRequiredTitle(title) : title
            return {
                ...col,
                title: newTitle,
                render: (text: any, record: any, index: any) => render(text, record, index, this.onTableCellChange, () => { this.deleteParams(index) })
            }
        })
    }

    render () {
        const { dataSource } = this.state
        return (
            <>
                <Table
                    bordered
                    rowKey= {(key, value) => `${key}${value}`}
                    dataSource={dataSource}
                    columns={this.initColumns()}
                    pagination={false}
                />
                <a className='c-ws-add' onClick={this.addRow}>
                    <PlusCircleOutlined />
                    <span>新增参数</span>
                </a>
            </>
        )
    }
}
export default EditTable
