import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, 
    Card, Button, message, Checkbox,
    DatePicker,
} from 'antd';

import utils from 'utils';

import Api from '../../../api/dataModel';

const Option = Select.Option;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;

export default class FieldCheck extends Component {

    state ={
        table: {data: []},
        loading: false,

        params: {
            currentPage: 1,
            ignore: false,
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
        Api.getCheckPartitions(params).then(res => {
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
            id: record.id
        }).then(res => {
            if (res.code === 1) {
                this.loadData()
                message.success('已经成功忽略该字段！')
            }
        })
    }

    changeParams = (field, value) => {
        let params = Object.assign(this.state.params);
        if (field === 'range' && value) {
            params.startTime = value[0].valueOf();
            params.endTime = value[1].valueOf();
        } else {
            params[field] = value;
        }
        this.setState({
            params,
        }, this.loadData)
    }

    initColumns = () => {
        return [{
            title: '字段名称',
            dataIndex: 'alarmName',
            key: 'alarmName',
        }, {
            width: 80,
            title: '字段描述',
            dataIndex: 'taskName',
            key: 'taskName',
        }, {
            width: 80,
            title: '字段类型',
            dataIndex: 'myTrigger',
            key: 'myTrigger',
        }, {
            width: 80,
            title: '所属表',
            dataIndex: 'senderTypes',
            key: 'senderTypes',
        }, {
            title: '最后修改人',
            dataIndex: 'alarmStatus',
            key: 'alarmStatus',
        }, {
            title: '最后修改时间',
            dataIndex: 'createTime',
            key: 'createTime',
            render: text => utils.formatDateTime(text),
        }, {
            title: '检测结果',
            dataIndex: 'createUser',
            key: 'createUser',
        }, {
            title: '操作',
            key: 'operation',
            render: (record) => {
                return (
                    <div key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>修改</a>
                        <span className="ant-divider" />
                        <a onClick={() => { this.ignore(record) }}>忽略</a>
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
                                    defaultValue={params.type}
                                    onChange={(value) => this.changeParams('type', value)}
                                >
                                    <Option value="1">字段名称不合理</Option>
                                    <Option value="2">字段类型不合理</Option>
                                    <Option value="3">字段描述不合理</Option>
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
                                    onChange={(e) => this.changeParams('ignore', e.target.checked)}
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