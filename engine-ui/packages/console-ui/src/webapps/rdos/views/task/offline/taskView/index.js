import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link, hashHistory } from 'react-router'

import {
    Button, Tooltip, Spin,
    Modal, notification, Icon,
} from 'antd'

import utils from 'utils'

import Api from '../../../../api'
import MyIcon from '../../../../components/icon'
import { taskTypeText } from '../../../../components/display'

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
    mxMorphing,
    mxUtils,
    mxXmlCanvas2D,
    mxImageExport,
    mxXmlRequest,
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
        visible: false,
    }

    componentDidMount() {
        this._vertexCells = [] // 用于缓存创建的顶点节点
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.graph = "";
        this.layout = "";
        
        const editor = this.Container
        const currentTask = this.props.tabData
        this.initEditor()
        this.loadEditor(editor)
        this.listenDoubleClick()
        this.hideMenu()
        this.loadTaskChidren({
            taskId: currentTask.id,
            level: 6,
        })
    }

    loadTaskChidren = (params) => {
        const ctx = this
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

    insertVertex = (parent, data) => {

        const graph = this.graph;
        const rootCell = graph.getDefaultParent()

        const exist = this._vertexCells.find((cell) => {
            const dataStr = cell.getAttribute('data')
            if (!dataStr) return null
            const itemData = JSON.parse(dataStr)
            return itemData.id === data.id
        })

        if (exist) {
            const edges = graph.getEdgesBetween(parent, exist)
            if (edges.length === 0) {
                graph.insertEdge(parent, null, '', parent, exist)
            }
            return exist;
        } else {// 如果该节点为新节点， 则从新生成并创建
            const style = this.getStyles(data)

            // 创建节点
            const doc = mxUtils.createXmlDocument()
            const tableInfo = doc.createElement('Task')
            tableInfo.setAttribute('id', data.id)
            tableInfo.setAttribute('data', JSON.stringify(data))

            let newVertex = '';
            this.executeLayout(() => {
                newVertex = graph.insertVertex(rootCell, null, tableInfo, 0, 0,
                    VertexSize.width, VertexSize.height, style
                )
                graph.view.refresh(newVertex)
                graph.insertEdge(parent, null, '', parent, newVertex)
            }, () => {
                graph.scrollCellToVisible(newVertex);
            })
            // 缓存节点
            this._vertexCells.push(newVertex)

            return newVertex;
        }
    }

    loopTree = (graph, treeNodeData) => {

        if (treeNodeData) {

            const rootCell = graph.getDefaultParent()

            const parentNodes = treeNodeData.taskVOS; // 父节点
            const childNodes = treeNodeData.subTaskVOS; // 子节点

            const currentNodeData = getVertexNode(treeNodeData)
            const currentNode = this.insertVertex(rootCell, currentNodeData)

            // 处理依赖节点
            if (parentNodes && parentNodes.length > 0) {
                for (let i = 0; i < parentNodes.length; i++) {
                    const nodeData = getVertexNode(parentNodes[i])
                    // 插入新节点
                    const newNode = this.insertVertex(rootCell, nodeData)
                    // 创建连接线
                    const newEdge = this.insertVertex(newNode, currentNodeData)

                    if (parentNodes[i].taskVOS) {
                        this.loopTree(graph, parentNodes[i])
                    }
                }
            }

            // 处理被依赖节点
            if (childNodes && childNodes.length > 0) {
                for (let i = 0; i < childNodes.length; i++) {
                    const nodeData = getVertexNode(childNodes[i])
                    // 插入新节点
                    const newNode = this.insertVertex(rootCell, nodeData)
                    const newEdge = this.insertVertex(currentNode, nodeData)
                    if (childNodes[i].subTaskVOS) {
                        this.loopTree(graph, childNodes[i])
                    }
                }
            }
        }
    }

    doInsertVertex = (data) => {
        const graph = this.graph;
        let layout = this.layout;
        const cx = (graph.container.clientWidth - VertexSize.width) / 2
        const cy = 200

        const model = graph.getModel();
        const parent = graph.getDefaultParent();

        if (!layout) {
            layout = new mxCompactTreeLayout(graph)
            layout.horizontal = false;
            layout.useBoundingBox = false;
            layout.edgeRouting = false;
            // layout = new mxCompactTreeLayout(graph)
            this.layout = layout
            this.executeLayout = function(change, post) {

                model.beginUpdate();

                try {
                    if (change != null) {
                        change();
                    }
                    layout.execute(parent);
                } catch (e) {
                    throw e;
                } finally {
                    var morph = new mxMorphing(graph);
                    morph.addListener(mxEvent.DONE, mxUtils.bind(this, function() {
                        graph.getModel().endUpdate();
                        if (post != null) { post();}
                    }));
                    morph.startAnimation();
                }
            }
        }

        this.loopTree(graph, data);
        this.executeLayout();
        graph.view.setTranslate(cx, cy);
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
    }

    getStyles = (type) => {
        return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;'
    }

    formatTooltip = (cell) => {
        const data = cell.getAttribute('data');
        const task = data ? JSON.parse(data) : ''; 
        return task ? task.name : ''
    }

    corvertValueToString = (cell) => {
        if (mxUtils.isNode(cell.value)) {
            if (cell.value.nodeName.toLowerCase() == 'task') {
                const data = cell.getAttribute('data');
                const task = data ? JSON.parse(data) : '';
                const taskType = taskTypeText(task.taskType);
                if (task) {
                    return `<div class="vertex"><span class="vertex-title">${task.name || ''}</span>
                    <span style="font-size:10px; color: #666666;">${taskType}</span>
                    </div>`
                }
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
                let data = cell.getAttribute('data')
                data = data ? JSON.parse(data) : ''
                ctx.setState({ selectedTask: data })
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
        const task = this.state.selectedTask
        const project = this.props.project
        return (
            <div className="graph-editor" 
                style={{  position: 'relative', }}
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
