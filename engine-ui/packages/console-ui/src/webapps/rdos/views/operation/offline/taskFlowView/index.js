import React, { Component } from 'react'
import { Link, hashHistory } from 'react-router'

import {
    Button, Tooltip, Spin,
    Modal, message, Icon,
} from 'antd'

import utils from 'utils'
import Api from '../../../../api'
import MyIcon from '../../../../components/icon'
import { getVertxtStyle } from '../../../../comm'
import { TASK_STATUS, TASK_TYPE } from '../../../../comm/const'
import { taskTypeText } from '../../../../components/display'
import { TaskInfo } from './taskInfo'
import { LogInfo } from '../taskLog'
import RestartModal from './restartModal'

import {
    workbenchActions
} from '../../../../store/modules/offlineTask/offlineAction' 
import { workbenchAction } from '../../../../store/modules/offlineTask/actionType'
import * as FlowAction from '../../../../store/modules/operation/taskflow'

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
    mxCellState,
    mxConstants,
    mxEdgeStyle,
    mxPopupMenu,
    mxPerimeter,
    mxUndoManager,
    mxCompactTreeLayout,
    mxUtils,
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
        this._vertexCells = [] // 用于缓存创建的顶点节点
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        const editor = this.Container
        this.initEditor()
        this.loadEditor(editor)
        this.listenOnClick()
        this.hideMenu()
        this.loadTaskChidren({
            jobId: id,
            level: 6,
        })
    }

    componentWillReceiveProps(nextProps) {
        const currentJob = this.props.taskJob
        const { taskJob, visibleSlidePane } = nextProps
        if (taskJob && visibleSlidePane && taskJob.id !== currentJob.id) {
            this.initGraph(taskJob.id)
        }
    }

    loadEditor = (container) => {
        // Disable context menu
        mxEvent.disableContextMenu(container)
        const graph = new mxGraph(container)
        this.graph = graph
        // 启用绘制
        graph.setPanning(true);
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.setConnectable(true)
        graph.setTooltips(true)
        graph.view.setScale(1)
        // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)
        // 禁止连接
        graph.setConnectable(false)
        // 禁止Edge对象移动
        graph.isCellsMovable = function(cell) {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge)
        }
        // 禁止cell编辑
        graph.isCellEditable = function() {
            return false
        }
        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle)
        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip
        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';

        // enables rubberband
        new mxRubberband(graph)
        this.initContextMenu(graph)
    }

    formatTooltip = (cell) => {
        const data = cell.getAttribute('data');
        const task = data ? JSON.parse(data).batchTask : '';
        return task ? task.name : ''
    }

    corvertValueToString = (cell) => {
        if (mxUtils.isNode(cell.value)) {
            if (cell.value.nodeName.toLowerCase() == 'task') {
                const data = cell.getAttribute('data');
                const task = data ? JSON.parse(data).batchTask : '';
                const taskType = taskTypeText(task.taskType);
                if (task) {
                    return `<div class="vertex"><span class="vertex-title"><span>${task.name || ''}</span>
                    <span style="font-size:10px; color: #666666;">${taskType}</span></span>
                    </div>`
                }
            }
        }
        return '';
    }

    insertEdge = (graph, type, parent, child) => {
        if (type === 'children') {
            graph.insertEdge(parent, null, '', parent, child)
        } else {
            graph.insertEdge(parent, null, '', child, parent)
        }
    }

    insertVertex = (graph, data, parent, type) => {
        if (data) {

            const style = getVertxtStyle(data.status)

            const exist = this._vertexCells.find((cell) => {
                const dataStr = cell.getAttribute('data')
                if (!dataStr) return null
                const itemData = JSON.parse(dataStr)
                return itemData.taskId === data.taskId
            })

            let newVertex = exist;

            if (exist && parent.id !== '1') {
                this.insertEdge(graph, type, parent, exist)
            } else if (!exist) {
                // 创建节点
                const doc = mxUtils.createXmlDocument()
                const taskInfo = doc.createElement('Task')
                taskInfo.setAttribute('id', data.id)
                taskInfo.setAttribute('data', JSON.stringify(data))

                // 插入当前节点
                newVertex = graph.insertVertex(
                    graph.getDefaultParent(), null, taskInfo, 0, 0,
                    VertexSize.width, VertexSize.height, style
                )

                this.insertEdge(graph, type, parent, newVertex)

                // 缓存节点
                this._vertexCells.push(newVertex)
            }

            if (data.jobVOS) {
                const children = data.jobVOS
                for (let i = 0; i < children.length; i++) {
                    this.insertVertex(graph, children[i], newVertex, type)
                }
            }
        }
    }

    doInsertVertex = (data, type) => {
        const graph = this.graph
        let layout = this.layout;

        if (!layout) {
            layout = new mxCompactTreeLayout(graph, false)
            this.layout = layout;
        }
        const cx = (graph.container.clientWidth - VertexSize.width) / 2;
        const cy = 200;

        const parent = graph.getDefaultParent()
        const model = graph.getModel()
        model.beginUpdate()

        try {
            this.insertVertex(graph, data, parent, type)
            // Executes the layout
            layout.execute(parent);
            graph.view.setTranslate(cx, cy);

        } finally {
            model.endUpdate()
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
            ctx.setState({ loading: 'success'})
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
            ctx.setState({ loading: 'success'})
        })
    }

    initContextMenu = (graph) => {
        const ctx = this
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
        mxPopupMenu.prototype.showMenu = function() {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0 && cells[0].vertex) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };
        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function(menu, cell, evt) {

            if (!cell) return

            const currentNode = JSON.parse(cell.getAttribute('data'))

            menu.addItem('展开上游（6层）', null, function() {
                ctx.loadTaskParent({
                    jobId: currentNode.id,
                    level: 6,
                })
            })
            menu.addItem('展开下游（6层）', null, function() {
                ctx.loadTaskChidren({
                    jobId: currentNode.id,
                    level: 6,
                })
            })
            // menu.addSeparator()
            menu.addItem('查看任务日志', null, function() {
                // const url = `/operation/task-log/${currentNode.jobId}`
                // hashHistory.push(url)
                ctx.showJobLog(currentNode.jobId)
            })
            menu.addItem('修改任务', null, function() {
                ctx.props.goToTaskDev(currentNode.taskId)
            })
            menu.addItem('查看任务属性', null, function() {
                ctx.setState({  visible: true })
            })
            // menu.addSeparator()
            menu.addItem('终止', null, function() {
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

            menu.addItem('刷新任务实例', null, function() {
                ctx.resetGraph(cell)
            })

            menu.addItem('重跑并恢复调度', null, function() {
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

            menu.addItem('置成功并恢复调度', null, function() {
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

            menu.addItem('重跑下游并恢复调度', null, function() {
                ctx.setState({ visibleRestart: true })
            })
        }
    }

    stopTask = (params) => {
        Api.stopJob(params).then(res => {
            if (res.code === 1 ) {
                message.success('任务终止运行命令已提交！')
            }
            this.refresh()
        })
    }

    restartAndResume = (params, msg) => { // 重跑并恢复任务
        const { reload } = this.props
        Api.restartAndResume(params).then(res => {
            if (res.code === 1 ) {
                message.success(`${msg}命令已提交!`)
                if (reload) reload();
            } else {
                message.error(`${msg}提交失败！`)
            }
            this.refresh()
        })
    }

    listenDoubleClick() {
        this.graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell) {
                // window.open("http://www.google.com")
            }
        })
    }

    listenOnClick() {
        const ctx = this
        this.graph.addListener(mxEvent.CLICK, function(sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {
                let data = cell.getAttribute('data')
                data = data ? JSON.parse(data) : ''
                ctx.setState({ selectedJob: data })
            }
        })
    }

    refreshTask = () => {
        const ctx = this
        const { selectedJob, data, sort } = this.state
        this.setState({ loading: 'loading' })
        if (selectedJob) {
            Api.getJobChildren({ jobId: selectedJob.id, level: 1, }).then(res => {
                if (ctx.graph) {
                    ctx.graph.getModel().clear();
                }
                const task = res.data
                const tree = Object.assign({}, data)
                replaceTreeNode(tree, task)
                ctx.doInsertVertex(tree, 'children')
                ctx.setState({
                    selectedJob: Object.assign(selectedJob, task),
                    data: tree,
                    loading: 'success',
                })
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
                this.setState({ taskLog: res.data, logVisible: true })
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
        const selectedJob = this.state.selectedJob
        const taskLog = this.state.taskLog
        const { goToTaskDev, project, taskJob } = this.props
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
                        }}
                    />
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="刷新">
                        <Icon type="reload" onClick={this.refresh}/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="放大">
                        <MyIcon onClick={this.zoomIn} type="zoom-in"/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="缩小">
                        <MyIcon onClick={this.zoomOut} type="zoom-out"/>
                    </Tooltip>
                </div>
                <div 
                    className="box-title graph-info"  
                    style={{
                        bottom: 0
                    }}
                >
                    <span>{ taskJob.batchTask && taskJob.batchTask.name || '-' }</span>&nbsp;
                    <span>{ (taskJob.batchTask && taskJob.batchTask.createUser && taskJob.batchTask.createUser.userName) || '-' }</span>&nbsp;
                    发布于&nbsp;
                    <span>{ taskJob.batchTask && utils.formatDateTime(taskJob.batchTask.gmtModified) }</span>&nbsp;
                    <a onClick={ () => { goToTaskDev(taskJob.taskId) }}>查看代码</a>
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
                    title="运行日志"
                    wrapClassName="vertical-center-modal m-log-modal"
                    visible={ this.state.logVisible }
                    onCancel={() => { this.setState({ logVisible: false }) }}
                    footer={null}
                >
                    <LogInfo 
                        log={taskLog.logInfo} 
                        syncJobInfo={taskLog.syncJobInfo}
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
        // style[mxConstants.STYLE_ROUNDED] = true; // 设置radius
        style[mxConstants.STYLE_FILLCOLOR] = '#E6F7FF;';
        // style[mxConstants.STYLE_GRADIENTCOLOR] = '#e9e9e9';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333;';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
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
        return style
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
