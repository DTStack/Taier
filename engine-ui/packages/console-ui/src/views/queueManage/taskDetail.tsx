import * as React from 'react';
import { get } from 'lodash';
import { Table, Card, message, Radio, Row, Col, Checkbox, Dropdown, Menu, Icon } from 'antd'

import utils from 'dt-common/src/utils';
import styled from 'styled-components';

import '../../styles/main.scss'
import { displayTaskStatus } from '../../consts/clusterFunc';
import ViewDetail from '../../components/viewDetail';
import KillTask from '../../components/killTask';
import Api from '../../api/console';
import GoBack from '../../components/go-back';
import KillAllTask from '../../components/killAllTask';

import { JobStageText, JobStage } from '../../consts'

const PAGE_SIZE = 10;
const RadioGroup = Radio.Group;
const RadioButton = Radio.Button;

const HeaderColTxt = styled.span`
    margin-left: 24px;
`;

const getURLParam = utils.getParameterByName;

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
                            total: get(res, 'data.total', 0)
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
        const taskParams = get(JSON.parse(record.jobInfo), 'taskParams', '');
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
        const { table } = this.state;
        return [
            {
                title: '序号',
                dataIndex: 'id',
                width: '80px',
                render (text, record, index) {
                    return PAGE_SIZE * (table.pageIndex - 1) + (index + 1);
                }
            },
            {
                title: '任务名称',
                dataIndex: 'jobName',
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
        const columns = this.initTableColumns();
        const {
            killResource, resource, node, isKillAllTasks, isShowTaskParams,
            dataSource, table, selectedRowKeys, killTaskInfo, engineType, radioValue,
            isShowViewDetail, isShowKill, isShowAllKill, clusterName
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

        const cardTitle = <div style={{ fontSize: '12px', color: '#333333', fontWeight: 'normal' }}>
            <span>总任务数：{ table.total || 0 }</span>
            <HeaderColTxt>集群：{ clusterName }</HeaderColTxt>
            <HeaderColTxt>节点：{ node }</HeaderColTxt>
            <HeaderColTxt>计算类型：{ engineType }</HeaderColTxt>
            <span className="right pointer" onClick={this.getDetailTaskList}><Icon type="sync" /></span>
        </div>;

        const totalModel = isKillAllTasks ? (radioValue === 1 ? 0 : 1) : undefined;

        return (
            <div>
                <div style={{ margin: 20 }}><GoBack size="default" type="textButton" style={{ fontSize: '16px', color: '#333333', top: 0, letterSpacing: 0 }}> 返回</GoBack></div>
                <div className="box-2 m-card">
                    <Card title={cardTitle}>
                        <div style={{ margin: '16px 20px' }}>
                            <RadioGroup onChange={this.changeRadioValue.bind(this)} value={this.state.radioValue}>
                                {
                                    Object.getOwnPropertyNames(JobStageText).map(statusValue =>
                                        <RadioButton key={statusValue} value={statusValue}>{JobStageText[statusValue]}</RadioButton>
                                    )
                                }
                            </RadioGroup>
                        </div>
                        <Table
                            rowKey={(record: any) => {
                                return record.jobId
                            }}
                            loading={loading}
                            className="m-table s-table q-table detail-table"
                            pagination={this.getPagination()}
                            rowSelection={rowSelection}
                            dataSource={dataSource}
                            columns={columns}
                            onChange={this.onTableChange}
                            footer={this.tableFooter}
                        />
                    </Card>
                </div>
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
