import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, 
    Card, Button, message, Checkbox,
    DatePicker,
} from 'antd';

import utils from 'utils';

const Option = Select.Option;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;

export default class ModelCheck extends Component {

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
    }

    loadData = () => {
        const { params } = this.state;

    }

    changeParams = (field, value) => {
        this.setState({
            params: Object.assign(this.state.params, { [field]: value })
        })
    }

    initColumns = () => {
        return [{
            title: '表名',
            dataIndex: 'alarmName',
            key: 'alarmName',
        }, {
            width: 80,
            title: '表描述',
            dataIndex: 'taskName',
            key: 'taskName',
        }, {
            width: 80,
            title: '模型层级',
            dataIndex: 'myTrigger',
            key: 'myTrigger',
        }, {
            width: 80,
            title: '主题域',
            dataIndex: 'senderTypes',
            key: 'senderTypes',
        }, {
            title: '增量标识',
            dataIndex: 'receiveUsers',
            key: 'receiveUsers',
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
                        <a onClick={() => { this.updateAlarmStatus(record) }}>忽略</a>
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
                                    onSelect={(value) => this.changeParams.bind('type', value)}
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
                                    onChange={(value) => this.changeParams.bind('range', value)}
                                />
                            </FormItem>
                            <FormItem>
                                <Checkbox 
                                    onChange={(value) => this.changeParams.bind('ignore', value)}
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
                            onChange={this.handleTableChange}
                            dataSource={table.data || []}
                        />
                </Card>
            </div>
        )
    }

}