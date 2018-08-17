import React, { Component } from 'react'
import { connect } from 'react-redux'
import { 
    Tooltip, Spin, Icon, 
    Button, Modal, message 
} from 'antd'

import KeyEventListener from 'widgets/keyCombiner/listener'
import KEY_CODE from 'widgets/keyCombiner/keyCode'

import Api from '../../../../api'
import MyIcon from '../../../../components/icon'
import { taskTypeText } from '../../../../components/display'

import {
    workbenchActions,
} from '../../../../store/modules/offlineTask/offlineAction'

const Mx = require('public/rdos/mxgraph')({
    mxImageBasePath: 'public/rdos/mxgraph/images',
    mxBasePath: 'public/rdos/mxgraph',
})

const {
    mxGraph,
    mxShape,
    mxPoint,
    mxCell,
    mxText,
    mxGeometry,
    mxUtils,
    mxEvent,
    mxPopupMenu,
    mxDragSource,
    mxPolyline,
    mxConstants,
    mxEdgeStyle,
    mxPerimeter,
    mxRubberband,
    mxUndoManager,
    mxGraphView,
    mxGraphHandler,
    mxCompactTreeLayout,
    mxConnectionConstraint,
} = Mx;

const VertexSize = { // vertex大小
    width: 150,
    height: 36,
}

const WIDGETS_PREFIX = 'JS_WIDGETS_'; // Prefix for widgets

const applyCellStyle = (cellState, style) => {
    if (cellState) {
        cellState.style = Object.assign(cellState.style, style);
        cellState.shape.apply(cellState)
        cellState.shape.redraw();
    }
}

@connect(state => {
    const { offlineTask } = state;
    return {
        taskTypes: offlineTask.comm.taskTypes,
        workflow: state.offlineTask.workflow,
    }
}, workbenchActions )
class WorkflowEditor extends Component {

    state = {}

    componentDidMount() {
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.graph = "";
        const editor = this.Container;
        console.log('init workflow:', this.props.data)
        this.initEditor()
        this.loadEditor(editor)
        this.hideMenu()
        const workflowData = this.props.data.workflow;
        if (workflowData) {
            this.renderData(workflowData);
        }
    }

    componentWillReceiveProps(nextProps) {
        const old = this.props.workflow;
        const next = nextProps.workflow;
        if (old !== next) {
            console.log('nextStatus:', nextProps)
            if (next.status === 'cancel') {
                // this.graph.removeCells();
                this._currentNewVertex = null;
            } else if (next.status === 'created') {
                this.updateCellData(next.node);
            }
        }
    }

    shouldComponentUpdate (nextProps, nextState) {
        return false;
    }

    loadEditor = (container) => {
        // Disable default context menu
        mxGraphView.prototype.optimizeVmlReflows = false;
        mxText.prototype.ignoreStringSize = true; //to avoid calling getBBox
        // 启用辅助线
        mxGraphHandler.prototype.guidesEnabled = true;
        mxEvent.disableContextMenu(container);

        const graph = new mxGraph(container)
        this.graph = graph
        // 启用绘制
        graph.setPanning(true);
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.keepEdgesInBackground = true;
        graph.allowLoops = false;
        graph.cellsResizable = false;
        graph.setConnectable(true);
        graph.setTooltips(true)
        graph.view.setScale(1)
        // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)

        // 启用/禁止连接
        graph.setConnectable(true)

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
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);
        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip
        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';
        mxConstants.GUIDE_COLOR = '#2491F7';

        // enables rubberband
        new mxRubberband(graph)

        // Initial draggable elements
        this.listenConnection();
        this.initDraggableToolBar();
        this.initGraphLayout();
        this.initUndoManager();
        this.initContextMenu();
        this.initGraphEvent();
    }

    getStyles = (type) => {
        return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;'
    }

    formatTooltip = (cell) => {
        const task = cell.data || '';
        const tips = task ? `${task.name}${task.notSync ? ' (未保存) ' : ''}` : '';
        return tips
    }

    corvertValueToString = (cell) => {
        if (cell && cell.vertex) {
            const task = cell.data;
            console.log('task:', task)
            if (task) {
                let unSave = task.notSync ? '<span style="color:red;">*</span>' : '';
                const taskType = taskTypeText(task.taskType);
                return `<div class="vertex"><span class="vertex-title">${unSave} ${task.name || ''}</span>
                <span style="font-size:10px; color: #666666;">${taskType}</span>
                </div>`
            }
        }
        return '';
    }

    onkeyDown = (evt) => {
        const keyCode = evt.keyCode;
        const ctx = this;
        switch(keyCode) {
            case KEY_CODE.BACKUP: {
                // ctx.removeCell()
                break;
            }
            default:
        }
    }

    toggleCreate = (taskType) => {
        const { toggleCreateTask, updateWorkflow, data } = this.props
        const workflow = {
            workflowId: data.id,
            taskType: taskType,
            status: 'create',
        }
        toggleCreateTask();
        updateWorkflow(workflow);
    }

    updateCellData = (cellData) => {
        const cell = this._currentNewVertex;
        if (cell) {
            const cellState = this.graph.view.getState(cell);
            if (cellState.text) {
                cellState.data = cellData;
                state.text.apply(state);
                state.text.redraw();
                this.updateGraphData();
            }
        }
    }

    insertItemVertex = (graph, evt, target, x, y) => {

        const taskType = this._currentSourceType.key;
        const newCell = new mxCell(
            '新节点',
            new mxGeometry(0, 0, VertexSize.width, VertexSize.height)
        );
        newCell.vertex = true;
        newCell.data = {
            taskType: taskType,
            name: '新节点',
        }

        const cells = graph.importCells([newCell], x, y, target);
        if (cells != null && cells.length > 0) {
            this.toggleCreate(taskType);
            this._currentNewVertex = cells[0];
            graph.scrollCellToVisible(cells[0]);
            graph.setSelectionCells(cells);
        }
    }

    getUnderMouseGraph = (evt) => {
        const x = mxEvent.getClientX(evt);
        const y = mxEvent.getClientY(evt);

        const elt = document.elementFromPoint(x, y);
        if (mxUtils.isAncestorNode(this.graph.container, elt)) {
            return this.graph;
        }
        return null;
    }

    removeCell(cells) {
        const ctx = this;
        // 获取选中的Cell
        const cell = cells || this.graph.getSelectionCells() // getSelectionCell
        if (cell && cell.length > 0) {
            if (cell[0].vertex) {
                Modal.confirm({
                    title: '确认对话框',
                    okText: '确认',
                    cancelText: '取消',
                    content: (
                        <div>
                            <p>您确认删除当前节点吗？</p>
                        </div>
                    ),
                    onOk() {
                        ctx.graph.removeCells(cell)
                    },
                });
            } else if (cell[0].edge) {
                ctx.graph.removeCells(cell)
            }
        }
    }

    initUndoManager() {
        const undoManager = new mxUndoManager()
        const graph = this.graph
        this.undoMana = undoManager
        const listener = function(sender, evt) {
            undoManager.undoableEditHappened(evt.getProperty('edit'));
        }
        graph.getModel().addListener(mxEvent.UNDO, listener);
        graph.getView().addListener(mxEvent.UNDO, listener);
    }

    initGraphLayout = () => {
        const graph = this.graph;
        const model = graph.getModel();

        this.executeLayout = function(change, post) {

            const parent = graph.getDefaultParent();
            model.beginUpdate();

            try {
                const layout = new mxCompactTreeLayout(graph, false);
                layout.horizontal = false;
                layout.useBoundingBox = false;
                layout.edgeRouting = false;
                layout.levelDistance = 40;
                layout.nodeDistance = 20;

                if (change != null) {
                    change();
                }
                layout.execute(parent);
            } catch (e) {
                throw e;
            } finally {
                graph.getModel().endUpdate();
                if (post != null) { post();}
            }
        }
    }

    initContextMenu = () => {
        const ctx = this;
        const graph = this.graph;

        const { openTaskInDev } = this.props;
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
        mxPopupMenu.prototype.showMenu = function() {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };

        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function(menu, cell, evt) {

            if (!cell) return

            const currentNode = cell.data || {};

            if (cell.vertex) {
                menu.addItem('保存', null, function() {

                }, null, null, true) // 正常状态
    
                menu.addItem('编辑', null, function() {
                    openTaskInDev(currentNode.id);
                }, null, null, true) // 正常状态
            }

            menu.addItem('删除', null, function() {
                ctx.removeCell([cell]);
            }, null, null, true) // 正常状态

        }
    }

    getPreviewEle() {
        const previewDragTarget = document.createElement('div');
        previewDragTarget.style.width = VertexSize.width + 'px';
        previewDragTarget.style.height = VertexSize.height + 'px';
        previewDragTarget.className = 'preview-drag-vertex';
        previewDragTarget.innerHTML = '新节点';
        return previewDragTarget;
    }

    listenConnection() { // 仅仅限制有效的链接
        const graph = this.graph

        graph.isValidConnection = (source, target) => {

            // 限制，只能vertex可连接
            if (!source.vertex || !target.vertex) return false;

            // 限制连接线条数
            const edges = graph.getEdgesBetween(source, target);
            if (edges.length > 1) return false;

            // 限制循环依赖
            let isLoop = false;
            graph.traverse(target, false, function(vertex, edge) {
                if (source.id === vertex.id) {
                    isLoop = true;
                    return false;
                }
            });
            if (isLoop) return false;

            return true;
        }

    }

    initDraggableToolBar() {

        const ctx = this;
        const graph = this.graph;
        const { taskTypes } = this.props;
        const previewDragTarget = this.getPreviewEle();

        for (let i = 0; i < taskTypes.length; i++ ) {
            const type = taskTypes[i];
            const dragTarget = document.getElementById(`${WIDGETS_PREFIX}${type.key}`);
            if (dragTarget) {
                previewDragTarget.setAttribute('data-item', type.key)
                const draggabledEle = mxUtils.makeDraggable(
                    dragTarget,
                    this.getUnderMouseGraph,
                    this.insertItemVertex,
                    previewDragTarget,
                    null,
                    null,
                    graph.autoscroll,
                    true,
                );

                draggabledEle.createPreviewElement = function(graph, event) {
                    ctx._currentSourceType = type;
                    return previewDragTarget;
                }

                draggabledEle.isGuidesEnabled = () => {
                    return graph.graphHandler.guidesEnabled;
                };

                draggabledEle.createDragElement = mxDragSource.prototype.createDragElement;
            }
        }
    }

    updateGraphData = () => {
        const { 
            updateTaskField, 
         } = this.props;

        const workflow = this.getGraphData();
        updateTaskField({ workflow, });
    }

    initGraphEvent = () => {

        const graph = this.graph;
        let selectedCell = null;
        const { openTaskInDev, } = this.props;

        graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {
                const data = cell.data;
                openTaskInDev(data.id);
            }
        })

        graph.addListener(mxEvent.CLICK, function(sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {

                graph.clearSelection();
                const cellState = graph.view.getState(cell);
                const style = {}
                style[mxConstants.STYLE_FILLCOLOR] = '#90D5FF';
                applyCellStyle(cellState, style);
                
                const edges = graph.getOutgoingEdges(cell);
                graph.setCellStyle(`strokeColor=#90D5FF;strokeWidth=2;`, edges);
                selectedCell = cell;
            }
        })

        graph.clearSelection = function(evt) {
            if (selectedCell) {
                const cellState = graph.view.getState(selectedCell);
                const style = {}
                style[mxConstants.STYLE_FILLCOLOR] = '#E6F7FF';
                applyCellStyle(cellState, style);

                const edges = graph.getOutgoingEdges(selectedCell);
                graph.setCellStyle(`strokeColor=#9EABB2;strokeWidth=1;`, edges);

                selectedCell = null;
            }
        }

        // graph.addListener(mxEvent.CELLS_ADDED, addCell)
        graph.addListener(mxEvent.CELLS_MOVED, this.updateGraphData)
        graph.addListener(mxEvent.CELLS_REMOVED, this.updateGraphData)
        graph.addListener(mxEvent.CELL_CONNECTED, this.updateGraphData)
    }

    getGraphData = () => {
        const rootCell = this.graph.getDefaultParent();
        const cells =  this.graph.getChildCells(rootCell);
        const cellData = [];
        const getCellData = (cell) => {
            return cell && {
                vertex: cell.vertex,
                edge: cell.edge,
                data: cell.data,
                x: cell.geometry.x,
                y: cell.geometry.y,
                value: cell.value,
                id: cell.id,
            }
        }
        for (let i = 0; i < cells.length; i++) {
            const cell = cells[i];
            const cellItem = getCellData(cell);
            if (cell.edge) {
                cellItem.source = getCellData(cell.source);
                cellItem.target = getCellData(cell.target);
            }
            cellData.push(cellItem);
        }
        return cellData;
    }

    renderData = (data) => {
        const graph = this.graph;
        const rootCell = this.graph.getDefaultParent();
        const cellMap = {};
        const cellStyle = this.getStyles();

        if (data) {
            for (let i = 0; i < data.length; i++) {
                const item = data[i];
                if (item.vertex) {
                    const cell = graph.insertVertex(
                        rootCell, 
                        item.id, 
                        item.value, 
                        item.x, item.y,
                        VertexSize.width, VertexSize.height, 
                        cellStyle,
                    )
                    cell.data = item.data;
                    cellMap[item.id] = cell;
                } else if (item.edge) {
                    const source = cellMap[item.source.id];
                    const target = cellMap[item.target.id];
                    graph.insertEdge(rootCell, item.id, '', source, target)
                }
            }

            graph.center(true, true, 0.5, 0.4);
        }
    }

    renderToolBar = () => {
        const { taskTypes } = this.props;
        const widgets = taskTypes.map(item =>
            <Button
                id={`${WIDGETS_PREFIX}${item.key}`}
                className="widgets-items"
                key={item.key}
                value={item.key}>{item.value}
            </Button>
        )

        return (
            <div className="graph-widgets bd">
                <header className="widgets-header bd-bottom">
                    节点组件
                </header>
                <div className="widgets-content">
                    { widgets }
                </div>
            </div>
        )
    }

    /* eslint-enable */
    render() {

        return (
            <KeyEventListener 
                onKeyDown={this.onkeyDown} 
            >
                <div className="graph-editor" 
                    style={{ 
                        position: 'relative',
                    }}
                >
                    { this.renderToolBar() }
                    <div className="editor pointer graph-bg" ref={(e) => { this.Container = e }} />
                    <Spin
                        tip="Loading..."
                        size="large"
                        spinning={this.state.loading === 'loading'}
                    >
                        <div className="absolute-middle" style={{ width: '100%', height: '100%' }}/>
                    </Spin>
                    <div className="graph-toolbar">
                        <Tooltip placement="bottom" title="布局">
                            <MyIcon type="flowchart" onClick={this.layout}/>
                        </Tooltip>
                        {/* <Tooltip placement="bottom" title="撤销">
                            <Icon type="rollback" onClick={this.undo}/>
                        </Tooltip> */}
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
                </div>
            </KeyEventListener>
        )
    }

    hideMenu = () => {
        document.addEventListener('click', (e) => {
            const graph = this.graph
            if (graph.popupMenuHandler.isMenuShowing()) {
                graph.popupMenuHandler.hideMenu()
            }
        })
    }

    refresh = () => {
        this.componentDidMount()
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

    layout = () => {
        this.executeLayout(null, () => {
            this.graph.center(true, true, 0.5, 0.4);
        });
    }

    undo = () => { // 撤销上一步
        this.undoMana.undo()
    }

    getDefaultVertexStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = '#90D5FF';
        style[mxConstants.STYLE_FILLCOLOR] = '#E6F7FF';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333';
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

export default WorkflowEditor;