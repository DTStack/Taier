import React from 'react';

import {
    Button, Tooltip, Spin, Icon
} from 'antd'

import Mx from 'widgets/mxGraph';

import Api from '../../../api/dataManage'
import MyIcon from '../../../components/icon'

import { isEqTable } from './tableRelation'

const {
    mxGraph,
    mxEvent,
    mxRubberband,
    mxConstants,
    mxEdgeStyle,
    mxHierarchicalLayout,
    mxUtils
} = Mx

const VertexSize = { // vertex大小
    width: 120,
    height: 35
}

export default class TableRelation extends React.Component {
    state = {
        selectedData: '', // 选中的数据
        data: {}, // 数据
        tableInfo: {},
        loading: 'success',
        columnName: '',
        visible: false
    }

    componentDidMount () {
        this._vertexCells = [] // 用于缓存创建的顶点节点
        this.Container.innerHTML = ''; // 清理容器内的Dom元素
        this.layout = '';
        this.graph = '';
        const editor = this.Container
        const tableData = this.props.tableData
        this.loadEditor(editor)
        this.listenOnClick();
        if (tableData) {
            const params = {
                tableName: tableData.tableName,
                belongProjectId: tableData.belongProjectId,
                dataSourceId: tableData.dataSourceId
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

    getXmlNode = (data) => {
        const doc = mxUtils.createXmlDocument()
        const xmlNode = doc.createElement('MyTable')
        xmlNode.setAttribute('data', JSON.stringify(data))
        return xmlNode;
    }

    insertRootTree = (data) => {
        const graph = this.graph;
        const tableData = this.getXmlNode(data);
        const rootCell = graph.getDefaultParent();

        const height = ((data.columns ? data.columns.length : 0) + 2) * VertexSize.height;
        const rootVertex = graph.insertVertex(
            rootCell,
            null,
            tableData,
            this.cx,
            this.cy,
            VertexSize.width,
            height
        );
        return rootVertex;
    }

    insertTableColumnVertext = (parent, data) => {
        const graph = this.graph;
        const tableData = this.getXmlNode(data);
        const rootCell = graph.getDefaultParent();

        const height = ((data.columns ? data.columns.length : 0) + 2) * VertexSize.height;

        const newVertex = graph.insertVertex(
            rootCell,
            null,
            tableData,
            1,
            1,
            VertexSize.width,
            height
        );
        graph.view.refresh(newVertex);
        if (data.isParent) {
            graph.insertEdge(rootCell, null, '', newVertex, parent);
        } else if (data.isChild) {
            graph.insertEdge(rootCell, null, '', parent, newVertex);
        }

        return newVertex;
    }

    insertRelationColumn = (data) => {
        const graph = this.graph;
        const originTable = this.state.tableInfo;
        const columnName = this.state.columnName;

        const parents = data.parentTables;
        const children = data.childTables;

        graph.getModel().clear();
        this.executeLayout(() => {
            originTable.currentColumn = columnName;
            const originCell = this.insertRootTree(originTable);
            if (parents && parents.length > 0) {
                for (let i = 0; i < parents.length; i++) {
                    const node = parents[i];
                    node.isParent = true;
                    this.insertTableColumnVertext(originCell, node);
                }
            }

            if (children && children.length > 0) {
                for (let i = 0; i < children.length; i++) {
                    const node = children[i];
                    node.isChild = true;
                    this.insertTableColumnVertext(originCell, node); ;
                }
            }
        })
        graph.center();
    }

    doInsertVertex = (data) => {
        const graph = this.graph;
        this.cx = (graph.container.clientWidth - VertexSize.width) / 2;
        this.cy = 100;

        const model = graph.getModel();

        this.executeLayout = function (change, post) {
            model.beginUpdate();
            try {
                const layout = new mxHierarchicalLayout(graph, false);// eslint-disable-line
                layout.orientation = 'west';
                layout.disableEdgeStyle = false;

                if (change != null) {
                    change();
                }
                layout.execute(graph.getDefaultParent())
            } catch (e) {
                throw e;
            } finally {
                if (post != null) { post(); }
                model.endUpdate();
            }
        }

        this.executeLayout(() => {
            this.insertRootTree(data);
        })
        graph.view.setTranslate(this.cx, 50);
    }

    loadEditor = (container) => {
        // Disables the context menu
        mxEvent.disableContextMenu(container);

        const graph = new mxGraph(container);// eslint-disable-line
        this.graph = graph;

        // Disables global features
        graph.setConnectable(true);
        graph.setPanning(true);
        graph.centerZoom = false;
        graph.keepEdgesInBackground = true;

        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.setTooltips(false);
        graph.view.setScale(1)
        // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)

        // 禁止Edge对象移动
        graph.isCellsMovable = function () {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge)
        }
        // 禁止cell编辑
        graph.isCellEditable = function () {
            return false;
        }

        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);

        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // Anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';

        // enables rubberband
        new mxRubberband(graph);// eslint-disable-line
        // 重置tooltip
        // 定义lable渲染
        graph.getLabel = function (cell) {
            if (this.getModel().isVertex(cell)) {
                const data = cell.getAttribute('data');
                const table = data ? JSON.parse(data) : { columns: [] };
                const tableTitle = table.isParent ? '上游' : table.isChild ? '下游' : '本表';
                let lis = ''
                for (let i = 0; i < table.columns.length; i++) {
                    const col = table.columns[i]
                    lis += `<li key="${col}" title="${col}" data-col="${col}" class="tcolumn" style="color:${col === table.currentColumn ? '#2491F7' : '#595959'}">${col}</li>`
                }
                return `<ul class="t-vertext"><li key="tableTitle" class="tname bd-top" title="${tableTitle}">${tableTitle}</li><li key="tableName" class="tname" title="${table.tableName}">${table.tableName}</li>${lis}</ul>`;
            } else {
                return '';
            }
        };
    }

    getStyles = (data) => {
        return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;'
    }

    showLoading = () => {
        this.setState({ loading: 'loading' })
    }

    hideLoading = () => {
        this.setState({ loading: 'success' })
    }

    listenOnClick () {
        const ctx = this;

        this.graph.addListener(mxEvent.CLICK, function (sender, evt) {
            const cell = evt.getProperty('cell')
            const cellTarget = evt.getProperty('event')
            const CLICK_LEFT = 1;
            if (cellTarget.which === CLICK_LEFT && cell && cell.vertex) {
                let data = cell.getAttribute('data')
                const obj = data ? JSON.parse(data) : '';
                const colName = cellTarget.target.getAttribute('data-col');
                if (colName && isEqTable(ctx.state.tableInfo, obj)) {
                    const params = {
                        tableName: obj.tableName,
                        belongProjectId: obj.belongProjectId,
                        dataSourceId: obj.dataSourceId,
                        column: colName
                    }
                    ctx.setState({
                        columnName: colName
                    })
                    ctx.loadRelationColumns(params)
                }
            }
        })
    }

    refresh = () => {
        this.componentDidMount()
    }

    graphEnable () {
        const status = this.graph.isEnabled()
        this.graph.setEnabled(!status)
    }

    zoomIn = () => {
        this.graph.zoomIn()
    }

    zoomOut = () => {
        this.graph.zoomOut()
    }

    render () {
        return (
            <div className="graph-editor col-relation" style={{ position: 'relative' }}>
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={this.state.loading === 'loading'}
                >
                    <div className="absolute-middle txt-bg">字段血缘信息</div>
                    <div
                        className="editor pointer" ref={(e) => { this.Container = e }}
                    />
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="刷新">
                        <Icon type="reload" onClick={this.refresh} style={{ color: '#333333' }}/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="放大">
                        <MyIcon onClick={this.zoomIn} type="zoom-in" />
                    </Tooltip>
                    <Tooltip placement="bottom" title="缩小">
                        <MyIcon onClick={this.zoomOut} type="zoom-out" />
                    </Tooltip>
                </div>
                <Button style={{
                    position: 'absolute',
                    top: -30,
                    right: 10
                }}
                onClick={this.props.onShowTable}
                size="small">
                    <Icon type="left" />返回
                </Button>
            </div>
        )
    }

    getDefaultVertexStyle () {
        let style = [];
        style[mxConstants.STYLE_FILLCOLOR] = '#E6F7FF';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        return style;
    }

    getDefaultEdgeStyle () {
        let style = [];
        style[mxConstants.STYLE_STROKECOLOR] = '#9EABB2';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
        style[mxConstants.STYLE_ROUNDED] = true;
        return style;
    }
}
