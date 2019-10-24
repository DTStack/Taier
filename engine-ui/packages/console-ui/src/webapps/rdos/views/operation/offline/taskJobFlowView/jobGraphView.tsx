import * as React from 'react'
import { get, cloneDeep } from 'lodash'

import {
    Tooltip, Spin, Icon
} from 'antd'

import utils from 'utils'
import { removeToolTips } from 'funcs'

import MyIcon from '../../../../components/icon'
import { getVertxtStyle } from '../../../../comm'
import { TASK_TYPE } from '../../../../comm/const'
import { taskTypeText, taskStatusText } from '../../../../components/display'
import {
    getGeoByStartPoint
} from 'utils/layout';
import MxFactory from 'widgets/mxGraph';

const Mx = MxFactory.create();
const {
    mxGraph,
    mxEvent,
    mxRubberband,
    mxConstants,
    mxEdgeStyle,
    mxPerimeter,
    mxGraphView,
    mxRectangle,
    mxPoint,
    mxUtils,
    mxText
} = Mx

export const VertexSize: any = { // vertex大小
    width: 150,
    height: 40
}

export const defaultGeo: any = { // 默认几何对象;
    count: 1,
    index: 1,
    level: 0,
    x: 10,
    y: 10,
    width: VertexSize.width,
    height: VertexSize.height,
    margin: 50
}

export const replaceTreeNodeField = (treeNode: any, sourceField: any, targetField: any, arrField: any) => {
    if (treeNode) {
        treeNode[targetField] = cloneDeep(treeNode[sourceField]);
        treeNode[sourceField] = undefined;
    }
    const children = treeNode[arrField];
    if (children) {
        for (let i = 0; i < children.length; i++) {
            replaceTreeNodeField(children[i], sourceField, targetField, arrField);
        }
    }
}

/**
 * 合并Tree数据
 * @param {*} origin
 * @param {*} target
 */
export const mergeTreeNodes = (treeNodeData: any, mergeSource: any) => {
    if (treeNodeData) {
        if (treeNodeData.id === mergeSource.id) {
            if (mergeSource.jobVOS) {
                treeNodeData.jobVOS = cloneDeep(mergeSource.jobVOS);
            } else if (mergeSource.parentNodes) {
                treeNodeData.parentNodes = cloneDeep(mergeSource.parentNodes);
            }
            treeNodeData.subNodes = cloneDeep(mergeSource.subNodes);
            return;
        }

        const childNodes = treeNodeData.jobVOS; // 子节点
        const parentNodes = treeNodeData.parentNodes; // 父节点

        // 处理依赖节点
        if (parentNodes && parentNodes.length > 0) {
            for (let i = 0; i < parentNodes.length; i++) {
                mergeTreeNodes(parentNodes[i], mergeSource);
            }
        }

        // 处理被依赖节点
        if (childNodes && childNodes.length > 0) {
            for (let i = 0; i < childNodes.length; i++) {
                mergeTreeNodes(childNodes[i], mergeSource);
            }
        }
    }
}

/**
 * 替换 TreeNode
 */
export const replaceTreeNode = (treeNodeData: any, target: any) => {
    if (treeNodeData) {
        if (treeNodeData.id === target.id) {
            treeNodeData = Object.assign({}, treeNodeData, target);
            return;
        }

        const childNodes = treeNodeData.jobVOS; // 子节点
        const parentNodes = treeNodeData.parentNodes; // 父节点

        // 处理依赖节点
        if (parentNodes && parentNodes.length > 0) {
            for (let i = 0; i < parentNodes.length; i++) {
                replaceTreeNode(parentNodes[i], target);
            }
        }

        // 处理被依赖节点
        if (childNodes && childNodes.length > 0) {
            for (let i = 0; i < childNodes.length; i++) {
                replaceTreeNode(childNodes[i], target);
            }
        }
    }
}

export const getLevelKey = function (node: any) {
    return `${node.batchTask.flowId || ''}-${node._geometry.level}`;
}

/* eslint-disable */
class JobGraphView extends React.Component<any, any> {

    state: any = {
        loading: 'success',
    }

    _view: any = null; // 存储view信息
    Container: any;
    graph: any;
    _cacheLevel: any;
    static getDerivedStateFromProps (props: any, state: any) {
        return {
            loading: props.loading
        }
    }

    componentDidMount() {
        this.initGraph(this.props.graphData);
        document.addEventListener('click', this.hideMenu, false)
    }

    componentDidUpdate(prevProps: any) {
        const nextGraphData = this.props.graphData
        const { graphData } = prevProps
        if (nextGraphData && nextGraphData !== graphData) {
            this.initGraph(nextGraphData);
        }
    }
    componentWillUnmount () {
        document.removeEventListener('click', this.hideMenu, false);
    }
    initGraph = (graphData: any) => {
        this.Container.innerHTML = ''; // 清理容器内的Dom元素
        this.graph = '';
        const editor = this.Container;
        this.loadEditor(editor)
        this.initRender(graphData);
        console.log('initGraph:', graphData);
     
    }

    loadEditor = (container: any) => {
        mxGraphView.prototype.optimizeVmlReflows = false;
        mxText.prototype.ignoreStringSize = true; // to avoid calling getBBox
        // Disable context menu
        mxEvent.disableContextMenu(container)
        const graph = new mxGraph(container)
        // 启用绘制
        graph.setPanning(true);
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.setConnectable(true);
        graph.setTooltips(true);
        graph.view.setScale(1);
        // Enables HTML labels
        graph.setHtmlLabels(true);

        graph.setAllowDanglingEdges(false);
        // 禁止连接
        graph.setConnectable(false);
        // 禁止Edge对象移动
        graph.isCellsMovable = function(cell: any) {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge);
        }
        // 禁止cell编辑
        graph.isCellEditable = function () {
            return false;
        }
        graph.isCellResizable = function(cell: any) {
            return false;
        }
        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle)

        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip;
        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString
        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';
        mxConstants.STYLE_OVERFLOW = 'hidden';

        // enables rubberband
        new mxRubberband(graph);
        this.graph = graph;
    }

    corvertValueToString = (cell: any) => {
        const { isCurrentProjectTask } = this.props;
        if (cell.vertex && cell.value) {
            const task = get(cell, 'value.batchTask', {});
            const taskType = taskTypeText(task.taskType);
            if (task) {
                return `<div class="vertex">
                <span class="vertex-title">
                    ${task.name}
                </span>
                ${!isCurrentProjectTask(task) ? "<img class='vertex-across-logo' src='/public/rdos/img/across.svg' />" : ''}
                <br>
                <span class="vertex-desc">${taskType}</span>
                </div>`.replace(/(\r\n|\n)/g, '');
            }
        }
        return '';
    }
    formatTooltip = (cell: any) => {
        if (cell.vertex) {
            const currentNode = cell.data;
            return this.getNodeDisplayName(currentNode.batchTask);
        }
    }
    getNodeDisplayName (node: any = {}) {
        const { isCurrentProjectTask } = this.props;
        const taskName = node.name || '';
        if (isCurrentProjectTask(node)) {
            return taskName;
        } else {
            return `${taskName} (${node.projectName || ''})`;
        }
    }
    /**
     * 该方法渲染cell内容为纯字符串，
     * 而非HTML渲染，可以提高绘制效率
     */
    getShowStr = (data: any) => {
        const task = data.batchTask;
        if (!task) return '';
        const taskType = taskTypeText(task.taskType);
        const taskStatus = taskStatusText(data.status);
        const taskName = task.name.length > 12 ? `${task.name.substring(0, 10)}...` : task.name;
        const str = `${taskName || ''} \n ${taskType}(${taskStatus})`;
        return str;
    }

    preHandGraphTree = (data: any) => {
        const relationTree: any = [];
        const cachedNodes: any = [];  // 缓存节点

        let level = 0; // default level

        // 计算节点的 Level
        const caculateNodeLevel = (source: any, target: any, parent: any, level: any) => {

            let node: any = null;
            if (source && !source._geometry) {
                node = source;
            } else if (target && !target._geometry) {
                node = target;
            } else return;

            const childNodes = node.jobVOS; // 子节点
            const parentNodes = node.parentNodes; // 父节点
      
            const currentNodeGeo = Object.assign({}, defaultGeo, { level });

            // Assign geo
            node._geometry = currentNodeGeo;

            // 缓存节点，更新已存在节点的 Level 信息
            const exsitNode = cachedNodes.find((item: any) => item.id === node.id);
            if (!exsitNode) {
                cachedNodes.push(node);
            } else {
                exsitNode._geometry.level = currentNodeGeo.level;
            }
       
            relationTree.push({
                parent: parent,
                source: source,
                target: target
            });

            // 处理父亲依赖节点
            if (parentNodes) {
                for (let i = 0; i < parentNodes.length; i++) {
                    const nodeData = parentNodes[i];
                    if (!nodeData) continue;
                    const l = level - 1;
                    caculateNodeLevel(nodeData, node, parent, l)
                }
            }

            if (childNodes) {
                // 处理被依赖节点
                for (let i = 0; i < childNodes.length; i++) {
                    const nodeData = childNodes[i];
                    if (!nodeData) continue;
                    const l = level + 1;
                    caculateNodeLevel(node, nodeData, parent, l)
                }
            }
        }

        const caculateTheIndexOfSameLevel = (arr: any) => {
            const IndexMap = this._cacheLevel; // 缓存 Level 信息  key(flowId-level): value( index )
            for (let i = 0; i < arr.length ; i++ ) {
                const node = arr[i];
                const levelKey = getLevelKey(node);
                if (IndexMap[levelKey] !== undefined) {
                    const index = IndexMap[levelKey] + 1;
                    node._geometry.index = index;
                    IndexMap[levelKey] = index;
                } else {
                    IndexMap[levelKey] = 1;
                }
            }
        }

        // 计算 Level
        caculateNodeLevel(null, data, null, level);

        // 计算 index
        caculateTheIndexOfSameLevel(cachedNodes);

        console.log('cacheLevel:', this._cacheLevel);
        return relationTree;
    }

    initRender = (data: any) => {
        const graph = this.graph;
        // Clean data;
        this._cacheLevel = {};
        graph.getModel().clear();
        const cells = graph.getChildCells(graph.getDefaultParent());
        graph.removeCells(cells);
        // Init container scroll
        this.initContainerScroll(graph);
        this.initContextMenu(graph);
        this.initGraphEvent(graph);
        this.renderGraph(data);
    }

    renderGraph = (originData: any) => {
        const cellCache: any = {};
        const graph = this.graph;
        const defaultParent = graph.getDefaultParent();
        const cacheLevel = this._cacheLevel; // 缓存 Level 信息
        const dataArr = this.preHandGraphTree(originData);
        console.log('renderData:', dataArr);

        const getVertex = (parentCell: any, data: any) => {
            if (!data) return null;

            let style = getVertxtStyle(data.status);
            // const showText = this.getShowStr(data);

            const isWorkflow = data.batchTask.taskType === TASK_TYPE.WORKFLOW;
            const isWorkflowNode = data.batchTask.flowId && data.batchTask.flowId !== 0;
            let nodeGeo = data._geometry;
            const startPoint = Object.assign({}, defaultGeo);
            startPoint.x = 0;
            startPoint.y = 0;

            // 缓存的Index值为最大Count值
            const levelKey = cacheLevel[getLevelKey(data)];
            if (levelKey !== undefined) {
                nodeGeo.count = levelKey;
            }

            if (isWorkflowNode) {
                style += 'rounded=1;arcSize=60;';
            }
            nodeGeo = getGeoByStartPoint(startPoint, nodeGeo);
            console.log('nodeGeo:', data.batchTask.name, startPoint, nodeGeo);

            const cell = graph.insertVertex(
                isWorkflow ? null : parentCell,
                data.id,
                data,
                nodeGeo.x, nodeGeo.y,
                nodeGeo.width, nodeGeo.height,
                style
            )
            if (isWorkflow) {
                cell.collapsed = true;
                // Mock node
                graph.insertVertex(
                    cell,
                    null,
                    '',
                    0, 50,
                    VertexSize.width, VertexSize.height, // geo.width, geo.height,
                    style
                )
                cell.geometry.alternateBounds = new mxRectangle(10, 10, VertexSize.width, VertexSize.height);
            }

            cell.data = data; // 添加 data 属性，便于节点操作的时候使用
            cell.isPart = isWorkflowNode;

            return cell
        }

        if (dataArr) {
            for (let i = 0; i < dataArr.length; i++) {
                const { source, target, parent } = dataArr[i];

                let sourceCell = source ? cellCache[source.id] : undefined;
                let targetCell = target ? cellCache[target.id] : undefined;
                let parentCell = defaultParent;
                const isWorkflowNode = source && source.batchTask && source.batchTask.flowId !== 0;

                if (parent) {
                    const existCell = cellCache[parent.id];
                    if (existCell) {
                        parentCell = existCell
                    } else {
                        parentCell = getVertex(defaultParent, parent);
                        cellCache[parent.id] = parentCell;
                    }
                }

                if (source && !sourceCell) {
                    sourceCell = getVertex(parentCell, source);
                    cellCache[source.id] = sourceCell;
                }

                if (target && !targetCell) {
                    targetCell = getVertex(parentCell, target);
                    cellCache[target.id] = targetCell;
                }

                if (sourceCell && targetCell) {
                    const edges = graph.getEdgesBetween(sourceCell, targetCell);
                    const edgeStyle = !isWorkflowNode ? null : 'strokeColor=#B7B7B7;';
    
                    if (edges.length === 0) {
                        graph.insertEdge(defaultParent, null, '', sourceCell, targetCell, edgeStyle)
                    }
                }
            }
        }
        this.layoutView();
        removeToolTips();
    }

    initContextMenu = (graph: any) => {
        const { registerContextMenu } = this.props;
        if (registerContextMenu) registerContextMenu(graph);
    }

    initGraphEvent = (graph: any) => {
        const { registerEvent } = this.props;
        if (registerEvent) registerEvent(graph);
    }

    saveViewInfo = () => {
        const view = this.graph.getView();
        const translate = view.getTranslate();
        if (translate.x > 0) {
            this._view = {
                translate: translate,
                scale: view.getScale()
            };
        }
    }

    layoutView = () => {
        const view = this._view;
        const graph = this.graph;
        if (view) {
            const scale = view.scale;
            const dx = view.translate.x;
            const dy = view.translate.y;
            graph.view.setScale(scale);
            graph.view.setTranslate(dx, dy);
        }
        // Sets initial scrollbar positions
        window.setTimeout(function() {
            var bounds = graph.getGraphBounds();
            var width = Math.max(bounds.width, graph.scrollTileSize.width * graph.view.scale);
            var height = Math.max(bounds.height, graph.scrollTileSize.height * graph.view.scale);
            graph.container.scrollTop = Math.floor(Math.max(0, bounds.y - Math.max(20, (graph.container.clientHeight - height) / 2)));
            graph.container.scrollLeft = Math.floor(Math.max(0, bounds.x - Math.max(0, (graph.container.clientWidth - width) / 2)));
        }, 0);
    }

    graphEnable () {
        const status = this.graph.isEnabled()
        this.graph.setEnabled(!status)
    }

    refresh = () => {
        this.saveViewInfo();
        this.props.refresh();
    }

    zoomIn = () => {
        this.graph.zoomIn()
    }

    zoomOut = () => {
        this.graph.zoomOut()
    }

    hideMenu = () => {
        const popMenus = document.querySelector('.mxPopupMenu')
            if (popMenus) {
                document.body.removeChild(popMenus)
        }
    }

    render () {
        const { goToTaskDev, data, isPro, showJobLog, isCurrentProjectTask } = this.props;
        return (
            <div className="graph-editor"
                style={{
                    position: 'relative',
                    height: '100%'
                }}
            >
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={this.state.loading === 'loading'}
                    wrapperClassName='c-jobGraph__spin-box'
                >
                    <div
                        className="editor pointer"
                        style={{
                            position: 'relative',
                            overflow: 'auto',
                            width: '100%',
                            height: 'calc(100% - 35px)'
                        }}
                        ref={(e: any) => { this.Container = e }}
                    >
                    </div>
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
                <div
                    className="box-title graph-info"
                    style={{
                        bottom: 0,
                        height: '35px',
                        lineHeight: '35px'
                    }}
                >
                    <span>{this.getNodeDisplayName(data.batchTask)}</span>
                    <span style={{ marginLeft: '15px' }}>{get(data, 'batchTask.createUser.userName', '-')}</span>&nbsp;
                    { isPro ? '发布' : '提交' }于&nbsp;
                    <span>{ utils.formatDateTime(get(data, 'batchTask.gmtModified')) }</span>&nbsp;
                    <a title="双击任务可快速查看日志" onClick={() => { showJobLog(get(data, 'jobId' )) }} style={{ marginRight: '8' }}>查看日志</a>&nbsp;
                    {isCurrentProjectTask(data.batchTask) && (<a onClick={() => { goToTaskDev(get(data, 'batchTask.id')) }}>查看代码</a>)}
                </div>
            </div>
        )
    }

    initContainerScroll(graph: any) {
        /**
         * Specifies the size of the size for "tiles" to be used for a graph with
         * scrollbars but no visible background page. A good value is large
         * enough to reduce the number of repaints that is caused for auto-
         * translation, which depends on this value, and small enough to give
         * a small empty buffer around the graph. Default is 400x400.
         */
        graph.scrollTileSize = new mxRectangle(0, 0, 200, 200);

        /**
         * Returns the padding for pages in page view with scrollbars.
         */
        graph.getPagePadding = function () {
            return new mxPoint(Math.max(0, Math.round(graph.container.offsetWidth - 34)),
                Math.max(0, Math.round(graph.container.offsetHeight - 34)));
        };

        /**
         * Returns the size of the page format scaled with the page size.
         */
        graph.getPageSize = function () {
            return (this.pageVisible) ? new mxRectangle(0, 0, this.pageFormat.width * this.pageScale,
                this.pageFormat.height * this.pageScale) : this.scrollTileSize;
        };

        /**
         * Returns a rectangle describing the position and count of the
         * background pages, where x and y are the position of the top,
         * left page and width and height are the vertical and horizontal
         * page count.
         */
        graph.getPageLayout = function () {
            var size = (this.pageVisible) ? this.getPageSize() : this.scrollTileSize;
            var bounds = this.getGraphBounds();

            if (bounds.width == 0 || bounds.height == 0) {
                return new mxRectangle(0, 0, 1, 1);
            } else {
                // Computes untransformed graph bounds
                var x = Math.ceil(bounds.x / this.view.scale - this.view.translate.x);
                var y = Math.ceil(bounds.y / this.view.scale - this.view.translate.y);
                var w = Math.floor(bounds.width / this.view.scale);
                var h = Math.floor(bounds.height / this.view.scale);

                var x0 = Math.floor(x / size.width);
                var y0 = Math.floor(y / size.height);
                var w0 = Math.ceil((x + w) / size.width) - x0;
                var h0 = Math.ceil((y + h) / size.height) - y0;

                return new mxRectangle(x0, y0, w0, h0);
            }
        };

        // Fits the number of background pages to the graph
        graph.view.getBackgroundPageBounds = function () {
            var layout = this.graph.getPageLayout();
            var page = this.graph.getPageSize();

            return new mxRectangle(this.scale * (this.translate.x + layout.x * page.width),
                this.scale * (this.translate.y + layout.y * page.height),
                this.scale * layout.width * page.width,
                this.scale * layout.height * page.height);
        };

        graph.getPreferredPageSize = function (bounds: any, width: any, height: any) {
            var pages = this.getPageLayout();
            var size = this.getPageSize();

            return new mxRectangle(0, 0, pages.width * size.width, pages.height * size.height);
        };

        /**
         * Guesses autoTranslate to avoid another repaint (see below).
         * Works if only the scale of the graph changes or if pages
         * are visible and the visible pages do not change.
         */
        var graphViewValidate = graph.view.validate;
        graph.view.validate = function () {
            if (this.graph.container != null && mxUtils.hasScrollbars(this.graph.container)) {
                var pad = this.graph.getPagePadding();
                var size = this.graph.getPageSize();

                // Updating scrollbars here causes flickering in quirks and is not needed
                // if zoom method is always used to set the current scale on the graph.
                var tx = this.translate.x;
                var ty = this.translate.y;
                this.translate.x = pad.x / this.scale - (this.x0 || 0) * size.width;
                this.translate.y = pad.y / this.scale - (this.y0 || 0) * size.height;
            }

            graphViewValidate.apply(this, arguments);
        };

        var graphSizeDidChange = graph.sizeDidChange;
        graph.sizeDidChange = function () {
            if (this.container != null && mxUtils.hasScrollbars(this.container)) {
                var pages = this.getPageLayout();
                var pad = this.getPagePadding();
                var size = this.getPageSize();

                // Updates the minimum graph size
                var minw = Math.ceil(2 * pad.x / this.view.scale + pages.width * size.width);
                var minh = Math.ceil(2 * pad.y / this.view.scale + pages.height * size.height);

                var min = graph.minimumGraphSize;

                // LATER: Fix flicker of scrollbar size in IE quirks mode
                // after delayed call in window.resize event handler
                if (min == null || min.width != minw || min.height != minh) {
                    graph.minimumGraphSize = new mxRectangle(0, 0, minw, minh);
                }

                // Updates auto-translate to include padding and graph size
                var dx = pad.x / this.view.scale - pages.x * size.width;
                var dy = pad.y / this.view.scale - pages.y * size.height;

                if (!this.autoTranslate && (this.view.translate.x != dx || this.view.translate.y != dy)) {
                    this.autoTranslate = true;
                    this.view.x0 = pages.x;
                    this.view.y0 = pages.y;

                    // NOTE: THIS INVOKES THIS METHOD AGAIN. UNFORTUNATELY THERE IS NO WAY AROUND THIS SINCE THE
                    // BOUNDS ARE KNOWN AFTER THE VALIDATION AND SETTING THE TRANSLATE TRIGGERS A REVALIDATION.
                    // SHOULD MOVE TRANSLATE/SCALE TO VIEW.
                    var tx = graph.view.translate.x;
                    var ty = graph.view.translate.y;

                    graph.view.setTranslate(dx, dy);
                    graph.container.scrollLeft += (dx - tx) * graph.view.scale;
                    graph.container.scrollTop += (dy - ty) * graph.view.scale;

                    this.autoTranslate = false;
                    return;
                }

                graphSizeDidChange.apply(this, arguments);
            }
        };
    }

    getDefaultVertexStyle () {
        let style: any = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = '#A7CDF0';
        style[mxConstants.STYLE_FILLCOLOR] = '#EDF6FF';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTFAMILY] = 'PingFangSC-Regular';
        style[mxConstants.FONT_BOLD] = 'normal';
        style[mxConstants.STYLE_WHITE_SPACE] = 'nowrap';
        // style[mxConstants.STYLE_OVERFLOW] = 'hidden';

        return style;
    }

    getDefaultEdgeStyle () {
        let style: any = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#2491F7';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
        style[mxConstants.STYLE_FONTSIZE] = '10';
        style[mxConstants.STYLE_ROUNDED] = false;
        return style;
    }
}
export default JobGraphView;
