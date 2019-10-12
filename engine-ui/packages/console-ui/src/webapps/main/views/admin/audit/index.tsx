import * as React from 'react';
import { connect } from 'react-redux';
import { hashHistory } from 'react-router'
import moment from 'moment';

import {
    Table, Card,
    DatePicker, Input, Select
} from 'antd'

import utils from 'utils'

import { MY_APPS } from '../../../consts'
import Api from '../../../api'
import AppTabs from '../../../components/app-tabs'

const Search = Input.Search;
const Option = Select.Option;
const { RangePicker } = DatePicker;

interface AdminAuditState {
    active: string;
    loading: 'success' | 'loading';
    operationList: { name: string; code: number }[];
    tableData: {
        data: any[];
        totalCount?: number;
        currentPage?: number;
    };
    reqParams: {
        currentPage: number;
        operator?: string; // 操作人
        operationObject?: string; // 操作对象
        operation?: string; // 动作
        startTime: number;
        endTime: number;
        pageSize: number;
    };
}

@(connect((state: any) => {
    return {
        user: state.user,
        licenseApps: state.licenseApps
    }
}) as any)
class AdminAudit extends React.Component<any, AdminAuditState> {
    state: AdminAuditState = {
        active: '',
        loading: 'success',
        operationList: [],
        tableData: {
            data: []
        },
        reqParams: {
            currentPage: 1,
            startTime: null,
            endTime: null,
            pageSize: 10
        }

    }
    resetFetchState (state = {}, callback: () => void) {
        this.setState({
            tableData: {
                data: []
            },
            reqParams: {
                currentPage: 1,
                operator: undefined,
                operationObject: undefined,
                operation: undefined,
                startTime: null,
                endTime: null,
                pageSize: 10
            },
            ...state
        }, callback)
    }
    componentDidMount () {
        const { user } = this.props
        // 非Root 用户重定向到首页
        if (!user.isRoot) {
            hashHistory.push('/')
        }
    }
    componentDidUpdate (prevProps: any, prevState: any) {
        const { licenseApps } = this.props

        if (this.props.licenseApps.length > 0 && prevProps.licenseApps !== this.props.licenseApps) {
            const initialApp = utils.getParameterByName('app');

            const defaultApp = licenseApps.find((licapp: any) => licapp.isShow) || [];
            const appKey = initialApp || defaultApp.id;

            this.setState({ active: appKey }, () => {
                this.fetchData();
                if (appKey == MY_APPS.API) {
                    this.getOperationList();
                }
            })
        }
    }
    fetchData = async () => {
        const { active, reqParams } = this.state;

        this.setState({
            tableData: {
                data: []
            }
        })

        const res = await Api.getSafeAuditList(active, reqParams);
        if (res.code === 1 && res.data && active == this.state.active) {
            this.setState({
                tableData: res.data
            })
        }
    }
    getOperationList = async () => {
        const { active } = this.state;
        const res = await Api.getOperationList(active);
        if (res.code === 1 && res.data) {
            this.setState({
                operationList: res.data
            })
        }
    }
    onPaneChange = (key: any) => {
        this.resetFetchState({
            active: key
        }, () => {
            this.fetchData();
            if (key == MY_APPS.API) {
                this.getOperationList();
            }
        })
    }

    initColumns = () => {
        const { active } = this.state;
        switch (active) {
            case MY_APPS.RDOS: {
                return [{
                    title: '时间',
                    dataIndex: 'createTime',
                    key: 'createTime',
                    render (time: any, record: any) {
                        return utils.formatDateTime(time);
                    }
                }, {
                    title: '操作人',
                    dataIndex: 'operator',
                    key: 'operator'
                }, {
                    title: '动作',
                    dataIndex: 'action',
                    key: 'action',
                    render (text: any) {
                        return text;
                    }
                }]
            }
            case MY_APPS.API: {
                return [{
                    title: '时间',
                    dataIndex: 'createTime',
                    key: 'createTime',
                    width: 200,
                    render (time: any, record: any) {
                        return utils.formatDateTime(time);
                    }
                }, {
                    title: '操作人',
                    dataIndex: 'operator',
                    key: 'operator',
                    width: 200
                }, {
                    title: '动作',
                    dataIndex: 'operation',
                    key: 'operation',
                    width: 150,
                    render (text: any) {
                        return text;
                    }
                }, {
                    title: '操作对象',
                    dataIndex: 'operationObject',
                    key: 'operationObject',
                    render (text: any) {
                        return text;
                    }
                }, {
                    title: '详细内容',
                    dataIndex: 'action',
                    key: 'action',
                    render (text: any) {
                        return text;
                    }
                }]
            }
            default: {
                return [];
            }
        }
    }

    onSearchNameChange = (e: any) => {
        this.setState({
            reqParams: {
                ...this.state.reqParams,
                operator: e.target.value
            }
        })
    }

    onSearchTypeChange = (e: any) => {
        this.setState({
            reqParams: {
                ...this.state.reqParams,
                operationObject: e.target.value
            }
        })
    }

    onSearch = (value: any) => {
        this.setState({
            reqParams: {
                ...this.state.reqParams,
                currentPage: 1
            }
        }, this.fetchData)
    }

    onSelectAction = (value: any) => {
        this.setState({
            reqParams: {
                ...this.state.reqParams,
                currentPage: 1,
                operation: value
            }
        }, this.fetchData)
    }

    handleTableChange = (pagination: any) => {
        this.setState({
            reqParams: {
                ...this.state.reqParams,
                currentPage: pagination.current
            }
        }, this.fetchData)
    }

    onRangePickerChange = (arr: any) => {
        this.setState({
            reqParams: {
                ...this.state.reqParams,
                currentPage: 1,
                startTime: arr[0] ? arr[0].unix() : null,
                endTime: arr[1] ? arr[1].unix() : null
            }
        }, this.fetchData)
    }

    renderTitle = () => {
        const {
            reqParams,
            active,
            operationList
        } = this.state;
        const timePicker = <RangePicker
            size="default"
            format="YYYY-MM-DD HH:mm"
            placeholder={['开始时间', '结束时间']}
            style={{ width: '200px', marginRight: '10px' }}
            showTime={{
                defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')],
                disabledSeconds: true,
                hideDisabledOptions: true,
                disabled: true,
                format: 'HH:mm'
            } as any}
            onChange={this.onRangePickerChange} />
        const nameSearch = <Search
            placeholder="按操作人搜索"
            value={reqParams.operator}
            onChange={this.onSearchNameChange}
            style={{ width: '220px', marginRight: '10px' }}
            onSearch={this.onSearch}
        />
        switch (active) {
            case MY_APPS.RDOS: {
                return <span>
                    {timePicker}
                    {nameSearch}
                </span>
            }
            case MY_APPS.API: {
                return <span>
                    {timePicker}
                    {nameSearch}
                    <Search
                        placeholder="按操作对象搜索"
                        value={reqParams.operationObject}
                        onChange={this.onSearchTypeChange}
                        style={{ width: '220px', marginRight: '10px' }}
                        onSearch={this.onSearch}
                    />
                    <Select
                        placeholder='按动作筛选'
                        onChange={this.onSelectAction}
                        allowClear={true}
                        value={reqParams.operation}
                        style={{ width: '220px', marginRight: '10px' }}
                    >
                        {operationList.map((operation) => {
                            return <Option key={operation.code} value={operation.code}>{operation.name}</Option>
                        })}
                    </Select>
                </span>
            }
        }
        const title = (
            <span>
                <RangePicker
                    size="default"
                    format="YYYY-MM-DD"
                    placeholder={['开始时间', '结束时间']}
                    style={{ float: 'left', width: '200px', margin: '10px 10px 0 0' }}
                    onChange={this.onRangePickerChange} />

                <Search
                    placeholder="按操作人搜索"
                    value={reqParams.operator}
                    onChange={this.onSearchNameChange}
                    style={{ width: '220px' }}
                    onSearch={this.onSearch}
                />
            </span>
        )

        return title
    }

    renderPane = () => {
        const { tableData, loading } = this.state;

        const pagination = {
            total: tableData.totalCount,
            defaultPageSize: 10,
            current: tableData.currentPage
        };

        return (
            <Card
                bordered={false}
                noHovering
                title={this.renderTitle()}
                extra={null}
            >
                <Table
                    rowKey="userId"
                    className="m-table"
                    columns={this.initColumns()}
                    onChange={this.handleTableChange}
                    loading={loading === 'loading'}
                    pagination={pagination}
                    dataSource={tableData.data || []}
                />
            </Card>
        )
    }

    render () {
        const { apps, licenseApps } = this.props

        const {
            active
        } = this.state

        const content = this.renderPane();
        const finalApps = apps.filter((app: any) => app.id == MY_APPS.RDOS || app.id == MY_APPS.API);

        return (
            <div className="user-admin">
                <h1 className="box-title">安全审计</h1>
                <div className="box-2 m-card" style={{ height: '785px' }}>
                    <AppTabs
                        apps={finalApps}
                        licenseApps={licenseApps}
                        activeKey={active}
                        content={content}
                        onPaneChange={this.onPaneChange}
                    />
                </div>
            </div>
        )
    }
}

export default AdminAudit
