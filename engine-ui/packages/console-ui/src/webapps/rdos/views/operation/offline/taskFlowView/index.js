import React, { Component } from 'react'

import {
    Tooltip, Spin,
    Modal, message, Icon,
} from 'antd'

import utils from 'utils'
import Api from '../../../../api'
import MyIcon from '../../../../components/icon'
import { getVertxtStyle } from '../../../../comm'
import { TASK_STATUS, TASK_TYPE } from '../../../../comm/const'
import { taskTypeText, taskStatusText } from '../../../../components/display'
import { TaskInfo } from './taskInfo'
import { LogInfo } from '../taskLog'
import RestartModal from './restartModal'

const Mx = require('public/rdos/mxgraph')({
    mxImageBasePath: 'public/rdos/mxgraph/images',
    mxBasePath: 'public/rdos/mxgraph',
})

const {
    mxGraph,
    mxShape,
    mxConnectionConstraint,
    mxPoint,
    mxPolyline,
    mxEvent,
    mxRubberband,
    mxConstants,
    mxEdgeStyle,
    mxPopupMenu,
    mxPerimeter,
    mxCompactTreeLayout,
    mxGraphView,
    mxGraphHandler,
    mxRectangle,
    mxText,
} = Mx

const VertexSize = { // vertex大小
    width: 150,
    height: 36,
}

// 遍历树形节点，用新节点替换老节点
export function replaceTreeNode(treeNode, replace) {
    if (treeNode.id === parseInt(replace.id, 10)) {
        replace.jobVOS = treeNode.jobVOS ? [...treeNode.jobVOS] : []
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.jobVOS) {
        const children = treeNode.jobVOS
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace)
        }
    }
}

class TaskFlowView extends Component {

    state = {
        selectedJob: '', // 选中的Job
        data: {}, // 数据
        loading: 'success',
        lastVertex: '',
        sort: 'children',
        taskLog: {},
        logVisible: false,
        visible: false,
        visibleRestart: false,
    }

    initGraph = (id) => {
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.graph = "";
        this._vertexCells = {} // 缓存创建的节点

        const editor = this.Container
        this.initEditor()
        this.loadEditor(editor)
        this.hideMenu();
        this.loadTaskChidren({
            jobId: id,
            level: 6,
        })
    }

    componentWillReceiveProps(nextProps) {
        const currentJob = this.props.taskJob
        const { taskJob, visibleSlidePane } = nextProps
        if (taskJob && visibleSlidePane && (!currentJob || taskJob.id !== currentJob.id)) {
            this.initGraph(taskJob.id)
        }
    }

    loadTaskChidren = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        Api.getJobChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ selectedJob: data, data, sort: 'children' })
                ctx.doInsertVertex(res.data, 'children')
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadTaskParent = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        Api.getJobParents(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ data, selectedJob: data, sort: 'parent' })
                ctx.doInsertVertex(res.data, 'parent')
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadEditor = (container) => {

        mxGraphView.prototype.optimizeVmlReflows = false;
        mxText.prototype.ignoreStringSize = true; //to avoid calling getBBox
        // Disable context menu
        mxEvent.disableContextMenu(container)
        const graph = new mxGraph(container)
        // 启用绘制
        graph.setPanning(true);
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.setConnectable(true)
        graph.setTooltips(true)
        graph.view.setScale(1)
        // Enables HTML labels
        graph.setHtmlLabels(false);

        graph.setAllowDanglingEdges(false)
        // 禁止连接
        graph.setConnectable(false)
        // 禁止Edge对象移动
        graph.isCellsMovable = function (cell) {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge)
        }
        // 禁止cell编辑
        graph.isCellEditable = function () {
            return false
        }

        /**
         * Redirects start drag to parent.
        */
        const graphHandlerGetInitialCellForEvent = mxGraphHandler.prototype.getInitialCellForEvent;
        mxGraphHandler.prototype.getInitialCellForEvent = function(me) {
            var cell = graphHandlerGetInitialCellForEvent.apply(this, arguments);
            if (cell.isPart) {
                cell = graph.getModel().getParent(cell)
            }
            return cell;
        };

        // Redirects selection to parent
        graph.selectCellForEvent = function(cell) {
            if (cell.isPart) {
                cell = graph.getModel().getParent(cell)
                return cell;
            }
            mxGraph.prototype.selectCellForEvent.apply(this, arguments);
        };

        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle)

        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip

        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';
        mxConstants.STYLE_OVERFLOW = 'hidden';

        // enables rubberband
        new mxRubberband(graph)

        this.graph = graph;
        this.initContextMenu(graph);
        this.initGraphEvent();
    }

    formatTooltip = (cell) => {
        if (cell.vertex) {
            const currentNode = cell.data; //this._vertexCells[cell.id].data;
            return currentNode.batchTask.name;
        }
    }

    getShowStr = (data) => {
        const task = data.batchTask;
        if (!task) return '';
        const taskType = taskTypeText(task.taskType);
        const taskStatus = taskStatusText(data.status);
        const taskName = task.name.length > 12 ? `${task.name.substring(0, 10)}...` : task.name;
        const str = `${taskName || ''} \n ${taskType}(${taskStatus})`;
        return str;
    }

    corvertValueToString = (cell) => {
        if (cell.vertex && cell.value) {
            const dataParse = cell.value ? cell.value : {};
            const task = dataParse.batchTask || '';
            const taskType = taskTypeText(task.taskType);
            const taskStatus = taskStatusText(dataParse.status);
            if (task) {
                return `<div class="vertex"><span class="vertex-title"><span>${task.name || ''}</span>
                <span style="font-size:10px; color: #666666;">${taskType}(${taskStatus})</span></span>
                </div>`
            }
        }
    }

    insertEdge = (graph, type, parent, child) => {
        if (type === 'children' && parent) {
            graph.insertEdge(graph.getDefaultParent(), null, '', parent, child)
        } else {
            graph.insertEdge(graph.getDefaultParent(), null, '', child, parent)
        }
    }

    insertVertex = (rootCell, data, parent, type) => {

        if (data) {
            const ctx = this;
            const graph = this.graph;
            const cacheKey = data.id;
            let style = getVertxtStyle(data.status)
            const isWorkflow = data.batchTask && data.batchTask.taskType === TASK_TYPE.WORKFLOW;

            const exist = this._vertexCells[cacheKey];
            let newVertex = exist;

            if (exist && parent !== graph.getDefaultParent()) {
                this.insertEdge(graph, type, parent, exist);
            } else if (!exist) {
                // 插入当前节点
                const str = this.getShowStr(data);
               
                let width = VertexSize.width;
                let height = VertexSize.height;
                if (isWorkflow) {
                    width = width + 20;
                    height = height + 100;
                    style += 'shape=swimlane;';
                }
                newVertex = graph.insertVertex(
                    rootCell, null, str, ctx.cx, ctx.cy,
                    width, height, style
                );
                
                newVertex.data = data;
                newVertex.isPart = (data.batchTask && data.batchTask.flowId && data.batchTask.flowId !== 0) ? true : false;

                this.insertEdge(graph, type, parent, newVertex);
                // 缓存节点
                this._vertexCells[cacheKey] = newVertex;
                
                // 遍历工作流节点
                if (isWorkflow) {
                    newVertex.geometry.alternateBounds = new mxRectangle(0, 0, VertexSize.width, VertexSize.height);
                    this.executeLayout(newVertex, () => {
                        this.insertVertex(newVertex, data.subNodes, null, type);
                    })
                }
            }

            if (data.jobVOS) {
                const children = data.jobVOS;
                for (let i = 0; i < children.length; i++) {
                    this.insertVertex(rootCell, children[i], newVertex, type);
                }
            }
        }
    }

    doInsertVertex = (data, type) => {

        const graph = this.graph;
        const parent = graph.getDefaultParent();
        const model = graph.getModel();
        this.cx = (graph.container.clientWidth - VertexSize.width) / 2;
        this.cy = 200;

        const layout = new mxCompactTreeLayout(graph, false);
        layout.horizontal = false;
        layout.useBoundingBox = false;
        layout.edgeRouting = false;
        layout.levelDistance = 30;
        layout.nodeDistance = 10;
        layout.resizeParent = true;

        this.executeLayout = function (layoutNode, change, post) {
            model.beginUpdate();
            try {
                if (change != null) { change(); }
                layout.execute(layoutNode);
            } catch (e) {
                throw e;
            } finally {
                graph.getModel().endUpdate();
                if (post != null) { post(); }
            }
        }

        this.executeLayout(parent, () => {
            this.insertVertex(parent, data, null, type);
        })
    }

    initContextMenu = (graph) => {
        const ctx = this;
        const {isPro}=ctx.props;
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
        mxPopupMenu.prototype.showMenu = function () {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0 && cells[0].vertex) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };
        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function (menu, cell, evt) {

            if (!cell) return
            const currentNode = cell.data;

            const isWorkflowNode = currentNode.batchTask && currentNode.batchTask.flowId && currentNode.batchTask.flowId !== 0;

            if (!isWorkflowNode) {
                menu.addItem('展开上游（6层）', null, function () {
                    ctx.loadTaskParent({
                        jobId: currentNode.id,
                        level: 6,
                    })
                })
                menu.addItem('展开下游（6层）', null, function () {
                    ctx.loadTaskChidren({
                        jobId: currentNode.id,
                        level: 6,
                    })
                })
            }
            menu.addItem('查看任务日志', null, function () {
                ctx.showJobLog(currentNode.jobId)
            })
            menu.addItem(`${isPro?'查看':'修改'}任务`, null, function () {
                ctx.props.goToTaskDev(currentNode.taskId)
            })
            menu.addItem('查看任务属性', null, function () {
                ctx.setState({ visible: true })
            })
            menu.addItem('终止', null, function () {
                ctx.stopTask({
                    jobId: currentNode.id,
                })
            }, null, null,
                // 显示终止操作
                currentNode.status === TASK_STATUS.RUNNING || // 运行中
                currentNode.status === TASK_STATUS.RESTARTING || // 重启中
                currentNode.status === TASK_STATUS.WAIT_SUBMIT || // 等待提交
                currentNode.status === TASK_STATUS.WAIT_RUN
            )

            menu.addItem('刷新任务实例', null, function () {
                ctx.resetGraph(cell)
            })

            menu.addItem('重跑并恢复调度', null, function () {
                ctx.restartAndResume({
                    jobId: currentNode.id,
                    justRunChild: false, // 只跑子节点
                    setSuccess: false, // 更新节点状态
                }, '重跑并恢复调度')

            }, null, null,
                // 重跑并恢复调度
                currentNode.status === TASK_STATUS.WAIT_SUBMIT || // 未运行
                currentNode.status === TASK_STATUS.FINISHED || // 已完成
                currentNode.status === TASK_STATUS.RUN_FAILED || // 运行失败
                currentNode.status === TASK_STATUS.SUBMIT_FAILED || // 提交失败
                currentNode.status === TASK_STATUS.SET_SUCCESS || // 手动设置成功
                currentNode.status === TASK_STATUS.STOPED) // 已停止

            menu.addItem('置成功并恢复调度', null, function () {
                ctx.restartAndResume({
                    jobId: currentNode.id,
                    justRunChild: true, // 只跑子节点
                    setSuccess: true,
                }, '置成功并恢复调度')
            }, null, null,
                //（运行失败、提交失败）重跑并恢复调度
                currentNode.status === TASK_STATUS.RUN_FAILED ||
                currentNode.status === TASK_STATUS.STOPED ||
                currentNode.status === TASK_STATUS.SUBMIT_FAILED)

            menu.addItem('重跑下游并恢复调度', null, function () {
                ctx.setState({ visibleRestart: true })
            })
        }
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

    initGraphEvent = () => {
        const ctx = this;
        const graph = this.graph;
        if (graph) {
            graph.addListener(mxEvent.DOUBLE_CLICK, function (sender, evt) {
                const cell = evt.getProperty('cell')
                if (cell && cell.vertex) {
                    const currentNode = cell.data;
                    ctx.showJobLog(currentNode.jobId)
    
                }
            })
    
            graph.addListener(mxEvent.CLICK, function (sender, evt) {
                const cell = evt.getProperty('cell')
                if (cell && cell.vertex) {
                    const currentNode = cell.data;
                    ctx.setState({ selectedJob: currentNode })
                }
            })
        }
    }

    resetGraph = () => {
        const { taskJob } = this.props
        if (taskJob) {
            this.loadTaskChidren({
                jobId: taskJob.id,
                level: 6,
            })
        }
    }

    showJobLog = (jobId) => {
        Api.getOfflineTaskLog({ jobId: jobId }).then((res) => {
            if (res.code === 1) {
                this.setState({ taskLog: res.data, logVisible: true, taskLogId:jobId })
            }
        })
    }

    graphEnable() {
        const status = this.graph.isEnabled()
        this.graph.setEnabled(!status)
    }

    refresh = () => {
        this.initGraph(this.props.taskJob.id)
    }

    zoomIn = () => {
        this.graph.zoomIn()
    }

    zoomOut = () => {
        this.graph.zoomOut()
    }

    hideMenu = () => {
        document.addEventListener('click', (e) => {
            const popMenus = document.querySelector('.mxPopupMenu')
            if (popMenus) {
                document.body.removeChild(popMenus)
            }
        })
    }

    /* eslint-enable */
    render() {
        const { selectedJob, taskLog } = this.state;
        const { goToTaskDev, project, taskJob, isPro } = this.props;

        return (
            <div className="graph-editor"
                style={{
                    position: 'relative',
                }}
            >
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={this.state.loading === 'loading'}
                >
                   <div
                        className="editor pointer"
                        ref={(e) => { this.Container = e }}
                        style={{
                            position: 'relative',
                            overflow: 'hidden',
                            overflowX: 'auto',
                            paddingBottom: '20px',
                            height: '95%',
                        }}
                    >
                    </div>
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="刷新">
                        <Icon type="reload" onClick={this.refresh} />
                    </Tooltip>
                    <Tooltip placement="bottom" title="放大">
                        <MyIcon onClick={this.zoomIn} type="zoom-in" />
                    </Tooltip>
                    <Tooltip placement="bottom" title="缩小">
                        <MyIcon onClick={this.zoomOut} type="zoom-out" />
                    </Tooltip>
                </div>
                <div
                    className="box-title graph-info"
                    style={{
                        bottom: 0
                    }}
                >
                    <span>{taskJob && taskJob.batchTask && taskJob.batchTask.name || '-'}</span>
                    <span style={{ marginLeft: "15px" }}>{(taskJob && taskJob.batchTask && taskJob.batchTask.createUser && taskJob.batchTask.createUser.userName) || '-'}</span>&nbsp;
                    {isPro?'发布':'提交'}于&nbsp;
                    <span>{taskJob && taskJob.batchTask && utils.formatDateTime(taskJob.batchTask.gmtModified)}</span>&nbsp;
                    <a onClick={() => { goToTaskDev(taskJob && taskJob.batchTask.id) }}>查看代码</a>
                </div>
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
                    width={600}
                    title={(
                        <span>
                            任务日志
                            <Tooltip placement="right" title="刷新">
                                <Icon style={{cursor:"pointer",marginLeft:"5px"}} onClick={()=>{this.showJobLog(this.state.taskLogId)}} type="reload" />
                            </Tooltip>
                        </span>
                    )}
                    wrapClassName="vertical-center-modal m-log-modal"
                    visible={this.state.logVisible}
                    onCancel={() => { this.setState({ logVisible: false }) }}
                    footer={null}
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

    getDefaultVertexStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = '#90D5FF';
        style[mxConstants.STYLE_FILLCOLOR] = '#E6F7FF;';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333;';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        style[mxConstants.STYLE_OVERFLOW] = 'hidden';

        return style;
    }

    getDefaultEdgeStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#9EABB2';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
        style[mxConstants.STYLE_FONTSIZE] = '10';
        style[mxConstants.STYLE_ROUNDED] = true;
        return style;
    }

    /* eslint-disable */
    initEditor() {
        // Overridden to define per-shape connection points
        mxGraph.prototype.getAllConnectionConstraints = function (terminal, source) {
            if (terminal != null && terminal.shape != null) {
                if (terminal.shape.stencil != null) {
                    if (terminal.shape.stencil != null) {
                        return terminal.shape.stencil.constraints;
                    }
                }
                else if (terminal.shape.constraints != null) {
                    return terminal.shape.constraints;
                }
            }
            return null;
        };
        // Defines the default constraints for all shapes
        mxShape.prototype.constraints = [new mxConnectionConstraint(new mxPoint(0.25, 0), true),
        new mxConnectionConstraint(new mxPoint(0.5, 0), true),
        new mxConnectionConstraint(new mxPoint(0.75, 0), true),
        new mxConnectionConstraint(new mxPoint(0, 0.25), true),
        new mxConnectionConstraint(new mxPoint(0, 0.5), true),
        new mxConnectionConstraint(new mxPoint(0, 0.75), true),
        new mxConnectionConstraint(new mxPoint(1, 0.25), true),
        new mxConnectionConstraint(new mxPoint(1, 0.5), true),
        new mxConnectionConstraint(new mxPoint(1, 0.75), true),
        new mxConnectionConstraint(new mxPoint(0.25, 1), true),
        new mxConnectionConstraint(new mxPoint(0.5, 1), true),
        new mxConnectionConstraint(new mxPoint(0.75, 1), true)];
        // Edges have no connection points
        mxPolyline.prototype.constraints = null;
    }
}
export default TaskFlowView;