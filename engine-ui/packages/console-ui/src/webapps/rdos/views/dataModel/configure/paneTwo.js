import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, Card,
    Input, Button, message, Popconfirm,
} from 'antd';

import utils from 'utils';

import ThemeDomainModal from './paneTwoModal';
import BasePane from './basePane';

const Option = Select.Option;
const FormItem = Form.Item;

export default class ThemeDomain extends BasePane {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.setState({
            params: Object.assign(this.state.params, { 
                type: 2, // 主题域	
            }),
        }, this.loadData)
    }

    initColumns = () => {
        return [{
            title: '主题域名称',
            dataIndex: 'alarmName',
            key: 'alarmName',
        }, {
            width: 80,
            title: '主题域说明',
            dataIndex: 'taskName',
            key: 'taskName',
        }, {
            width: 80,
            title: '主题域前缀',
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
                        <a onClick={() => { this.delete(record) }}>忽略</a>
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
                <ThemeDomainModal 
                    data={ modalData }
                    handOk={ this.update }
                    handCancel={ () => this.setState({ modalVisible: false })}
                    visible={ modalVisible }
                />
            </div>
        )
    }

}