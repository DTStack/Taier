import * as React from 'react';
import { Link, hashHistory } from 'react-router';

import { isProjectCouldEdit } from '../../../comm';
import {
    Table, Select, Form,
    message, Checkbox,
    DatePicker, Input
} from 'antd';
import moment from 'moment'
import utils from 'utils';

import Api from '../../../api/dataModel';

const Option = Select.Option;
const FormItem = Form.Item;
const { RangePicker } = DatePicker;

export default class FieldCheck extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        const { triggerType2, startTime2, endTime2 } = this.props.location.query;
        this.state = {
            table: { data: [] },
            loading: false,
            params: {
                pageIndex: 1,
                pageSize: 10,
                tableName: '',
                ignore: 0, // 1 忽略，0 不忽略
                type: '2',
                triggerType: (triggerType2 && triggerType2.split(',')) || [],
                startTime: startTime2,
                endTime: endTime2
            }
        }
    }

    componentDidMount () {
        this.loadData();
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
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
        const pathQuery: any = {
            currentTab: '2',
            startTime2: startTime,
            endTime2: endTime,
            triggerType2: triggerType.join(',') || undefined
        };
        hashHistory.push({
            pathname,
            query: Object.assign(query, pathQuery)
        })
        this.setState({
            loading: true
        })
        Api.getCheckList(params).then((res: any) => {
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

    ignore = (record: any) => {
        Api.ignoreCheck({
            id: record.id,
            type: '2'
        }).then((res: any) => {
            if (res.code === 1) {
                this.loadData()
                message.success('已经成功忽略该字段！')
            }
        })
    }

    changeParams = (field: any, value: any) => {
        const params: any = {
            [field]: value
        }

        if (field !== 'pageIndex') params.pageIndex = 1;

        this.setState(Object.assign(this.state.params, params), this.loadData)
    }

    onTableNameChange = (e: any) => {
        this.setState({
            params: Object.assign(this.state.params, {
                columnName: e.target.value
            })
        })
    }

    initColumns = () => {
        const { project, user } = this.props;
        const couldEdit = isProjectCouldEdit(project, user);
        return [{
            title: '字段名称',
            dataIndex: 'columnName',
            key: 'columnName'
        }, {
            title: '字段描述',
            dataIndex: 'columnDesc',
            key: 'columnDesc'
        }, {
            title: '字段类型',
            dataIndex: 'columnType',
            key: 'columnType'
        }, {
            title: '所属表',
            dataIndex: 'tableName',
            key: 'tableName'
        }, {
            title: '最近检测时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: (text: any) => utils.formatDateTime(text)
        }, {
            title: '检测结果',
            dataIndex: 'checkResult',
            key: 'checkResult'
        }, {
            title: '操作',
            key: 'operation',
            render: (record: any) => {
                const showText = record.isIgnore ? '恢复' : '忽略';

                return (
                    <div key={record.id}>
                        <Link disabled={!couldEdit} to={`/data-model/table/modify/${record.tableId}`}>修改</Link>
                        <span className="ant-divider" />
                        <a disabled={!couldEdit} onClick={() => { this.ignore(record) }}>{showText}</a>
                    </div>
                )
            }
        }]
    }

    onChangeTime = (value: any) => {
        this.changeParams('startTime', this.handleMoment(value[0]));
        this.changeParams('endTime', this.handleMoment(value[1]));
    }

    handleMoment = (moment: any) => { // moment对象转成时间戳(毫秒数)
        return moment && moment.unix() * 1000
    }

    onOk = (value: any) => {
        console.log(value);
        this.changeParams('startTime', this.handleMoment(value[0]));
        this.changeParams('endTime', this.handleMoment(value[1]));
    }

    render () {
        const { loading, table, params } = this.state
        console.log('params', params);

        const pagination: any = {
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
                            placeholder="按字段名搜索"
                            style={{ width: 200 }}
                            size="default"
                            onChange={ this.onTableNameChange }
                            onSearch={ this.loadData }
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
                            onChange={(value: any) => this.changeParams('triggerType', value)}
                        >
                            <Option value="0">规范</Option>
                            <Option value="1">字段名称不合理</Option>
                            <Option value="2">字段类型不合理</Option>
                            <Option value="3">字段描述不合理</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="最后修改时间">
                        <RangePicker
                            size="default"
                            value={(params.startTime && params.endTime && [moment(Number(params.startTime)), moment(Number(params.endTime))]) || []}
                            showTime={{ format: 'HH:mm:ss' }}
                            format="YYYY-MM-DD HH:mm:ss"
                            placeholder={['开始时间', '结束时间']}
                            onChange={this.onChangeTime}
                            style={{ width: 200 }}
                            onOk={this.onOk}
                        />
                    </FormItem>
                    <FormItem>
                        <Checkbox
                            onChange={(e: any) => this.changeParams('ignore', e.target.checked ? 1 : 0)}
                        >
                            已忽略
                        </Checkbox>
                    </FormItem>
                </Form>
                <Table
                    rowKey="id"
                    className="dt-ant-table dt-ant-table--border"
                    pagination={pagination}
                    loading={loading}
                    columns={this.initColumns()}
                    onChange={(pagination: any) => this.changeParams('pageIndex', pagination.current)}
                    dataSource={table.data || []}
                />
            </div>
        )
    }
}
