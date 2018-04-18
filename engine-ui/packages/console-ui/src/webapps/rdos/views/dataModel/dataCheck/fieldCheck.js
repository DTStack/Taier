import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, 
    Card, Button, message, Checkbox,
    DatePicker,
} from 'antd';

import utils from 'utils';
import Api from '../../../api/dataModel';
import { FieldNameCheck } from '../../../components/display'

const Option = Select.Option;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;

export default class FieldCheck extends Component {

    state ={
        table: {data: []},
        loading: false,

        params: {
            currentPage: 1,
            pageSize: 20,
            ignore: 0, // 1 忽略，0 不忽略
            type: '2',
        },
    }

  
    componentDidMount() {
        this.loadData();
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
            monitorId: record.id,
            type: '2',
        }).then(res => {
            if (res.code === 1) {
                this.loadData()
                message.success('已经成功忽略该字段！')
            }
        })
    }

    changeParams = (field, value) => {
        let params = Object.assign(this.state.params);
        if (field === 'range' && value && value.length > 0) {
            params.startTime = value[0].valueOf();
            params.endTime = value[1].valueOf();
        } else {
            params[field] = value;
        }
        this.setState({
            params,
        }, this.loadData)
    }

    initColumns = () => {
        return [{
            title: '字段名称',
            dataIndex: 'columnName',
            key: 'columnName',
        }, {
            width: 80,
            title: '字段描述',
            dataIndex: 'description',
            key: 'description',
        }, {
            width: 80,
            title: '字段类型',
            dataIndex: 'dataType',
            key: 'dataType',
        }, {
            width: 80,
            title: '所属表',
            dataIndex: 'tableName',
            key: 'tableName',
        }, {
            title: '最后修改人',
            dataIndex: 'modifyUserName',
            key: 'modifyUserName',
        }, {
            title: '最后修改时间',
            dataIndex: 'lastModify',
            key: 'lastModify',
            render: text => utils.formatDateTime(text),
        }, {
            title: '检测结果',
            dataIndex: 'triggerType',
            key: 'triggerType',
            render: value => <FieldNameCheck value={value} />,
        }, {
            title: '操作',
            key: 'operation',
            render: (record) => {
                return (
                    <div key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>修改</a>
                        <span className="ant-divider" />
                        <a onClick={() => { this.ignore(record) }}>忽略</a>
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
                                    onChange={(value) => this.changeParams('triggerType', value)}
                                >
                                    <Option value="1">字段名称不合理</Option>
                                    <Option value="2">字段类型不合理</Option>
                                    <Option value="3">字段描述不合理</Option>
                                </Select>
                            </FormItem>
                            <FormItem label="时间">
                                <RangePicker 
                                    size="default" 
                                    style={{width: 180}} 
                                    format="YYYY-MM-DD" 
                                    onChange={(value) => this.changeParams('range', value)}
                                />
                            </FormItem>
                            <FormItem>
                                <Checkbox 
                                    onChange={(e) => this.changeParams('ignore', e.target.checked ? 1 : 0 )}
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
                            onChange={(pagination) => this.changeParams('currentPage', pagination.current )}
                            dataSource={table.data || []}
                        />
                </Card>
            </div>
        )
    }

}