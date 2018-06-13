
import React from 'react';
import {
    Button, Tooltip, Spin, Icon, Pagination, message,
} from 'antd'

import { cloneDeep } from 'lodash';

import utils from 'utils'

import Api from '../../../api/dataManage'
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
    mxCompactTreeLayout,
    mxLayoutManager,
    mxHierarchicalLayout,
    mxSwimlaneLayout,
    mxMorphing,
    mxUtils,
    mxXmlCanvas2D,
    mxImageExport,
    mxXmlRequest,
} = Mx

const VertexSize = { // vertex大小
    width: 30,
    height: 30,
}

const getVertexNode = (obj) => {
    return obj
}

const testData = require('./treeTest.json');
const testData2 = require('./json2.json');

const getTableReqParams = (tableData) => {
    if (!tableData) return {};
    const params = {
        tableName: tableData.tableName,
        pageIndex: 1,
        pageSize: 6,
        belongProjectId: tableData.belongProjectId,
        dataSourceId: tableData.dataSourceId,
    }
    return params;
}

export const isEqTable = (from, compareTo) => {
    return from.tableName === compareTo.tableName  &&
    from.belongProjectId === compareTo.belongProjectId &&
    from.dataSourceId === compareTo.dataSourceId
}

export default class TableRelation extends React.Component {

    state = {
        selectedData: '', // 选中的数据
        treeData: {}, // 树形数据
        tableInfo: {},
        relationTasks: {}, // 关联任务
        loading: 'success',
        currentPage: 1, 
        currentChild: {},
        currentParent: {},
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
            const params = getTableReqParams(tableData)
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
                const treeData = this.initRootTree(data);
                this.doInsertVertex(treeData)
            }
            this.hideLoading();
        })
    }

    loadChildrenTable = (params) => {
        this.showLoading()
        Api.getChildRelTables(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                if (data.childResult.data && data.childResult.data.length > 0) {
                    this.setState({ currentChild: data })
                    const treeNodes = this.preHandTreeNodes(data, 'child');
                    this.renderTree(treeNodes)
                } else {
                    message.warning('当前表没有下游表！')
                }
            }
            this.hideLoading();
        })
    }

    loadParentTable = (params) => {
        this.showLoading()
        Api.getParentRelTable(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                if (data.parentResult.data && data.parentResult.data.length > 0) {
                    this.setState({ currentParent: data })
                    const treeNodes = this.preHandTreeNodes(data, 'parent');
                    this.renderTree(treeNodes)
                } else {
                    message.warning('当前表没有上游表！')
                }
            }
            this.hideLoading();
        })
    }

    onPageChange = (current, type) => {
        console.log('onPageChange:', current, type)
        const { currentChild, currentParent } = this.state;
       
        if (type === 'parent') {
            const params = getTableReqParams(currentParent);
            params.pageIndex = current;
            this.loadParentTable(params);
        } else {
            const params = getTableReqParams(currentChild);
            params.pageIndex = current;
            this.loadChildrenTable(params);
        }
    }

    handParent(parent) {
        const cloneParent = Object.assign({}, parent);
        cloneParent.childResult = null;
        cloneParent.parentResult = null;
        return cloneParent;
    }

    /**
     * 初始化树形结构
     */
    initRootTree = (rootData) => {
        rootData.isRoot = true;
        const loop = (treeItem, parent) => {
            if (treeItem) {
                treeItem.hide = false;

                treeItem.parent = this.handParent(parent);

                const childNodes = treeItem.childResult && treeItem.childResult.data;
                const parentNodes = treeItem.parentResult && treeItem.parentResult.data;

                if (childNodes && childNodes.length > 0) {
                    for (let i = 0; i < childNodes.length; i++) {
                        loop(childNodes[i], treeItem);
                    }
                }

                if (parentNodes && parentNodes.length > 0) {
                    for (let i = 0; i < parentNodes.length; i++) {
                        loop(parentNodes[i], treeItem);
                    }
                }
            }
        }

        loop(rootData);
        this.setState({ 
            treeData: rootData, 
            currentChild: rootData,
            currentParent: rootData,
        });
        return rootData;
    }
    /**
     * treeNode: 树形节点
     * treeType: child 或者 parent
     * 数据处理主要包含如下几部分工作：
     * 1. 追加（替换）新加载的子树到原有树结构中去，
     * 2. 给子节点的非父节点添加隐藏显示标记
     * 3. 给当前父节点添加标记
     */
    preHandTreeNodes = (treeNode, treeType) => {
        const { treeData, currentParent, currentChild } = this.state;
        const myTree = cloneDeep(treeData);
        myTree.isRoot = true;
        const props = treeType === 'parent' ? 'parentResult' : 'childResult';
        const nodeFlag = treeType === 'parent' ? 'isCurrentParent' : 'isCurrentChild';

        const loop = (treeItem, parent) => {
            treeItem.parent = this.handParent(parent);
            if (isEqTable(treeItem, treeNode)){
                treeItem[nodeFlag] = true;
                treeItem.hide = false;
                treeItem[props] = Object.assign({}, treeNode[props]);
                return;
            } else if (!treeItem.isRoot) {
                treeItem.hide = true;
            }
            const children = treeItem[props] ? treeItem[props].data : [];
            if (children.length > 0) {
                for (let i = 0; i < children.length; i++) {
                    loop(children[i], treeItem);
                }
            }
        }

        loop(myTree);
        this.setState({ treeData: myTree });
        return myTree;
    }

    doInsertVertex = (data) => {
        const graph = this.graph;
        const startX = Math.floor((graph.container.clientWidth - VertexSize.width) / 2);
        const startY = 100;

        this.startX = startX;
        this.startY = startY;

        const parent = graph.getDefaultParent();
        const model = graph.getModel();

        const layout = new mxCompactTreeLayout(graph, false);
        layout.horizontal = true;
        layout.useBoundingBox = false;
        layout.edgeRouting = false;
        layout.levelDistance = 60;
        layout.nodeDistance = 20;

        var layoutMgr = new mxLayoutManager(graph);

        layoutMgr.getLayout = function(cell) {
            if (cell.getChildCount() > 0) {
                return layout;
            }
        };

        this.executeLayout = function(change, post) {
            model.beginUpdate();
            try {
                if (change != null) {
                    change();
                }
                layout.execute(graph.getDefaultParent());
            } catch (e) {
                throw e;
            } finally {
                if (post != null) { post(); }
                model.endUpdate();
            }
        }

        this.renderTree(data);
    }

    generateSourceTarget = () => {
        const data = [];

    }

    renderTree = (treeNodeData) => {

        console.log('renderTree', treeNodeData)
        const graph = this.graph;

        graph.getModel().clear();
        this.parentCells = [];

        const rootCell = graph.getDefaultParent();

        this.executeLayout(() => {
            const currentNodeData = getVertexNode(treeNodeData)
            currentNodeData.isRoot = true;
            const currentNode = this.insertVertex(rootCell, currentNodeData);
            this.rootCell = currentNode;
            this.parentCells.push(rootCell, currentNode);
            this.loopTree(currentNode, treeNodeData);
        }, () => {
            graph.scrollCellToVisible(this.rootCell);
        })

        graph.view.setTranslate(200, 100);

    }

    insertVertex = (parent, data) => {
        // 隐藏节点不展示
        if (data.hide === true) return;
        const graph = this.graph;

        const rootCell = graph.getDefaultParent()
        const style = this.getStyles(data)

        // 创建节点
        const doc = mxUtils.createXmlDocument()
        const tableInfo = doc.createElement('table')
        tableInfo.setAttribute('data', JSON.stringify(data))

        let newVertex = '';
        newVertex = graph.insertVertex(rootCell, null,
            tableInfo, 20, 20,
            VertexSize.width, VertexSize.height, style
        )
        graph.view.refresh(newVertex);

        if (data.isParent) {
            this.parentCells.push(newVertex);
            graph.insertEdge(rootCell, null, '', newVertex, parent)
            console.log('isParent newVertex:', newVertex)
        } else {
            graph.insertEdge(rootCell, null, '', parent, newVertex)
        }

        this._vertexCells.push(newVertex)

        return newVertex;
    }

    loopTree = (currentNode, treeNodeData) => {

        if (treeNodeData) {
            const graph = this.graph;

            const rootCell = graph.getDefaultParent();
            const parentNodes = treeNodeData.parentResult && treeNodeData.parentResult.data;
            const childNodes = treeNodeData.childResult && treeNodeData.childResult.data;

            if (parentNodes && parentNodes.length > 0) {
                for (let i = 0; i < parentNodes.length; i++) {
                    const nodeData = getVertexNode(parentNodes[i])
                    nodeData.isParent = true;
                    const current = this.insertVertex(currentNode, nodeData);

                    if (parentNodes[i].parentResult && parentNodes[i].parentResult.data && parentNodes[i].parentResult.data.length > 0) {
                        this.loopTree(current, parentNodes[i])
                    }
                }
            }

            // 处理被依赖节点
            if (childNodes && childNodes.length > 0) {
                for (let i = 0; i < childNodes.length; i++) {
                    const nodeData = getVertexNode(childNodes[i])
                    nodeData.isChild = true;
                    // 插入新节点
                    const current = this.insertVertex(currentNode, nodeData)
                    if (childNodes[i].childResult && childNodes[i].childResult.data && childNodes[i].childResult.data.length > 0) {
                        this.loopTree(current, childNodes[i])
                    }
                }
            }
        }
    }

    getStyles = (data) => {
        if (data.isParent) {
            return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;verticalLabelPosition=bottom;verticalAlign=top'
        } else if (data.isRoot) {
            return 'whiteSpace=wrap;fillColor=#F6FFED;strokeColor=#B7EB8F;verticalLabelPosition=bottom;verticalAlign=top'
        } else if (data.isChild) {
            return 'whiteSpace=wrap;fillColor=#FFFBE6;strokeColor=#FFE58F;verticalLabelPosition=bottom;verticalAlign=top'
        }
    }

    formatTooltip = (cell) => {
        const data = cell.getAttribute('data');
        const obj = data ? JSON.parse(data) : '';
        return obj ? obj.name : ''
    }

    corvertValueToString = (cell) => {
        if (mxUtils.isNode(cell.value)) {
            if (cell.value.nodeName.toLowerCase() == 'table') {
                console.log('getChildCount:', cell.getChildCount(), cell.getIndex())
                const data = cell.getAttribute('data');
                const obj = data ? JSON.parse(data) : '';
                if (obj) {
                    return `<div class="table-vertex"><span style="text-align: center;" class="table-vertex-content"><span class="table-vertex-title" style="color: #333333;">${obj.tableName || ''}</span></span>
                    </div>`
                }
            }
        }
        return '';
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

            const table = JSON.parse(cell.getAttribute('data'));
            const params = getTableReqParams(table);
            const parentParams = getTableReqParams(table.parent);

            if (table.isParent) {
                if (table.isCurrentParent) {
                    menu.addItem('收起上游', null, function () {
                        ctx.loadParentTable(parentParams)
                        ctx.loadVertexData(parentParams)
                    })
                } else {
                    menu.addItem('展开上游（1层）', null, function () {
                        ctx.loadParentTable(params)
                        ctx.loadVertexData(params)
                    })
                }
            }

            if (table.isChild) {
                if (table.isCurrentChild) {
                    menu.addItem('收起下游', null, function () {
                        ctx.loadChildrenTable(parentParams)
                        ctx.loadVertexData(parentParams)
                    })
                } else {
                    menu.addItem('展开下游（1层）', null, function () {
                        ctx.loadChildrenTable(params)
                        ctx.loadVertexData(params)
                    })
                }
            }

        }
    }

    listenOnClick() {
        const ctx = this;
        this.graph.addListener(mxEvent.CLICK, function (sender, evt) {
            const cell = evt.getProperty('cell')
            const target = evt.getProperty('event')
            const CLICK_LEFT = 1;
            if (target.which === CLICK_LEFT && cell && cell.vertex) {
                let data = cell.getAttribute('data')
                data = data ? JSON.parse(data) : '';
                if (data) {
                    ctx.setState({ selectedData: data })
                    const params = getTableReqParams(data);
                    ctx.loadVertexData(params)
                }
            }
        })
    }

    render() {
        const { tableInfo, relationTasks, currentParent, currentChild } = this.state;
        const parentPage = currentParent.parentResult ? currentParent.parentResult : {};
        const childPage = currentChild.childResult ? currentChild.childResult : {};

        return (
            <div className="graph-editor" 
                style={{ position: 'relative', background: '#FAFAFA'}}
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
                <div className="graph-legend">
                    <div>
                        <span 
                            className="legend-item" 
                            style={{background: '#E6F7FF', border: '1px solid #90D5FF'}}>
                        </span>
                        上游
                    </div>
                    <div>
                        <span 
                            className="legend-item current" 
                            style={{background: '#F6FFED', border: '1px solid #B7EB8F'}}
                        >
                        </span>
                        当前
                    </div>
                    <div>
                        <span 
                            className="legend-item child"
                            style={{background: '#FFFBE6', border: '1px solid #FFE58F'}}
                        >
                        </span>
                        下游
                    </div>
                </div>
                <div className="graph-pagination">
                    <Pagination
                        simple
                        className="parent"
                        defaultCurrent={1}
                        pageSize={6}
                        onChange={(page) => this.onPageChange(page, 'parent')}
                        total={parentPage.totalCount}
                        current={parentPage.currentPage}
                    />
                    <Pagination
                        simple
                        className="child"
                        defaultCurrent={1}
                        pageSize={6}
                        onChange={(page) => this.onPageChange(page, 'child')}
                        current={childPage.currentPage}
                        total={childPage.totalCount}
                    />
                </div>
                <RelationDetail
                    data={tableInfo}
                    onShowColumn={this.props.onShowColumn}
                    relationTasks={relationTasks}
                    loadRelTasks={this.loadRelTableTasks}
                />
            </div>
        )
    }

    loadEditor = (container) => {
        // // Disable context menu
        mxEvent.disableContextMenu(container)
        const graph = new mxGraph(container)

        this.graph = graph
        // 启用绘制
        graph.setPanning(true);
        graph.keepEdgesInBackground = true;
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
        
        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);
        
        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';

        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip

        // enables rubberband
        new mxRubberband(graph)
        // 启用菜单
        this.initContextMenu(graph)
        this.hideMenu();
    }

    showLoading = () => {
        this.setState({ loading: 'loading' })
    }

    hideLoading = () => {
        this.setState({ loading: 'success' })
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

    getDefaultVertexStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_ELLIPSE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.EllipsePerimeter;
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
        style[mxConstants.STYLE_STROKECOLOR] = '#9EABB2';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
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
