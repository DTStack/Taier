import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';

import {
    Table, Row, Col, Select, Form,
    Card, Button, message, Checkbox,
    DatePicker, Input,
} from 'antd';

import utils from 'utils';

import Api from '../../../api/dataModel';
import { TableNameCheck } from '../../../components/display'

const Option = Select.Option;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;

export default class ModelCheck extends Component {

    state = {
        table: { data: [] },
        loading: false,

        params: {
            pageIndex: 1,
            pageSize: 10,
            tableName: '',
            ignore: 0, // 1 忽略，0 不忽略
            type: '1',
        },
    }

    componentDidMount() {
        this.loadData();
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
           this.componentDidMount();
        }
    }

    loadData = () => {
        const { params } = this.state;
        this.setState({
            loading: true,
        })
        Api.getCheckList(params).then(res => {
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
            id: record.id,
            type: '1',
            ignore: record.isIgnore ? false : true,
        }).then(res => {
            if (res.code === 1) {
                this.loadData()
                message.success('已经成功忽略该表！')
            }
        })
    }

    onTableNameChange = (e) => {
        this.setState({
            params: Object.assign(this.state.params, {
                tableName: e.target.value
            })
        })
    }

    changeParams = (field, value) => {
        console.log('changeParams',value);
        
        const params = {
            [field]: value,
        }

        if (field !== 'pageIndex') params.pageIndex = 1;

        this.setState(Object.assign(this.state.params, params), this.loadData)
    }

    initColumns = () => {
        return [{
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
        }, {
            width: 80,
            title: '表描述',
            dataIndex: 'tableDesc',
            key: 'tableDesc',
        }, {
            width: 80,
            title: '模型层级',
            dataIndex: 'grade',
            key: 'grade',
        }, {
            width: 80,
            title: '主题域',
            dataIndex: 'subject',
            key: 'subject',
        }, {
            width: 80,
            title: '刷新频率',
            dataIndex: 'refreshRate',
            key: 'refreshRate',
        }, {
            width: 80,
            title: '增量标识',
            dataIndex: 'increType',
            key: 'increType',
        }, {
            title: '负责人',
            dataIndex: 'chargeUser',
            key: 'chargeUser',
        }, {
            title: '最后修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text),
        }, {
            title: '检测结果',
            dataIndex: 'checkResult',
            key: 'checkResult',
        }, {
            width: 80,
            title: '操作',
            key: 'operation',
            render: (record) => {
                const showText = record.isIgnore ? '恢复' : '忽略';
                return (
                    <div key={record.id}>
                        <Link to={`/data-model/table/modify/${record.id}`}>修改</Link>
                        <span className="ant-divider" />
                        <a onClick={() => { this.ignore(record) }}>{showText}</a>
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
            current: table.currentPage,
        };

        return (
            <div className="m-card antd-input">
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
                            <FormItem>
                                <Input.Search
                                    placeholder="按表名搜索"
                                    style={{ width: 200 }}
                                    size="default"
                                    onChange={this.onTableNameChange}
                                    onSearch={this.loadData}
                                />
                            </FormItem>
                            <FormItem label="检测结果">
                                <Select
                                    allowClear
                                    showSearch
                                    mode="multiple"
                                    size="default"
                                    style={{ minWidth: 200, marginTop: 3 }}
                                    placeholder="选择检测结果"
                                    optionFilterProp="name"
                                    onChange={(value) => this.changeParams('triggerType', value)}
                                >
                                    <Option value="0">规范</Option>
                                    <Option value="1">分层不合理</Option>
                                    <Option value="2">主题域不合理</Option>
                                    <Option value="3">刷新频率不合理</Option>
                                    <Option value="4">增量不合理</Option>
                                </Select>
                            </FormItem>
                            <FormItem>
                                <Checkbox
                                    onChange={(e) => this.changeParams('ignore', e.target.checked ? 1 : 0)}
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
                        onChange={(pagination) => this.changeParams('pageIndex', pagination.current)}
                        dataSource={table.data || []}
                    />
                </Card>
            </div>
        )
    }

}
