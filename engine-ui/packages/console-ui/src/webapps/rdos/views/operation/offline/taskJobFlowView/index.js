import React, { Component } from 'react'
import { cloneDeep, get } from 'lodash'
import moment from 'moment';
import { Tooltip, Modal, message, Icon } from 'antd'

import { TaskInfo } from './taskInfo'
import { LogInfo } from '../taskLog'
import RestartModal from './restartModal'

import Api from '../../../../api'
import { TASK_STATUS, TASK_TYPE } from '../../../../comm/const'
import { taskStatusText } from '../../../../components/display'

import JobGraphView, {
    mergeTreeNodes, replacTreeNodeField
} from './jobGraphView';
const Mx = require('public/main/mxgraph')({
    mxBasePath: 'public/main/mxgraph',
    mxImageBasePath: 'public/main/mxgraph/images',
    mxLanguage: 'none',
    mxLoadResources: false,
    mxLoadStylesheets: false
})
const {
    mxEvent,
    mxCellHighlight,
    mxPopupMenu
} = Mx

class TaskJobFlowView extends Component {
    state = {
        selectedJob: '', // 选中的Job
        data: {}, // 数据
        loading: 'success',
        lastVertex: '',
        taskLog: {},
        logVisible: false,
        visible: false,
        visibleRestart: false,
        visibleWorkflow: false, // 是否打开工作流
        workflowData: null, // 选中的工作流节点
        graphData: null, // 图形数据
        frontPeriodsList: [], // 前周期返回数据
        nextPeriodsList: []
    }

    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const currentJob = this.props.taskJob
        const { taskJob, visibleSlidePane } = nextProps
        if (taskJob && visibleSlidePane && (!currentJob || taskJob.id !== currentJob.id)) {
            this.loadByJobId(taskJob.id);
        }
    }

    renderGraph = (data) => {
        const originData = this._originData;
        if (originData) {
            mergeTreeNodes(originData, data);
        } else {
            this._originData = cloneDeep(data);
        }
        const graphData = cloneDeep(this._originData);
        this.setState({
            graphData: graphData
        })
    }

    resetData = () => {
        this._originData = null; // 清空缓存数据
        this.setState({
            graphData: null
        })
    }

    refresh = () => {
        this.resetData();
        this.loadTaskChidren({ jobId: this.props.taskJob.id });
    }

    loadByJobId = (jobId) => {
        this.resetData();
        this.loadTaskChidren({ jobId: jobId });
    }

    loadTaskChidren = (params) => {
        const ctx = this;
        this.setState({ loading: 'loading' });
        Api.getJobChildren(params).then(res => {
            if (res.code === 1 && res.data) {
                const data = res.data
                ctx.setState({ selectedJob: data, data })
                ctx.renderGraph(res.data)
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadTaskParent = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' });
        Api.getJobParents(params).then(res => {
            if (res.code === 1 && res.data) {
                const data = res.data
                ctx.setState({ data, selectedJob: data })
                // 替换 jobVos 字段为 parentNodes
                replacTreeNodeField(res.data, 'jobVOS', 'parentNodes', 'parentNodes')
                ctx.renderGraph(res.data);
            }
            ctx.setState({ loading: 'success' })
        })
    }

    /**
     * 加载前后周期数据
     * @param menu 子菜单
     * @param params 请求参数
     * @param periodsType 前后周期类型
     */
    loadPeriodsData = (menu, params, periodsType) => {
        const ctx = this;
        const isNext = params.isAfter;
        Api.getOfflineTaskPeriods(params).then(res => {
            if (res.code === 1) {
                !isNext ? ctx.setState({
                    frontPeriodsList: res.data
                }, () => {
                    ctx.state.frontPeriodsList.map(item => {
                        const times = moment(item.cycTime).format('YYYY-MM-DD HH:mm:ss');
                        const statusText = taskStatusText(item.status);
                        return (
                            menu.addItem(`${times} (${statusText})`, null, function () {
                                ctx.loadByJobId(item.jobId);
                            }, periodsType)
                        )
                    })
                }) : ctx.setState({
                    nextPeriodsList: res.data
                }, () => {
                    ctx.state.nextPeriodsList.map(item => {
                        const times = moment(item.cycTime).format('YYYY-MM-DD HH:mm:ss');
                        const statusText = taskStatusText(item.status);
                        return (
                            menu.addItem(`${times} (${statusText})`, null, function () {
                                ctx.loadByJobId(item.jobId);
                            }, periodsType)
                        )
                    })
                })
            }
        })
    }

    loadWorkflowNodes = async (workflow) => {
        const ctx = this;
        this.setState({ loading: 'loading' });
        // 如果折叠状态，加载
        const res = await Api.getTaskJobWorkflowNodes({ jobId: workflow.id });
        if (res.code === 1) {
            if (res.data && res.data.subNodes) {
                ctx.setState({
                    visibleWorkflow: true,
                    workflowData: res.data
                })
            } else {
                message.warning('当前工作流没有子节点！');
            }
        }
        this.setState({ loading: 'success' })
    }

    stopTask = (params) => {
        Api.stopJob(params).then(res => {
            if (res.code === 1) {
                message.success('任务终止运行命令已提交！')
            }
            this.refresh()
        })
    }

    restartAndResume = (params, msg) => { // 重跑并恢复任务
        const { reload } = this.props
        Api.restartAndResume(params).then(res => {
            if (res.code === 1) {
                message.success(`${msg}命令已提交!`)
                if (reload) reload();
            } else {
                message.error(`${msg}提交失败！`)
            }
            this.refresh()
        })
    }

    initContextMenu = (graph) => {
        const ctx = this;
        const { isPro } = this.props;
        if (graph) {
            var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
            mxPopupMenu.prototype.showMenu = function () {
                var cells = graph.getSelectionCells()
                if (cells.length > 0 && cells[0].vertex) {
                    mxPopupMenuShowMenu.apply(this, arguments);
                } else return false
            };
            graph.popupMenuHandler.autoExpand = true
            graph.popupMenuHandler.factoryMethod = function (menu, cell, evt) {
                if (!cell) return;

                const currentNode = cell.data;

                const isWorkflowNode = currentNode.batchTask && currentNode.batchTask.flowId !== 0;
                const taskId = currentNode.batchTask && currentNode.batchTask.id;
                const isDelete = currentNode.batchTask && currentNode.batchTask.isDeleted === 1; // 已删除
                if (isDelete) return;
                menu.addItem('展开上游（6层）', null, function () {
                    ctx.loadTaskParent({
                        jobId: currentNode.id,
                        level: 6
                    })
                })
                menu.addItem('展开下游（6层）', null, function () {
                    ctx.loadTaskChidren({
                        jobId: currentNode.id,
                        level: 6
                    })
                })

                menu.addItem('查看任务日志', null, function () {
                    ctx.showJobLog(currentNode.jobId)
                })
                menu.addItem('查看任务属性', null, function () {
                    ctx.setState({ visible: true })
                })
                const frontPeriods = menu.addItem('转到前一周期实例', null, null);
                const frontParams = {
                    jobId: currentNode.id,
                    isAfter: false,
                    limit: 6
                }
                ctx.loadPeriodsData(menu, frontParams, frontPeriods)
                const nextPeriods = menu.addItem('转到下一周期实例', null, null);
                const nextParams = {
                    jobId: currentNode.id,
                    isAfter: true,
                    limit: 6
                }
                ctx.loadPeriodsData(menu, nextParams, nextPeriods)
                menu.addItem(`${isPro ? '查看' : '修改'}任务`, null, function () {
                    ctx.props.goToTaskDev(taskId)
                })
                menu.addItem('终止', null, function () {
                    ctx.stopTask({
                        jobId: currentNode.id
                    })
                }, null, null,
                // 显示终止操作
                currentNode.status === TASK_STATUS.WAIT_SUBMIT || // 等待提交
                    currentNode.status === TASK_STATUS.SUBMITTING || // 提交中
                    currentNode.status === TASK_STATUS.WAIT_RUN || // 等待运行
                    currentNode.status === TASK_STATUS.RUNNING // 运行中
                )

                menu.addItem('刷新任务实例', null, function () {
                    if (isWorkflowNode) {
                        ctx.loadWorkflowNodes(ctx.state.workflowData)
                    } else {
                        ctx.resetGraph(cell)
                    }
                })

                menu.addItem('置成功并恢复调度', null, function () {
                    ctx.restartAndResume({
                        jobId: currentNode.id,
                        justRunChild: true, // 只跑子节点
                        setSuccess: true
                    }, '置成功并恢复调度')
                }, null, null,
                // （运行失败、提交失败）重跑并恢复调度
                currentNode.status === TASK_STATUS.RUN_FAILED ||
                    currentNode.status === TASK_STATUS.STOPED ||
                    currentNode.status === TASK_STATUS.SUBMIT_FAILED)

                menu.addItem('重跑并恢复调度', null, function () {
                    ctx.setState({ visibleRestart: true })
                })
            }
        }
    }

    initGraphEvent = (graph) => {
        const ctx = this;
        let highlightEdges = [];
        let selectedCell = null;

        if (graph) {
            // Double event
            graph.addListener(mxEvent.DOUBLE_CLICK, function (sender, evt) {
                const cell = evt.getProperty('cell')
                if (cell && cell.vertex) {
                    const currentNode = cell.data;
                    ctx.showJobLog(currentNode.jobId)
                }
            })

            graph.addListener(mxEvent.CELLS_FOLDED, function (sender, evt) {
                const cells = evt.getProperty('cells');
                const cell = cells && cells[0];
                const collapse = evt.getProperty('collapse');
                const isWorkflow = get(cell, 'data.batchTask.taskType') === TASK_TYPE.WORKFLOW;

                if (cell && isWorkflow && !collapse) {
                    ctx.loadWorkflowNodes(cell.data);
                    // 始终保持折叠状态
                    cell.collapsed = true;
                }
            })

            // Click event
            graph.addListener(mxEvent.CLICK, function (sender, evt) {
                const cell = evt.getProperty('cell');

                if (cell && cell.vertex) {
                    const currentNode = cell.data;
                    graph.clearSelection();
                    ctx.setState({ selectedJob: currentNode });

                    const outEdges = graph.getOutgoingEdges(cell);
                    const inEdges = graph.getIncomingEdges(cell);
                    const edges = outEdges.concat(inEdges);
                    for (let i = 0; i < edges.length; i++) {
                        /* eslint-disable-next-line */
                        const highlight = new mxCellHighlight(graph, '#2491F7', 2);
                        const state = graph.view.getState(edges[i]);
                        highlight.highlight(state);
                        highlightEdges.push(highlight);
                    }
                    selectedCell = cell;
                } else if (cell === undefined) {
                    const cells = graph.getSelectionCells();
                    graph.removeSelectionCells(cells);
                }
            })

            graph.clearSelection = function (evt) {
                if (selectedCell) {
                    for (let i = 0; i < highlightEdges.length; i++) {
                        highlightEdges[i].hide();
                    }
                    selectedCell = null;
                }
            };
        }
    }

    resetGraph = () => {
        const { taskJob } = this.props
        if (taskJob) {
            this.loadTaskChidren({
                jobId: taskJob.id,
                level: 6
            })
        }
    }

    showJobLog = (jobId) => {
        Api.getOfflineTaskLog({ jobId: jobId }).then((res) => {
            if (res.code === 1) {
                this.setState({ taskLog: res.data, logVisible: true, taskLogId: jobId })
            }
        })
    }

    onCloseWorkflow = () => {
        this.setState({ visibleWorkflow: false, workflowData: null });
    }

    render () {
        const { selectedJob, taskLog, workflowData, graphData, loading } = this.state;
        const { project, taskJob, goToTaskDev, isPro } = this.props;
        return (
            <div
                style={{
                    position: 'relative',
                    height: '100%'
                }}
            >
                <JobGraphView
                    data={taskJob}
                    isPro={isPro}
                    graphData={graphData}
                    loading={loading}
                    goToTaskDev={goToTaskDev}
                    showJobLog={this.showJobLog}
                    refresh={this.refresh}
                    registerEvent={this.initGraphEvent}
                    key={`graph-${graphData && graphData.id}`}
                    registerContextMenu={this.initContextMenu}
                />
                <Modal
                    width={900}
                    height={600}
                    zIndex={999}
                    footer={null}
                    maskClosable={true}
                    visible={this.state.visibleWorkflow}
                    title={`工作流-${get(workflowData, 'batchTask.name', '')}`}
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    onCancel={this.onCloseWorkflow}
                >
                    <JobGraphView
                        isPro={isPro}
                        loading={loading}
                        data={selectedJob}
                        goToTaskDev={goToTaskDev}
                        showJobLog={this.showJobLog}
                        registerEvent={this.initGraphEvent}
                        registerContextMenu={this.initContextMenu}
                        graphData={workflowData && workflowData.subNodes}
                        key={`graph-workflow-${workflowData && workflowData.id}`}
                        refresh={this.loadWorkflowNodes.bind(this, workflowData)}
                    />
                </Modal>
                <Modal
                    title="查看属性"
                    width="60%"
                    wrapClassName="vertical-center-modal"
                    visible={this.state.visible}
                    onCancel={() => { this.setState({ visible: false }) }}
                    footer={null}
                >
                    <TaskInfo task={selectedJob} project={project} />
                </Modal>
                <Modal
                    key={taskJob && taskJob.id}
                    width={800}
                    title={(
                        <span>
                            任务日志
                            <Tooltip placement="right" title="刷新">
                                <Icon style={{ cursor: 'pointer', marginLeft: '5px' }} onClick={() => { this.showJobLog(this.state.taskLogId) }} type="reload" />
                            </Tooltip>
                        </span>
                    )}
                    wrapClassName="vertical-center-modal m-log-modal"
                    visible={this.state.logVisible}
                    onCancel={() => { this.setState({ logVisible: false }) }}
                    footer={null}
                    maskClosable={true}
                >
                    <LogInfo
                        log={taskLog.logInfo}
                        syncJobInfo={taskLog.syncJobInfo}
                        downloadLog={taskLog.downloadLog}
                        height="520px"
                    />
                </Modal>
                <RestartModal
                    restartNode={selectedJob}
                    visible={this.state.visibleRestart}
                    onCancel={() => {
                        this.setState({ visibleRestart: false })
                    }}
                />
            </div>
        )
    }
}
export default TaskJobFlowView;
