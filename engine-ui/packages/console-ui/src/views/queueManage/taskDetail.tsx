/*
* @Author: 12574
* @Date:   2018-09-17 15:22:48
* @Last Modified by:   12574
* @Last Modified time: 2018-09-30 17:13:12
*/

import * as React from 'react';
import { Select, Table, Button, message, Radio, Row, Col, Checkbox, Dropdown, Menu } from 'antd'
import moment from 'moment';
import '../../styles/main.scss'
import { displayTaskStatus } from '../../consts/clusterFunc';
import ViewDetail from '../../components/viewDetail';
import KillTask from '../../components/killTask';
import Reorder from '../../components/reorder';
import Resource from '../../components/resource';
import Api from '../../api/console';
import KillAllTask from '../../components/killAllTask';
const PAGE_SIZE = 10;
// const Search = Input.Search;
const Option = Select.Option;
const RadioGroup = Radio.Group;

class TaskDetail extends React.Component<any, any> {
    state: any = {
        dataSource: [],
        table: {
            pageIndex: 1,
            total: 0,
            loading: false
        },
        taskList: [],
        computeType: 'batch',
        jobName: '',
        engineList: [],
        groupList: [],
        engineType: undefined,
        groupName: undefined,
        clusterName: undefined,
        node: undefined,
        // 执行顺序
        queueNum: undefined,
        // moreTaskNum: undefined,
        // 剩余资源
        isShowResource: false,
        editModalKey: null,
        // 查看详情
        isShowViewDetail: false,
        resource: {},
        // 杀任务
        // 多个任务id
        killIds: [],
        isShowKill: false,
        killResource: {},
        // 顺序调整
        priorityResource: {},
        // 置顶
        // 一键展现此任务所在group下的所有任务
        // 获取单个任务
        singleTaskInfo: undefined,
        setNode: undefined,
        isShowAll: false,
        isShowReorder: false,
        // 单选框值
        radioValue: 1,
        // 更多任务列表记录值
        isClickGroup: false,
        selectedRowKeys: [],
        isShowAllKill: false,
        isKillAllTasks: false, // 是否杀死全部任务
        killTaskInfo: []
    }
    componentDidMount () {
        const { query = {} } = this.props;
        this.searchTaskFuzzy();
        this.getEngineList();
        this.setState({
            engineType: query.engineType,
            groupName: query.groupName,
            clusterName: query.clusterName,
            node: query.node
        }, () => {
            this.getDetailTaskList();
            this.getGroupList();
        })
        this.searchTaskList();
    }
    // 获取计算类型
    changeComputeValue (value: any) {
        this.setState({
            computeType: value
        }, this.searchTaskFuzzy.bind(this))
    }
    // 获取改变模糊任务值
    changeTaskName (value: any) {
        const { table } = this.state;
        if (!value) {
            this.setState({
                dataSource: [],
                selectedRowKeys: [],
                killTaskInfo: [],
                table: {
                    ...table,
                    total: 0
                },
                jobName: '',
                isShowAll: false
            }, this.searchTaskFuzzy.bind(this))
        } else {
            this.setState({
                jobName: value
            }, this.searchTaskFuzzy.bind(this))
        }
    }
    // 模糊查询任务
    searchTaskFuzzy () {
        const computeType = this.state.computeType;
        const jobName = this.state.jobName;
        this.setState({
            taskList: []
        })
        if (jobName) {
            return Api.searchTaskFuzzy({
                computeType: computeType,
                jobName: jobName
            }).then((res: any) => {
                if (res.code == 1) {
                    const data = res.data;
                    this.setState({
                        taskList: data
                    })
                }
            })
        }
    }
    // 渲染任务下拉列表
    getTaskNameListView () {
        const taskList = this.state.taskList;
        return taskList.map((item: any, index: any) => {
            return <Option key={index} value={item}>{item}</Option>
        })
    }

    // 选中某个值
    changeSelectValue (value: any) {
        this.setState({
            jobName: value,
            engineType: undefined,
            groupName: undefined,
            clusterName: undefined
        }, this.searchTaskList.bind(this))
    }

    // 显示更多任务
    handleGroupClick () {
        const { table } = this.state;
        const { singleTaskInfo } = this.state;
        const { setNode } = this.state;
        // 获取集群
        // const clusterNameTran = singleTaskInfo[0].groupName.subString(0, singleTaskInfo[0].groupName.indexOf('_'))
        let clusterNameTran: any;
        const arr = (singleTaskInfo[0].groupName || '').split('_');
        if (arr.length == 1) {
            clusterNameTran = singleTaskInfo[0].groupName
        } else {
            for (var i = 0; i <= arr.length; i++) {
                clusterNameTran = arr[0];
            }
        }
        this.setState({
            isClickGroup: true,
            table: {
                ...table,
                loading: false,
                total: 1
            },
            radioValue: 1,
            engineType: singleTaskInfo[0].engineType,
            groupName: singleTaskInfo[0].groupName,
            clusterName: clusterNameTran,
            node: setNode
        }, this.getDetailTaskList.bind(this))
    }
    // 根据任务名搜索任务
    searchTaskList () {
        const computeType = this.state.computeType;
        const jobName = this.state.jobName;
        const { table } = this.state;
        const { pageIndex } = table;
        this.setState({
            dataSource: [],
            selectedRowKeys: [],
            killTaskInfo: []
        })
        if (jobName) {
            Api.searchTaskList({
                jobName: jobName,
                computeType: computeType,
                pageSize: PAGE_SIZE,
                currentPage: pageIndex
            }).then((res: any) => {
                if (res.code == 1) {
                    this.setState({
                        dataSource: res.data ? this.parsePluginInfo(res.data.theJob) : [],
                        // 单个任务信息
                        singleTaskInfo: res.data ? res.data.theJob : undefined,
                        // 获取执行顺序
                        queueNum: res.data,
                        setNode: res.data ? res.data.node : '',
                        // moreTaskNum: res.data.queueSize,
                        table: {
                            ...table,
                            loading: false,
                            total: 1
                        },
                        isShowAll: true
                    })
                    console.log(res);
                } else {
                    this.setState({
                        table: {
                            ...table,
                            loading: false,
                            total: 0
                        }
                    })
                }
            })
        }
    }

    // 获取集群下拉
    getClusterListOptionView () {
        const { clusterList = [] } = this.props;
        return clusterList.map((item: any, index: any) => {
            return <Option key={item.id} value={item.clusterName}>{item.clusterName}</Option>
        })
    }
    // 改变集群值
    changeClusterValue (value: any) {
        const engineType = this.state.engineType;
        this.setState({
            clusterName: value,
            engineType: engineType,
            groupName: undefined
        }, this.getGroupList.bind(this))
    }

    // 获取节点下拉
    getNodeAddressOptionView () {
        const { nodeList } = this.props;
        return nodeList.map((item: any, index: any) => {
            return <Option key={item} value={item}>{item}</Option>
        })
    }
    // 改变节点值
    changeNodeAddressValue (value: any) {
        const { table } = this.state;
        if (!value) {
            this.setState({
                dataSource: [],
                selectedRowKeys: [],
                node: value,
                groupList: [],
                groupName: undefined,
                table: {
                    ...table,
                    total: 0
                },
                killTaskInfo: []
            })
        } else {
            this.setState({
                node: value,
                dataSource: [],
                selectedRowKeys: [],
                killTaskInfo: [],
                groupName: undefined,
                table: {
                    ...table,
                    loading: false,
                    total: 0
                }
            }, this.getGroupList.bind(this))
        }
    }

    // 获取引擎下拉数据
    getEngineList () {
        return Api.getEngineList().then((res: any) => {
            if (res.code == 1) {
                const data = res.data;
                this.setState({
                    engineList: data
                })
            }
        })
    }
    getEngineListOptionView () {
        const engineList = this.state.engineList;
        return engineList.map((item: any, index: any) => {
            return <Option key={index} value={item}>{item}</Option>
        })
    }
    // 改变引擎option值
    changeEngineValue (value: any) {
        const { node } = this.state;
        const { table } = this.state;
        if (!value) {
            this.setState({
                dataSource: [],
                selectedRowKeys: [],
                killTaskInfo: [],
                engineType: undefined,
                groupName: undefined,
                node: node,
                table: {
                    ...table,
                    loading: false,
                    total: 0
                }
            }, this.getGroupList.bind(this))
        } else {
            this.setState({
                dataSource: [],
                selectedRowKeys: [],
                killTaskInfo: [],
                jobName: undefined,
                engineType: value,
                groupName: undefined,
                table: {
                    ...table,
                    loading: false,
                    total: 0
                }
            }, this.getGroupList.bind(this))
        }
    }
    getGroupList () {
        const { engineType, clusterName, node } = this.state;
        this.setState({
            groupList: []
        })
        if (engineType && node) {
            return Api.getGroupList({
                engineType: engineType,
                clusterName: clusterName,
                node: node
            })
                .then((res: any) => {
                    if (res.code == 1) {
                        const data = res.data;
                        this.setState({
                            groupList: data
                        })
                    }
                })
        } else {
            this.setState({
                dataSource: [],
                selectedRowKeys: [],
                killTaskInfo: []
            })
        }
    }
    // 获取group下拉视图
    getGroupOptionView () {
        const groupList = this.state.groupList;
        return groupList.map((item: any, index: any) => {
            return <Option key={index} value={item}>{item}</Option>
        })
    }
    changeGroupValue (value: any) {
        const { table } = this.state;
        if (!value) {
            this.setState({
                dataSource: [],
                selectedRowKeys: [],
                killTaskInfo: [],
                groupName: value,
                table: {
                    ...table,
                    loading: false,
                    total: 0
                }
            })
        } else {
            this.setState({
                groupName: value
            }, this.getDetailTaskList.bind(this))
        }
    }
    getInitialState () {

    }
    // 获取详细任务
    getDetailTaskList () {
        const { engineType, groupName, node, clusterName } = this.state;
        const { table } = this.state;
        const { pageIndex } = table;
        this.setState({
            dataSource: [],
            selectedRowKeys: [],
            killTaskInfo: []
        })
        if (engineType && groupName && node && clusterName) {
            this.setState({
                table: {
                    ...table,
                    loading: true
                }
            })
            Api.getViewDetail({
                engineType: engineType,
                groupName: groupName,
                node: node,
                pageSize: PAGE_SIZE,
                currentPage: pageIndex,
                clusterName
            }).then((res: any) => {
                if (res.code == 1) {
                    const data = res.data || {};
                    const isReducePageIndex = data.topN && data.topN.length === 0 && (data.queueSize != 0 && data.queueSize % PAGE_SIZE === 0);
                    this.setState({
                        dataSource: Array.isArray(data.topN) ? this.parsePluginInfo(data.topN) : [],
                        table: {
                            ...table,
                            loading: false,
                            total: data.queueSize,
                            pageIndex: isReducePageIndex ? pageIndex - 1 : pageIndex
                        }
                    }, () => {
                        isReducePageIndex && this.getDetailTaskList()
                    })
                } else {
                    this.setState({
                        table: {
                            ...table,
                            loading: false
                        }
                    })
                }
            })
        }
    }

    // 解析数据中PluginInfo中的clusterName
    parsePluginInfo (data: any = []) {
        return data.map(item => {
            let clusterName = item.pluginInfo ? JSON.parse(item.pluginInfo).cluster : '';
            return {
                ...item,
                clusterName
            }
        })
    }

    // 请求置顶调整接口
    /* eslint-disable */
    changeJobPriority(record: any) {
        // 获取集群
        // let clusterName = record.groupName.subString(0, record.groupName.indexOf('_'));
        // let groupName = record.groupName.subString(record.groupName.indexOf('_') + 1);
        const { node } = this.state;
        return Api.changeJobPriority({
            engineType: record.engineType,
            groupName: record.groupName,
            node: node,
            jobId: record.taskId,
            clusterName: record.clusterName,
            jobIndex: 1
        }).then((res: any) => {
            if (res.code == 1) {
                message.success('置顶成功');
                return true;
            }
        })
    }
    /* eslint-enable */
    // 获取分页信息
    getPagination () {
        const { pageIndex, total } = this.state.table;
        return {
            current: pageIndex,
            pageSize: PAGE_SIZE,
            total: total
        }
    }
    // 表格换页
    onTableChange = (pagination: any, filters: any, sorter: any) => {
        const table = Object.assign(this.state.table, { pageIndex: pagination.current })
        this.setState({
            table,
            selectedRowKeys: [],
            killTaskInfo: []
        },
        () => {
            this.getDetailTaskList();
        })
    }

    initTableColumns () {
        const { queueNum } = this.state;
        const { pageIndex } = this.state.table;
        const { radioValue } = this.state;
        return [
            {
                title: '执行顺序',
                dataIndex: 'theJobIdx',
                render (text: any, record: any, index: any) {
                    return (radioValue == 1) ? ((++index) + PAGE_SIZE * (pageIndex - 1))
                        : (queueNum.theJobIdx)
                    // return ((++index) + PAGE_SIZE * (pageIndex - 1))
                },
                width: '80px'
            },
            {
                title: '任务名称',
                dataIndex: 'taskName',
                render (text: any, record: any) {
                    return record.jobName;
                },
                width: '350px'
            },
            {
                title: '状态',
                dataIndex: 'status',
                render (text: any, record: any) {
                    return displayTaskStatus(text)
                }
            },
            {
                title: '已等待',
                dataIndex: 'waitTime',
                render (text: any, record: any) {
                    return record.waitTime;
                }
            },
            {
                title: '提交时间',
                dataIndex: 'generateTime',
                render (text: any) {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '集群',
                dataIndex: 'clusterName',
                width: '70px'
            },
            {
                title: '组件',
                dataIndex: 'engine',
                render (text: any, record: any) {
                    return record.engineType;
                },
                width: '80px'
            },
            {
                title: 'group',
                dataIndex: 'groupName',
                render (text: any, record: any) {
                    return record.groupName;
                }
            },
            {
                title: '租户',
                dataIndex: 'tenement',
                render (text: any, record: any) {
                    return record.tenantName;
                }
            },
            {
                title: '操作',
                dataIndex: 'deal',
                render: (text: any, record: any, index: any) => {
                    return (
                        <div>
                            <a onClick={this.viewDetails.bind(this, record)}>查看详情</a>
                            <span className="ant-divider" ></span>
                            <a onClick={this.killTask.bind(this, record)}>杀任务</a>
                            <span className="ant-divider" ></span>
                            <a onClick={this.sequentialAdjustment.bind(this, record)}>顺序调整</a>

                            {((++index) + PAGE_SIZE * (pageIndex - 1)) !== 1 ? (
                                <span>
                                    <span className="ant-divider" ></span>
                                    <a onClick={this.stickTask.bind(this, record, index)}>置顶</a>
                                </span>
                            ) : null}
                        </div>
                    )
                },
                width: '220px'
            }
        ]
    }

    // 剩余资源
    handleClickResource () {
        this.setState({
            isShowResource: true,
            editModalKey: Math.random()
        })
    }
    handleCloseResource () {
        this.setState({
            isShowResource: false
        })
    }
    // 刷新
    handleClickRefresh () {
        // 刷新根据引擎,group搜索出的任务
        // 刷新根据任务搜索
        const { radioValue } = this.state;
        if (radioValue == 1) {
            this.getDetailTaskList();
        } else {
            this.searchTaskList();
        }
    }

    // 杀死选中的任务
    handleKillSelect = () => {
        const selected = this.state.selectedRowKeys

        if (!selected || selected.length <= 0) {
            message.error('您没有选择任何任务！')
            return false;
        }
        this.setState({
            isKillAllTasks: false,
            isShowAllKill: true
        })
    }
    // 查看详情
    viewDetails (record: any) {
        this.setState({
            isShowViewDetail: true,
            resource: record
        })
    }
    handleCloseViewDetail () {
        this.setState({
            isShowViewDetail: false
        })
    }
    // 杀任务
    killTask (record: any) {
        this.setState({
            isShowKill: true,
            killResource: record
        })
    }
    handleCloseKill () {
        this.setState({
            isShowKill: false,
            isShowAllKill: false
        })
    }
    // kill
    killSuccess (killId: any) {
        this.setState({
            killIds: [...this.state.killIds].concat(killId)
        })
    }
    // 顺序调整
    sequentialAdjustment (record: any) {
        this.setState({
            isShowReorder: true,
            priorityResource: record
        })
    }
    autoRefresh () {
        const { radioValue } = this.state;
        if (radioValue == 1) {
            this.getDetailTaskList();
        } else {
            this.searchTaskList();
        }
    }
    handleCloseReorder () {
        this.setState({
            isShowReorder: false
        })
    }
    // 置顶
    stickTask (record: any) {
        const { radioValue } = this.state;
        this.changeJobPriority(record).then((isSuccess: any) => {
            if (isSuccess) {
                if (radioValue == 1) {
                    this.getDetailTaskList();
                } else {
                    this.searchTaskList();
                }
            }
        });
    }
    onCheckAllChange = (e: any) => {
        let selectedRowKeys: any = [];
        let killTaskInfo: any = [];
        if (e.target.checked) {
            selectedRowKeys = this.state.dataSource.map((item: any) => item.taskId);
            killTaskInfo = this.state.dataSource.map((item: any) => {
                const { taskId, groupName, jobType, engineType, computeType, clusterName } = item;
                return {
                    taskId,
                    groupName,
                    jobType,
                    engineType,
                    clusterName,
                    computeType
                }
            })
        }

        this.setState({
            selectedRowKeys,
            checkAll: e.target.checked,
            killTaskInfo
        })
    }
    handleKillAll = (e: any) => {
        this.setState({
            isShowAllKill: true,
            isKillAllTasks: true
        })
    }
    // 改变单选框值
    changeRadioValue (e: any) {
        const { table } = this.state;
        this.setState({
            dataSource: [],
            selectedRowKeys: [],
            killTaskInfo: [],
            table: {
                ...table,
                total: 0
            },
            engineType: undefined,
            groupName: undefined,
            clusterName: undefined,
            jobName: '',
            isShowAll: false,
            radioValue: e.target.value
        })
    }

    tableFooter = (currentPageData: any) => {
        const { selectedRowKeys, dataSource } = this.state;
        const indeterminate = !!selectedRowKeys.length && (selectedRowKeys.length < dataSource.length);
        const checked = !!dataSource.length && (selectedRowKeys.length === dataSource.length);
        const menu = (
            <Menu onClick={this.handleKillAll}>
                <Menu.Item key="1" style={{ width: 118 }}>杀死全部任务</Menu.Item>
            </Menu>
        )
        return (
            <Row className="table-footer">
                <Col className="inline">
                    <Checkbox
                        className="select-all"
                        indeterminate={indeterminate}
                        checked={checked}
                        onChange={this.onCheckAllChange}
                    >
                        全选
                    </Checkbox>
                </Col>
                <Col className="inline">
                    <Dropdown.Button
                        size="small"
                        trigger={['click']}
                        onClick={this.handleKillSelect}
                        type="primary"
                        overlay={menu}>
                        杀死选中任务
                    </Dropdown.Button>
                </Col>
            </Row>
        )
    }

    render () {
        const { isShowResource, isShowViewDetail, isShowKill, isShowReorder, editModalKey, isShowAllKill, isKillAllTasks } = this.state;
        const columns = this.initTableColumns();
        const { dataSource, table, selectedRowKeys, killTaskInfo } = this.state;
        const { loading } = table;
        const { resource } = this.state;
        const { killResource, priorityResource } = this.state;
        const isShowAll = this.state.isShowAll ? 'inline-block' : 'none';
        const style: any = {
            display: isShowAll
        }
        const { total } = this.state.table;
        const { clusterList } = this.props;
        const { node } = this.state;
        const { radioValue } = this.state;
        const { singleTaskInfo } = this.state;
        const rowSelection: any = {
            onChange: (selectedRowKeys: any, selectedRows: any) => {
                this.setState({
                    selectedRowKeys,
                    // killTaskInfo: selectedRows.map(({ taskId, groupName, jobType, engineType, computeType }) => {
                    killTaskInfo: selectedRows.map((item: any) => {
                        const { taskId, groupName, jobType, engineType, computeType } = item;
                        return {
                            taskId,
                            groupName,
                            jobType,
                            engineType,
                            computeType
                        }
                    })
                })
            },
            selectedRowKeys: selectedRowKeys
        };
        let totalModel = isKillAllTasks ? (radioValue === 1 ? 0 : 1) : undefined;
        return (
            <div>
                <div style={{ margin: '20px' }}>
                    <RadioGroup onChange={this.changeRadioValue.bind(this)} value={this.state.radioValue}>
                        <Radio value={1}>按group筛选</Radio>
                        <Radio value={2}>按任务搜索</Radio>
                    </RadioGroup>
                </div>
                {(radioValue == 1) ? (
                    <div className="select">
                        集群：
                        <Select
                            placeholder="请选择集群"
                            style={{ width: '150px', marginRight: '10px' }}
                            value={this.state.clusterName}
                            onChange={this.changeClusterValue.bind(this)}
                        >
                            {this.getClusterListOptionView()}
                        </Select>
                        组件：
                        <Select
                            placeholder="请选择组件"
                            style={{ width: '150px', marginRight: '10px' }}
                            onChange={this.changeEngineValue.bind(this)}
                            value={this.state.engineType}
                            allowClear
                        >
                            {this.getEngineListOptionView()}
                        </Select>
                        节点：
                        <Select
                            value={this.state.node}
                            placeholder="请选择节点"
                            style={{ width: '150px', marginRight: '10px' }}
                            onChange={this.changeNodeAddressValue.bind(this)}
                            allowClear={true}
                        >
                            {this.getNodeAddressOptionView()}
                        </Select>
                        group：
                        <Select
                            value={this.state.groupName}
                            placeholder="请选择group"
                            style={{ width: '150px', marginRight: '10px' }}
                            onChange={this.changeGroupValue.bind(this)}
                            allowClear={true}
                        >
                            {this.getGroupOptionView()}
                        </Select>
                        <div style={{ float: 'right' }}>
                            <Button type="primary" style={{ marginRight: '9px' }} onClick={this.handleClickResource.bind(this)}>剩余资源</Button>
                            <Button onClick={this.handleClickRefresh.bind(this)}>刷新</Button>
                        </div>
                    </div>
                ) : (
                    <div style={{ margin: '20px' }}>
                        计算类型: <Select
                            style={{ width: '80px', marginRight: '10px' }}
                            value={this.state.computeType}
                            onChange={this.changeComputeValue.bind(this)}
                        >
                            <Option value="batch">离线</Option>
                            <Option value="stream">实时</Option>
                        </Select>

                        <Select className="task-search"
                            mode="combobox"
                            value={this.state.jobName}
                            style={{ width: '250px' }}
                            notFoundContent="没有搜索到该任务"
                            filterOption={false}
                            onChange={this.changeTaskName.bind(this)}
                            onSelect={this.changeSelectValue.bind(this)}
                            allowClear
                            // onPressEnter={this.searchTask}
                            placeholder="输入任务名称搜索">
                            {
                                this.getTaskNameListView()
                            }
                        </Select>

                        {
                            (singleTaskInfo) ? (
                                <span style={style}>查找此任务所在<a onClick={this.handleGroupClick.bind(this)}>group</a>的所有任务</span>
                            ) : null
                        }
                        <div style={{ float: 'right' }}>
                            <Button type="primary" style={{ marginRight: '9px' }} onClick={this.handleClickResource.bind(this)}>剩余资源</Button>
                            <Button onClick={this.handleClickRefresh.bind(this)}>刷新</Button>
                        </div>
                    </div>
                )}
                <Table
                    rowKey={(record: any) => {
                        return record.taskId
                    }}
                    loading={loading}
                    // className="m-table no-card-table q-table"
                    className="m-table s-table q-table detail-table"
                    pagination={this.getPagination()}
                    rowSelection={rowSelection}
                    dataSource={dataSource}
                    columns={columns}
                    onChange={this.onTableChange}
                    footer={this.tableFooter}
                />
                <Resource
                    key={editModalKey}
                    visible={isShowResource}
                    onCancel={this.handleCloseResource.bind(this)}
                    clusterList={clusterList}
                />
                <ViewDetail
                    visible={isShowViewDetail}
                    onCancel={this.handleCloseViewDetail.bind(this)}
                    resource={resource}
                />
                <KillTask
                    visible={isShowKill}
                    onCancel={this.handleCloseKill.bind(this)}
                    killResource={killResource}
                    killSuccess={this.killSuccess.bind(this)}
                    autoRefresh={this.autoRefresh.bind(this)}
                    node={node}
                />
                <KillAllTask
                    visible={isShowAllKill}
                    onCancel={this.handleCloseKill.bind(this)}
                    killSuccess={this.killSuccess.bind(this)}
                    autoRefresh={this.autoRefresh.bind(this)}
                    killResource={killTaskInfo}
                    node={node}
                    totalModel={totalModel}
                    totalSize={total}
                    engineType={this.state.engineType}
                    groupName={this.state.groupName}
                    jobName={this.state.jobName}
                    computeType={this.state.computeType}
                    clusterName={this.state.clusterName}
                />
                <Reorder
                    visible={isShowReorder}
                    onCancel={this.handleCloseReorder.bind(this)}
                    priorityResource={priorityResource}
                    node={node}
                    autoRefresh={this.autoRefresh.bind(this)}
                    total={total}
                />
            </div>
        )
    }
}
export default TaskDetail;
