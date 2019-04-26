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
import { VertexSize, TASK_STATUS } from '../../../../../consts'
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
    // eslint-disable-next-line no-unused-vars
    mxEventSource
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
        const { data: oldData } = prevProps
        if (data && data !== oldData) {
            this.initRender(data);
        }
    }

    initContextMenu = (graph) => {
        const { registerContextMenu } = this.props;
        if (registerContextMenu) {
            registerContextMenu(graph);
        }
    }

    initGraphEvent = (graph) => {
        const { registerEvent } = this.props;
        if (registerEvent) {
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
            registerEvent(graph);
        }
    }

    initGraph = (data) => {
        this.Container.innerHTML = ''; // 清理容器内的Dom元素
        this.graph = '';
        const graphContainer = this.Container;
        this.initGraphEditor(graphContainer);
        this.initGraphLayout();
        this.initRender(data);
        this.hideMenu();
    }

    initRender = (data) => {
        if (!data) return;
        this._cacheCells = {};
        const graph = this.graph;
        graph.getModel().clear();
        this._edges = []; // 清空
        const cells = graph.getChildCells(graph.getDefaultParent());
        // Clean data;
        graph.removeCells(cells);

        this.initContextMenu(graph);
        this.initGraphEvent(graph);
        this.renderData(data);
        this.renderAnimation();
    }

    /**
     * @param {mxCell} edge
     */
    isFlowLine = (edge) => {
        const target = edge.target;
        const source = edge.source;
        if (source.data.status === TASK_STATUS.success && target.data.status === TASK_STATUS.runnning) {
            return true;
        } else {
            return false;
        }
    }
    renderAnimation = () => {
        const graph = this.graph;
        const edges = this._edges;
        for (let i = 0; i < edges.length; i++) {
            let state = graph.view.getState(edges[i]);
            if (state && this.isFlowLine(edges[i])) {
                state.shape.node.getElementsByTagName('path')[2].setAttribute('fill', '#2491F7');
                state.shape.node.getElementsByTagName('path')[2].setAttribute('stroke', '#2491F7');
                state.shape.node.getElementsByTagName('path')[1].setAttribute('class', 'flow');
            }
        }
    }

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
                        VertexSize.height
                    )
                    cell.data = item.data;
                    cellMap[item.id] = cell;
                } else if (item.edge) {
                    const source = cellMap[item.source.id];
                    const target = cellMap[item.target.id];
                    const edge = graph.insertEdge(rootCell, item.id, '', source, target);
                    this._edges.push(edge);
                }
            }
            this.executeLayout();
            model.endUpdate();
        }
    }

    corvertValueToString = (cell) => {
        if (cell && cell.vertex) {
            const task = cell.data;
            if (task) {
                let unSave = task.notSynced ? '<span style="color:red;display: inline-block;vertical-align: middle;">*</span>' : '';
                return `<div class="vertex"><div class="vertex-title">${nodeTypeIcon(task.taskType)} ${unSave} <span style="display: inline-block;max-width: 90%;">${task.name || ''}</span>${nodeStatus(task.status)}
                <input class="vertex-input ant-input" type="text" data-id="${task.id}" id="JS_cell_${task.id}" value="${task.name || ''}" /></div>
                </div>`
            }
            return '';
        }
    }

    initGraphLayout = () => {
        const graph = this.graph;
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

    listenConnection () { // 仅仅限制有效的链接
        const graph = this.graph

        graph.isValidConnection = (source, target) => {
            // 限制，只能vertex可连接
            if (!source.vertex || !target.vertex) return false;

            // 限制连接线条数
            const edges = graph.getEdgesBetween(source, target);
            if (edges.length > 0) return false;

            // 限制循环依赖
            let isLoop = false;
            graph.traverse(target, true, function (vertex, edge) {
                if (source.id === vertex.id) {
                    isLoop = true;
                    return false;
                }
            });
            if (isLoop) return false;

            return true;
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

    hideMenu = () => {
        document.addEventListener('click', (e) => {
            const graph = this.graph
            if (graph.popupMenuHandler.isMenuShowing()) {
                graph.popupMenuHandler.hideMenu()
            }
        })
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

    layoutCenter = () => {
        this.graph.center(true, true, 0.55, 0.4);
    }

    layout = () => {
        this.executeLayout(null, null, () => {
            this.layoutCenter();
        });
    }

    undo = () => { // 撤销上一步
        this.undoMana.undo()
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

    initEditor () {
        // Overridden to define per-shape connection points
        // mxGraph.prototype.getAllConnectionConstraints = function (terminal, source) {
        //     if (terminal != null && terminal.shape != null) {
        //         if (terminal.shape.stencil != null) {
        //             if (terminal.shape.stencil != null) {
        //                 return terminal.shape.stencil.constraints;
        //             }
        //         } else if (terminal.shape.constraints != null) {
        //             return terminal.shape.constraints;
        //         }
        //     }
        //     return null;
        // };

        // Defines the default constraints for all shapes
        // mxShape.prototype.constraints = [
        //     new mxConnectionConstraint(new mxPoint(0.5, 0), true),
        //     new mxConnectionConstraint(new mxPoint(0, 0.5), true),
        //     new mxConnectionConstraint(new mxPoint(1, 0.5), true),
        //     new mxConnectionConstraint(new mxPoint(0.5, 1), true)
        // ];
        // // Edges have no connection points
        // mxPolyline.prototype.constraints = null;
    }

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
        // 允许鼠标移动画布
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
}

GraphEditor.propTypes = propType

export default GraphEditor;
