import * as React from 'react'
import { hashHistory } from 'react-router'
import { cloneDeep, get } from 'lodash'

import {
    message, Modal
} from 'antd'

import Api from '../../../../api'
import { TASK_TYPE } from '../../../../consts/comm'
import { APP_TYPE } from '../../../../consts'

import TaskGraphView, { mergeTreeNodes } from './taskGraphView'
import MxFactory from 'dt-common/src/widgets/mxGraph';

const Mx = MxFactory.create();
const {
    mxEvent,
    mxCellHighlight,
    mxPopupMenu
} = Mx

class TaskFlowView extends React.Component<any, any> {
    state: any = {
        loading: 'success',
        selectedTask: '', // 选中的节点
        graphData: null, // 图表数据
        workflowData: null, // 选中的工作流节点
        selectedWorkflowNode: null, // 选中的工作流子节点
        visibleWorkflowVisible: false, // 是否打开工作流
        currentInfo: {}
    }
    _originData: any;
    componentDidMount () {
        this.refresh();
    }

    refresh = () => {
        const tabData = this.props.tabData;
        console.log(tabData)
        if (tabData) {
            this._originData = null; // 清空缓存数据
            this.setState({
                graphData: null,
                selectedTask: tabData
            })
            this.loadTaskChidren({
                taskId: tabData.id,
                appType: tabData.appType,
                projectId: tabData.projectId
            })
        }
    }

    isCurrentProjectTask = (node: any) => {
        return !node?.existsOnRule || false;
    }
    renderGraph = (data: any) => {
        const originData = this._originData;
        console.log('originData: ', originData);
        if (originData) {
            mergeTreeNodes(originData, data);
        } else {
            this._originData = cloneDeep(data);
        }
        const graphData = cloneDeep(this._originData);
        this.setState({
            graphData
        })
    }

    loadTaskChidren = (params: any) => {
        console.log(params)
        const ctx = this;
        const LOAD_CHILDREN = 2;
        params.type = LOAD_CHILDREN;
        this.setState({ loading: 'loading' })
        Api.getTaskChildren(params).then((res: any) => {
            if (res.code === 1) {
                const data = res.data ? res.data : {}
                ctx.setState({ selectedTask: data });
                ctx.renderGraph(data);
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadTaskParent = (params: any) => {
        const ctx = this;
        const LOAD_PARENT = 1;
        params.type = LOAD_PARENT;
        this.setState({ loading: 'loading' })
        Api.getTaskChildren(params).then((res: any) => {
            if (res.code === 1) {
                const data = res.data || []
                ctx.setState({ selectedJob: data });
                ctx.renderGraph(data);
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadWorkflowNodes = async (workflow: any) => {
        const ctx = this;
        this.setState({ loading: 'loading' });
        // 如果折叠状态，加载
        const res = await Api.getTaskWorkflowNodes({ taskId: workflow.id });
        if (res.code === 1) {
            if (res.data && res.data.subTaskVOS) {
                ctx.setState({
                    visibleWorkflow: true,
                    workflowData: res.data,
                    selectedWorkflowNode: workflow
                })
            } else {
                message.warning('当前工作流没有子节点！');
            }
        }
        this.setState({ loading: 'success' })
    }

    forzenTasks = (ids: any, mode: any) => {
        const ctx = this
        Api.forzenTask({
            taskIdList: ids,
            scheduleStatus: mode, //  1正常调度, 2暂停 NORMAL(1), PAUSE(2),
            appType: ctx.state.appType,
            projectId: ctx.state.projectId
        }).then((res: any) => {
            if (res.code === 1) {
                message.success('操作成功！');
                ctx.props.reload();
                ctx.refresh();
            }
        })
    }

    initContextMenu = (graph: any) => {
        const ctx = this
        const { clickPatchData } = this.props;
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
        mxPopupMenu.prototype.showMenu = function () {
            var cells = this.graph.getSelectionCells()
            console.log(cells)
            if (cells.length > 0 && cells[0].vertex) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };
        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function (menu: any, cell: any, evt: any) {
            if (!cell || !cell.vertex) return;

            const currentNode = cell.value || {};
            const isCurrentProjectTask = ctx.isCurrentProjectTask(currentNode);
            const isWorkflowNode = currentNode.flowId && currentNode.flowId !== 0;
            // 如果为工作流节点，且工作流处于冻结状态时，需要禁用子节点的解冻或者调用功能
            // const disableRunCtrl = isWorkflowNode && currentNode.workflow && currentNode.workflow.scheduleStatus === SCHEDULE_STATUS.STOPPED;
            const appType = cell?.value?.appType
            if (APP_TYPE[appType] === APP_TYPE[10] || APP_TYPE[appType] === APP_TYPE[1]) {
                if (!isWorkflowNode) {
                    menu.addItem('展开上游（6层）', null, function () {
                        ctx.loadTaskParent({
                            taskId: currentNode.id,
                            level: 6,
                            appType: currentNode.appType,
                            projectId: currentNode.projectId
                        })
                    })
                    menu.addItem('展开下游（6层）', null, function () {
                        ctx.loadTaskChidren({
                            taskId: currentNode.id,
                            level: 6,
                            appType: currentNode.appType,
                            projectId: currentNode.projectId
                        })
                    })
                }
                menu.addItem('补数据', null, function () {
                    clickPatchData(currentNode)
                })
                // menu.addItem('操作记录', null, function () {
                //     ctx.clickOperaRecord(currentNode)
                // })
                // menu.addItem('冻结', null, function () {
                //     ctx.forzenTasks([currentNode.id], SCHEDULE_STATUS.STOPPED)
                // }, null, null,
                // currentNode.scheduleStatus === SCHEDULE_STATUS.NORMAL && !disableRunCtrl
                // ) // 正常状态

                // menu.addItem('解冻', null, function () {
                //     ctx.forzenTasks([currentNode.id], SCHEDULE_STATUS.NORMAL);
                // }, null, null,
                // currentNode.scheduleStatus === SCHEDULE_STATUS.STOPPED && !disableRunCtrl
                // ) // 冻结状态
                // 再来一个flag
                if (isCurrentProjectTask) {
                    // menu.addItem('查看代码', null, function () {
                    //     goToTaskDev(currentNode.id)
                    // })
                    if (!isWorkflowNode) {
                        menu.addItem('查看实例', null, function () {
                            hashHistory.push(`/operation/offline-operation?job=${currentNode.name}`)
                        })
                    }
                }
            } else {
                menu.addItem('展开上游（6层）', null, function () {
                    ctx.loadTaskParent({
                        taskId: currentNode.id,
                        level: 6,
                        appType: currentNode.appType,
                        projectId: currentNode.projectId
                    })
                })
                menu.addItem('展开下游（6层）', null, function () {
                    ctx.loadTaskChidren({
                        taskId: currentNode.id,
                        level: 6,
                        appType: currentNode.appType,
                        projectId: currentNode.projectId
                    })
                })
            }
        }
    }

    initGraphEvent = (graph: any) => {
        const ctx = this;
        let highlightEdges: any = [];
        let selectedCell: any = null;
        if (graph) {
            graph.addListener(mxEvent.CELLS_FOLDED, function (sender: any, evt: any) {
                const cells = evt.getProperty('cells');
                const cell = cells && cells[0];
                const collapse = evt.getProperty('collapse');
                const isWorkflow = get(cell, 'value.taskType') === TASK_TYPE.WORKFLOW;

                if (cell && isWorkflow && !collapse) {
                    ctx.loadWorkflowNodes(cell.value);
                    // 始终保持折叠状态
                    cell.collapsed = true;
                }
            })

            graph.addListener(mxEvent.onClick, async function (sender: any, evt: any) {
                const cell = evt.getProperty('cell')
                const which = evt?.properties?.event?.which
                if (cell && which === 1) {
                    const taskId = cell?.value?.taskId;
                    const appType = cell?.value?.appType;
                    if (taskId && appType) {
                        let res = await Api.findTaskRuleTask({ appType, taskId })
                        ctx.setState({
                            currentInfo: res.data
                        })
                    }
                }
                if (cell && cell.vertex) {
                    graph.clearSelection();
                    const data = cell.value;
                    const isWorkflowNode = data.flowId && data.flowId !== 0;
                    if (isWorkflowNode) {
                        ctx.setState({ selectedWorkflowNode: data })
                    } else {
                        ctx.setState({ selectedTask: data })
                    }
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
                } else {
                    const cells = graph.getSelectionCells();
                    graph.removeSelectionCells(cells);
                }
            });

            graph.clearSelection = function (evt: any) {
                if (selectedCell) {
                    for (let i = 0; i < highlightEdges.length; i++) {
                        highlightEdges[i].hide();
                    }
                    selectedCell = null;
                }
            };
        }
    }

    onCloseWorkflow = () => {
        this.setState({ visibleWorkflow: false, workflowData: null, selectedTask: this.props.tabData });
    }

    render () {
        const { goToTaskDev } = this.props
        const {
            selectedTask, loading, workflowData,
            graphData, selectedWorkflowNode, currentInfo
        } = this.state;
        const heightFix = {
            height: '600px'
        }
        return (
            <div
                style={{
                    position: 'relative',
                    height: '100%',
                    overflow: 'hidden'
                }}
            >
                <TaskGraphView
                    flag='task-view'
                    data={selectedTask}
                    isCurrentProjectTask={this.isCurrentProjectTask}
                    graphData={graphData}
                    loading={loading}
                    goToTaskDev={goToTaskDev}
                    refresh={this.refresh}
                    registerEvent={this.initGraphEvent}
                    key={`task-graph-view-${graphData && graphData.id}`}
                    registerContextMenu={this.initContextMenu}
                    currentInfo={currentInfo}
                />
                <Modal
                    zIndex={999}
                    width={900}
                    {...heightFix}
                    footer={null}
                    maskClosable={true}
                    visible={this.state.visibleWorkflow}
                    title={`工作流-${get(workflowData, 'name', '')}`}
                    wrapClassName="vertical-center-modal modal-body-nopadding modal-body--height100"
                    onCancel={this.onCloseWorkflow}
                >
                    <TaskGraphView
                        style={{ height: '100%' }}
                        loading={loading}
                        data={selectedWorkflowNode}
                        isCurrentProjectTask={this.isCurrentProjectTask}
                        goToTaskDev={goToTaskDev}
                        registerEvent={this.initGraphEvent}
                        registerContextMenu={this.initContextMenu}
                        graphData={workflowData && workflowData.subTaskVOS[0]}
                        key={`task-graph-workflow-${workflowData && workflowData.id}`}
                        refresh={this.loadWorkflowNodes.bind(this, workflowData)}
                        currentInfo={currentInfo}
                    />
                </Modal>
            </div>
        )
    }
}
export default TaskFlowView;
