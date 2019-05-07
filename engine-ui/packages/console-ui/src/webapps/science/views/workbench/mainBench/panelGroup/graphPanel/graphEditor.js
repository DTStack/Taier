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
import { VertexSize, taskStatus, COMPONENT_TYPE } from '../../../../../consts'
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

    componentDidUpdate (prevProps) {
        const data = this.props.data
        const { data: oldData } = prevProps;
        // TODO 目前 data 更新太容易触发 didUpdate
        if (data && data !== oldData) {
            this.initRender(data);
        }
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
        graph.keepEdgesInBackground = false;
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
        this.renderData(data);
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
            // this.executeLayout();
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
            case taskStatus.SET_SUCCESS: {
                // 成功
                return 1
            }
            case taskStatus.STOPED:
            case taskStatus.RUN_FAILED:
            case taskStatus.SUBMIT_FAILED:
            case taskStatus.KILLED:
            case taskStatus.FROZEN:
            case taskStatus.PARENT_FAILD:
            case taskStatus.FAILING: {
                // 失败
                return 2;
            }
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
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, 'HDFS数据源输出')
                        ].map(item => { item.id = 'outputs'; return item; });
                        return outputs;
                    }
                    case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE: {
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, '写数据表输入1')
                        ];
                    }
                    case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, 'SQL结果输出')
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.2, 0), perimeter, 'sql脚本表1'),
                            new mxConnectionConstraint(new mxPoint(0.4, 0), perimeter, 'sql脚本表2'),
                            new mxConnectionConstraint(new mxPoint(0.6, 0), perimeter, 'sql脚本表3'),
                            new mxConnectionConstraint(new mxPoint(0.8, 0), perimeter, 'sql脚本表4')
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, '转化结果输出')
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, '类型转换输入1')
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_MERGE.NORMALIZE: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.25, 1), perimeter, '输出参数表'),
                            new mxConnectionConstraint(new mxPoint(0.75, 1), perimeter, '输出结果表')
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.25, 0), perimeter, '归一化输入参数表'),
                            new mxConnectionConstraint(new mxPoint(0.75, 0), perimeter, '归一化输入结果表')
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.25, 1), perimeter, '输出表1'),
                            new mxConnectionConstraint(new mxPoint(0.75, 1), perimeter, '输出表2')
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, '拆分输入1')
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, '模型输出')
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, '逻辑回归二分类输入1')
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, '预测结果输出')
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.25, 0), perimeter, '模型'),
                            new mxConnectionConstraint(new mxPoint(0.75, 0), perimeter, '预测数据')
                        ].concat(outputs);
                    }
                    case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION: {
                        const outputs = [
                            new mxConnectionConstraint(new mxPoint(0.25, 1), perimeter, '综合指标表'),
                            new mxConnectionConstraint(new mxPoint(0.5, 1), perimeter, '等频详细数据表'),
                            new mxConnectionConstraint(new mxPoint(0.75, 1), perimeter, '等宽详细数据表')
                        ].map(item => { item.id = 'outputs'; return item; });
                        return [
                            new mxConnectionConstraint(new mxPoint(0.5, 0), perimeter, '二分类评估输入1')
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
            // 限制，必须从输出点开始连线
            if (sourceConstraint && sourceConstraint.id !== 'outputs') return false;
            // 限制，禁止连接输出点
            if (targetConstraint && targetConstraint.id === 'outputs') return false;
            // 限制，输出点的contransint只能有一条线
            if (sourceConstraint && source.edges && source.edges.length > 0 && source.edges.findIndex(o => o.value.indexOf(sourceConstraint.name) !== -1) !== -1) return false;
            // 限制，输入点的contransint只能有一条线
            if (targetConstraint && target.edges && target.edges.length > 0 && target.edges.findIndex(o => o.value.indexOf(targetConstraint.name) !== -1) !== -1) return false;
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
            return graph.isValidSource(source) && graph.isValidTarget(target);
        }
    }

    render () {
        const { onSearchNode } = this.props;
        return (
            <div className="graph-editor"
                style={{
                    position: 'relative',
                    overflow: 'hidden'
                }}
            >
                <div className="editor pointer graph-bg"
                    style={{
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
                    <Tooltip placement="bottom" title="刷新">
                        <Icon
                            type="refresh"
                            onClick={this.refresh}
                            style={{ fontSize: '17px', color: '#333333' }}
                        />
                    </Tooltip>
                </div>
            </div>
        )
    }

    refresh = () => {
        this.graph.refresh()
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
            graph.view.setScale(scale);
            graph.view.setTranslate(dx, dy);
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
    }

    layoutCenter = () => {
        this.graph.center(true, true, 0.3, 0.3);
    }

    layout = () => {
        this.executeLayout(null, null, () => {
            this.layoutCenter();
        });
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
