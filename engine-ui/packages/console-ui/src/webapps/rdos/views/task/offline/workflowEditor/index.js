import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Tooltip, Spin, Icon, Button } from 'antd'

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
    mxGeometry,
    mxUtils,
    mxEvent,
    mxDragSource,
    mxPolyline,
    mxConstants,
    mxEdgeStyle,
    mxPerimeter,
    mxRubberband,
    mxUndoManager,
    mxGraphHandler,
    mxCompactTreeLayout,
    mxConnectionConstraint,
} = Mx;

const VertexSize = { // vertex大小
    width: 150,
    height: 36,
}

const WIDGETS_PREFIX = 'JS_WIDGETS_'; // Prefix for widgets

@connect(state => {
    const { offlineTask } = state;
    return {
        taskTypes: offlineTask.comm.taskTypes,
    }
}, workbenchActions )
class WorkflowEditor extends Component {

    state = {}

    componentDidMount() {
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.graph = "";
        const editor = this.Container;
        this.initEditor()
        this.loadEditor(editor)
        this.hideMenu()
    }

    shouldComponentUpdate (nextProps, nextState) {
        return false;
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
        graph.keepEdgesInBackground = true;
        graph.cellsResizable = false;
        // 启用辅助线
        mxGraphHandler.prototype.guidesEnabled = true;
        mxGraphHandler.prototype.previewColor = '#2491F7';

        graph.setConnectable(true)
        graph.setTooltips(true)
        graph.view.setScale(1)
        // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)

        // 禁止连接
        // graph.setConnectable(false)

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

        // enables rubberband
        new mxRubberband(graph)

        // Initial draggable elements
        this.listenConnection()
        this.initDraggableToolBar();
        this.initGraphLayout();
        this.initUndoManager();
    }

    getStyles = (type) => {
        return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;'
    }

    formatTooltip = (cell) => {
        const task = cell.data || '';
        return task ? task.name : ''
    }

    corvertValueToString = (cell) => {
        console.log('corvertValueToString:', cell)
        if (cell && cell.vertex) {
            const task = cell.data;
            const taskType = taskTypeText(task.taskType);
            if (task) {
                return `<div class="vertex"><span class="vertex-title">${task.name || ''}</span>
                <span style="font-size:10px; color: #666666;">${taskType}</span>
                </div>`
            }
        }
        return '';
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
        this.graph.addListener(mxEvent.onClick, function(sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell) {
                ctx.setState({ selectedData: cell.data || '' })
            }
        })
    }

    onkeyDown = (evt) => {
        const keyCode = evt.keyCode;
        const ctx = this;
        switch(keyCode) {
            case KEY_CODE.BACKUP: {
                ctx.removeCell()
                break;
            }
            default:
        }
    }

    insertItemVertex = (graph, evt, target, x, y) => {

        console.log('insertItemVertex:', this._currentSourceType)
        const { toggleCreateTask, data } = this.props;

        const taskType = this._currentSourceType.key; 
        const newCell = new mxCell(
            '新节点', 
            new mxGeometry(0, 0, VertexSize.width, VertexSize.height)
        )
        newCell.vertex = true;
        newCell.data = {
            taskType: taskType,
            name: '新节点'
        }

        const cells = graph.importCells([newCell], x, y, target);
        if (cells != null && cells.length > 0) {
            this._currentCell = cells[0];
            const createOrigin = {
                name: 'graph',
                data: data,
                taskType: taskType,
            }
            toggleCreateTask(createOrigin);
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
        // 获取选中的Cell
        const cell = cells || this.graph.getSelectionCells() // getSelectionCell
        if (cell && cell.length > 0) {
            this.graph.removeCells(cell)
        }
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
            this.graph.center();
        });
    }

    undo = () => { // 撤销上一步
        this.undoMana.undo()
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
                layout.levelDistance = 30;
                layout.nodeDistance = 10;

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
        graph.connectionHandler.addListener(mxEvent.CONNECT, function(sender, evt) {
            var edge = evt.getProperty('cell');
            var source = graph.getModel().getTerminal(edge, true);
            var target = graph.getModel().getTerminal(edge, false);

            const edges = graph.getEdgesBetween(source, target);
            if (edges.length > 1) {
                graph.removeCells([edge])
            }
        })
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

                // Double Click
            }
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
                        <Tooltip placement="bottom" title="撤销">
                            <Icon type="rollback" onClick={this.undo}/>
                        </Tooltip>
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