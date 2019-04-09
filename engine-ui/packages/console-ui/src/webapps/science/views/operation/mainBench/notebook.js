import React, { PureComponent } from 'react';
import { Card, Input, Table, Row, Col, Button, Pagination } from 'antd';
import { offlineTaskPeriodFilter } from '../../../comm/const.js'
const Search = Input.Search;
class Notebook extends PureComponent {
    state = {
        data: [],
        loading: false,
        pagination: {
            current: 1,
            total: 20
        },
        selectedRowKeys: [],
        params: {
            search: ''
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
            }
        }, this.getTableData)
    }
    handlePaginationChange = (page, pageSize) => {
        this.setState({
            pagination: {
                ...this.state.pagination,
                current: page
            }
        }, this.getTableData)
    }
    handleForzenTasks = (flag) => {
        if (flag === 0) {
            // 冻结实验
        } else if (flag === 1) {
            // 解冻实验
        }
    }
    getTableData = () => {
        console.log('getTableData');
        this.setState({
            data: [{
                id: 1,
                experiment: 1,
                submitTime: 1,
                taskPeriodId: 1,
                creator: 1
            }, {
                id: 2,
                experiment: 1,
                submitTime: 1,
                taskPeriodId: 1,
                creator: 1
            }],
            loading: false,
            selectedRowKeys: [] // 重置选中的条数
        })
    }
    initCol = () => {
        return [{
            width: '25%',
            title: 'Notebook名称',
            dataIndex: 'notebook',
            key: 'notebook'
        }, {
            width: '25%',
            title: '提交时间',
            dataIndex: 'submitTime',
            key: 'submitTime'
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
            dataIndex: 'creator',
            key: 'creator'
        }]
    }
    tableFooter = () => {
        return (
            <Row>
                <Col span={12}>
                    <Button
                        style={{ marginRight: 10 }}
                        onClick={this.handleForzenTasks(0)}
                        size="small"
                        type="primary"
                    >
                        冻结实验
                    </Button>
                    <Button
                        size="small"
                        onClick={this.handleForzenTasks(1)}
                    >
                        解冻实验
                    </Button>
                </Col>
                <Col span={12}>
                    <Pagination
                        onChange={this.handlePaginationChange}
                        current={this.state.pagination.current}
                        total={this.state.pagination.total} />
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
                            placeholder='按Notebook名称搜索'
                            style={{ width: 267 }} />
                    }
                    extra={<a href="javascript:void(0)">前往离线计算运维中心，查看实例运行情况</a>}>
                    <Table
                        rowSelection={rowSelection}
                        rowKey="id"
                        className='m-table'
                        loading={loading}
                        columns={this.initCol()}
                        dataSource={data}
                        pagination={false}
                        footer={this.tableFooter}
                    />
                </Card>
            </>
        );
    }
}

export default Notebook;
