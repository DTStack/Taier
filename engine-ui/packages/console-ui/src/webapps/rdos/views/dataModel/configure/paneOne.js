import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, Card,
    Input, Button, message, Popconfirm,
} from 'antd';

import utils from 'utils';

import Api from '../../../api/dataModel';
import ModelLevelModal from './paneOneModal';
import BasePane from './basePane';

const Option = Select.Option;
const FormItem = Form.Item;

export default class ModelLevel extends BasePane {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.setState({
            params: Object.assign(this.state.params, { 
                type: 1, // 模型层级
            }),
        }, this.loadData)
    }

    initColumns = () => {
        return [{
            title: '层级编号',
            dataIndex: 'alarmName',
            key: 'alarmName',
        }, {
            width: 80,
            title: '层级名称',
            dataIndex: 'taskName',
            key: 'taskName',
        }, {
            width: 80,
            title: '层级说明',
            dataIndex: 'myTrigger',
            key: 'myTrigger',
        }, {
            width: 80,
            title: '声明周期',
            dataIndex: 'senderTypes',
            key: 'senderTypes',
        }, {
            title: '是否记入层级依赖',
            dataIndex: 'receiveUsers',
            key: 'receiveUsers',
        }, {
            title: '最近修改人',
            dataIndex: 'alarmStatus',
            key: 'alarmStatus',
        }, {
            title: '最近修改时间',
            dataIndex: 'createTime',
            key: 'createTime',
            render: text => utils.formatDateTime(text),
        }, {
            title: '操作',
            key: 'operation',
            render: (record) => {
                return (
                    <div key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>编辑</a>
                        <span className="ant-divider" />
                        <a onClick={() => { this.delete(record) }}>删除</a>
                    </div>
                )
            },
        }]
    }

    render() {

        const { loading, table, modalVisible, modalData } = this.state

        console.log('state:', this.state)

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
                    title=""
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
                <ModelLevelModal 
                    data={ modalData }
                    handOk={ this.update }
                    handCancel={ () => this.setState({ modalVisible: false })}
                    visible={ modalVisible }
                />
            </div>
        )
    }

}