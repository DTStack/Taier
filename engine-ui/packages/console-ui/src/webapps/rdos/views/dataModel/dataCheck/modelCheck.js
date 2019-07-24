import React, { Component } from 'react';
import { Link, hashHistory } from 'react-router';

import { isProjectCouldEdit } from '../../../comm';
import {
    Table, Select, Form,
    message, Checkbox,
    DatePicker, Input
} from 'antd';

import moment from 'moment';
import utils from 'utils';

import Api from '../../../api/dataModel';

const Option = Select.Option;
const FormItem = Form.Item;
const { RangePicker } = DatePicker;

export default class ModelCheck extends Component {
    constructor (props) {
        super(props)
        const { triggerType1, startTime1, endTime1 } = this.props.location.query;
        this.state = {
            table: { data: [] },
            loading: false,
            params: {
                pageIndex: 1,
                pageSize: 10,
                tableName: '',
                ignore: 0, // 1 忽略，0 不忽略
                type: '1',
                triggerType: (triggerType1 && triggerType1.split(',')) || [],
                startTime: startTime1,
                endTime: endTime1
            }
        }
    }

    componentDidMount () {
        this.loadData();
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.componentDidMount();
        }
    }

    loadData = () => {
        const { params } = this.state;
        const { startTime, endTime, triggerType } = params;
        const { pathname, query } = this.props.location;

        const pathQuery = {
            currentTab: '1',
            startTime1: startTime,
            endTime1: endTime,
            triggerType1: triggerType.join(',') || undefined
        };
        hashHistory.push({
            pathname,
            query: Object.assign(query, pathQuery)
        })
        this.setState({
            loading: true
        })
        Api.getCheckList(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data
                })
            }
            this.setState({
                loading: false
            })
        })
    }

    ignore = (record) => {
        Api.ignoreCheck({
            id: record.id,
            type: '1',
            ignore: !record.isIgnore
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
        const params = {
            [field]: value
        }

        if (field !== 'pageIndex') params.pageIndex = 1;

        this.setState(Object.assign(this.state.params, params), this.loadData)
    }

    initColumns = () => {
        const { project, user } = this.props;
        const couldEdit = isProjectCouldEdit(project, user);
        return [{
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName'
        }, {
            width: 80,
            title: '表描述',
            dataIndex: 'tableDesc',
            key: 'tableDesc'
        }, {
            width: 80,
            title: '模型层级',
            dataIndex: 'grade',
            key: 'grade'
        }, {
            width: 80,
            title: '主题域',
            dataIndex: 'subject',
            key: 'subject'
        }, {
            width: 80,
            title: '刷新频率',
            dataIndex: 'refreshRate',
            key: 'refreshRate'
        }, {
            width: 80,
            title: '增量标识',
            dataIndex: 'increType',
            key: 'increType'
        }, {
            title: '负责人',
            dataIndex: 'chargeUser',
            key: 'chargeUser'
        }, {
            title: '最后修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text)
        }, {
            title: '检测结果',
            dataIndex: 'checkResult',
            key: 'checkResult'
        }, {
            width: 80,
            title: '操作',
            key: 'operation',
            render: (record) => {
                const showText = record.isIgnore ? '恢复' : '忽略';
                return (
                    <div key={record.id}>
                        <Link disabled={!couldEdit} to={`/data-model/table/modify/${record.id}`}>修改</Link>
                        <span className="ant-divider" />
                        <a disabled={!couldEdit} onClick={() => { this.ignore(record) }}>{showText}</a>
                    </div>
                )
            }
        }]
    }

    onChangeTime = (value) => {
        this.changeParams('startTime', this.handleMoment(value[0]));
        this.changeParams('endTime', this.handleMoment(value[1]));
    }

    handleMoment = (moment) => { // moment对象转成时间戳(毫秒数)
        return moment && moment.unix() * 1000
    }

    onOk = (value) => {
        this.changeParams('startTime', this.handleMoment(value[0]));
        this.changeParams('endTime', this.handleMoment(value[1]));
    }

    render () {
        const { loading, table, params } = this.state

        const pagination = {
            total: table.totalCount,
            defaultPageSize: 10,
            current: table.currentPage
        };
        return (
            <div className="m-card antd-input">
                <Form
                    className="m-form-inline"
                    layout="inline"
                    style={{ margin: '10px 0 0 20px' }}
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
                            value={params.triggerType}
                            style={{ minWidth: 126, marginTop: 3 }}
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
                    <FormItem label="最后修改时间">
                        <RangePicker
                            size="default"
                            value={params.startTime && params.endTime && [moment(Number(params.startTime)), moment(Number(params.endTime))]}
                            showTime={{ format: 'HH:mm:ss' }}
                            format="YYYY-MM-DD HH:mm:ss"
                            placeholder={['开始时间', '结束时间']}
                            onChange={this.onChangeTime}
                            style={{ width: 200 }}
                            // ranges={{ Today: [moment(todayRange.today0), moment(todayRange.today24)]}}
                            onOk={this.onOk}
                        />
                    </FormItem>
                    <FormItem>
                        <Checkbox
                            onChange={(e) => this.changeParams('ignore', e.target.checked ? 1 : 0)}
                        >
                                已忽略
                        </Checkbox>
                    </FormItem>
                </Form>
                <Table
                    rowKey="id"
                    className="m-table"
                    pagination={pagination}
                    loading={loading}
                    columns={this.initColumns()}
                    onChange={(pagination) => this.changeParams('pageIndex', pagination.current)}
                    dataSource={table.data || []}
                />

            </div>
        )
    }
}
