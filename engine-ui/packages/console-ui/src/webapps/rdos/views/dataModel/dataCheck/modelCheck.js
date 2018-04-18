import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';

import {
    Table, Row, Col, Select, Form, 
    Card, Button, message, Checkbox,
    DatePicker,
} from 'antd';

import utils from 'utils';

import Api from '../../../api/dataModel';
import { TableNameCheck } from '../../../components/display'

const Option = Select.Option;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;

export default class ModelCheck extends Component {

    state ={
        table: {data: []},
        loading: false,

        params: {
            currentPage: 1,
            pageSize: 10,
            ignore: 0, // 1 忽略，0 不忽略
            type: '1',
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
            monitorId: record.id,
            type: '1',
            ignore: record.isIgnore ? false : true,
        }).then(res => {
            if (res.code === 1) {
                this.loadData()
                message.success('已经成功忽略该表！')
            }
        })
    }

    changeParams = (field, value) => {
        let params = Object.assign(this.state.params);
        if (field === 'range' && value) {
            params.startTime = value.length > 0 ? value[0].valueOf() : undefined;
            params.endTime = value.length > 1 ? value[1].valueOf() : undefined;
        } else {
            params[field] = value;
        }
        this.setState({
            params,
        }, this.loadData)
    }

    initColumns = () => {
        return [{
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
        }, {
            width: 80,
            title: '表描述',
            dataIndex: 'desc',
            key: 'desc',
        }, {
            width: 80,
            title: '模型层级',
            dataIndex: 'grade',
            key: 'grade',
        }, {
            width: 80,
            title: '主题域',
            dataIndex: 'subject',
            key: 'subject',
        }, {
            title: '增量标识',
            dataIndex: 'increType',
            key: 'increType',
        }, {
            title: '最后修改人',
            dataIndex: 'modifyUserName',
            key: 'modifyUserName',
        }, {
            title: '最后修改时间',
            dataIndex: 'lastModify',
            key: 'lastModify',
            render: text => utils.formatDateTime(text),
        }, {
            title: '检测结果',
            dataIndex: 'triggerType',
            key: 'triggerType',
            render: value => <TableNameCheck value={value} />,
        }, {
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
                            <FormItem label="类型">
                                <Select
                                    allowClear
                                    showSearch
                                    style={{ width: 200 }}
                                    placeholder="选择类型"
                                    optionFilterProp="name"
                                    onChange={(value) => this.changeParams('triggerType', value)}
                                >
                                    <Option value="1">分层不合理</Option>
                                    <Option value="2">主题域不合理</Option>
                                    <Option value="3">引用标识不合理</Option>
                                    <Option value="4">引用不合理</Option>
                                </Select>
                            </FormItem>
                            <FormItem label="时间">
                                <RangePicker 
                                    size="default" 
                                    style={{width: 180}} 
                                    format="YYYY-MM-DD" 
                                    onChange={(value) => this.changeParams('range', value)}
                                />
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
                            onChange={(pagination) => this.changeParams('currentPage', pagination.current )}
                            dataSource={table.data || []}
                        />
                </Card>
            </div>
        )
    }

}