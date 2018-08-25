import React, { Component } from 'react'
import { hashHistory } from 'react-router'

import {
    Tooltip, Spin, message, Icon,
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import MyIcon from '../../../components/icon'
import { taskTypeText } from '../../../components/display'
import { TASK_STATUS, TASK_TYPE, SCHEDULE_STATUS } from '../../../comm/const'

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
    mxSwimlaneManager,
    mxPerimeter,
    mxGraphHandler,
    mxCompactTreeLayout,
} = Mx

const VertexSize = { // vertex大小
    width: 150,
    height: 36,
}

const getVertexNode = (obj) => {
    return obj;
}

export default class TaskView extends Component {

    state = {
        selectedTask: '', // 选中的Task
        data: {}, // 数据
        loading: 'success',
        lastVertex: '',
        sort: 'children',
        visible: false,
    }

    initGraph = (id) => {
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.graph = "";
        this._vertexCells = []; // 用于缓存创建的顶点节点

        const editor = this.Container
        this.initEditor()
        this.loadEditor(editor)
        this.hideMenu()
        this.loadTaskChidren({
            taskId: id,
            level: 6,
        })
    }

    componentWillReceiveProps(nextProps) {
        const currentTask = this.props.tabData
        const { tabData, visibleSlidePane, tabKey } = nextProps
        if ((!currentTask&&tabData)||tabData && visibleSlidePane && tabData.id !== currentTask.id) {
            this.initGraph(tabData.id)
        }
        if (tabKey && this.props.tabKey !== tabKey && tabKey === 'taskFlow') {
            this.refresh();
        }
    }

    loadTaskChidren = (params) => {
        const ctx = this

        params.type = 2;

        this.setState({ loading: 'loading' })
        Api.getTaskChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ selectedTask: data, data })
                ctx.doInsertVertex(data)
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadTaskParent = (params) => {
        const ctx = this

        params.type = 1;

        this.setState({ loading: 'loading' })
        Api.getTaskChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ data, selectedJob: data })
                ctx.doInsertVertex(res.data)
            }
            ctx.setState({ loading: 'success'})
        })
    }

    insertVertex = (rootCell, parent, data) => {

        const graph = this.graph;
        const exist = this._vertexCells[data.id];

        if (exist) {
            const edges = graph.getEdgesBetween(parent, exist)
            if (edges.length === 0) {
                graph.insertEdge(rootCell, null, '', parent, exist)
            }
            return exist;
        } else {// 如果该节点为新节点， 则从新生成并创建
            let style = this.getStyles(data.taskType);
            const isWorkflow = data.taskType === TASK_TYPE.WORKFLOW;

            let width = VertexSize.width;
            let height = VertexSize.height;
            if (isWorkflow) {
                width = width + 20;
                height = height + 100;
                style += 'shape=swimlane;';
            }

            let newVertex = graph.insertVertex(rootCell, null, data, 0, 0,
                width, height, style
            )
            newVertex.isPart = data.flowId && data.flowId !== 0;
            // 缓存节点
            this._vertexCells[data.id] = newVertex
            return newVertex;
        }
    }

    loopTree = (rootCell, treeNodeData, parent) => {

        if (treeNodeData) {
            const graph = this.graph;
            const parentNodes = treeNodeData.taskVOS; // 父节点
            const childNodes = treeNodeData.subTaskVOS; // 子节点

            const currentNodeData = getVertexNode(treeNodeData)
            const currentNode = this.insertVertex(rootCell, parent, currentNodeData)

            // 处理依赖节点
            if (parentNodes && parentNodes.length > 0) {
                for (let i = 0; i < parentNodes.length; i++) {
                    const nodeData = getVertexNode(parentNodes[i])
                    // 插入新节点
                    const newNode = this.insertVertex(rootCell, null, nodeData)
                    // 创建连接线
                    graph.insertEdge(rootCell, null, '', newNode, currentNode)

                    if (parentNodes[i].taskVOS) {
                        this.loopTree(rootCell, parentNodes[i], null)
                    }
                }
            }

            // 处理被依赖节点
            if (childNodes && childNodes.length > 0) {
                for (let i = 0; i < childNodes.length; i++) {
                    const nodeData = getVertexNode(childNodes[i])

                    // 插入新节点
                    const newNode = this.insertVertex(rootCell, currentNode, nodeData)
                    graph.insertEdge(rootCell, null, '', currentNode, newNode);

                    if (nodeData.taskType === TASK_TYPE.WORKFLOW) {
                        const workflowData = nodeData.subNodes;
                        if (workflowData) {
                            this.executeLayout(newNode, () => {
                                this.loopTree(newNode, workflowData, null);
                            })
                        }
                    }
                    if (childNodes[i].subTaskVOS) {
                        this.loopTree(rootCell, childNodes[i], null)
                    }
                }
            }
        }
    }

    doInsertVertex = (data) => {
        const graph = this.graph;
        const model = graph.getModel();
        const parent = graph.getDefaultParent();

        new mxSwimlaneManager(graph);

        const layout = new mxCompactTreeLayout(graph)
        layout.horizontal = false;
        layout.useBoundingBox = false;
        layout.edgeRouting = false;
        layout.levelDistance = 30;
        layout.nodeDistance = 10;
        layout.resizeParent = true;

        this.executeLayout = function(layoutNode, change, post) {
            model.beginUpdate();
            try {
                if (change != null) { change(); }
                layout.execute(layoutNode);
            } catch (e) {
                throw e;
            } finally {
                graph.getModel().endUpdate();
                if (post != null) { post();}
            }
        }

        this.executeLayout(parent, () => {
            this.loopTree(parent, data, parent);
        }, () => {
            graph.center();
        })
    }

    loadEditor = (container) => {
        // Disable default context menu
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
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);
        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip;
        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';

        // enables rubberband
        new mxRubberband(graph)

        this.initContextMenu(graph);
        this.initGraphEvent();
    }

    formatTooltip = (cell) => {
        const task = cell.value || ''; 
        return task ? task.name : ''
    }

    corvertValueToString = (cell) => {
        if (cell.vertex && cell.value) {
            const task = cell.value || {};
            const taskType = taskTypeText(task.taskType);
            if (task) {
                return `<div class="vertex"><span class="vertex-title">${task.name || ''}</span>
                <span style="font-size:10px; color: #666666;">${taskType}</span>
                </div>`
            }
        }
        return '';
    }

    getStyles = (type) => {
        return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;';
    }

    forzenTasks = (ids, mode) => {
        const ctx = this
        Api.forzenTask({
            taskIdList: ids, 
            scheduleStatus: mode  //  1正常调度, 2暂停 NORMAL(1), PAUSE(2),
        }).then((res) => {
            if (res.code === 1) {
                message.success('操作成功！');
                ctx.props.reload();
                ctx.refresh();
            }
        })
    }

    initContextMenu = (graph) => {
        const ctx = this
        const { goToTaskDev, clickPatchData, tabData } = this.props

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

            const currentNode = cell.value || {};

            menu.addItem('展开上游（6层）', null, function() {
                ctx.loadTaskParent({
                    taskId: currentNode.id,
                    level: 6,
                })
            })
            menu.addItem('展开下游（6层）', null, function() {
                ctx.loadTaskChidren({
                    taskId: currentNode.id,
                    level: 6,
                })
            })
            menu.addItem('补数据', null, function() {
                clickPatchData(currentNode)
            })
            menu.addItem('查看代码', null, function() {
                goToTaskDev(currentNode.id)
            })
            menu.addItem('冻结', null, function() {
                ctx.forzenTasks([currentNode.id], SCHEDULE_STATUS.STOPPED)
            }, null, null, currentNode.scheduleStatus === SCHEDULE_STATUS.NORMAL) // 正常状态

            menu.addItem('解冻', null, function() {
                ctx.forzenTasks([currentNode.id], SCHEDULE_STATUS.NORMAL);
            }, null, null, currentNode.scheduleStatus === SCHEDULE_STATUS.STOPPED) // 冻结状态

            menu.addItem('查看实例', null, function() {
                hashHistory.push(`/operation/offline-operation?job=${currentNode.name}`)
            })
        }
    }

    initGraphEvent = () => {

        const ctx = this
        const graph = this.graph;
        
        graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell) {
                // window.open("http://www.google.com")
            }
        })

        graph.addListener(mxEvent.onClick, function(sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {
                let data = cell.value;
                ctx.setState({ selectedTask: data })
            }
        })

        graph.addListener(mxEvent.FOLD_CELLS, function(sender, evt) {
            const cells = evt.getProperty('cells');
            const collapse = evt.getProperty('collapse');
            const cell = cells[0];
            // if (cell && collapse) {
            //     cell.style = cell.style += 'verticalLabelPosition=middle;verticalAlign=middle;';
            // } else {
            //     cell.style = cell.style += 'verticalLabelPosition=top;verticalAlign=bottom;';
            // }
        })
    }

    refresh = () => {
        this.initGraph(this.props.tabData.id)
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
            const popMenus = document.querySelector('.mxPopupMenu')
            if (popMenus) {
                document.body.removeChild(popMenus)
            }
        })
    }

    /* eslint-enable */
    render() {
        const task = this.state.selectedTask
        const { goToTaskDev } = this.props

        return (
            <div className="graph-editor" 
                style={{
                    position: 'relative',
                    height: '100%',
                }}
            >
                <div className="editor pointer" ref={(e) => { this.Container = e }} />
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={this.state.loading === 'loading'}
                >
                    <div className="absolute-middle" style={{ width: '100%', height: '100%' }}/>
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="刷新">
                        <Icon type="reload" onClick={this.refresh} />
                    </Tooltip>
                    <Tooltip placement="bottom" title="放大">
                        <MyIcon onClick={this.zoomIn} type="zoom-in"/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="缩小">
                        <MyIcon onClick={this.zoomOut} type="zoom-out"/>
                    </Tooltip>
                </div>
                <div className="box-title graph-info">
                    <span>{task.name || '-'}</span>
                    <span style={{marginLeft:"15px"}} >{ (task.createUser && task.createUser.userName) || '-' }</span>&nbsp;
                    发布于&nbsp;
                    <span>{utils.formatDateTime(task.gmtModified)}</span>&nbsp;
                    <a onClick={() => { goToTaskDev(task.id) }}>查看代码</a>
                    {/* <a href={`/rdos.html#/view/task/${task.id}`} target="_blank">查看代码</a> */}
                </div>
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
