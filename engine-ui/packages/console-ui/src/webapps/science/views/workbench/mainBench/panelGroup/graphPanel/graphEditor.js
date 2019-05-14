import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux';

import {
    Tooltip, Icon
} from 'antd';

import Mx from 'widgets/mxGraph';

import MyIcon from '../../../../../components/icon';
import { nodeTypeIcon, nodeStatus } from '../../../../../components/display';
import * as componentActions from '../../../../../actions/componentActions';
import { VertexSize, taskStatus, COMPONENT_TYPE, CONSTRAINT_TEXT } from '../../../../../consts'
const propType = {
    data: PropTypes.object,
    registerContextMenu: PropTypes.func,
    registerEvent: PropTypes.func,
    onSearchNode: PropTypes.func
}

const {
    mxGraph,
    mxText,
    mxEvent,
    mxConstants,
    mxEdgeStyle,
    mxPerimeter,
    mxGraphView,
    mxGraphHandler,
    mxConstraintHandler,
    mxHierarchicalLayout,
    mxPoint,
    mxEventSource,
    mxConnectionHandler,
    mxConnectionConstraint,
    mxUtils,
    mxImageShape,
    mxRectangle,
    mxClient
} = Mx;

const BASE_COLOR = '#2491F7';

/* eslint new-cap: ["error", { "newIsCap": false }] */
@connect(null, (dispatch) => {
    return bindActionCreators(componentActions, dispatch);
})
class GraphEditor extends Component {
    componentDidMount () {
        const data = this.props.data;
        this.initGraph(data);
    }
    _edges = []; //
    shouldComponentUpdate (nextProps, nextState) {
        console.group();
        console.log('version:', nextProps.version, '<-', this.props.version);
        console.log(nextProps.data)
        console.groupEnd();
        if (this.calculateStatus(nextProps.data) !== this.calculateStatus(this.props.data)) {
            return true;
        }
        if (nextProps.version === this.props.version) {
            return false
        }
        return true;
    }

    componentDidUpdate (prevProps) {
        const data = this.props.data
        const { data: oldData } = prevProps;
        // TODO 目前 data 更新太容易触发 didUpdate
        if (data && data !== oldData) {
            this.initRender(data);
        }
    }
    /**
     * 用于计算组件的状态是否有发生改变
     * 返回组件的total，和之前的状态比较，若有不同表示有变化
     */
    calculateStatus = (arr) => {
        let statusTotal = 0;
        for (let index = 0; index < arr.length; index++) {
            if (!arr[index].vertex) {
                continue;
            }
            const status = arr[index].data.status || 0;
            statusTotal += status;
        }
        return statusTotal;
    }
    /* 初始化整个graph */
    initGraph = (data) => {
        this.Container.innerHTML = ''; // 清理容器内的Dom元素
        this.graph = '';
        const graphContainer = this.Container;
        this.initGraphEditor(graphContainer);
        this.initEventListener();
        this.customizeInsertEdge();
        this.initConnector();
        this.listenConnection();
        this.initRender(data);
        this.hideMenu();
    }
    /*  初始化graph的editor */
    initGraphEditor = (container) => {
        mxConstants.DEFAULT_VALID_COLOR = 'none';
        mxConstants.HANDLE_STROKECOLOR = '#C5C5C5';
        mxConstants.CONSTRAINT_HIGHLIGHT_SIZE = 4;
        mxConstants.GUIDE_COLOR = BASE_COLOR;
        mxConstants.EDGE_SELECTION_COLOR = BASE_COLOR;
        mxConstants.VERTEX_SELECTION_COLOR = BASE_COLOR;
        mxConstants.HANDLE_FILLCOLOR = BASE_COLOR;
        mxConstants.VALID_COLOR = BASE_COLOR;
        mxConstants.HIGHLIGHT_COLOR = BASE_COLOR;
        mxConstants.OUTLINE_HIGHLIGHT_COLOR = BASE_COLOR;
        mxConstants.CONNECT_HANDLE_FILLCOLOR = BASE_COLOR;

        // Constraint highlight color
        mxConstraintHandler.prototype.highlightColor = BASE_COLOR;

        mxGraphView.prototype.optimizeVmlReflows = false;
        mxText.prototype.ignoreStringSize = true; // to avoid calling getBBox

        // Disable default context menu
        mxEvent.disableContextMenu(container);

        // 启用辅助线
        mxGraphHandler.prototype.guidesEnabled = true;

        const graph = new mxGraph(container)
        this.graph = graph
        this.props.saveGraph(graph);
        // 允许鼠标右键移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.keepEdgesInBackground = true;
        graph.allowLoops = false;
        // // Enable cell resize
        graph.cellsResizable = false;
        // 启用绘制
        graph.setPanning(true);
        graph.setConnectable(true);
        graph.setTooltips(false);
        // // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)
        // // 启用/禁止连接
        graph.foldingEnabled = false;
        // 禁止Edge对象移动
        graph.isCellsMovable = function () {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge)
        }
        // 禁止cell编辑
        graph.isCellEditable = function () {
            return false
        }

        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);
        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString
    }
    /* 重置一些添加事件的方法 */
    initEventListener = () => {
        mxEventSource.prototype.addListener = function (name, funct, isUpdate = false) {
            if (this.eventListeners == null) {
                this.eventListeners = [];
            }
            if (isUpdate) {
                let index = this.eventListeners.findIndex(o => o === name);
                if (index === -1) {
                    this.eventListeners.push(name);
                    this.eventListeners.push(funct);
                } else {
                    this.eventListeners[index + 1] = funct
                }
            } else {
                this.eventListeners.push(name);
                this.eventListeners.push(funct);
            }
        };
        mxGraph.prototype.addMouseListener = function (listener, isUpdate = false) {
            if (this.mouseListeners == null) {
                this.mouseListeners = [];
            }
            if (isUpdate) {
                let index = this.mouseListeners.findIndex(o => o.id === listener.id);
                if (index === -1) {
                    this.mouseListeners.push(listener);
                } else {
                    this.mouseListeners[index] = listener;
                }
            } else {
                this.mouseListeners.push(listener);
            }
        }
    }
    /* 初始化渲染数据 */
    initRender = (data) => {
        if (!data) return;
        this._cacheCells = {};
        const graph = this.graph;
        graph.getModel().clear();
        this._edges = []; // 清空
        const cells = graph.getChildCells(graph.getDefaultParent());
        // Clean data;
        graph.removeCells(cells);
        this.initGraphLayout();
        this.initContextMenu(graph);
        this.initGraphEvent(graph);
        // Init container scroll
        this.initContainerScroll(graph);
        this.renderData(data);
        this.initGraphEvent(graph);
        this.renderAnimation();
    }
    /* 初始化隐藏右键的菜单 */
    hideMenu = () => {
        document.addEventListener('click', (e) => {
            const graph = this.graph
            if (graph.popupMenuHandler.isMenuShowing()) {
                graph.popupMenuHandler.hideMenu()
            }
        })
    }
    /* 渲染graph上的edge和vertex */
    renderData = (data) => {
        const graph = this.graph;
        const rootCell = this.graph.getDefaultParent();
        const model = graph.getModel();
        const cellMap = this._cacheCells;
        console.log('data:', data);
        if (data) {
            model.beginUpdate();
            for (let i = 0; i < data.length; i++) {
                const item = data[i];
                if (item.vertex) {
                    const cell = graph.insertVertex(
                        rootCell,
                        item.id,
                        null,
                        item.x, item.y,
                        VertexSize.width,
                        VertexSize.height,
                        item.style || ''
                    )
                    cell.data = item.data;
                    cellMap[item.id] = cell;
                } else if (item.edge) {
                    const source = cellMap[item.source.id];
                    const target = cellMap[item.target.id];
                    const edge = graph.insertEdge(rootCell, item.id, item.value, source, target, item.style || '');
                    this._edges.push(edge);
                }
            }
            model.endUpdate();
        }
    }

    /* 初始化右键菜单 */
    initContextMenu = (graph) => {
        const { registerContextMenu } = this.props;
        if (registerContextMenu) {
            registerContextMenu(graph);
        }
    }
    /* 初始化事件 */
    initGraphEvent = (graph) => {
        const { registerEvent } = this.props;
        if (registerEvent) {
            console.log('graph evt:', this.props.data, graph);
            registerEvent(graph);
        }
    }
    /**
     * @param {mxCell} edge
     */
    isFlowEdge = (edge) => {
        const target = edge.target;
        const source = edge.source;
        if (this.vertexStatus(source.data.status) === 1 && this.vertexStatus(target.data.status) === 0) {
            return true
        } else {
            return false
        }
    }
    /**
     * @param {} status-vertex的状态
     * @returns 1 === 成功；2 === 失败； 0 === 中间状态；
     */
    vertexStatus = (status) => {
        if (!status) return;
        switch (status) {
            case taskStatus.FINISHED:
            case taskStatus.SET_SUCCESS:
                // 成功
                return 1

            case taskStatus.STOPED:
            case taskStatus.RUN_FAILED:
            case taskStatus.SUBMIT_FAILED:
            case taskStatus.KILLED:
            case taskStatus.FROZEN:
            case taskStatus.PARENT_FAILD:
            case taskStatus.FAILING:
                // 失败
                return 2;
            default: {
                // 中间状态，进行中
                return 0
            }
        }
    }
    /* 初始化edge的动画效果 */
    renderAnimation = () => {
        const graph = this.graph;
        const edges = this._edges;
        for (let i = 0; i < edges.length; i++) {
            let state = graph.view.getState(edges[i]);
            if (state && this.isFlowEdge(edges[i])) {
                state.shape.node.getElementsByTagName('path')[2].setAttribute('fill', '#2491F7');
                state.shape.node.getElementsByTagName('path')[2].setAttribute('stroke', '#2491F7');
                state.shape.node.getElementsByTagName('path')[1].setAttribute('class', 'flow');
            }
        }
    }
    corvertValueToString = (cell) => {
        if (cell && cell.vertex) {
            const task = cell.data;
            if (task) {
                let unSave = task.notSynced ? '<span style="color:red;display: inline-block;vertical-align: middle;">*</span>' : '';
                return `<div class="vertex"><div class="vertex-title">${nodeTypeIcon(task.componentType)} ${unSave} <span style="display: inline-block;max-width: 90%;">${task.name || ''}</span>${nodeStatus(this.vertexStatus(task.status))}
                <input class="vertex-input ant-input" placeholder="不超过12个字符" type="text" data-id="${task.id}" id="JS_cell_${task.id}" value="${task.name || ''}" /></div>
                </div>`
            }
            return '';
        }
    }
    /* 自定义插入的edge样式 */
    customizeInsertEdge = () => {
        const graph = this.graph;
        mxConnectionHandler.prototype.insertEdge = function (parent, id, value, source, target, style) {
            const sourceConstraint = graph.connectionHandler.sourceConstraint;
            const targetConstraint = graph.connectionHandler.constraintHandler.currentConstraint;
            if (sourceConstraint && sourceConstraint.id === 'outputs') {
                value = `${sourceConstraint.name}_${targetConstraint.name}`
            }
            var edge = this.createEdge(value, source, target, style);
            edge = graph.addEdge(edge, parent, source, target);
            return edge;
        };
    }
    /* 初始化各个组件类型的可连接数 */
    initConnector = () => {
        const graph = this.graph;
        graph.getAllConnectionConstraints = function (terminal) {
            if (terminal != null && this.model.isVertex(terminal.cell)) {
                const type = terminal.cell.data.componentType;
                const perimeter = true;
                switch (type) {
                    case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, CONSTRAINT_TEXT[type].output[0].value)
                        ].map(item => { item.id = 'outputs'; return item; });
                        return outputs;
                    }
                    case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE: {
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, CONSTRAINT_TEXT[type].input[0].value)
                        ];
                    }
                    case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, CONSTRAINT_TEXT[type].output[0].value)
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.2, 0), perimeter, CONSTRAINT_TEXT[type].input[0].value),
                            new mxConnectionConstraint(new mxPoint(0.4, 0), perimeter, CONSTRAINT_TEXT[type].input[1].value),
                            new mxConnectionConstraint(new mxPoint(0.6, 0), perimeter, CONSTRAINT_TEXT[type].input[2].value),
                            new mxConnectionConstraint(new mxPoint(0.8, 0), perimeter, CONSTRAINT_TEXT[type].input[3].value)
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, CONSTRAINT_TEXT[type].output[0].value)
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, CONSTRAINT_TEXT[type].input[0].value)
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_MERGE.NORMALIZE: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.25, 1), perimeter, CONSTRAINT_TEXT[type].output[0].value),
                            new mxConnectionConstraint(new mxPoint(0.75, 1), perimeter, CONSTRAINT_TEXT[type].output[1].value)
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.25, 0), perimeter, CONSTRAINT_TEXT[type].input[0].value),
                            new mxConnectionConstraint(new mxPoint(0.75, 0), perimeter, CONSTRAINT_TEXT[type].input[1].value)
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.25, 1), perimeter, CONSTRAINT_TEXT[type].output[0].value),
                            new mxConnectionConstraint(new mxPoint(0.75, 1), perimeter, CONSTRAINT_TEXT[type].output[1].value)
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, CONSTRAINT_TEXT[type].input[0].value)
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, CONSTRAINT_TEXT[type].output[0].value)
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, CONSTRAINT_TEXT[type].input[0].value)
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, CONSTRAINT_TEXT[type].output[0].value)
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.25, 0), perimeter, CONSTRAINT_TEXT[type].input[0].value),
                            new mxConnectionConstraint(new mxPoint(0.75, 0), perimeter, CONSTRAINT_TEXT[type].input[1].value)
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.25, 1), perimeter, CONSTRAINT_TEXT[type].output[0].value),
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, CONSTRAINT_TEXT[type].output[1].value),
                            new mxConnectionConstraint(new mxPoint(0.75, 1), perimeter, CONSTRAINT_TEXT[type].output[2].value)
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, CONSTRAINT_TEXT[type].input[0].value)
                        ].concat(outputs);
                    }
                    default:
                        break;
                }
            }
            return null;
        };
    }
    /* 自定义有效的连接 */
    listenConnection () {
        const graph = this.graph
        // mxConstraintHandler.prototype.pointImage = new mxImage('images/dot.gif', 10, 10);
        // 限制，只能连接contraint
        mxConstraintHandler.prototype.intersects = function (icon, point, source, existingEdge) {
            return (!source || existingEdge) || mxUtils.intersects(icon.bounds, point);
        };
        // 重写该方法，hover on constraint的时候可以展示title
        mxConstraintHandler.prototype.setFocus = function (me, state, source) {
            this.constraints = (state != null && !this.isStateIgnored(state, source) &&
                this.graph.isCellConnectable(state.cell)) ? ((this.isEnabled())
                    ? (this.graph.getAllConnectionConstraints(state, source) || []) : []) : null;

            // Only uses cells which have constraints
            if (this.constraints != null) {
                this.currentFocus = state;
                this.currentFocusArea = new mxRectangle(state.x, state.y, state.width, state.height);

                if (this.focusIcons != null) {
                    for (let i = 0; i < this.focusIcons.length; i++) {
                        this.focusIcons[i].destroy();
                    }

                    this.focusIcons = null;
                    this.focusPoints = null;
                }

                this.focusPoints = [];
                this.focusIcons = [];

                for (let i = 0; i < this.constraints.length; i++) {
                    // 这里是根据constraints来生成页面上的节点
                    var cp = this.graph.getConnectionPoint(state, this.constraints[i]);
                    var img = this.getImageForConstraint(state, this.constraints[i], cp);

                    var src = img.src;
                    var bounds = new mxRectangle(Math.round(cp.x - img.width / 2),
                        Math.round(cp.y - img.height / 2), img.width, img.height);
                    var icon = new mxImageShape(bounds, src);
                    icon.dialect = (this.graph.dialect != mxConstants.DIALECT_SVG)
                        ? mxConstants.DIALECT_MIXEDHTML : mxConstants.DIALECT_SVG;
                    icon.preserveImageAspect = false;
                    icon.init(this.graph.getView().getDecoratorPane());

                    // Fixes lost event tracking for images in quirks / IE8 standards
                    if (mxClient.IS_QUIRKS || document.documentMode == 8) {
                        mxEvent.addListener(icon.node, 'dragstart', function (evt) {
                            mxEvent.consume(evt);

                            return false;
                        });
                    }

                    // Move the icon behind all other overlays
                    if (icon.node.previousSibling != null) {
                        icon.node.parentNode.insertBefore(icon.node, icon.node.parentNode.firstChild);
                    }

                    var getState = mxUtils.bind(this, function () {
                        return (this.currentFocus != null) ? this.currentFocus : state;
                    });

                    icon.redraw(); // icon在redraw之后 icon.node里面才有节点
                    if (this.constraints[i].name) {
                        /**
                         * 这个方法里面的其他地方都是源码复制过来的
                         * 主要是这个if判断里面的内容
                         * 为constraints添加title
                         * hover展示name
                         */
                        let title = document.createElementNS(mxConstants.NS_SVG, 'title');
                        title.innerHTML = this.constraints[i].name;
                        icon.node.children[0].appendChild(title)
                    }
                    mxEvent.redirectMouseEvents(icon.node, this.graph, getState);
                    this.currentFocusArea.add(icon.bounds);
                    this.focusIcons.push(icon);
                    this.focusPoints.push(cp);
                }

                this.currentFocusArea.grow(this.getTolerance(me));
            } else {
                this.destroyIcons();
                this.destroyFocusHighlight();
            }
        };
        graph.isValidConnection = (source, target) => {
            const sourceConstraint = graph.connectionHandler.sourceConstraint;
            const targetConstraint = graph.connectionHandler.constraintHandler.currentConstraint;
            // eslint-disable-next-line no-debugger
            debugger;
            // 限制，必须从输出点开始连线
            if (sourceConstraint && sourceConstraint.id !== 'outputs') return false;
            // 限制，禁止连接输出点
            if (targetConstraint && targetConstraint.id === 'outputs') return false;
            // 不需要限制输出点的contransint只能有一条线
            // 限制，输入点的contransint只能有一条线
            if (
                targetConstraint &&
                target.edges &&
                target.edges.length > 0 &&
                target.edges.findIndex(o => {
                    if (o.target.data.id !== target.data.id) {
                        // 首先排除掉不是同一个target的edge
                        return false
                    } else {
                        // 其次再根据edge的pisition来判断目标位置上是否有edge
                        const positionEquals = `entryX=${targetConstraint.point.x};entryY=${targetConstraint.point.y};`
                        return o.style.indexOf(positionEquals) !== -1;
                    }
                }) !== -1
            ) return false;
            // 限制，只能vertex可连接
            if (!source.vertex || !target.vertex) return false;
            // 限制循环依赖
            let isLoop = false;
            graph.traverse(target, true, function (vertex, edge) {
                if (source.id === vertex.id) {
                    isLoop = true;
                    return false;
                }
            });
            if (isLoop) return false;
            return true
        }
    }

    render () {
        const { onSearchNode } = this.props;
        return (
            <div className="graph-editor"
                style={{
                    position: 'relative',
                    height: '100%'
                }}
            >
                <div className="editor pointer graph-bg"
                    style={{
                        position: 'relative',
                        overflow: 'auto',
                        width: '100%',
                        height: '100%'
                    }}
                    ref={(e) => { this.Container = e }}
                />
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="布局">
                        <MyIcon type="flowchart" onClick={this.layout}/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="放大">
                        <MyIcon onClick={this.zoomIn} type="zoom-in"/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="缩小">
                        <MyIcon onClick={this.zoomOut} type="zoom-out"/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="搜索节点">
                        <Icon
                            type="search"
                            onClick={onSearchNode}
                            style={{ fontSize: '17px', color: '#333333' }}
                        />
                    </Tooltip>
                </div>
            </div>
        )
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

    /* 初始化layout */
    initGraphLayout = () => {
        const graph = this.graph;
        const { data } = this.props;
        const index = data.findIndex(o => o.graph);
        if (index !== -1) {
            const scale = data[index].scale;
            const dx = data[index].translate.x;
            const dy = data[index].translate.y;
            graph.view.scaleAndTranslate(scale, dx, dy);
        } else {
            this.layoutCenter();
        }
        // 注册执行布局
        this.executeLayout = function (layoutTarget, change, post) {
            const parent = layoutTarget || graph.getDefaultParent();
            try {
                if (change != null) { change(); }
                const layout = new mxHierarchicalLayout(graph, 'north');
                layout.disableEdgeStyle = false;
                layout.interRankCellSpacing = 60;
                layout.intraCellSpacing = 60;
                layout.edgeStyle = mxConstants.EDGESTYLE_TOPTOBOTTOM;
                layout.execute(parent);
            } catch (e) {
                throw e;
            } finally {
                if (post != null) { post(); }
            }
        }
        // Sets initial scrollbar positions
        window.setTimeout(function () {
            var bounds = graph.getGraphBounds();
            var width = Math.max(bounds.width, graph.scrollTileSize.width * graph.view.scale);
            var height = Math.max(bounds.height, graph.scrollTileSize.height * graph.view.scale);
            graph.container.scrollTop = Math.floor(Math.max(0, bounds.y - Math.max(20, (graph.container.clientHeight - height) / 2)));
            graph.container.scrollLeft = Math.floor(Math.max(0, bounds.x - Math.max(0, (graph.container.clientWidth - width) / 2)));
        }, 0);
    }

    layoutCenter = () => {
        this.graph.center(true, true, 0.5, 0.5);
    }

    layout = () => {
        this.executeLayout(null, null, () => {
            this.layoutCenter();
        });
    }

    initContainerScroll (graph) {
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

        graph.getPreferredPageSize = function (bounds, width, height) {
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

    getDefaultVertexStyle () {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = '#90D5FF';
        style[mxConstants.STYLE_FILLCOLOR] = '#FFFFFF';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '14';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        style[mxConstants.STYLE_ARCSIZE] = 2;
        style[mxConstants.STYLE_ROUNDED] = true;
        return style;
    }

    getDefaultEdgeStyle () {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#999999';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
        style[mxConstants.STYLE_FONTSIZE] = '10';
        style[mxConstants.STYLE_ROUNDED] = true;
        style[mxConstants.STYLE_ARCSIZE] = 90;

        return style
    }
}

GraphEditor.propTypes = propType

export default GraphEditor;
