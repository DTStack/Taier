import React from 'react';

import {
    Button, Tooltip, Spin, Icon,
} from 'antd'

import utils from 'utils'
import GoBack from 'main/components/go-back'

import Api from '../../../api/dataManage'
import MyIcon from '../../../components/icon'

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
    mxHierarchicalLayout,
    mxMorphing,
    mxGraphView,
    mxCircleLayout,
    mxGraphHandler,
    mxStackLayout,
    mxLayoutManager,
    mxUtils,
    mxXmlCanvas2D,
    mxImageExport,
    mxXmlRequest,
} = Mx

const VertexSize = { // vertex大小
    width: 120,
    height: 35,
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
        loading: 'success',
        visible: false,
    }

    componentDidMount() {
        this._vertexCells = [] // 用于缓存创建的顶点节点
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.layout = "";
        this.graph = "";
        const editor = this.Container
        const tableData = this.props.tableData
        this.initEditor()
        this.loadEditor(editor)
        this.listenOnClick();
        if (tableData) {
            const params = {
                tableName: tableData.tableName,
                belongProjectId: tableData.belongProjectId,
                dataSourceId: tableData.dataSourceId,
            }
            this.loadTableColumn(params)
        }
    }

    loadTableColumn = (params) => {
        const ctx = this;
        Api.getRelTableColumns(params).then(res => {
            if (res.code === 1) {
                ctx.setState({ tableInfo: res.data })
                ctx.doInsertVertex(res.data)
            }
        })
    }

    loadRelationColumns = (params) => {
        const ctx = this;
        ctx.showLoading()
        Api.getRelTableUpDownColumns(params).then(res => {
            if (res.code === 1) {
              
                ctx.insertRelationColumn(res.data);
            }
            ctx.hideLoading();
        })
    }

    insertTableColumnVertext = (data) => {
        const graph = this.graph;
        const parent = graph.getDefaultParent()

        const style = this.getStyles(data)
        const vertexStyle = this.getDefaultVertexStyle()

        const doc = mxUtils.createXmlDocument()
        const tableData = doc.createElement('table')
        tableData.setAttribute('data',  JSON.stringify(data))

        const height = (data.columns.length + 2) * VertexSize.height;
        const newVertext = graph.insertVertex(parent, null, tableData, 0, 0, VertexSize.width, height);;
    

        return newVertext;
    }

    insertRelationColumn = (data) => {
        const graph = this.graph;
        const model = graph.getModel();

        const rootCell = graph.getDefaultParent();

        const parents = data.parentTables;
        const children = data.childTables;
        const currentCell = this.rootCell; //this.insertTableColumnVertext(this.state.tableInfo);

        let newVertex = '';
        for (let i = 0; i < parents.length; i++) {
            const node = parents[i];
            node.isParent = true;
            newVertex = this.insertTableColumnVertext(node);
            graph.insertEdge(rootCell, null, '', newVertex, currentCell);
        }
        for (let i = 0; i < children.length; i++) {
            const node = children[i];
            node.isChild = true;
            const newVertex = this.insertTableColumnVertext(node);;
            graph.insertEdge(rootCell, null, '', currentCell, newVertex);
        }

        this.executeLayout(() => {
            graph.view.refresh(newVertex);
        }, () => {
            graph.scrollCellToVisible(newVertex);
        })
    }

    doInsertVertex = (data) => {
        const graph = this.graph;
        const cx = (graph.container.clientWidth - VertexSize.width) / 2;
        const cy = 100;

        const model = graph.getModel();
        const parent = graph.getDefaultParent();

        const layout = new mxHierarchicalLayout(graph); // new mxCircleLayout(graph, true); 
        layout.orientation = 'west';
        // layout.interHierarchySpacing = 20;
        this.layout = layout;

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
                    if (post != null) { post();}
                    model.endUpdate();
                }));
                morph.startAnimation();
            }
        }

        let newVertxt = '';
        this.executeLayout(() => {
            newVertxt = this.insertTableColumnVertext(data);
            graph.view.refresh(this.rootCell);
        }, () => {
            graph.scrollCellToVisible(this.rootCell);
        })
        this.rootCell = newVertxt
        graph.view.setTranslate(cx, cy);
    }

    loadEditor = (container) => {

        // Disables the context menu
        mxEvent.disableContextMenu(container);

        mxGraphView.prototype.optimizeVmlReflows = false;
        mxGraphHandler.prototype.htmlPreview = true;

        const graph = new mxGraph(container);

        // Disables global features
        graph.setCellsDisconnectable(false);
        graph.setAllowDanglingEdges(false);
        graph.setCellsEditable(false);
        graph.setConnectable(true);
        graph.setPanning(true);
        graph.centerZoom = false;
        graph.keepEdgesInBackground = true;
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.setTooltips(true)
        graph.view.setScale(1)
        // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)

        // 禁止Edge对象移动
        graph.isCellsMovable = function () {
            if (this.graph) {
                var cell = this.graph.getSelectionCell()
            }
            return true;
        }
        // 禁止cell编辑
        graph.isCellEditable = function () {
            return false;
        }

        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);

        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_STROKECOLOR] = '#DDDDDD';
        style[mxConstants.STYLE_FILLCOLOR] = '#FFFFFF';
        style[mxConstants.STYLE_FOLDABLE] = false;
        graph.getStylesheet().putCellStyle('column', style);

        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // Anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';

        // 转换value显示的内容
        // graph.convertValueToString = this.corvertValueToString
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip

        // enables rubberband
        new mxRubberband(graph);

        // 定义lable渲染
        graph.getLabel = function (cell) {
            if (this.getModel().isVertex(cell)) {
                const data = cell.getAttribute('data');
                const table = data ? JSON.parse(data) : {};
                const tableTitle = table.isParent ? '上游' : table.isChild ? '下游' : '本表';
                let lis = ''
                for (let i = 0; i < table.columns.length; i++) {
                    const col = table.columns[i]
                    lis += `<li key="${col}" title="${col}" data-col="${col}" class="tcolumn">${col}</li>`
                }
                return `<ul class="t-vertext"><li class="tname bd-top">${tableTitle}</li><li class="tname">${table.tableName}</li>${lis}</ul>`;
            } else {
                return '';
            }
        };

        this.graph = graph;
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
                    return obj.name || ''
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

    listenOnClick() {
        const ctx = this;
        this.graph.addListener(mxEvent.CLICK, function (sender, evt) {
            const cell = evt.getProperty('cell')
            const cellTarget = evt.getProperty('event')
            const CLICK_LEFT = 1;
            if (cellTarget.which === CLICK_LEFT && cell && cell.vertex) {
                let data = cell.getAttribute('data')
                const obj = data ? JSON.parse(data) : '';
                
                const colName = cellTarget.target.getAttribute('data-col');

                if (ctx.state.tableInfo.tableName === obj.tableName && colName) {
                    const params = {
                        tableName: obj.tableName,
                        belongProjectId: obj.belongProjectId,
                        dataSourceId: obj.dataSourceId,
                        column: colName,
                    }
                    ctx.loadRelationColumns(params)
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

    render() {
        const { tableInfo, relationTasks } = this.state
        return (
            <div className="graph-editor col-relation" style={{position: 'relative'}}>
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={this.state.loading === 'loading'}
                >
                    <div className="absolute-middle graph-bg">字段血缘信息</div>
                    <div className="editor pointer" ref={(e) => { this.Container = e }} />
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="返回">
                        <Icon
                            type="left-circle"
                            onClick={this.props.onShowTable}
                        />
                    </Tooltip>
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
            </div>
        )
    }

    getDefaultVertexStyle() {
        let style = [];
        // style[mxConstants.STYLE_SHAPE] = 'swimlane';
        // style[mxConstants.STYLE_STROKECOLOR] = '#90D5FF';
        // style[mxConstants.STYLE_ROUNDED] = true; // 设置radius
        style[mxConstants.STYLE_FILLCOLOR] = '#E6F7FF';
        // style[mxConstants.STYLE_GRADIENTCOLOR] = '#e9e9e9';
        // style[mxConstants.STYLE_FONTCOLOR] = '#333333';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        // style[mxConstants.STYLE_STARTSIZE] = 30;

        return style;
    }

    getDefaultEdgeStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#9EABB2';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        // style[mxConstants.STYLE_EDGE] = mxEdgeStyle.SideToSide;
        // style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
        // style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
        // style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
        // style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
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
