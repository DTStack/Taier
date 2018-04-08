import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, Card,
    Input, Button, message, Popconfirm,
} from 'antd';

import utils from 'utils';

import BasePane from './basePane';
import Api from '../../../api/dataModel';
import AtomIndexDefineModal from './paneSixModal';

const Option = Select.Option;
const FormItem = Form.Item;

export default class AtomIndexDefine extends BasePane {

    componentDidMount() {
        this.setState({
            params: Object.assign(this.state.params, { 
                type: 1, // 原子指标
            }),
        }, this.loadData)
    }

    loadData = () => {
        const { params } = this.state;
        this.setState({
            loading: true,
        })
        Api.getModelIndexs(params).then(res => {
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

    update = (formData) => {
        Api.addModelIndex(formData).then(res => {
            if (res.code === 1) {
                this.loadData();
            }
        })
    }

    delete = (data) => {
        const { params } = this.state;
        Api.deleteModelIndex(params).then(res => {
            if (res.code === 1) {
                this.loadData();
            }
        })
    }

    initColumns = () => {
        return [{
            title: '指标类型',
            dataIndex: 'alarmName',
            key: 'alarmName',
        }, {
            width: 80,
            title: '原子指标名称',
            dataIndex: 'taskName',
            key: 'taskName',
        }, {
            width: 80,
            title: '数据类型',
            dataIndex: 'myTrigger',
            key: 'myTrigger',
        }, {
            width: 80,
            title: '最近修改人',
            dataIndex: 'senderTypes',
            key: 'senderTypes',
        }, {
            title: '最后修改时间',
            dataIndex: 'createTime',
            key: 'createTime',
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
                <AtomIndexDefineModal 
                    data={ modalData }
                    handOk={ this.update }
                    handCancel={ () => this.setState({ modalVisible: false })}
                    visible={ modalVisible }
                />
            </div>
        )
    }

}