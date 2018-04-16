import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, Card,
    Input, Button, message, Popconfirm,
} from 'antd';

import utils from 'utils';

import SubjectDomainModal from './paneTwoModal';
import BasePane from './basePane';

const Option = Select.Option;
const FormItem = Form.Item;

export default class SubjectDomain extends BasePane {

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
            dataIndex: 'name',
            key: 'name',
        }, {
            width: 80,
            title: '主题域说明',
            dataIndex: 'modelDesc',
            key: 'modelDesc',
        }, {
            width: 80,
            title: '主题域前缀',
            dataIndex: 'prefix',
            key: 'prefix',
        }, {
            width: 80,
            title: '最近修改人',
            dataIndex: 'userName',
            key: 'userName',
        }, {
            title: '最后修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text),
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
                <SubjectDomainModal 
                    data={ modalData }
                    handOk={ this.update }
                    handCancel={ () => this.setState({ modalVisible: false })}
                    visible={ modalVisible }
                />
            </div>
        )
    }

}