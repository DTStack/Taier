import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';

import {
    Table, Row, Col, Select, Form, 
    Card, Button, message, Checkbox,
    DatePicker, Input,
} from 'antd';

import utils from 'utils';
import Api from '../../../api/dataModel';
import { FieldNameCheck } from '../../../components/display'

const Option = Select.Option;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;

export default class FieldCheck extends Component {

    state ={
        table: {data: []},
        loading: false,

        params: {
            pageIndex: 1,
            pageSize: 10,
            columnName: '',
            ignore: 0, // 1 忽略，0 不忽略
            type: '2',
        },
    }

  
    componentDidMount() {
        this.loadData();
    }

    loadData = () => {
        const { params } = this.state;
        this.setState({
            loading: true,
        })
        Api.getCheckList(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data
                })
            }
            this.setState({
                loading: false,
            })
        })
    }

    ignore = (record) => {
        Api.ignoreCheck({
            id: record.id,
            type: '2',
        }).then(res => {
            if (res.code === 1) {
                this.loadData()
                message.success('已经成功忽略该字段！')
            }
        })
    }

    changeParams = (field, value) => {
        const params = {
            [field]: value,
        }

        if (field !== 'pageIndex') params.pageIndex = 1;

        this.setState(Object.assign(this.state.params, params), this.loadData)
    }

    onTableNameChange = (e) => {
        this.setState({
            params: Object.assign(this.state.params, {
                columnName: e.target.value
            })
        })
    }

    initColumns = () => {
        return [{
            title: '字段名称',
            dataIndex: 'columnName',
            key: 'columnName',
        }, {
            width: 80,
            title: '字段描述',
            dataIndex: 'columnDesc',
            key: 'columnDesc',
        }, {
            width: 80,
            title: '字段类型',
            dataIndex: 'columnType',
            key: 'columnType',
        }, {
            width: 80,
            title: '所属表',
            dataIndex: 'tableName',
            key: 'tableName',
        }, {
            title: '最近检测时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text),
        }, {
            title: '检测结果',
            dataIndex: 'checkResult',
            key: 'checkResult',
        }, {
            width: 80,
            title: '操作',
            key: 'operation',
            render: (record) => {
                const showText = record.isIgnore ? '恢复' : '忽略';

                return (
                    <div key={record.id}>
                        <Link to={`/data-model/table/modify/${record.tableId}`}>修改</Link>
                        <span className="ant-divider" />
                        <a onClick={() => { this.ignore(record) }}>{showText}</a>
                    </div>
                )
            },
        }]
    }

    render() {

        const { loading, table, params } = this.state

        const pagination = {
            total: table.totalCount,
            defaultPageSize: 10,
            current: table.currentPage,
        };

        return (
            <div className="m-card">
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <Form 
                            className="m-form-inline" 
                            layout="inline"
                            style={{ marginTop: '10px' }}
                        >
                            <FormItem>
                                <Input.Search
                                    placeholder="按字段名搜索"
                                    style={{ width: 200 }}
                                    size="default"
                                    onChange={ this.onTableNameChange }
                                    onSearch={ this.loadData }
                                />
                            </FormItem>
                            <FormItem label="类型">
                                <Select
                                    allowClear
                                    showSearch
                                    style={{ width: 200 }}
                                    placeholder="选择类型"
                                    optionFilterProp="name"
                                    onChange={(value) => this.changeParams('triggerType', value)}
                                >
                                    <Option value="1">字段名称不合理</Option>
                                    <Option value="2">字段类型不合理</Option>
                                    <Option value="3">字段描述不合理</Option>
                                </Select>
                            </FormItem>
                            <FormItem>
                                <Checkbox 
                                    onChange={(e) => this.changeParams('ignore', e.target.checked ? 1 : 0 )}
                                >
                                    已忽略
                                </Checkbox>
                            </FormItem>
                        </Form>
                    }
                >
                        <Table
                            rowKey="id"
                            className="m-table"
                            pagination={pagination}
                            loading={loading}
                            columns={this.initColumns()}
                            onChange={(pagination) => this.changeParams('pageIndex', pagination.current )}
                            dataSource={table.data || []}
                        />
                </Card>
            </div>
        )
    }

}