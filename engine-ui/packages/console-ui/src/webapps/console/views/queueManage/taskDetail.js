/*
* @Author: 12574
* @Date:   2018-09-17 15:22:48
* @Last Modified by:   12574
* @Last Modified time: 2018-09-30 17:13:12
*/

import React, { Component } from 'react';
import { Select, Table, Button, message, Radio } from 'antd'
import moment from 'moment';
import '../../styles/main.scss'
import ViewDetail from '../../components/viewDetail';
import KillTask from '../../components/killTask';
import Reorder from '../../components/reorder';
import Resource from '../../components/resource';
import Api from '../../api/console';
import { TASK_STATE } from '../../consts/index.js';
const PAGE_SIZE = 10;
// const Search = Input.Search;
const Option = Select.Option;
const RadioGroup = Radio.Group;
class TaskDetail extends Component {
    state = {
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
        isClickGroup: false
    }
    componentDidMount () {
        const { query = {} } = this.props;
        this.searchTaskFuzzy();
        this.getEngineList();
        this.getGroupList();
        this.setState({
            engineType: query.engineType,
            groupName: query.groupName,
            node: this.props.node
        }, this.getDetailTaskList.bind(this))
        this.searchTaskList();
    }
    // 获取计算类型
    changeComputeValue (value) {
        this.setState({
            computeType: value
        }, this.searchTaskFuzzy.bind(this))
    }
    // 获取改变模糊任务值
    changeTaskName (value) {
        const { table } = this.state;
        if (!value) {
            this.setState({
                dataSource: [],
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
            }).then(res => {
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
        return taskList.map((item, index) => {
            return <Option key={index} value={item}>{item}</Option>
        })
    }

    // 选中某个值
    changeSelectValue (value) {
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
        const clusterNameTran = singleTaskInfo[0].groupName.subString(0, singleTaskInfo[0].groupName.indexOf('_'))
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
            dataSource: []
        })
        if (jobName) {
            Api.searchTaskList({
                jobName: jobName,
                computeType: computeType,
                pageSize: PAGE_SIZE,
                currentPage: pageIndex
            }).then(res => {
                if (res.code == 1) {
                    this.setState({
                        dataSource: res.data ? res.data.theJob : [],
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
        const { clusterList } = this.props;
        return clusterList.map((item, index) => {
            return <Option key={item.id} value={item.clusterName}>{item.clusterName}</Option>
        })
    }
    // 改变集群值
    changeClusterValue (value) {
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
        return nodeList.map((item, index) => {
            return <Option key={item} value={item}>{item}</Option>
        })
    }
    // 改变节点值
    changeNodeAddressValue (value) {
        const { table } = this.state;
        if (!value) {
            this.setState({
                dataSource: [],
                node: value,
                groupList: [],
                groupName: undefined,
                table: {
                    ...table,
                    total: 0
                }
            })
        } else {
            this.setState({
                node: value,
                dataSource: [],
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
        return Api.getEngineList().then((res) => {
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
        return engineList.map((item, index) => {
            return <Option key={index} value={item}>{item}</Option>
        })
    }
    // 改变引擎option值
    changeEngineValue (value) {
        const { node } = this.state;
        const { table } = this.state;
        if (!value) {
            this.setState({
                dataSource: [],
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
                .then((res) => {
                    if (res.code == 1) {
                        const data = res.data;
                        this.setState({
                            groupList: data
                        })
                    }
                })
        } else {
            this.setState({
                dataSource: []
            })
        }
    }
    // 获取group下拉视图
    getGroupOptionView () {
        const groupList = this.state.groupList;
        return groupList.map((item, index) => {
            return <Option key={index} value={item}>{item}</Option>
        })
    }
    changeGroupValue (value) {
        const { table } = this.state;
        if (!value) {
            this.setState({
                dataSource: [],
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
        const { engineType, groupName, node } = this.state;
        const { table } = this.state;
        const { pageIndex } = table;
        this.setState({
            dataSource: []
        })
        if (engineType && groupName && node) {
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
                currentPage: pageIndex
            }).then((res) => {
                if (res.code == 1) {
                    const data = res.data || {};
                    this.setState({
                        dataSource: data.topN,
                        table: {
                            ...table,
                            loading: false,
                            total: data.queueSize
                        }
                    })
                    console.log(res);
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

    // 请求置顶调整接口
    /* eslint-disable */
    changeJobPriority (record) {
        // 获取集群
        // let clusterName = record.groupName.subString(0, record.groupName.indexOf('_'));
        // let groupName = record.groupName.subString(record.groupName.indexOf('_') + 1);
        const { node } = this.state;
        return Api.changeJobPriority({
            engineType: record.engineType,
            groupName: record.groupName,
            node: node,
            jobId: record.taskId,
            jobIndex: 1
        }).then((res) => {
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
            currentPage: pageIndex,
            pageSize: PAGE_SIZE,
            total: total
        }
    }
    // 表格换页
    onTableChange = (page, sorter) => {
        this.setState({
            table: {
                pageIndex: page.current
            }
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
                render (text, record, index) {
                    return (radioValue == 1) ? ((++index) + PAGE_SIZE * (pageIndex - 1))
                        : (queueNum.theJobIdx)
                    // return ((++index) + PAGE_SIZE * (pageIndex - 1))
                },
                width: '80px'
            },
            {
                title: '任务名称',
                dataIndex: 'taskName',
                render (text, record) {
                    return record.jobName;
                },
                width: '350px'
            },
            {
                title: '状态',
                dataIndex: 'status',
                render (text, record) {
                    switch (text) {
                        case TASK_STATE.UNSUBMIT:
                            return 'UNSUBMIT';
                        case TASK_STATE.CREATED:
                            return 'CREATED';
                        case TASK_STATE.SCHEDULED:
                            return 'SCHEDULED';
                        case TASK_STATE.DEPLOYING:
                            return 'DEPLOYING';
                        case TASK_STATE.RUNNING:
                            return 'RUNNING';
                        case TASK_STATE.FINISHED:
                            return 'FINISHED';
                        case TASK_STATE.CANCELLING:
                            return 'CANCELLING';
                        case TASK_STATE.CANCELED:
                            return 'CANCELED';
                        case TASK_STATE.FAILED:
                            return 'FAILED';
                        case TASK_STATE.SUBMITFAILD:
                            return 'SUBMITFAILD';
                        case TASK_STATE.SUBMITTING:
                            return 'SUBMITTING';
                        case TASK_STATE.RESTARTING:
                            return 'RESTARTING';
                        case TASK_STATE.MANUALSUCCESS:
                            return 'MANUALSUCCESS';
                        case TASK_STATE.KILLED:
                            return 'KILLED';
                        case TASK_STATE.SUBMITTED:
                            return 'SUBMITTED';
                        case TASK_STATE.NOTFOUND:
                            return 'NOTFOUND';
                        case TASK_STATE.WAITENGINE:
                            return 'WAITENGINE';
                        case TASK_STATE.WAITCOMPUTE:
                            return 'WAITCOMPUTE';
                        case TASK_STATE.FROZEN:
                            return 'FROZEN';
                        case TASK_STATE.ENGINEACCEPTED:
                            return 'ENGINEACCEPTED';
                        case TASK_STATE.ENGINEDISTRIBUTE:
                            return 'ENGINEDISTRIBUTE';
                        default:
                            return null;
                    }
                }
            },
            {
                title: '已等待',
                dataIndex: 'waitTime',
                render (text, record) {
                    return record.waitTime;
                }
            },
            {
                title: '提交时间',
                dataIndex: 'generateTime',
                render (text) {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '集群',
                dataIndex: 'clusterName',
                render (text, record) {
                    // let str = record.groupName;
                    // return str ? String(str).subString(0, String(str).indexOf('_')) : ''
                    const arr = (record.groupName || '').split('_');
                    if (arr.length == 1) {
                        return record.groupName
                    } else {
                        return arr[0]
                    }
                },
                width: '70px'
            },
            {
                title: '引擎',
                dataIndex: 'engine',
                render (text, record) {
                    return record.engineType;
                },
                width: '80px'
            },
            {
                title: 'group',
                dataIndex: 'groupName',
                render (text, record) {
                    return record.groupName;
                }
            },
            {
                title: '租户',
                dataIndex: 'tenement',
                render (text, record) {
                    return record.tenantName;
                }
            },
            {
                title: '操作',
                dataIndex: 'deal',
                render: (text, record, index) => {
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
            isShowResource: true
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

    // 查看详情
    viewDetails (record) {
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
    killTask (record) {
        this.setState({
            isShowKill: true,
            killResource: record
        })
    }
    handleCloseKill () {
        this.setState({
            isShowKill: false
        })
    }
    // kill
    killSuccess (killId) {
        this.setState({
            killIds: [...this.state.killIds, killId]
        })
    }
    // 顺序调整
    sequentialAdjustment (record) {
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
    stickTask (record) {
        const { radioValue } = this.state;
        this.changeJobPriority(record).then((isSuccess) => {
            if (isSuccess) {
                if (radioValue == 1) {
                    this.getDetailTaskList();
                } else {
                    this.searchTaskList();
                }
            }
        });
    }
    // 集群筛选
    onClusterChange () {

    }
    // 改变单选框值
    changeRadioValue (e) {
        const { table } = this.state;
        this.setState({
            dataSource: [],
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
    render () {
        const { isShowResource, isShowViewDetail, isShowKill, isShowReorder } = this.state;
        const columns = this.initTableColumns();
        const { dataSource, table } = this.state;
        const { loading } = table;
        const { resource } = this.state;
        const { killResource, priorityResource } = this.state;
        const isShowAll = this.state.isShowAll ? 'inline-block' : 'none';
        const style = {
            display: isShowAll
        }
        const { total } = this.state.table;
        const { clusterList } = this.props;
        const { node } = this.state;
        const { radioValue } = this.state;
        const { singleTaskInfo } = this.state;
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
                            allowClear
                        >
                            {this.getClusterListOptionView()}
                        </Select>
                        引擎：
                        <Select
                            placeholder="请选择引擎"
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
                    rowKey={(record) => {
                        return record.taskId
                    }}
                    loading={loading}
                    // className="m-table no-card-table q-table"
                    className="m-table s-table q-table"
                    pagination={this.getPagination()}
                    rowClassName={(record, index) => {
                        if (this.state.killIds.indexOf(record.taskId) > -1) {
                            return 'killTask'
                        }
                    }}
                    dataSource={dataSource}
                    columns={columns}
                    onChange={this.onTableChange}
                    footer={() => {
                        return (
                            <div style={{ lineHeight: '20px', paddingLeft: '18px' }}>
                                任务总数<span>{total}</span>个
                            </div>
                        )
                    }}
                />
                <Resource
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
