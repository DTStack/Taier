import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, Card,
    Input, Button, message, Popconfirm,
} from 'antd';

import utils from 'utils';

import BasePane from './basePane';
import DeriveIndexModal from './paneSevenModal';

const Option = Select.Option;
const FormItem = Form.Item;

export default class DeriveIndexDefine extends BasePane {


    componentDidMount() {
        this.setState({
            params: Object.assign(this.state.params, { 
                type: 2, // 原子指标
            }),
        }, this.loadData)
    }

    update = (formData) => {}

    delete = (data) => {}

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
                        <a onClick={() => { this.delete(record) }}>删除</a>
                    </div>
                )
            },
        }]
    }

    render() {

        const { loading, table, modalVisible, modalData } = this.state

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
                                    placeholder="选择创建人"
                                    optionFilterProp="name"
                                    onChange={this.changeReceive}
                                >
                                    <Option value="1">分层不合理</Option>
                                    <Option value="2">主题域不合理</Option>
                                    <Option value="3">引用标识不合理</Option>
                                    <Option value="4">引用不合理</Option>
                                </Select>
                            </FormItem>
                        </Form>
                    }

                    extra={
                        <Button
                            style={{ marginTop: '10px' }}
                            type="primary"
                            onClick={() => { this.setState({ modalVisible: true }) }}
                        >
                            新建
                        </Button>
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
                <DeriveIndexModal 
                    data={ modalData }
                    handOk={ this.update }
                    handCancel={ () => this.setState({ modalVisible: false })}
                    visible={ modalVisible }
                />
            </div>
        )
    }

}