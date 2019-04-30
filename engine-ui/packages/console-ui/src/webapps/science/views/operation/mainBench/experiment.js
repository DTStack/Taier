import React, { PureComponent } from 'react';
import { Card, Input, Table, Row, Col, Button, Pagination, message } from 'antd';

import Api from '../../../api'
import { offlineTaskPeriodFilter } from '../../../comm/const.js'
import { taskType } from '../../../consts';

import utils from 'utils';

const Search = Input.Search;
class Experiment extends PureComponent {
    state = {
        data: [],
        loading: false,
        pagination: {
            current: 1,
            total: 0,
            pageSize: 20
        },
        selectedRowKeys: [],
        params: {
            search: '',
            filter: ''
        }
    }
    componentDidMount () {
        this.getTableData();
    }

    handleSearch = (value) => {
        this.setState({
            params: {
                ...this.state.params,
                search: value
            },
            pagination: {
                ...this.state.pagination,
                current: 1
            }
        }, this.getTableData)
    }
    handleTableChange = (pagination, filters, sorter) => {
        const params = Object.assign({}, this.state.params);
        params.filter = filters.taskPeriodId.length ? filters.taskPeriodId.join(',') : '';
        this.setState({
            params
        }, this.getTableData);
    }
    handlePaginationChange = (page, pageSize) => {
        this.setState({
            pagination: {
                ...this.state.pagination,
                current: page
            }
        }, this.getTableData)
    }
    handleForzenTasks = async (flag) => {
        const { selectedRowKeys } = this.state;
        let res = await Api.comm.frozenTask({
            scheduleStatus: flag,
            taskIdList: selectedRowKeys
        })
        if (res && res.code == 1) {
            message.success('操作成功！');
            this.getTableData();
        }
    }
    getTableData = async () => {
        const { params, pagination } = this.state;
        this.setState({
            loading: true,
            selectedRowKeys: []
        })
        let res = await Api.comm.queryTask({
            taskType: taskType.EXPERIMENT,
            name: params.search,
            currentPage: pagination.current,
            pageSize: pagination.pageSize,
            taskPeriodId: params.filter || undefined
        });
        if (res && res.code == 1) {
            this.setState({
                pagination: {
                    ...pagination,
                    total: res.data.totalCount
                },
                data: res.data.data
            });
        }
        this.setState({
            loading: false
        });
    }
    initCol = () => {
        return [{
            width: '25%',
            title: '实验名称',
            dataIndex: 'name',
            render (name, record) {
                return record.scheduleStatus == 0 ? `${name}（已冻结）` : name
            }
        }, {
            width: '25%',
            title: '提交时间',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render (t) {
                return utils.formatDateTime(t);
            }
        }, {
            width: '25%',
            title: '调度周期',
            dataIndex: 'taskPeriodId',
            key: 'taskPeriodId',
            render: (text) => {
                switch (text) {
                    case 0:
                        return <span>分钟任务</span>
                    case 1:
                        return <span>小时任务</span>
                    case 3:
                        return <span>周任务</span>
                    case 4:
                        return <span>月任务</span>
                    case 2:
                    default:
                        return <span>天任务</span>
                }
            },
            filters: offlineTaskPeriodFilter
        }, {
            width: '25%',
            title: '创建人',
            dataIndex: 'ownerUser.userName'
        }]
    }
    tableFooter = () => {
        return (
            <Row>
                <Col span={12}>
                    <Button
                        style={{ marginRight: 10 }}
                        onClick={this.handleForzenTasks.bind(this, 0)}
                        size="small"
                        type="primary"
                    >
                        冻结实验
                    </Button>
                    <Button
                        size="small"
                        onClick={this.handleForzenTasks.bind(this, 1)}
                    >
                        解冻实验
                    </Button>
                </Col>
                <Col span={12}>
                    <Pagination
                        onChange={this.handlePaginationChange}
                        {...this.state.pagination} />
                </Col>
            </Row>
        )
    }
    render () {
        const { data, loading, selectedRowKeys } = this.state;
        const rowSelection = {
            selectedRowKeys,
            onChange: (selectedRowKeys) => this.setState({ selectedRowKeys })
        }
        return (
            <>
                <Card
                    noHovering
                    bordered={false}
                    title={
                        <Search
                            onSearch={this.handleSearch}
                            placeholder='按实验名称搜索'
                            style={{ width: 267 }} />
                    }
                    extra={<a href="javascript:void(0)">前往开发套件运维中心，查看实例运行情况</a>}>
                    <Table
                        rowSelection={rowSelection}
                        rowKey="id"
                        className='m-table'
                        loading={loading}
                        columns={this.initCol()}
                        onChange={this.handleTableChange}
                        dataSource={data}
                        pagination={false}
                        footer={this.tableFooter}
                    />
                </Card>
            </>
        );
    }
}

export default Experiment;
