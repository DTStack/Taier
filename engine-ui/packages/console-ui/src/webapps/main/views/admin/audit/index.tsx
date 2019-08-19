import * as React from 'react';
import { connect } from 'react-redux';
import { hashHistory } from 'react-router'

import {
    Table, Card,
    DatePicker, Input
} from 'antd'

import utils from 'utils'

import { MY_APPS } from '../../../consts'
import Api from '../../../api'
import AppTabs from '../../../components/app-tabs'

const Search = Input.Search;
const { RangePicker } = DatePicker;

@(connect((state: any) => {
    return {
        user: state.user
    }
}) as any)
class AdminAudit extends React.Component<any, any> {
    state: any = {
        active: '',
        loading: 'success',

        tableData: {
            data: []
        },

        reqParams: {
            currentPage: 1,
            operator: undefined,
            startTime: null,
            endTime: null,
            pageSize: 10
        }

    }

    componentDidMount () {
        const { apps, user } = this.props

        if (apps && apps.length > 0) {
            const initialApp = utils.getParameterByName('app');

            const defaultApp = apps.find((app: any) => app.default);
            const appKey = initialApp || defaultApp.id;

            this.setState({ active: appKey }, () => {
                this.fetchData();
            })
        }
        // 非Root 用户重定向到首页
        if (!user.isRoot) {
            hashHistory.push('/')
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
        if (res.code === 1 && res.data) {
            this.setState({
                tableData: res.data
            })
        }
    }

    onPaneChange = (key: any) => {
        this.setState({
            active: key,
            reqParams: {
                currentPage: 1,
                operator: null
            }
        }, this.fetchData)
    }

    initColumns = () => {
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

    onSearchNameChange = (e: any) => {
        this.setState({
            reqParams: {
                ...this.state.reqParams,
                operator: e.target.value
            }
        })
    }

    onSearch = (value: any) => {
        this.setState({
            reqParams: {
                ...this.state.reqParams,
                currentPage: 1,
                operator: value
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
            reqParams
        } = this.state;

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
        const { apps } = this.props

        const {
            active
        } = this.state

        const content = this.renderPane();

        const finalApps = apps.filter((app: any) => app.id == MY_APPS.RDOS);

        return (
            <div className="user-admin">
                <h1 className="box-title">离线计算</h1>
                <div className="box-2 m-card" style={{ height: '785px' }}>
                    <AppTabs
                        apps={finalApps}
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
