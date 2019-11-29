import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux'
import { Card, Input, Table, Row, Col, Button, Pagination, message } from 'antd';

import * as experimentActions from '../../../actions/experimentActions'
import Api from '../../../api'
import { offlineTaskPeriodFilter, ScheduleStatus } from '../../../comm/const'
import { taskType } from '../../../consts';
import { appUriDict } from 'main/consts';
import { toRdosGateway } from 'funcs';

import utils from 'utils';

const Search = Input.Search;

@(connect((state: any) => {
    return {
        tabs: state.experiment.localTabs || []
    }
}, (dispatch: any) => {
    return {
        ...bindActionCreators(experimentActions, dispatch)
    }
}) as any)

class Experiment extends React.PureComponent<any, any> {
    state: any = {
        data: [],
        loading: false,
        pagination: {
            current: 1,
            total: 0,
            pageSize: 15
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

    handleSearch = (value: any) => {
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
    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        const params = Object.assign({}, this.state.params);
        params.filter = filters.taskPeriodId.length ? filters.taskPeriodId.join(',') : '';
        this.setState({
            params
        }, this.getTableData);
    }
    handlePaginationChange = (page: any, pageSize: any) => {
        this.setState({
            pagination: {
                ...this.state.pagination,
                current: page
            }
        }, this.getTableData)
    }
    handleForzenTasks = async (flag: ScheduleStatus, taskList: any []) => {
        // flag  2 冻结实验  flag 1解冻实验
        const { selectedRowKeys } = this.state;
        let res = await Api.comm.frozenTask({
            scheduleStatus: flag,
            taskIdList: selectedRowKeys
        })
        if (res && res.code == 1) {
            message.success('操作成功！');
            // 更新算法实验调度周期组件冻结状态 后端拿不到实验id  前端根据中文名称来匹配
            let taskTabs = taskList.filter(item => selectedRowKeys.indexOf(item.id) > -1)
            this.updateLocalTabs(taskTabs, flag)
            this.getTableData();
        }
    }
    /**
     * 运维中心任务管理某个实验冻结/未冻结 更新缓存的实验列表
     * @param experimentTabs 冻结/未冻结的实验列表
     * @param flag 2 冻结实验 1 解冻实验
     */
    updateLocalTabs (experimentTabs: any[], flag: ScheduleStatus) {
        let scheduleStatus = flag === ScheduleStatus.FREZED ? ScheduleStatus.FREZED : ScheduleStatus.UNFREZED
        let { tabs } = this.props;
        tabs.forEach((tab: any) => {
            let experimentTab = experimentTabs.find((ele: any) => tab.name === ele.name)
            if (experimentTab) {
                tab.scheduleStatus = scheduleStatus;
                this.props.updateExperimentTab(tab)
            }
        })
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
            render (name: any, record: any) {
                return record.scheduleStatus == 2 ? `${name}（已冻结）` : name
            }
        }, {
            width: '25%',
            title: '提交时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render (t: any) {
                return utils.formatDateTime(t);
            }
        }, {
            width: '25%',
            title: '调度周期',
            dataIndex: 'taskPeriodId',
            key: 'taskPeriodId',
            render: (text: any) => {
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
    tableFooter = (item: any[]) => {
        return (
            <Row>
                <Col span={12}>
                    <Button
                        style={{ marginRight: 10 }}
                        onClick={this.handleForzenTasks.bind(this, ScheduleStatus.FREZED, item)}
                        // onClick={this.handleForzenTasks.bind(this, 2, item)}
                        size="small"
                        type="primary"
                    >
                        冻结实验
                    </Button>
                    <Button
                        size="small"
                        onClick={this.handleForzenTasks.bind(this, ScheduleStatus.UNFREZED, item)}
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
        const { currentProject } = this.props;
        const { data, loading, selectedRowKeys } = this.state;
        const rowSelection: any = {
            selectedRowKeys,
            onChange: (selectedRowKeys: any) => this.setState({ selectedRowKeys })
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
                    extra={<a onClick={toRdosGateway.bind(null, appUriDict.RDOS.OPERATION_MANAGER, { projectId: currentProject.refProjectId })} >前往离线计算运维中心，查看实例运行情况</a>}>
                    <Table
                        rowSelection={rowSelection}
                        rowKey="id"
                        className='dt-ant-table--border dt-ant-table--border-lr dt-ant-table'
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
