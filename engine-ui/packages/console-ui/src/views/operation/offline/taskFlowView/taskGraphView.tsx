/* eslint-disable new-cap */
import * as React from 'react'
import { get, cloneDeep } from 'lodash'

import {
    Tooltip, Spin, Icon, Card
} from 'antd'

import Api from '../../../../api/operation'

import utils from 'dt-common/src/utils'
import { removeToolTips } from 'dt-common/src/funcs';

import MyIcon from '../../../../components/icon'
import { APP_TYPE } from '../../../../consts'
import { TASK_TYPE, SCHEDULE_STATUS, TASK_ALL_TYPE } from '../../../../consts/comm'
import { taskTypeText } from '../../../../components/display'
import { goToTaskDev } from '../hlep'

import MxFactory from 'dt-common/src/widgets/mxGraph';

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
    mxText,
    mxHierarchicalLayout
} = Mx

export const VertexSize: any = { // vertex大小
    width: 210,
    height: 50
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

/**
 * 合并Tree数据
 * @param {*} origin
 * @param {*} target
 */
export const mergeTreeNodes = (treeNodeData: any, mergeSource: any) => {
    if (treeNodeData) {
        if (treeNodeData.id === mergeSource.id) {
            if (mergeSource.taskVOS) {
                treeNodeData.taskVOS = cloneDeep(mergeSource.taskVOS);
            } else if (mergeSource.subTaskVOS) {
                treeNodeData.subTaskVOS = cloneDeep(mergeSource.subTaskVOS);
            }
            treeNodeData.subNodes = cloneDeep(mergeSource.subNodes);
            return;
        }

        const parentNodes = treeNodeData.taskVOS; // 父节点
        const childNodes = treeNodeData.subTaskVOS; // 子节点

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

export const getLevelKey = function (node: any) {
    return `${node.flowId || ''}-${node._geometry.level}`;
}

class TaskGraphView extends React.Component<any, any> {
    state: any = {
        loading: 'success',
        currentInfo: {}
    }
    _view: any = null; // 存储view信息
    Container: any;
    graph: any;
    _cacheLevel: any;
    executeLayout: Function;

    static getDerivedStateFromProps (props: any, state: any) {
        return {
            loading: props.loading
        }
    }

    componentDidMount () {
        const graphData = this.props.graphData;
        this.initGraph(graphData);
        document.addEventListener('click', this.hideMenu, true)
    }

    componentDidUpdate (prevProps: any) {
        const nextGraphData = this.props.graphData
        const { graphData, currentInfo } = prevProps
        const newInfo = this.props.currentInfo
        if (currentInfo !== newInfo) {
            this.setState({
                currentInfo: newInfo
            })
            if (newInfo && JSON.stringify(newInfo) !== '{}') {
                this.setState({
                    modalShow: true
                })
            }
        }
        if (nextGraphData && nextGraphData !== graphData) {
            this.initGraph(nextGraphData);
        }
    }
    componentWillUnmount () {
        this.hideMenu();
        document.removeEventListener('click', this.hideMenu);
    }
    initGraph = async (graphData: any) => {
        this.Container.innerHTML = ''; // 清理容器内的Dom元素
        this.graph = '';
        const editor = this.Container;
        const res: any = (graphData?.taskId && graphData?.appType) ? await Api.findTaskRuleTask({ appType: graphData?.appType, taskId: graphData?.taskId }) : { data: {} }
        this.loadEditor(editor, res?.data || {});
        this.initRender(graphData);
    }

    loadEditor = (container: any, toolTipData: any) => {
        mxGraphView.prototype.optimizeVmlReflows = false;
        mxText.prototype.ignoreStringSize = true; // to avoid calling getBBox
        // Disable context menu
        mxEvent.disableContextMenu(container);
        /* eslint-disable-next-line */
        const graph = new mxGraph(container);
        this.graph = graph;

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
        graph.isCellsMovable = function () {
            const cell = graph.getSelectionCell()
            return !(cell && cell.edge);
        }
        // 禁止cell编辑
        graph.isCellEditable = function () {
            return false;
        }
        graph.isCellResizable = function (cell: any) {
            return false;
        }

        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle)
        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString;
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip.bind(this, toolTipData);
        // 转换value显示的内容

        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';
        mxConstants.STYLE_OVERFLOW = 'hidden';

        /* eslint-disable-next-line */
        new mxRubberband(graph); // enables rubberband

        this.executeLayout = function (layoutTarget: any, change: any, post: any) {
            const parent = layoutTarget || graph.getDefaultParent();
            graph.getModel().beginUpdate();
            try {
                const layout2 = new mxHierarchicalLayout(graph, 'north');
                layout2.disableEdgeStyle = false;
                layout2.interRankCellSpacing = 40;
                layout2.intraCellSpacing = 60;
                layout2.edgeStyle = mxEdgeStyle.TopToBottom;
                if (change != null) { change(); }
                layout2.execute(parent);
            } catch (e) {
                throw e;
            } finally {
                graph.getModel().endUpdate();
                if (post != null) { post(); }
            }
        }
    }

    formatTooltip = (toolTipData, cell: any) => {
        if (cell.vertex) {
            const currentNode = cell.value;
            return this.getToolTip(currentNode, toolTipData);
        }
    }
    getToolTip = (node: any, toolTipData: any) => {
        console.log(node)
        return `${node.name}`;
    }

    getNodeDisplayName (node: any) {
        const { isCurrentProjectTask } = this.props;
        const taskName = node.name || '';
        if (isCurrentProjectTask(node)) {
            return taskName;
        } else {
            return `${taskName} (${node.projectName})`;
        }
    }

    corvertValueToString = (cell: any) => {
        const { isCurrentProjectTask } = this.props;
        console.log(cell)
        if (cell.vertex && cell.value) {
            const task = cell.value || {};
            const taskType = taskTypeText(task.taskType);
            if (task) {
                return `<div class="vertex" >
                <span class='blood-vertex-title blood-title-flag'>
                    ${task.name}
                </span>
                ${!isCurrentProjectTask(task) ? "<img class='vertex-across-logo' src='/batch/public/img/across.svg' />" : ''}
                <br>
                <span class="vertex-desc">${taskType}</span>
                </div>`.replace(/(\r\n|\n)/g, '');
            }
        }
        return '';
    }

    preHandGraphTree = (data: any) => {
        const relationTree: any = [];
        const loop = (source: any, target: any, parent: any) => {
            let node: any = null;
            if (source && !source.isPushed) {
                node = source;
            } else if (target && !target.isPushed) {
                node = target;
            } else return;

            const childNodes = node.subTaskVOS; // 子节点
            const parentNodes = node.taskVOS; // 父节点
            // Assign geo
            node.isPushed = true;

            relationTree.push({
                parent: parent,
                source: source,
                target: target
            });

            // 处理父亲依赖节点
            if (parentNodes) {
                for (let i = 0; i < parentNodes.length; i++) {
                    const sourceData = parentNodes[i];
                    if (!sourceData) continue;
                    loop(sourceData, node, parent)
                }
            }

            if (childNodes) {
                // 处理被依赖节点
                for (let i = 0; i < childNodes.length; i++) {
                    const targetData = childNodes[i];
                    if (!targetData) continue;
                    loop(node, targetData, parent)
                }
            }
        }

        loop(null, data, null);

        console.log('cacheLevel:', this._cacheLevel);
        return relationTree;
    }

    initRender = (data: any) => {
        if (!data) return;
        const graph = this.graph;
        this._cacheLevel = {};
        graph.getModel().clear();
        console.log(graph.getDefaultParent())
        const cells = graph.getChildCells(graph.getDefaultParent());
        console.log(cells)
        // Clean data;
        graph.removeCells(cells);

        // Init container scroll
        this.initContainerScroll(graph);
        this.initContextMenu(graph);
        this.initGraphEvent(graph);
        console.log(data)
        this.renderGraph(data);
    }

    getVertxtStyles = (data: any) => {
        if (data.scheduleStatus === SCHEDULE_STATUS.STOPPED) {
            return 'whiteSpace=wrap;fillColor=#EFFFFE;strokeColor=#26DAD1;'
        }
        return 'whiteSpace=wrap;fillColor=#EDF6FF;strokeColor=#A7CDF0;';
    }

    renderGraph = (originData: any) => {
        const cellCache: any = {};
        const graph = this.graph;
        const defaultParent = graph.getDefaultParent();
        const dataArr = this.preHandGraphTree(originData);

        const getVertex = (parentCell: any, data: any) => {
            if (!data) return null;
            let style = this.getVertxtStyles(data);
            const isWorkflow = data.taskType === TASK_TYPE.WORKFLOW;
            const isWorkflowNode = data.flowId && data.flowId !== 0;
            if (isWorkflowNode) {
                style += 'rounded=1;arcSize=60;';
                data.workflow = parentCell.value;
            }

            const cell = graph.insertVertex(
                isWorkflow ? null : parentCell,
                data.id,
                data,
                0, 0,
                VertexSize.width, VertexSize.height,
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
                /* eslint-disable-next-line */
                cell.geometry.alternateBounds = new mxRectangle(10, 10, VertexSize.width, VertexSize.height);
            }

            cell.isPart = isWorkflowNode;

            return cell
        }

        if (dataArr) {
            for (let i = 0; i < dataArr.length; i++) {
                const { source, target, parent } = dataArr[i];

                let sourceCell = source ? cellCache[source.id] : undefined;
                let targetCell = target ? cellCache[target.id] : undefined;
                let parentCell = defaultParent;
                const isWorkflowNode = source && source.flowId !== 0;

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
        this.executeLayout();
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
        window.setTimeout(function () {
            const bounds = graph.getGraphBounds();
            const width = Math.max(bounds.width, graph.scrollTileSize.width * graph.view.scale);
            const height = Math.max(bounds.height, graph.scrollTileSize.height * graph.view.scale);
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
    closeModal = () => {
        this.setState({
            modalShow: false
        })
    }
    renderInfo = () => {
        const { modalShow, currentInfo } = this.state
        const name = currentInfo.name || '';
        const tenantName = currentInfo?.tenantName || ''
        const productName = APP_TYPE[currentInfo.appType] || ''
        const projectName = currentInfo?.projectName || ''
        let taskType = ''
        const ruleType = ['无规则', '弱规则', '强规则']
        TASK_ALL_TYPE.forEach(item => {
            if (item.type === currentInfo.taskType) {
                taskType = item.text
            }
        })
        const taskExist = Array.isArray(currentInfo.scheduleDetailsVOList) && currentInfo.scheduleDetailsVOList?.length > 0
        return modalShow && (
            <Card className='graphInfo' >
                <p style={{ float: 'right' }}><Icon type="close" onClick={this.closeModal} /></p>
                <h2 style={{ marginTop: 5 }}>{name}</h2>
                <p><span>任务类型：</span>{taskType}</p>
                <p><span>所属租户：</span>{tenantName}</p>
                <p><span>所属产品：</span>{productName}</p>
                <p><span>所属项目：</span>{projectName}</p>
                {taskExist &&
                        <p style={{ display: 'flex' }}>
                            <span style={{ width: 60 }}>已绑定数据质量任务：</span>
                            <span style={{ width: 190, lineHeight: '20px', overflowY: 'scroll', maxHeight: 100, paddingLeft: 12 }}>
                                {
                                    currentInfo.scheduleDetailsVOList.map((item, index) => {
                                        const content = `${item.name} (租户：${item.tenantName}，项目：${item.projectName}${item.taskRule !== 0 ? `${ruleType[item.taskRule] === '强规则' ? `，含${ruleType[item.taskRule]}` : ''}` : ''})`
                                        return (<p key={index} style={{ marginTop: 0, marginBottom: index < currentInfo.scheduleDetailsVOList.length - 1 ? 12 : 0 }}>{content}</p>)
                                    })
                                }
                            </span>
                        </p>}
            </Card>)
    }
    render () {
        const { data, hideFooter, height } = this.props;
        const editorHeight = hideFooter ? height || '800px' : 'calc(100% - 35px)';
        return (
            <div className="graph-editor"
                style={{
                    position: 'relative',
                    height: '100%'
                }}
            >
                {this.renderInfo()}
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={this.state.loading === 'loading'}
                    wrapperClassName='c-task__content__spin-box batch-task-height'
                >
                    <div
                        className="editor pointer"
                        style={{
                            position: 'relative',
                            overflow: 'auto',
                            width: '100%',
                            height: editorHeight,
                            backgroundColor: '#fff'
                        }}
                        ref={(e: any) => { this.Container = e }}
                    >
                    </div>
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
                {
                    !hideFooter
                        ? (
                            <>
                                <div
                                    className="box-title graph-info"
                                    style={{
                                        bottom: 0,
                                        height: '35px',
                                        lineHeight: '35px'
                                    }}
                                >
                                    <span>{this.getNodeDisplayName(data)}</span>
                                    <span style={{ marginLeft: '15px' }}>{get(data, 'createUser.userName', '-')}</span>&nbsp;
                                        发布于
                                    <span>{utils.formatDateTime(get(data, 'gmtModified'))}</span>&nbsp;
                                    {APP_TYPE[data.appType] === APP_TYPE[1] && (<a onClick={() => { goToTaskDev(data) }}>查看代码</a>)}
                                </div>
                            </>

                        )
                        : ''
                }
            </div>
        )
    }

    /* eslint-disable */
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
                // var tx = this.translate.x;
                // var ty = this.translate.y;
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

    getDefaultVertexStyle() {
        let style: any = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = '#A7CDF0';
        style[mxConstants.STYLE_FILLCOLOR] = '#EDF6FF';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '14';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        return style;
    }

    getDefaultEdgeStyle() {
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
export default TaskGraphView;
