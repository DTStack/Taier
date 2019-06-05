import React, { Component } from 'react'
import { connect } from 'react-redux';
import { cloneDeep, get } from 'lodash'

import {
    message, Modal
} from 'antd'

import Api from '../../../../api'
import { TASK_TYPE } from '../../../../comm/const'
import TaskGraphView, { mergeTreeNodes } from '../../../operation/offline/taskFlowView/taskGraphView'
import * as MxFactory from 'widgets/mxGraph';

const Mx = MxFactory.create();
const {
    mxEvent,
    mxCellHighlight
} = Mx

@connect(state => {
    return {
        project: state.project
    }
})
class TaskView extends Component {
    state = {
        loading: 'success',
        selectedTask: '', // 选中的节点
        graphData: null, // 图表数据
        workflowData: null, // 选中的工作流节点
        selectedWorkflowNode: null, // 选中的工作流子节点
        recordModalVisible: false,
        visibleWorkflowVisible: false, // 是否打开工作流
        currentNodeData: {} // 缓存节点数据
    }

    componentDidMount () {
        this.refresh();
    }

    loadTaskChidren = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        Api.getTaskChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ selectedTask: data, data })
                ctx.renderGraph(data)
            }
            ctx.setState({ loading: 'success' })
        })
    }

    refresh = () => {
        const tabData = this.props.tabData;
        if (tabData) {
            this._originData = null; // 清空缓存数据
            this.setState({
                graphData: null,
                selectedTask: tabData
            })
            this.loadTaskChidren({
                taskId: tabData.id,
                level: 6
            })
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
            graphData
        })
    }

    loadWorkflowNodes = async (workflow) => {
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

    initGraphEvent = (graph) => {
        const ctx = this;
        let highlightEdges = [];
        let selectedCell = null;

        if (graph) {
            graph.addListener(mxEvent.CELLS_FOLDED, function (sender, evt) {
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

            graph.addListener(mxEvent.onClick, function (sender, evt) {
                const cell = evt.getProperty('cell')
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

    onCloseWorkflow = () => {
        this.setState({ visibleWorkflow: false, workflowData: null, selectedTask: this.props.tabData });
    }
    isCurrentProjectTask = (node) => {
        const { project } = this.props;
        const projectId = project.id;
        return node.projectId == projectId;
    }
    render () {
        const {
            selectedTask, loading,
            workflowData, graphData, selectedWorkflowNode
        } = this.state;

        return (
            <div
                style={{
                    position: 'relative'
                }}
            >
                <TaskGraphView
                    data={selectedTask}
                    graphData={graphData}
                    loading={loading}
                    isCurrentProjectTask={this.isCurrentProjectTask}
                    hideFooter={true}
                    refresh={this.refresh}
                    registerEvent={this.initGraphEvent}
                    key={`task-graph-view-${graphData && graphData.id}`}
                    registerContextMenu={this.initContextMenu}
                />
                <Modal
                    zIndex={999}
                    width={800}
                    height={600}
                    footer={null}
                    maskClosable={true}
                    visible={this.state.visibleWorkflow}
                    title={`工作流-${get(workflowData, 'name', '')}`}
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    onCancel={this.onCloseWorkflow}
                >
                    <TaskGraphView
                        loading={loading}
                        isCurrentProjectTask={this.isCurrentProjectTask}
                        data={selectedWorkflowNode}
                        hideFooter={true}
                        registerEvent={this.initGraphEvent}
                        registerContextMenu={this.initContextMenu}
                        graphData={workflowData && workflowData.subTaskVOS[0]}
                        key={`task-graph-workflow-${workflowData && workflowData.id}`}
                        refresh={this.loadWorkflowNodes.bind(this, workflowData)}
                    />
                </Modal>
            </div>
        )
    }
}
export default TaskView;
