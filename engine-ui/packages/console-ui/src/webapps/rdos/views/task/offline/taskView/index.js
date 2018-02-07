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
    mxXmlCanvas2D,
    mxImageExport,
    mxXmlRequest,
} = Mx

const VertexSize = { // vertex大小
    width: 100,
    height: 30,
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

    componentDidMount() {
        this._vertexCells = [] // 用于缓存创建的顶点节点
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
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
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);
        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip
        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);
        // enables rubberband
        new mxRubberband(graph)
    }

    getStyles = (type) => {
        return 'whiteSpace=wrap;fillColor=#e3f7f3;strokeColor=#18a689;'
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
                let iconUrl = ''
                switch (task.taskType) {
                    case 1:
                        iconUrl = '/img/mr.svg'; break;
                    case 2:
                        iconUrl = '/img/datasync.svg'; break;
                    case 0:
                    default:
                        iconUrl = '/img/sql.svg'; break;
                }
                if (task) {
                    return `<div class="task-vertex"><span class="task-vertex-content"><img src="${iconUrl}" /> <span class="vertex-title">${task.name || ''}</span></span>
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

            const style = this.getStyles(data.status)

            const exist = this._vertexCells.find((cell) => {
                const dataStr = cell.getAttribute('data')
                if (!dataStr) return null
                const itemData = JSON.parse(dataStr)
                return itemData.id === data.id
            })

            let newVertex = '';

            if (exist) {
                this.insertEdge(graph, type, parent, exist)
                // graph.insertEdge(parent, null, '', parent, exist)
                // current = exist.node
            } else {

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

            if (data.taskVOS) {
                const children = data.taskVOS
                for (let i = 0; i < children.length; i++) {
                    this.insertVertex(graph, children[i], newVertex, type)
                }
            }
        }
    }

    doInsertVertex = (data, type) => {
        const graph = this.graph
        const model = graph.getModel()
        const layout = new mxHierarchicalLayout(graph)
        model.clear()
        const cx = (graph.container.clientWidth - VertexSize.width) / 3;
        const cy = 150;
        model.beginUpdate()
        const parent = graph.getDefaultParent()
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
        Api.getTaskChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ selectedTask: data, data, loading: 'success', sort: 'children' })
                ctx.doInsertVertex(data, 'children')
            }
        })
    }

    loadTaskParent = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        Api.getTaskParents(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ data, selectedTask: data, loading: 'success', sort: 'parent' })
                ctx.doInsertVertex(data, 'parent')
            }
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
        }
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

    showImage = () => {
        const graph = this.graph

        // const xmlDoc = mxUtils.createXmlDocument();
        // const root = xmlDoc.createElement('output');
        // xmlDoc.appendChild(root);
        this.setState({ visible: true })

        const bounds = graph.getGraphBounds();
        const w = Math.ceil(bounds.x + bounds.width);
        const h = Math.ceil(bounds.y + bounds.height);
 
        const myCanvas = this.MyCanvas
        const ctx = myCanvas.getContext('2d');
        const svgData = this.Container.innerHTML
        const DOMURL = window.URL || window.webkitURL || window;

        myCanvas.width = w
        myCanvas.height = h

        const img = new Image();
        const svg = new Blob([svgData], {type: 'image/svg+xml'});
        const url = DOMURL.createObjectURL(svg);

        img.onload = function() {
            ctx.drawImage(img, 0, 0);
            DOMURL.revokeObjectURL(url);
        }
        img.src = url;
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
            <div className="graph-editor" style={{position: 'relative'}}>
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
                        <MyIcon onClick={this.refresh} type="loop2"/>
                    </Tooltip>
                </div>
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
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.ElbowConnector;
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
