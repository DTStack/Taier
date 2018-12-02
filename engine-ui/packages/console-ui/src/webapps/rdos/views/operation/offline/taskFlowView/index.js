import React, { Component } from 'react'

import {
    Tooltip, Spin,
    Modal, message, Icon,
} from 'antd'

import utils from 'utils'
import { TaskInfo } from './taskInfo'
import { LogInfo } from '../taskLog'
import RestartModal from './restartModal'

import Api from '../../../../api'
import MyIcon from '../../../../components/icon'
import { getVertxtStyle } from '../../../../comm'
import { TASK_STATUS, TASK_TYPE } from '../../../../comm/const'
import { taskTypeText, taskStatusText } from '../../../../components/display'
import { getGeoByRelativeNode, getNodeWidth, getNodeHeight, getNodeLevelAndCount } from 'utils/layout';


const Mx = require('public/rdos/mxgraph')({
    mxBasePath: 'public/rdos/mxgraph',
    mxImageBasePath: 'public/rdos/mxgraph/images',
    mxLanguage: 'none',
    mxLoadResources: false,
    mxLoadStylesheets: false,
})

const {
    mxGraph,
    mxEvent,
    mxRubberband,
    mxConstants,
    mxEdgeStyle,
    mxPopupMenu,
    mxPerimeter,
    mxGraphView,
    mxGraphHandler,
    mxRectangle,
    mxText,
} = Mx

const VertexSize = { // vertex大小
    width: 150,
    height: 40,
}

/**
 * 合并Tree数据
 * @param {*} origin 
 * @param {*} target 
 */
const mergeTreeNodes = (treeNodeData, mergeSource, nodeType) => {
    if (treeNodeData) {

        if (treeNodeData.id === mergeSource.id) {
            if (mergeSource.jobVOS) {
                if (nodeType === 'children') {
                    treeNodeData.jobVOS = Object.assign(mergeSource.jobVOS);
                } else if (nodeType === 'parent') {
                    treeNodeData.parentNodes = Object.assign(mergeSource.jobVOS);
                }
            }
            return;
        }

        const childNodes = treeNodeData.jobVOS; // 子节点

        // 处理被依赖节点
        if (childNodes && childNodes.length > 0) {
            for (let i = 0; i < childNodes.length; i++) {
                mergeTreeNodes(childNodes[i], mergeSource);
            }
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

    _view = null; // 存储view信息

    initGraph = (id) => {
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.graph = "";
        this._vertexCells = {}; // 缓存创建的节点
        this._originData = "";

        const editor = this.Container
        this.loadEditor(editor)
        this.hideMenu();
        this.loadTaskChidren({
            jobId: id,
        })
    }

    componentWillReceiveProps(nextProps) {
        const currentJob = this.props.taskJob
        const { taskJob, visibleSlidePane } = nextProps
        if (taskJob && visibleSlidePane && (!currentJob || taskJob.id !== currentJob.id)) {
            this.initGraph(taskJob.id);
            this._view = null;
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
        graph.setHtmlLabels(true);

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
        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString

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
            const task = cell.value.batchTask || {};
            const taskType = taskTypeText(task.taskType);
            const taskStatus = taskStatusText(cell.value.status);

            if (task) {
                return `<div class="vertex"><span class="vertex-title" title="${task.name || ''}">${task.name || ''}</span>
                <span class="vertex-desc">${taskType}(${taskStatus})</span>
                </div>`
            }
        }
        return '';
    }

    preHandGraphTree = (data) => {

        const relationTree = [];
        let level = 0;
        let defaultRoot = { // 层级，默认0;
            count: 1,
            index: 1,
            level: 0,
            x: 10,
            y: 10,
            width: VertexSize.width,
            height: VertexSize.height,
            margin: 50,
        }

        const loop = (treeNodeData, parent, level, parentNode) => {

            if (treeNodeData) {
                const childNodes = treeNodeData.jobVOS; // 子节点
                const parentNodes = treeNodeData.parentNodes; // 父节点

                let currentNodeGeo;

                if (!treeNodeData._geometry) {
                    currentNodeGeo = Object.assign({}, defaultRoot);
                    currentNodeGeo.level = level;
                    currentNodeGeo.y = parentNode.y + defaultRoot.margin;
                } else {
                    currentNodeGeo = treeNodeData._geometry;
                }

                if (treeNodeData.batchTask.taskType === TASK_TYPE.WORKFLOW) {
                    const workflowData = treeNodeData.subNodes;

                    let newLevel = 0;

                    // 如果是工作流，需要重新计算工作流节点的高和宽
                    const nodeCount = getNodeLevelAndCount(workflowData, 'jobVOS');
                    const tempNode = Object.assign({}, defaultRoot);
                    tempNode.count = nodeCount.count;
                    tempNode.level = nodeCount.level;
                    currentNodeGeo.width = getNodeWidth(tempNode);
                    currentNodeGeo.height = getNodeHeight(tempNode);

                    const workflowDefaultRoot = Object.assign({}, defaultRoot);
                    workflowDefaultRoot.x = Math.round((currentNodeGeo.width - VertexSize.width) /2);
                    workflowDefaultRoot.y = workflowDefaultRoot.height + 25;

                    if (workflowData) {
                        loop(workflowData, treeNodeData, newLevel, workflowDefaultRoot);
                    }
                }

                currentNodeGeo = getGeoByRelativeNode(parentNode, currentNodeGeo);
                treeNodeData._geometry = currentNodeGeo;

                relationTree.push({
                    parent: parent,
                    source: treeNodeData,
                });

                // 处理父亲依赖节点
                if (parentNodes) {
                    for (let i = 0; i < parentNodes.length; i++) {
                        const nodeData = parentNodes[i];
                        const node = Object.assign({}, defaultRoot);
                        node.level = level - 1;
                        node.index = i + 1;
                        node.count = parentNodes.length;
                        nodeData._geometry = getGeoByRelativeNode(currentNodeGeo, node);

                        relationTree.push({
                            parent: parent,
                            source: nodeData,
                            target: treeNodeData,
                        });

                        if (nodeData.parentNodes) {
                            loop(nodeData, parent, level - 1, currentNodeGeo)
                        }
                    }
                }

                if (childNodes) {
                    // 处理被依赖节点
                    for (let i = 0; i < childNodes.length; i++) {
                        const nodeData = childNodes[i];
                        if (!nodeData) continue;

                        const node = Object.assign({}, defaultRoot);
                        node.level = level + 1;
                        node.index = i + 1;
                        node.count = childNodes.length;
                        nodeData._geometry = getGeoByRelativeNode(currentNodeGeo, node);

                        relationTree.push({
                            parent: parent,
                            source: treeNodeData,
                            target: nodeData,
                        });

                        if (nodeData.jobVOS) {
                            loop(nodeData, parent, ++level, currentNodeGeo)
                        }
                    }
                }
            }
        }

        loop(data, null, level, defaultRoot);

        return relationTree;
    }

    renderGraph = (dataArr) => {
        const cellCache = this._vertexCells;
        const graph = this.graph;
        const defaultParent = graph.getDefaultParent();

        const getVertex = (parentCell, data) => {
            if (!data) return null;

            let style = getVertxtStyle(data.status);

            const isWorkflow = data.batchTask.taskType === TASK_TYPE.WORKFLOW;
            const isWorkflowNode = data.batchTask.flowId && data.batchTask.flowId;

            if (isWorkflow) {
                style += 'shape=swimlane;swimlaneFillColor=#F7FBFF;fillColor=#D0E8FF;strokeColor=#92C2EF;dashed=1;color:#333333;';
            }

            if (isWorkflowNode && parentCell !== defaultParent) {
                style += 'rounded=1;arcSize=60;'
            }

            const geo = data._geometry || {
                x : 10, y : 10,
                width: VertexSize.width,
                height: VertexSize.height
            };

            const cell = graph.insertVertex(
                isWorkflow ? null : parentCell,
                data.id,
                data,
                geo.x, geo.y,
                geo.width, geo.height,
                style,
            )

            if (isWorkflow) {
                cell.geometry.alternateBounds = new mxRectangle(10, 10, VertexSize.width, VertexSize.height);
            }
        
            cell.data = data;
            cell.isPart = isWorkflowNode;

            return cell
        }

        if (dataArr) {
            for (let i = 0; i < dataArr.length; i++) {
                const { source, target, parent } = dataArr[i];

                let sourceCell = source ? cellCache[source.id] : undefined;
                let targetCell = target ? cellCache[target.id] : undefined;
                let parentCell = defaultParent;
                const isWorkflowNode = source && source.batchTask.flowId && source.batchTask.flowId;

                if (parent) {
                    const existCell = cellCache[parent.id];
                    if (existCell) {
                        parentCell = existCell
                    } else {
                        parentCell = getVertex(defaultParent, parent);
                        cellCache[parent.id] = parentCell;
                    }
                }

                if (source && !sourceCell) {
                    sourceCell = getVertex(parentCell, source);
                    cellCache[source.id] = sourceCell;
                }
                if (target && !targetCell) {
                    targetCell = getVertex(parentCell, target);
                    cellCache[target.id] = targetCell;
                }

                const edges = graph.getEdgesBetween(sourceCell, targetCell);
                const edgeStyle = !isWorkflowNode ? null : 'strokeColor=#B7B7B7;';

                if (edges.length === 0) {
                    graph.insertEdge(defaultParent, null, '', sourceCell, targetCell, edgeStyle)
                }
            }
        }
    }


    doInsertVertex = (data, nodeType) => {

        const graph = this.graph;

        // clean data;
        graph.getModel().clear();
        this._vertexCells = {};
        const cells = graph.getChildCells(graph.getDefaultParent());
        graph.removeCells(cells);

        // handData
        let originData = this._originData;
        if (originData) {
            mergeTreeNodes(originData, data, nodeType);
            this._originData = originData;
        } else {
            this._originData = data;
        }
  
        const arrayData = this.preHandGraphTree(this._originData);
        this.renderGraph(arrayData);
        this.initView();
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
            const taskId = currentNode.batchTask && currentNode.batchTask.id;

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
                ctx.props.goToTaskDev(taskId)
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
                currentNode.status === TASK_STATUS.STOPED) || // 已停止
                currentNode.status === TASK_STATUS.PARENT_FAILD


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

    saveViewInfo = () => {
        const view = this.graph.getView();
        const translate = view.getTranslate();
        if (translate.x > 0) {
            this._view = {
                translate: translate,
                scale: view.getScale(),
            };
        }
    }

    initView = () => {
        const view = this._view;
        const graph = this.graph;
        if (view) {
            const scale = view.scale;
            const dx = view.translate.x;
            const dy = view.translate.y;
            graph.view.setScale(scale);
            graph.view.setTranslate(dx, dy);
        } else {
            graph.center();
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
        this.saveViewInfo();
        setTimeout(() => {
            this.initGraph(this.props.taskJob.id);
        }, 0);
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
                    height: '100%',
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
                    >
                    </div>
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="刷新">
                        <Icon type="reload" onClick={this.refresh} style={{color: '#333333'}}/>
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
                    width={800}
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
                    maskClosable={false}
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
        style[mxConstants.STYLE_STROKECOLOR] = '#A7CDF0';
        style[mxConstants.STYLE_FILLCOLOR] = '#EDF6FF';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        style[mxConstants.FONT_BOLD] = 'normal';
        style[mxConstants.STYLE_OVERFLOW] = 'hidden';

        return style;
    }

    getDefaultEdgeStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#2491F7';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
        style[mxConstants.STYLE_FONTSIZE] = '10';
        style[mxConstants.STYLE_ROUNDED] = false;
        return style;
    }
}
export default TaskFlowView;