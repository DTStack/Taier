import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, Card,
    Input, Button, message, Popconfirm,
} from 'antd';

import utils from 'utils';

import BasePane from './basePane';
import PaneSix from './paneSix';
import DeriveIndexModal from './paneSevenModal';
import Api from '../../../api/dataModel';

const Option = Select.Option;
const FormItem = Form.Item;

export default class DeriveIndexDefine extends PaneSix {

    componentDidMount() {
        this.setState({
            params: Object.assign(this.state.params, { 
                type: 2, // 原子指标
            }),
        }, this.loadData)
    }

    initColumns = () => {
        return [{
            title: '衍生指标名称',
            width: 120,
            dataIndex: 'columnNameZh',
            key: 'columnNameZh',
        }, {
            title: '指标命名',
            width: 120,
            dataIndex: 'columnName',
            key: 'columnName',
        }, {
            width: 100,
            title: '数据类型',
            dataIndex: 'dataType',
            key: 'dataType',
        }, {
            title: '指标口径',
            dataIndex: 'modelDesc',
            key: 'modelDesc',
        }, {
            width: 120,
            title: '最后修改人',
            dataIndex: 'userName',
            key: 'userName',
        }, {
            width: 150,
            title: '最后修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text),
        }, {
            title: '操作',
            width: 80,
            key: 'operation',
            render: (record) => {
                return (
                    <div key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>修改</a>
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

        const { loading, table, modalVisible, modalData } = this.state;

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
                            <FormItem label="">
                                <Input.Search
                                    placeholder="按指标名称搜索"
                                    style={{ width: 200 }}
                                    size="default"
                                    onChange={ this.changeSearchName }
                                    onSearch={ this.loadData }
                                    ref={ el => this.searchInput = el }
                                />
                            </FormItem>
                        </Form>
                    }

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
                            pagination={pagination}
                            loading={loading}
                            columns={this.initColumns()}
                            onChange={(pagination) => this.changeParams('currentPage', pagination.current )}
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