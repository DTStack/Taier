import * as React from 'react';
import { get } from 'lodash';
import { Table, message, Radio, Row, Col, Icon,
    Dropdown, Menu, Breadcrumb, Pagination, Tooltip } from 'antd'
import { Utils } from '@dtinsight/dt-utils';
import CopyIcon from '../../components/copy-icon';
import utils from '../../utils';

import { displayTaskStatus } from '../../consts/clusterFunc';
import ViewDetail from '../../components/viewDetail';
import KillTask from '../../components/killTask';
import Api from '../../api/console';
import KillAllTask from '../../components/killAllTask';

import { JobStageText, JobStage } from '../../consts'

const RadioGroup = Radio.Group;
const RadioButton = Radio.Button;

const PAGE_SIZE = 15;
const getURLParam = Utils.getParameterByName;

type fixedType = 'right' | 'left'

class TaskDetail extends React.Component<any, any> {
    state = {
        dataSource: [],
        table: {
            pageIndex: 1,
            total: 0,
            loading: false
        },
        jobName: '',
        clusterName: undefined,
        node: undefined,
        jobResource: undefined, // 实例资源
        engineType: undefined,

        // 查看详情
        isShowViewDetail: false,
        // 查看任务参数
        isShowTaskParams: false,

        resource: {}, // modal 所要操作的 record
        // 杀任务
        // 多个任务id
        killIds: [],
        isShowKill: false,
        killResource: {},

        // 单选框值
        radioValue: null,
        // 更多任务列表记录值
        selectedRowKeys: [],
        isShowAllKill: false,
        isKillAllTasks: false, // 是否杀死全部任务
        killTaskInfo: []
    }

    componentDidMount () {
        this.setState({
            node: getURLParam('node'),
            clusterName: getURLParam('clusterName'),
            engineType: getURLParam('engineType'),
            jobResource: getURLParam('jobResource'),
            radioValue: getURLParam('jobStage')
        }, () => {
            this.getDetailTaskList();
        })
    }

    // 获取详细任务
    getDetailTaskList = () => {
        const { node, jobResource, radioValue } = this.state;
        const { table } = this.state;
        const { pageIndex } = table;
        this.setState({
            selectedRowKeys: [],
            killTaskInfo: []
        })
        if (jobResource) {
            this.setState({
                table: {
                    ...table,
                    loading: true
                }
            })
            Api.getViewDetail({
                nodeAddress: node,
                pageSize: PAGE_SIZE,
                currentPage: pageIndex,
                stage: radioValue,
                jobResource: jobResource
            }).then((res: any) => {
                if (res.code == 1) {
                    this.setState({
                        dataSource: get(res, 'data.data', []),
                        table: {
                            ...table,
                            loading: false,
                            total: get(res, 'data.totalCount', 0)
                        }
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

    // 请求置顶调整接口
    changeJobPriority (record: any) {
        // 获取集群
        const { jobResource, radioValue } = this.state;
        const msg = parseInt(radioValue, 10) === JobStage.Saved ? '插入队列头成功' : '置顶成功';
        return Api.stickJob({
            jobId: record.jobId,
            jobResource
        }).then((res: any) => {
            if (res.code == 1) {
                message.success(msg);
                return true;
            }
        })
    }

    // 获取分页信息
    getPagination () {
        const { pageIndex, total } = this.state.table;
        return {
            current: pageIndex,
            pageSize: PAGE_SIZE,
            total: total
        }
    }

    onPageChange = (current: any) => {
        this.setState({
            table: Object.assign(this.state.table, { pageIndex: current }),
            selectedRowKeys: [],
            killTaskInfo: []
        }, this.getDetailTaskList)
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
    showTaskParams (record: any) {
        const taskParams = record?.jobInfo?.taskParams ?? ''
        this.setState({
            isShowTaskParams: true,
            resource: taskParams
        })
    }
    handleCloseViewModal () {
        this.setState({
            isShowViewDetail: false,
            isShowTaskParams: false,
            resource: null
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

    autoRefresh () {
        this.getDetailTaskList();
    }

    // 置顶
    stickTask (record: any) {
        this.changeJobPriority(record).then((isSuccess: any) => {
            if (isSuccess) {
                this.getDetailTaskList();
            }
        });
    }

    onCheckAllChange = (e: any) => {
        let selectedRowKeys: any = [];
        let killTaskInfo: any = [];
        if (e.target.checked) {
            selectedRowKeys = this.state.dataSource.map((item: any) => item.jobId);
            killTaskInfo = this.state.dataSource.map((item: any) => {
                const { jobId, jobType, engineType, clusterName } = item;
                return {
                    jobId,
                    jobType,
                    engineType,
                    clusterName
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
            selectedRowKeys: [],
            killTaskInfo: [],
            table: {
                ...table,
                total: 0,
                pageIndex: 1
            },
            radioValue: e.target.value
        }, this.getDetailTaskList)
    }

    initTableColumns () {
        return [
            {
                title: '任务名称',
                dataIndex: 'jobName',
                fixed: 'left' as fixedType,
                width: 280,
                render (text: any, record: any) {
                    return record.jobName
                }
            },
            {
                title: '任务ID',
                dataIndex: 'jobId',
                render (text: any, record: any) {
                    return <span>
                        {record.jobId}
                        <CopyIcon
                            style={{ color: '#999', marginLeft: 4 }}
                            copyText={record.jobId}
                        />
                    </span>
                }
            },
            {
                title: '状态',
                dataIndex: 'status',
                render (text: any, record: any) {
                    return displayTaskStatus(text)
                }
            },
            {
                title: '节点',
                dataIndex: 'nodeAddress'
            },
            {
                title: '已等待',
                dataIndex: 'waitTime',
                render (text: any, record: any) {
                    return record.waitTime;
                }
            },
            {
                title: '等待原因',
                dataIndex: 'waitReason',
                width: 300,
                render (text: any, record: any) {
                    return <Tooltip title={record.waitReason} placement="top">
                        {Utils.textOverflowExchange(record.waitReason ?? '-', 20)}
                    </Tooltip>
                }
            },
            {
                title: '提交时间',
                dataIndex: 'generateTime',
                render (text: any) {
                    return utils.formatDateTime(text);
                }
            },
            {
                title: '租户',
                dataIndex: 'tenantName'
            },
            {
                title: '操作',
                dataIndex: 'deal',
                fixed: 'right' as fixedType,
                width: 250,
                render: (text: any, record: any, index: any) => {
                    const isSaved = record.stage === JobStage.Saved;
                    const isQueueing = record.stage === JobStage.Queueing;
                    const stickTxt = isQueueing ? '置顶' : isSaved ? '插入队列头' : null;
                    return (
                        <div>
                            <a onClick={this.viewDetails.bind(this, record)}>查看详情</a>
                            <span className="ant-divider" ></span>
                            <a onClick={this.killTask.bind(this, record)}>杀任务</a>
                            {stickTxt ? (
                                <span>
                                    <span className="ant-divider" ></span>
                                    <a onClick={this.stickTask.bind(this, record, index)}>{stickTxt}</a>
                                </span>
                            ) : null}
                            <span className="ant-divider" ></span>
                            <a onClick={this.showTaskParams.bind(this, record)}>任务参数</a>
                        </div>
                    )
                }
            }
        ]
    }

    tableFooter = () => {
        const { table, selectedRowKeys } = this.state;
        const menu = (
            <Menu onClick={this.handleKillAll}>
                <Menu.Item key="1" style={{ width: 118 }}>杀死全部任务</Menu.Item>
            </Menu>
        )
        return (
            <Row style={{ width: '100%' }}>
                <Col span={12}>
                    <Dropdown.Button
                        size="small"
                        trigger={['click']}
                        onClick={this.handleKillSelect}
                        type="primary"
                        overlay={menu}>
                        杀死选中任务
                    </Dropdown.Button>
                    <span style={{ marginLeft: 8, color: '#666' }}>已选中<a>{selectedRowKeys.length}</a>条</span>
                </Col>
                <Col span={12}>
                    <Pagination
                        current={table.pageIndex}
                        pageSize={PAGE_SIZE}
                        size='small'
                        total={table.total}
                        style={{ right: 0 }}
                        onChange={this.onPageChange}
                        showTotal={(total) => <span>
                            共<span style={{ color: '#3F87FF' }}>{total}</span>条数据，每页显示{PAGE_SIZE}条
                        </span>}
                    />
                </Col>
            </Row>
        )
    }

    render () {
        const className = 'c-taskDetail__container'
        const {
            killResource, resource, node, isKillAllTasks, isShowTaskParams,
            dataSource, table, selectedRowKeys, killTaskInfo, radioValue,
            isShowViewDetail, isShowKill, isShowAllKill, clusterName, jobResource
        } = this.state;
        const { total, loading } = table;

        const rowSelection: any = {
            onChange: (selectedRowKeys: any, selectedRows: any) => {
                this.setState({
                    selectedRowKeys,
                    killTaskInfo: selectedRows.map((item: any) => {
                        const { jobId, jobType, engineType } = item;
                        return {
                            jobId,
                            jobType,
                            engineType
                        }
                    })
                })
            },
            selectedRowKeys: selectedRowKeys
        };

        const totalModel = isKillAllTasks ? (radioValue === 1 ? 0 : 1) : undefined;

        return (
            <div className={`${className}`}>
                <div className={`${className}__title`}>
                    <Breadcrumb>
                        <Breadcrumb.Item>
                            <a onClick={() => { this.props.router.push('/console-ui/queueManage') }}>队列管理</a>
                        </Breadcrumb.Item>
                        <Breadcrumb.Item>{jobResource}</Breadcrumb.Item>
                    </Breadcrumb>
                    <span>集群：{clusterName}</span>
                </div>
                <div className={`${className}__radioWrap`}>
                    <RadioGroup onChange={this.changeRadioValue.bind(this)} value={this.state.radioValue}>
                        {
                            Object.getOwnPropertyNames(JobStageText).map(statusValue =>
                                <RadioButton key={statusValue} value={statusValue}>{JobStageText[statusValue]}</RadioButton>
                            )
                        }
                    </RadioGroup>
                    <Tooltip title="刷新数据">
                        <div className={`${className}__radioWrap__refresh`}>
                            <Icon type="sync"
                                onClick={this.getDetailTaskList}
                                style={{ cursor: 'pointer', color: '#94A8C6' }}
                            />
                        </div>
                    </Tooltip>
                </div>
                <Table
                    rowKey={(record: any) => {
                        return `${record.jobId}`
                    }}
                    loading={loading}
                    className='dt-table-fixed-contain-footer dt-table-border'
                    scroll={{ y: true, x: 1800 }}
                    style={{ height: 'calc(100vh - 190px)' }}
                    rowSelection={rowSelection}
                    dataSource={dataSource}
                    columns={this.initTableColumns()}
                    pagination={false}
                    footer={this.tableFooter}
                />
                <ViewDetail
                    visible={isShowViewDetail}
                    onCancel={this.handleCloseViewModal.bind(this)}
                    resource={JSON.stringify(resource, null, 2)}
                />
                <ViewDetail
                    title="任务参数"
                    visible={isShowTaskParams}
                    onCancel={this.handleCloseViewModal.bind(this)}
                    resource={resource}
                />
                <KillTask
                    visible={isShowKill}
                    onCancel={this.handleCloseKill.bind(this)}
                    killResource={killResource}
                    killSuccess={this.killSuccess.bind(this)}
                    autoRefresh={this.autoRefresh.bind(this)}
                    node={node}
                    jobResource={this.state.jobResource}
                    stage={this.state.radioValue}
                />
                <KillAllTask
                    visible={isShowAllKill}
                    totalModel={totalModel}
                    onCancel={this.handleCloseKill.bind(this)}
                    killSuccess={this.killSuccess.bind(this)}
                    autoRefresh={this.autoRefresh.bind(this)}
                    killResource={killTaskInfo}
                    node={node}
                    totalSize={total}
                    stage={this.state.radioValue}
                    jobName={this.state.jobName}
                    jobResource={this.state.jobResource}
                    clusterName={this.state.clusterName}
                />
            </div>
        )
    }
}
export default TaskDetail;
