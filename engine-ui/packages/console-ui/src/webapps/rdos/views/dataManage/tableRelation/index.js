import React from 'react';

import {
    Button, Tooltip, Spin, Icon,
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import MyIcon from '../../../components/icon'
import RelationDetail from './relationDetail';

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
    mxCircleLayout,
    mxCompactTreeLayout,
    mxMorphing,
    mxUtils,
    mxXmlCanvas2D,
    mxImageExport,
    mxXmlRequest,
} = Mx

const VertexSize = { // vertex大小
    width: 100,
    height: 30,
}

const getVertexNode = (obj) => {
    return {
        id: obj.tableId,
        name: obj.tableName
    }
}

export default class TableRelation extends React.Component {

    state = {
        selectedData: '', // 选中的数据
        data: {}, // 数据
        tableInfo: {},
        relationTasks: {}, // 关联任务
        loading: 'success',
        visible: false,
    }

    componentDidMount() {
        this._vertexCells = [] // 用于缓存创建的顶点节点
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        const editor = this.Container
        const tableData = this.props.tableData
        this.initEditor()
        this.loadEditor(editor)
        this.listenOnClick();
        if (tableData) {
            const params = {
                tableId: tableData.tableId,
            }
            this.loadTableTree(params)
            this.loadVertexData(params)
        }
    }

    loadVertexData = (params) => {
        this.loadTableInfo(params)
        this.loadRelTableTasks(params)
    }

    loadTableInfo = (params) => {
        Api.getRelTableInfo(params).then(res => {
            if (res.code === 1) {
                this.setState({ tableInfo: res.data })
            }
        })
    }

    loadRelTableTasks = (params) => {
        params.pageSize = 5;
        params.pageIndex = params.pageIndex || 1;
        Api.getRelTableTasks(params).then(res => {
            if (res.code === 1) {
                this.setState({ relationTasks: res.data })
            }
        })
    }

    loadTableTree = (params) => {
        this.showLoading()
        Api.getTableRelTree(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                this.setState({ selectedData: getVertexNode(data), data })
                this.doInsertVertex(data)
            }
            this.hideLoading();
        })
    }

    loadTableChildren = (params) => {
        this.showLoading()
        const graph = this.graph;
        Api.getChildRelTables(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                this.setState({ selectedData: getVertexNode(data), data })
                this.loopTree(graph, data)
            }
            this.hideLoading();
        })
    }

    loadTableParent = (params) => {
        this.showLoading()
        const graph = this.graph;
        Api.getParentRelTable(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                this.setState({ data, selectedData: getVertexNode(data) })
                this.loopTree(graph, data)
            }
            this.hideLoading();
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
            const tableInfo = doc.createElement('table')
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

            const parentNodes = treeNodeData.parentTables;
            const childNodes = treeNodeData.childTables;

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

                    if (parentNodes[i].parentTables) {
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
                    if (childNodes[i].childTables) {
                        this.loopTree(graph, childNodes[i])
                    }
                }
            }
        }
    }

    doInsertVertex = (data) => {
        const graph = this.graph;
        let layout = this.layout;
        const cx = (graph.container.clientWidth - VertexSize.width) / 3
        const cy = 10

        const model = graph.getModel();
        const parent = graph.getDefaultParent();

        if (!layout) {
            layout = new mxCircleLayout(graph)
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
        // // Disable context menu
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
        graph.isCellsMovable = function (cell) {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge)
        }
        // 禁止cell编辑
        graph.isCellEditable = function() {
            return false;
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

        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';

        // enables rubberband
        new mxRubberband(graph)
        // 启用菜单
        this.initContextMenu(graph)
        this.hideMenu();
    }

    getStyles = (data) => {
        return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;'
    }

    formatTooltip = (cell) => {
        const data = cell.getAttribute('data');
        const obj = data ? JSON.parse(data) : '';
        return obj ? obj.name : ''
    }

    corvertValueToString = (cell) => {
        if (mxUtils.isNode(cell.value)) {
            if (cell.value.nodeName.toLowerCase() == 'table') {
                const data = cell.getAttribute('data');
                const obj = data ? JSON.parse(data) : '';
                if (obj) {
                    return `<div class="table-vertex"><span class="table-vertex-content"><img src="/public/rdos/img/table.svg" /> <span class="table-vertex-title">${obj.name || ''}</span></span>
                    </div>`
                }
            }
        }
        return '';
    }

    showLoading = () => {
        this.setState({ loading: 'loading' })
    }

    hideLoading = () => {
        this.setState({ loading: 'success' })
    }

    initContextMenu = (graph) => {
        const ctx = this
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

            const table = JSON.parse(cell.getAttribute('data'))
            const params = { tableId: table.id, }
            menu.addItem('展开上游（1层）', null, function () {
                ctx.loadTableParent(params)
                ctx.loadVertexData(params)
            })
            
            menu.addItem('展开下游（1层）', null, function () {
                ctx.loadTableChildren(params)
                ctx.loadVertexData(params)
            })
        }
    }

    listenOnClick() {
        const ctx = this
        this.graph.addListener(mxEvent.CLICK, function (sender, evt) {
            const cell = evt.getProperty('cell')
            const target = evt.getProperty('event')
            const CLICK_LEFT = 1;
            if (target.which === CLICK_LEFT && cell && cell.vertex) {
                let data = cell.getAttribute('data')
                data = data ? JSON.parse(data) : ''
                if (data.id !== ctx.state.selectedData.id) {
                    ctx.setState({ selectedData: data })
                    ctx.loadVertexData({
                        tableId: data.id
                    })
                }
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

    render() {
        const { tableInfo, relationTasks } = this.state
        return (
            <div className="graph-editor" 
                style = {{ position: 'relative', height: '650px' }}
            >
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={this.state.loading === 'loading'}
                >
                    <div className="absolute-middle graph-bg">血缘关系</div>
                    <div className="editor pointer" ref={(e) => { this.Container = e }} />
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="刷新">
                        <Icon type="reload" onClick={this.refresh}/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="放大">
                        <MyIcon onClick={this.zoomIn} type="zoom-in" />
                    </Tooltip>
                    <Tooltip placement="bottom" title="缩小">
                        <MyIcon onClick={this.zoomOut} type="zoom-out" />
                    </Tooltip>
                </div>
                <RelationDetail 
                    data={tableInfo}
                    relationTasks={relationTasks}
                    loadRelTasks={this.loadRelTableTasks}
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
        style[mxConstants.STYLE_FILLCOLOR] = '#E6F7FF';
        // style[mxConstants.STYLE_GRADIENTCOLOR] = '#e9e9e9';
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
