import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, Card,
    Input, Button, message, Popconfirm,
} from 'antd';

import utils from 'utils';

import BasePane from './basePane';
import FreshFrequencyModal from './paneThreeModal';

const Option = Select.Option;
const FormItem = Form.Item;

export default class FreshFrequency extends BasePane {
    
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.setState({
            params: Object.assign(this.state.params, { 
                type: 3, // 刷新频率
            }),
        }, this.loadData)
    }

    initColumns = () => {
        return [{
            width: 120,
            title: '刷新频率',
            dataIndex: 'name',
            key: 'name',
        }, {
            title: '刷新方式标识',
            dataIndex: 'prefix',
            key: 'prefix',
        }, {
            title: '刷新方式描述',
            dataIndex: 'modelDesc',
            key: 'modelDesc',
        }, {
            width: 150,
            title: '最近修改人',
            dataIndex: 'userName',
            key: 'userName',
        }, {
            width: 150,
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text),
        }, {
            width: 80,
            title: '操作',
            key: 'operation',
            render: (record) => {
                return (
                    <div key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>编辑</a>
                        <span className="ant-divider" />
                        <Popconfirm 
                            title="确定删除此条记录吗?" 
                            onConfirm={() => { this.delete(record) }}
                            okText="是" cancelText="否"
                        >
                            <a>删除</a>
                        </Popconfirm>
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
                            onClick={this.initAdd}
                        >
                            新建
                        </Button>
                    }
                >
                        <Table
                            rowKey="id"
                            className="m-table"
                            style={{marginTop: '1px'}}
                            pagination={pagination}
                            loading={loading}
                            columns={this.initColumns()}
                            onChange={this.handleTableChange}
                            dataSource={table.data || []}
                        />
                </Card>
                <FreshFrequencyModal 
                    data={ modalData }
                    handOk={ this.update }
                    handCancel={ () => this.setState({ modalVisible: false })}
                    visible={ modalVisible }
                />
            </div>
        )
    }
}