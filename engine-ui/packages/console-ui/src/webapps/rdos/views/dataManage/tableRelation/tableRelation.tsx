import * as React from 'react';
import {
    Tooltip, Spin, Icon, message
} from 'antd'

import { cloneDeep } from 'lodash';

import Api from '../../../api/dataManage'
import MyIcon from '../../../components/icon'
import RelationDetail from './relationDetail';
import * as MxFactory from 'widgets/mxGraph';

const Mx = MxFactory.create();
const {
    mxGraph,
    mxEvent,
    mxRubberband,
    mxConstants,
    mxEdgeStyle,
    mxPopupMenu,
    mxPerimeter,
    mxHierarchicalLayout,
    mxUtils
} = Mx

const VertexSize: any = { // vertex大小
    width: 30,
    height: 30
}

const getVertexNode = (obj: any) => {
    return obj
}

const getTableReqParams = (tableData: any) => {
    if (!tableData) return {};
    const params: any = {
        tableName: tableData.tableName,
        pageIndex: 1,
        pageSize: 6,
        belongProjectId: tableData.belongProjectId,
        dataSourceId: tableData.dataSourceId,
        tableId: tableData.id
    }
    return params;
}

export const isEqTable = (from: any, compareTo: any) => {
    return from.tableName === compareTo.tableName &&
        from.belongProjectId === compareTo.belongProjectId &&
        from.dataSourceId === compareTo.dataSourceId
}

export default class TableRelation extends React.Component<any, any> {
    state: any = {
        selectedData: '', // 选中的数据
        treeData: {}, // 树形数据
        tableInfo: {},
        relationTasks: {}, // 关联任务
        loading: 'success',
        currentPage: 1,
        currentChild: {},
        currentParent: {},
        visible: false
    }
    Container: any;
    layout: any;
    graph: any;
    executeLayout: any;
    _parentPrev: any;
    _parentNext: any;
    _childPrev: any;
    _childNext: any;
    rootCell: any;
    componentDidMount () {
        this.Container.innerHTML = ''; // 清理容器内的Dom元素
        this.layout = '';
        this.graph = '';
        const editor = this.Container
        const tableData = this.props.tableData
        this.loadEditor(editor)
        this.listenOnClick();
        if (tableData) {
            const params = getTableReqParams(tableData)
            this.loadTableTree(params)
            this.loadVertexData(params)
        }
    }

    loadVertexData = (params: any) => {
        this.loadTableInfo(params)
        this.loadRelTableTasks(params)
    }

    loadTableInfo = (params: any) => {
        Api.getRelTableInfo(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({ tableInfo: res.data })
            }
        })
    }

    loadRelTableTasks = (params: any) => {
        params.pageSize = 5;
        params.pageIndex = params.pageIndex || 1;
        Api.getRelTableTasks(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({ relationTasks: res.data })
            }
        })
    }

    loadTableTree = (params: any) => {
        this.showLoading()
        Api.getTableRelTree(params).then((res: any) => {
            if (res.code === 1) {
                const data = res.data
                const treeData = this.initRootTree(data);
                this.doInsertVertex(treeData)
            }
            this.hideLoading();
        })
    }

    loadChildrenTable = (params: any) => {
        this.showLoading()
        Api.getChildRelTables(params).then((res: any) => {
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

    loadParentTable = (params: any) => {
        this.showLoading()
        Api.getParentRelTable(params).then((res: any) => {
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

    onPageChange = (current: any, type: any) => {
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

    handParent (parent: any) {
        const cloneParent = Object.assign({}, parent);
        cloneParent.childResult = null;
        cloneParent.parentResult = null;
        return cloneParent;
    }

    /**
     * 初始化树形结构
     */
    initRootTree = (rootData: any) => {
        rootData.isRoot = true;
        const loop = (treeItem?: any, parent?: any) => {
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
            currentParent: rootData
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
    preHandTreeNodes = (treeNode: any, treeType: any) => {
        const { treeData } = this.state;
        const myTree = cloneDeep(treeData);
        myTree.isRoot = true;
        const props = treeType === 'parent' ? 'parentResult' : 'childResult';
        const nodeFlag = treeType === 'parent' ? 'isCurrentParent' : 'isCurrentChild';

        const loop = (treeItem?: any, parent?: any) => {
            treeItem.parent = this.handParent(parent);
            if (isEqTable(treeItem, treeNode)) {
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

    doInsertVertex = (data: any) => {
        const graph = this.graph;

        const model = graph.getModel();

        this.executeLayout = function (change: any, post: any) {
            model.beginUpdate();
            try {
                const layout = new mxHierarchicalLayout(graph, false);// eslint-disable-line
                layout.orientation = 'west';
                layout.disableEdgeStyle = false;
                layout.interRankCellSpacing = 40;
                layout.intraCellSpacing = 10;

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
        this.renderTree(data);
    }

    renderPagination = () => {
        const graph = this.graph;
        const { currentChild, currentParent } = this.state;
        const rootCell = graph.getDefaultParent();
        const parentPage = currentParent.parentResult ? currentParent.parentResult : {};
        const childPage = currentChild.childResult ? currentChild.childResult : {};

        const getGeo = (pos?: any, po?: any) => {
            const x = pos && pos.geometry ? pos.geometry.x : 0;
            const y = pos && pos.geometry ? pos.geometry.y : 0;

            return po === 'prev' ? {
                x: x,
                y: y - 60
            } : {
                x: x,
                y: y + 80
            }
        }

        if (this._parentPrev && parentPage.totalPage > 0) {
            const geoParentPerv = getGeo(this._parentPrev, 'prev');
            const geoParentNext = getGeo(this._parentNext);

            if (parentPage.currentPage > 1) {
                graph.insertVertex(
                    rootCell,
                    'parentPrev',
                    'pagination',
                    geoParentPerv.x,
                    geoParentPerv.y,
                    VertexSize.width,
                    VertexSize.height,
                    'prevBtn'
                )
            }

            if (parentPage.currentPage < parentPage.totalPage) {
                graph.insertVertex(
                    rootCell,
                    'parentNext',
                    'pagination',
                    geoParentNext.x,
                    geoParentNext.y,
                    VertexSize.width,
                    VertexSize.height,
                    'nextBtn'
                )
            }
        }

        if (this._childPrev && childPage.totalPage > 0) {
            const geoChildPerv = getGeo(this._childPrev, 'prev');
            const geoChildNext = getGeo(this._childNext);
            if (childPage.currentPage > 1) {
                graph.insertVertex(
                    rootCell,
                    'childPrev',
                    'pagination',
                    geoChildPerv.x,
                    geoChildPerv.y,
                    VertexSize.width,
                    VertexSize.height,
                    'prevBtn'
                )
            }
            if (childPage.currentPage < childPage.totalPage) {
                graph.insertVertex(
                    rootCell,
                    'childNext',
                    'pagination',
                    geoChildNext.x,
                    geoChildNext.y,
                    VertexSize.width,
                    VertexSize.height,
                    'nextBtn'
                )
            }
        }
    }

    renderTree = (treeNodeData: any) => {
        const graph = this.graph;

        graph.getModel().clear();

        // 缓存分页的vertet
        this._parentPrev = '';
        this._parentNext = '';
        this._childPrev = '';
        this._childNext = '';

        const rootCell = graph.getDefaultParent();

        this.executeLayout(() => {
            const currentNodeData = getVertexNode(treeNodeData)
            currentNodeData.isRoot = true;
            const currentNode = this.insertVertex(rootCell, currentNodeData);
            this.rootCell = currentNode;
            this.loopTree(currentNode, treeNodeData);
        }, () => {
            this.renderPagination();
            graph.scrollCellToVisible(this.rootCell);
        });

        graph.center();
    }

    insertVertex = (parent: any, data: any) => {
        // 隐藏节点不展示
        if (data.hide === true && !data.isCurrentChild && !data.isCurrentParent) return;
        const graph = this.graph;

        const rootCell = graph.getDefaultParent()
        const style = this.getStyles(data)

        // 创建节点
        const doc = mxUtils.createXmlDocument()
        const tableInfo = doc.createElement('table')
        tableInfo.setAttribute('data', JSON.stringify(data))

        const newVertex = graph.insertVertex(rootCell, null,
            tableInfo, 20, 20,
            VertexSize.width, VertexSize.height, style
        )

        if (data.isParent) {
            graph.insertEdge(rootCell, null, '', newVertex, parent)
        } else {
            graph.insertEdge(rootCell, null, '', parent, newVertex)
        }
        graph.view.refresh(newVertex);

        if (!data.isRoot) {
            // 缓存分页节点位置
            if (data.isParentStart) {
                this._parentPrev = newVertex;
            } else if (data.isParentEnd) {
                this._parentNext = newVertex;
            } else if (data.isChildStart) {
                this._childPrev = newVertex;
            } else if (data.isChildEnd) {
                this._childNext = newVertex;
            }
        }

        return newVertex;
    }

    loopTree = (currentNode: any, treeNodeData: any) => {
        if (treeNodeData) {
            const parentNodes = treeNodeData.parentResult && treeNodeData.parentResult.data;
            const childNodes = treeNodeData.childResult && treeNodeData.childResult.data;

            if (parentNodes && parentNodes.length > 0) {
                for (let i = 0; i < parentNodes.length; i++) {
                    const nodeData = getVertexNode(parentNodes[i])
                    nodeData.isParent = true;
                    nodeData.isParentStart = i === 0;
                    nodeData.isParentEnd = i === parentNodes.length - 1;
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
                    nodeData.isChildStart = i === 0;
                    nodeData.isChildEnd = i === childNodes.length - 1;
                    // 插入新节点
                    const current = this.insertVertex(currentNode, nodeData)
                    if (childNodes[i].childResult && childNodes[i].childResult.data && childNodes[i].childResult.data.length > 0) {
                        this.loopTree(current, childNodes[i])
                    }
                }
            }
        }
    }

    getStyles = (data: any) => {
        if (data.isParent) {
            return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;verticalLabelPosition=bottom;verticalAlign=top'
        } else if (data.isRoot) {
            return 'whiteSpace=wrap;fillColor=#F6FFED;strokeColor=#B7EB8F;verticalLabelPosition=bottom;verticalAlign=top'
        } else if (data.isChild) {
            return 'whiteSpace=wrap;fillColor=#FFFBE6;strokeColor=#FFE58F;verticalLabelPosition=bottom;verticalAlign=top'
        }
    }

    formatTooltip = (cell: any) => {
        if (cell.value === 'pagination') {
            if (cell.id.indexOf('Next') > -1) {
                return '下一页'
            } else if (cell.id.indexOf('Prev') > -1) {
                return '上一页'
            }
        } else {
            const data = cell.getAttribute('data');
            const obj = data ? JSON.parse(data) : '';
            return obj ? `${obj.dataSource}.${obj.tableName}` : ''
        }
    }

    corvertValueToString = (cell: any) => {
        if (mxUtils.isNode(cell.value)) {
            if (cell.value.nodeName.toLowerCase() == 'table') {
                const data = cell.getAttribute('data');
                const obj = data ? JSON.parse(data) : '';
                if (obj) {
                    const name = obj.tableName || '';
                    return `<div class="table-vertex"><span style="text-align: center;" class="table-vertex-content"><span class="table-vertex-title" style="color: #333333;" title="${name}">${name}</span></span>
                    </div>`
                }
            }
        }
        return '';
    }

    initContextMenu = (graph: any) => {
        const ctx = this
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;

        mxPopupMenu.prototype.showMenu = function () {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0 && cells[0].vertex) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };

        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function (menu: any, cell: any, evt: any) {
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

    listenOnClick = () => {
        const ctx = this;

        this.graph.addListener(mxEvent.CLICK, function (sender: any, evt: any) {
            const cell = evt.getProperty('cell')
            const target = evt.getProperty('event')
            const CLICK_LEFT = 1;
            if (target.which === CLICK_LEFT && cell && cell.vertex) {
                if (cell.value === 'pagination') {
                    const currentChild = ctx.state.currentChild;
                    const currentParent = ctx.state.currentParent;
                    const parentPage = currentParent.parentResult ? currentParent.parentResult : {};
                    const childPage = currentChild.childResult ? currentChild.childResult : {};

                    switch (cell.id) {
                        case 'parentPrev': {
                            ctx.onPageChange(parentPage.currentPage - 1, 'parent');
                            return;
                        }
                        case 'parentNext': {
                            ctx.onPageChange(parentPage.currentPage + 1, 'parent');
                            return;
                        }
                        case 'childPrev': {
                            ctx.onPageChange(childPage.currentPage - 1, 'child');
                            return;
                        }
                        case 'childNext': {
                            ctx.onPageChange(childPage.currentPage + 1, 'child');
                        }
                    }
                } else {
                    let data = cell.getAttribute('data')
                    data = data ? JSON.parse(data) : '';
                    if (data) {
                        ctx.setState({ selectedData: data })
                        const params = getTableReqParams(data);
                        ctx.loadVertexData(params)
                    }
                }
            }
        })
    }

    render () {
        const { tableInfo, relationTasks, loading } = this.state;
        return (
            <div className="graph-editor"
                style={{ position: 'relative', background: '#FAFAFA', height: '1000px' }}
            >
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={loading === 'loading'}
                >
                    <div className="absolute-middle txt-bg">血缘关系</div>
                    <div className="editor pointer" style={{ height: '600px' }} ref={(e: any) => { this.Container = e }} />
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="刷新">
                        <Icon type="reload" onClick={this.refresh} style={{ color: '#333333' }} />
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
                            style={{ background: '#E6F7FF', border: '1px solid #90D5FF' }}>
                        </span>
                        上游
                    </div>
                    <div>
                        <span
                            className="legend-item current"
                            style={{ background: '#F6FFED', border: '1px solid #B7EB8F' }}
                        >
                        </span>
                        当前
                    </div>
                    <div>
                        <span
                            className="legend-item child"
                            style={{ background: '#FFFBE6', border: '1px solid #FFE58F' }}
                        >
                        </span>
                        下游
                    </div>
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

    loadEditor = (container: any) => {
        // // Disable context menu
        mxEvent.disableContextMenu(container)
        const graph = new mxGraph(container)// eslint-disable-line

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
        graph.isCellsMovable = function () {
            var cell = graph.getSelectionCell()
            return !(cell && (cell.edge || cell.value === 'pagination'))
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

        // 初始化分页样式
        this.initPaginationStyles();
        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';
        mxConstants.CURSOR_MOVABLE_VERTEX = 'pointer';

        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip

        // enables rubberband
        new mxRubberband(graph)// eslint-disable-line
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

    hideMenu = () => {
        document.addEventListener('click', (e: any) => {
            const graph = this.graph
            if (graph.popupMenuHandler.isMenuShowing()) {
                graph.popupMenuHandler.hideMenu()
            }
        })
    }

    getDefaultVertexStyle () {
        let style: any = [];
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

    initPaginationStyles () {
        let PrevStyle: any = {};
        PrevStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
        PrevStyle[mxConstants.STYLE_STROKECOLOR] = 'none';
        PrevStyle[mxConstants.STYLE_FILLCOLOR] = 'none';
        PrevStyle[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        PrevStyle[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        PrevStyle[mxConstants.STYLE_IMAGE] = '/public/rdos/img/icon/s-arrow-top.svg';

        let nextStyle = mxUtils.clone(PrevStyle);
        nextStyle[mxConstants.STYLE_IMAGE] = '/public/rdos/img/icon/s-arrow-bottom.svg';

        this.graph.getStylesheet().putCellStyle('prevBtn', PrevStyle);
        this.graph.getStylesheet().putCellStyle('nextBtn', nextStyle);
    }

    getDefaultEdgeStyle () {
        let style: any = [];
        style[mxConstants.STYLE_STROKECOLOR] = '#9EABB2';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
        style[mxConstants.STYLE_ROUNDED] = true;
        return style
    }
}
