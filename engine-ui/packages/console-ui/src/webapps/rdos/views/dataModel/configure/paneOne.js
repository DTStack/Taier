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
            dataIndex: 'id',
            key: 'id',
        }, {
            width: 100,
            title: '层级名称',
            dataIndex: 'name',
            key: 'name',
        }, {
            title: '层级说明',
            width: 200,
            dataIndex: 'modelDesc',
            key: 'modelDesc',
        }, {
            title: '层级前缀',
            dataIndex: 'prefix',
            key: 'prefix',
        }, {
            title: '生命周期',
            dataIndex: 'lifeDay',
            key: 'lifeDay',
        }, {
            title: '是否记入层级依赖',
            dataIndex: 'depend',
            key: 'depend',
            render: depend => depend === 1 ? '是' : '否',
        }, {
            title: '最近修改人',
            dataIndex: 'userName',
            key: 'userName',
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
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