import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link, hashHistory } from 'react-router'

import {
    Button, Tooltip, Spin,
    Modal, notification,
} from 'antd'

import utils from 'utils'
import Api from '../../../../api'
import MyIcon from '../../../../components/icon'
import { TASK_STATUS, TASK_TYPE } from '../../../../comm/const'

import { TaskInfo, TaskOverView } from './taskInfo'
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
    mxHierarchicalLayout,
    mxUtils,
} = Mx

const VertexSize = { // vertex大小
    width: 150,
    height: 60,
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

class GraphEditor extends Component {

    state = {
        selectedJob: '', // 选中的Job
        data: {}, // 数据
        loading: 'success',
        lastVertex: '',
        sort: 'children',
        logInfo: {},
        logVisible: false,
        visible: false,
        visibleRestart: false,
    }

    componentDidMount() {
        const editor = this.Container
        this.initEditor()
        this.loadEditor(editor)
        this.listenDoubleClick()
        this.listenOnClick()
        this.hideMenu()
    }

    componentWillReceiveProps(nextProps) {
        const taskFlow = nextProps.taskFlow
        const oldFlow = this.props.taskFlow

        if (taskFlow.id === 0) {
            this.graph.getModel().clear()
            this.setState({ selectedJob: '' })
        } else {
            this.loadTaskChidren({
                jobId: taskFlow.id,
                level: 2,
            })
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
        // enables rubberband
        new mxRubberband(graph)
        this.initContextMenu(graph)
    }

    formatTooltip = (cell) => {
        const data = cell.getAttribute('data');
        const task = data ? JSON.parse(data).batchTask : '';
        return task ? task.name : ''
    }

    getStyles = (type) => {
        switch (type) {
            case TASK_STATUS.RUNNING:
            case TASK_STATUS.FINISHED:
            case TASK_STATUS.SUBMITTING:
            case TASK_STATUS.RESTARTING:
            case TASK_STATUS.SET_SUCCESS:
                return 'whiteSpace=wrap;fillColor=#cfefdf;strokeColor=#18a689;'
            case TASK_STATUS.RUN_FAILED:
            case TASK_STATUS.SUBMIT_FAILED:
                return 'whiteSpace=wrap;fillColor=#fcdbd9;strokeColor=#f04134;'
            default:
                return 'whiteSpace=wrap;'
        }
    }

    corvertValueToString = (cell) => {
        if (mxUtils.isNode(cell.value)) {
            if (cell.value.nodeName.toLowerCase() == 'task') {
                const data = cell.getAttribute('data');
                const task = data ? JSON.parse(data).batchTask : '';
                let txt = ''
                switch (task.taskType) {
                    case TASK_TYPE.MR:
                        txt = 'MR'; break;
                    case TASK_TYPE.SYNC:
                        txt = 'Sync'; break;
                    case TASK_TYPE.VIRTUAL_NODE:
                        txt = 'Virtual'; break;
                    case TASK_TYPE.PYTHON:
                        txt = 'Python'; break;
                    case TASK_TYPE.R:
                        txt = 'R'; break;
                    case TASK_TYPE.SQL:
                    default:
                        txt = 'SQL'; break;
                }
                if (task) {
                    return `
                        <div class="vertex">
                            ${task.name || ''} <br/> ${txt}
                        </div>
                    `
                }
            }
        }
        return '';
    }

    insertVertex = (graph, data, parent, type, level, cacheArr) => {
        if (data) {
            const lev = level + 1 // 层级
            const style = this.getStyles(data.status)
            const doc = mxUtils.createXmlDocument()
            const taskInfo = doc.createElement('Task')
            taskInfo.setAttribute('id', data.id)
            taskInfo.setAttribute('data', JSON.stringify(data))

            const exist = cacheArr.find(item => {
                return item.id === data.id && item.level === lev
            })

            let current = '';

            if (exist) {
                current = exist.node
            } else {
                // 插入当前节点
                current = graph.insertVertex(
                    graph.getDefaultParent(), null, taskInfo, 0, 0,
                    VertexSize.width, VertexSize.height, style
                )
                cacheArr.push({
                    id: data.id,
                    node: current,
                    level: lev
                })
            }
            
            if (type === 'children') {
                graph.insertEdge(parent, null, '', parent, current)
            } else {
                graph.insertEdge(current, null, '', current, parent)
            }

            if (data.jobVOS) {
                const children = data.jobVOS
                for (let i = 0; i < children.length; i++) {
                    this.insertVertex(graph, children[i], current, type, lev, cacheArr)
                }
            }
        }
    }

    doInsertVertex = (data, type) => {
        const graph = this.graph
        const model = graph.getModel()
        const layout = new mxHierarchicalLayout(graph)
        model.clear()
        const cx = graph.container.clientWidth / 3;
        const cy = 150;
        const level = -1;
        const cacheArr = []
        model.beginUpdate()
        const parent = graph.getDefaultParent()
        try {
            this.insertVertex(graph, data, parent, type, level, cacheArr)
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
            menu.addSeparator()
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
            menu.addSeparator()
            menu.addItem('终止', null, function() {
                ctx.stopTask({
                    jobId: currentNode.id,
                })
            }, null, null,
                // 显示终止操作
                currentNode.status === TASK_STATUS.RUNNING || // 运行中
                currentNode.status === TASK_STATUS.RESTARTING || // 重启中
                currentNode.status === TASK_STATUS.SUBMITTING || // 提交中
                currentNode.status === TASK_STATUS.WAIT_SUBMIT || // 等待提交
                currentNode.status === TASK_STATUS.WAIT_RUN
            )

            menu.addItem('刷新任务实例', null, function() {
                ctx.refreshTask(cell)
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
                notification['success']({
                    message: '终止任务',
                    description: '任务终止运行命令已提交！',
                });
                this.props.udpateGraphStatus();
            } else {
                notification['error']({
                    message: '终止任务',
                    description: '任务终止提交失败！',
                });
            }
            this.refreshTask()
        })
    }

    restartAndResume = (params, msg) => { // 重跑并恢复任务
        Api.restartAndResume(params).then(res => {
            if (res.code === 1 ) {
                notification['success']({
                    message: msg,
                    description: `${msg}命令已提交!`,
                });
                this.props.udpateGraphStatus();
            } else {
                notification['error']({
                    message: msg,
                    description: `${msg}提交失败！`,
                });
            }
            this.refreshTask()
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
            if (cell) {
                let data = cell.getAttribute('data')
                data = data ? JSON.parse(data) : ''
                ctx.setState({ selectedJob: data })
            }
        })
    }

    resetGraph = () => {
        const { taskFlow } = this.props
        if (taskFlow) {
            this.loadTaskChidren({
                jobId: taskFlow.id,
                level: 2,
            })
        }
    }

    refreshTask = () => {
        const ctx = this
        const { selectedJob, data, sort } = this.state
        this.setState({ loading: 'loading' })
        if (selectedJob) {
            Api.getJobChildren({ jobId: selectedJob.id, level: 1, }).then(res => {
                const task = res.data
                const tree = Object.assign({}, data)
                replaceTreeNode(tree, task)
                ctx.doInsertVertex(tree, sort)
                ctx.setState({
                    selectedJob: Object.assign(selectedJob, task),
                    data: tree,
                    loading: 'success',
                })
            })
        }
    }

    showJobLog = (jobId) => {
        Api.getOfflineTaskLog({ jobId: jobId }).then((res) => {
            if (res.code === 1) {
                const logInfo = res.data && res.data.logInfo
                const log = logInfo ? JSON.parse(logInfo) : {}
                this.setState({ logInfo: log, logVisible: true })
            }
        })
    }

    graphEnable() {
        const status = this.graph.isEnabled()
        this.graph.setEnabled(!status)
    }

    zoomIn = () => {
        this.graph.zoomIn()
    }

    zoomOut = () => {
        this.graph.zoomOut()
    }

    hideMenu = () => {
        document.addEventListener('click', (e) => {
            const graph = this.graph
            const menu = graph.popupMenuHandler
            if (graph.popupMenuHandler.isMenuShowing()) {
                graph.popupMenuHandler.hideMenu()
            }
        })
    }

    /* eslint-enable */
    render() {
        const task = this.state.selectedJob
        const logInfo = this.state.logInfo
        const project = this.props.project
        return (
            <div className="graph-editor">
                <div className="editor pointer" ref={(e) => { this.Container = e }} />
                <div className="absolute-middle graph-bg">任务视图</div>
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={this.state.loading === 'loading'}
                >
                    <div className="absolute-middle" style={{ width: '100%', height: '100%' }}/>
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="放大">
                        <MyIcon onClick={this.zoomIn} type="zoom-in"/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="缩小">
                        <MyIcon onClick={this.zoomOut} type="zoom-out"/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="刷新">
                        <MyIcon onClick={this.refreshTask} type="loop2"/>
                    </Tooltip>
                </div>
                <div className="goback-list">
                    <Link to={'/operation/offline-operation'}>
                        <Button icon="rollback">
                            列表模式
                        </Button>
                    </Link>
                </div>
                {task ? <TaskOverView task={task} project={project} /> : ''}
                <Modal
                    title="查看属性"
                    wrapClassName="vertical-center-modal"
                    visible={this.state.visible}
                    onCancel={() => { this.setState({ visible: false }) }}
                    footer={null}
                >
                    <TaskInfo task={task} project={project} />
                </Modal>
                <Modal
                    width="80%"
                    title="任务日志"
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    visible={this.state.logVisible}
                    onCancel={() => { this.setState({ logVisible: false }) }}
                    footer={null}
                >
                    <LogInfo log={logInfo} height="600px"/>
                </Modal>
                <RestartModal 
                    restartNode={task}
                    udpateGraphStatus={this.props.udpateGraphStatus}
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
        style[mxConstants.STYLE_STROKECOLOR] = '#9e9e9e';
        style[mxConstants.STYLE_ROUNDED] = true;
        style[mxConstants.STYLE_FILLCOLOR] = '#e9e9e9';
        // style[mxConstants.STYLE_GRADIENTCOLOR] = '#e9e9e9';
        style[mxConstants.STYLE_FONTCOLOR] = '#000';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        return style;
    }

    getDefaultEdgeStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#18a689';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.ElbowConnector; // edgeStyle
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
        style[mxConstants.STYLE_ROUNDED] = true;
        style[mxConstants.STYLE_FONTSIZE] = '10';
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
export default connect((state) => {
    const { taskFlow } = state.operation
    return {
        taskFlow,
        project: state.project,
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }, 
        udpateGraphStatus() {
            dispatch(FlowAction.udpateGraphStatus('change'))
        }
    }
})(GraphEditor)
