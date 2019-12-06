/* eslint-disable no-redeclare */
/* eslint-disable new-cap */
import * as React from 'react';

import {
    Tooltip
} from 'antd';

import MxFactory from 'widgets/mxGraph';
import { numOrStr } from 'typing';

import MyIcon from '../icon';
import './graph.scss';

export enum GRAPH_MODE {
    EDIT = 'edit',
    READ = 'read',
}

export interface INode<T = {}> {
    id: number;
    name?: string;
    columns?: INode[];
    vertex?: boolean;
    edge?: boolean;
    source?: INode;
    target?: INode;
    data?: T;
    rowIndex?: number;
    geometry?: {
        x: number;
        y: number;
    };
    columnOptions?: INode[];
    [index: string]: any;
}

interface IProps<T = any> {
    /**
     * 展示隐藏工具栏
     */
    disableToolbar?: boolean;

    /**
     * 数据
     */
    data?: INode[];

    /**
     * 图表模式
     */
    mode?: GRAPH_MODE;

    /**
     * 实体列表
     */
    entities: T[];

    /**
     * 附加 class
     */
    attachClass?: string;

    /**
     * 事件注册
     */
    registerEvent?: (graph: any) => void;
    /**
     * 事件上下文菜单
     */
    registerContextMenu?: (graph: any) => void;
}

const Mx = MxFactory.create();

const {
    mxGraph,
    mxText,
    mxEvent,
    mxPoint,
    mxUtils,
    mxImage,
    mxConstants,
    mxPerimeter,
    mxGraphView,
    mxGraphHandler,
    mxCellState,
    mxRectangle,
    mxClient,
    mxDivResizer,
    mxEdgeStyle,
    mxTooltipHandler,
    mxConnectionHandler,
    mxConstraintHandler,
    mxHierarchicalLayout
} = Mx;

const BASE_COLOR = '#2491F7';

const VertexSize = {
    width: 160,
    height: 0
}

interface ISelect {
    options: any[];
    id?: numOrStr;
    value?: numOrStr;
    className?: string;
    placeholder?: string;
    /**
     * 选项索引
     */
    optionIndex: string;
    bind?: { attr: string; value?: any }[];
    bindOption?: { attr: string; value?: any }[];
}

function componentSelect (data: ISelect) {
    const { id, options = [], value, className, placeholder, bind = [], optionIndex, bindOption = [] } = data;
    const getDataAttr = (arr: any, columnData?: any) => {
        let bindAttr = '';
        if (arr.length > 0) {
            arr.forEach(o => {
                bindAttr += `data-${o.attr}="${columnData ? columnData[o.attr] : o.value}" `;
            })
        }
        return bindAttr;
    }
    return `
        <select id="${id}" value="${value}" placeholder="${placeholder}" class="${className}" ${getDataAttr(bind)}>
            ${placeholder ? `<option selected value="" data-default>${placeholder}</option>` : ''}
            ${options && options.map((o: any) => {
        return `<option title="${o.id}" value="${o.id}" ${o.id == value ? 'selected' : ''} ${getDataAttr(bindOption, o)}>${o[optionIndex]}</option>`;
    })}
        </select>
    `;
}

/**
 * @param header 头部内容
 * @param body body 内容
 */
function componentVertex (header: string, body: string, footer: boolean = true) {
    return `<div class="vertex">
        <header class="title">${header}</header>
        <div class="vertex-content">
            <table class="erd">
                ${body}
            </table>
        </div>
        ${footer ? '<div class="footer"><button class="btn btn-add-col"><i class="anticon anticon-plus"></i> 添加维度</button></div>' : ''}
    </div>`
}

// Defines global helper function to get y-coordinate for a given cell state and row
const getRowY = function (state: any, tr: any) {
    var s = state.view.scale;
    var div = tr.parentNode.parentNode.parentNode;
    var offsetTop = parseInt(div.style.top);
    var y = state.y + (tr.offsetTop + tr.offsetHeight / 2 - div.scrollTop + offsetTop) * s;
    y = Math.min(state.y + state.height, Math.max(state.y + offsetTop * s, y));
    console.log('getRow', tr, div, offsetTop, y);
    return y;
};

class RelationGraph<T = any> extends React.Component<IProps<T>, any> {
    private Container: HTMLDivElement;
    private graph: any;
    private _cacheCells: Map<number | string, any> = new Map();

    componentDidMount () {
        this.initGraph();
    }

    initGraph () {
        this.Container.innerHTML = ''; // 清理容器内的Dom元素
        this.graph = '';
        this.initGraphEditor(this.Container);
        this.renderData(this.props.data);
        setTimeout(() => {
            this.layout();
        }, 0)
    }

    componentDidUpdate (prevProps, prevState) {
        if (this.props.data !== prevProps.data || this.props.entities !== prevProps.entities) {
            this.renderData(this.props.data);
        }
    }

    renderData = (data: INode[]) => {
        console.log('renderData:', data);
        const graph = this.graph;
        const rootCell = this.graph.getDefaultParent();
        graph.removeCells(graph.selectAll(rootCell));
        graph.view.clear();
        graph.view.refresh();

        if (!data || data.length === 0) return;
        try {
            const doc = mxUtils.createXmlDocument();
            const model = graph.getModel();
            const cellMap = this._cacheCells;
            if (data) {
                model.beginUpdate();
                for (let i = 0; i < data.length; i++) {
                    const item = data[i];
                    if (item.vertex) {
                        const cell = graph.insertVertex(
                            rootCell,
                            item.id,
                            item,
                            item.geometry.x,
                            item.geometry.y,
                            VertexSize.width,
                            VertexSize.height,
                            ''
                        );
                        cell.index = i;
                        // Updates the height of the cell (override width
                        // for table width is set to 100%)
                        graph.updateCellSize(cell);
                        cell.geometry.width = VertexSize.width;
                        cell.geometry.alternateBounds = new mxRectangle(0, 0, VertexSize.width, 27);

                        cellMap.set(item.id, cell);
                    } else if (item.edge) {
                        const source = cellMap.get(item.source.id);
                        const target = cellMap.get(item.target.id);
                        const relation = doc.createElement('Relation');
                        relation.setAttribute('sourceRow', item.source.rowIndex);
                        relation.setAttribute('targetRow', item.target.rowIndex);
                        graph.insertEdge(rootCell, null, relation, source, target, '');
                    }
                }
                model.endUpdate();
            }
        } catch (e) {
            console.error(e);
        }
    }

    customLabelContent = (cell: any) => {
        const { mode, entities } = this.props;
        if (cell && cell.vertex) {
            const data: INode = cell.value;
            if (data) {
                let content = '';
                if (mode && mode === GRAPH_MODE.READ) {
                    let columns = '';
                    data.columns.forEach((o: INode) => columns += `<tr><td title="${o.attrName}">${o.attrName}</td></tr>`)
                    content = componentVertex(data.name, columns, false);
                } else {
                    const entitiesSelect = componentSelect({
                        options: entities,
                        id: data.id,
                        value: data.id,
                        className: 'relationEntity__select',
                        placeholder: '请选择实体',
                        bind: [{ attr: 'index', value: cell.index }],
                        optionIndex: 'entityName',
                        bindOption: [{ attr: 'entityName' }, { attr: 'dataSourceTable' }]
                    });
                    let columns = '';
                    data.columns.forEach((o: INode, i: number) => columns += `<tr><td>${componentSelect({
                        options: data.columnOptions,
                        id: '',
                        value: o.id,
                        className: 'relationEntityColumn__tr',
                        placeholder: '请选择维度',
                        bind: [{ attr: 'index', value: cell.index + '-' + i }],
                        optionIndex: 'entityAttr',
                        bindOption: [{ attr: 'entityAttr' }, { attr: 'entityAttrCn' }]
                    })}</td></tr>`)
                    content = componentVertex(entitiesSelect, columns);
                }
                return content.replace(/[\n]/g, '');
            }
            return '<span>No data.</span>';
        }
    }

    render () {
        const { disableToolbar, attachClass } = this.props;
        return (
            <div className="graph-editor"
                style={{
                    position: 'relative',
                    height: '100%',
                    width: '100%',
                    overflow: 'hidden'
                }}
            >
                <div id="JS_GRAPH" className={`editor pointer ${attachClass}`}
                    style={{
                        width: '100%',
                        height: '100%'
                    }}
                    ref={(e: HTMLDivElement) => { this.Container = e }}
                />
                {
                    disableToolbar
                        ? null
                        : <div className="graph-toolbar">
                            <Tooltip placement="bottom" title="布局">
                                <MyIcon type="flowchart" onClick={this.layout.bind(this)} />
                            </Tooltip>
                            <Tooltip placement="bottom" title="放大">
                                <MyIcon onClick={this.zoomIn.bind(this)} type="zoom-in" />
                            </Tooltip>
                            <Tooltip placement="bottom" title="缩小">
                                <MyIcon onClick={this.zoomOut.bind(this)} type="zoom-out" />
                            </Tooltip>
                        </div>
                }
            </div>
        )
    }

    /*  初始化graph的editor */
    initGraphEditor = (container: any) => {
        const { mode } = this.props;

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
        mxConstants.CURSOR_CONNECT = 'crosshair';

        // Constraint highlight color
        mxConstraintHandler.prototype.highlightColor = BASE_COLOR;

        mxGraphView.prototype.optimizeVmlReflows = false;
        mxText.prototype.ignoreStringSize = true; // to avoid calling getBBox
        mxTooltipHandler.prototype.delay = 0; // show tooltip delay as 0

        // If connect preview is not moved away then getCellAt is used to detect the cell under
        // the mouse if the mouse is over the preview shape in IE (no event transparency), ie.
        // the built-in hit-detection of the HTML document will not be used in this case. This is
        // not a problem here since the preview moves away from the mouse as soon as it connects
        // to any given table row. This is because the edge connects to the outside of the row and
        // is aligned to the grid during the preview.
        mxConnectionHandler.prototype.movePreviewAway = false;

        // Disables foreignObjects
        mxClient.NO_FO = true;

        // Enables move preview in HTML to appear on top
        mxGraphHandler.prototype.htmlPreview = true;
        // Enables connect icons to appear on top of HTML
        mxConnectionHandler.prototype.moveIconFront = true;
        // Defines an icon for creating new connections in the connection handler.
        // This will automatically disable the highlighting of the source vertex.
        mxConnectionHandler.prototype.connectImage = new mxImage('/public/tagEngine/img/connector.gif', 16, 16);

        // Support for certain CSS styles in quirks mode
        if (mxClient.IS_QUIRKS) {
            // eslint-disable-next-line no-new
            new mxDivResizer(container);
        }
        // Disable default context menu
        mxEvent.disableContextMenu(container);

        // 启用辅助线
        mxGraphHandler.prototype.guidesEnabled = true;

        // Implements a special perimeter for table rows inside the table markup
        mxGraphView.prototype.updateFloatingTerminalPoint = function (edge: any, start: any, end: any, source: any) {
            var next = this.getNextPoint(edge, end, source);
            var div = start.text.node.getElementsByClassName('vertex-content')[0]; // start.text.node.getElementsByTagName('div')[0];

            var x = start.x;
            var y = start.getCenterY();

            // Checks on which side of the terminal to leave
            if (next.x > x + start.width / 2) {
                x += start.width;
            }

            if (div != null) {
                y = start.getCenterY() - div.scrollTop;

                if (mxUtils.isNode(edge.cell.value) && !this.graph.isCellCollapsed(start.cell)) {
                    var attr = (source) ? 'sourceRow' : 'targetRow';
                    var row = parseInt(edge.cell.value.getAttribute(attr));

                    // HTML labels contain an outer table which is built-in
                    var table = div.getElementsByTagName('table')[0];
                    var trs = table.getElementsByTagName('tr');
                    var tr = trs[Math.min(trs.length - 1, row - 1)];

                    // Gets vertical center of source or target row
                    if (tr != null) {
                        y = getRowY(start, tr);
                    }
                }

                // Keeps vertical coordinate inside start
                var offsetTop = parseInt(div.style.top) * start.view.scale;
                y = Math.min(start.y + start.height, Math.max(start.y + offsetTop, y));

                // Updates the vertical position of the nearest point if we're not
                // dealing with a connection preview, in which case either the
                // edgeState or the absolutePoints are null
                if (edge != null && edge.absolutePoints != null) {
                    next.y = y;
                }
            }

            edge.setAbsoluteTerminalPoint(new mxPoint(x, y), source);

            // Routes multiple incoming edges along common waypoints if
            // the edges have a common target row
            if (source && mxUtils.isNode(edge.cell.value) && start != null && end != null) {
                var edges = this.graph.getEdgesBetween(start.cell, end.cell, true);
                var tmp = [];

                // Filters the edges with the same source row
                var row: number = edge.cell.value.getAttribute('targetRow');
                for (var i = 0; i < edges.length; i++) {
                    if (mxUtils.isNode(edges[i].value) &&
                        edges[i].value.getAttribute('targetRow') == row) {
                        tmp.push(edges[i]);
                    }
                }

                edges = tmp;

                if (edges.length > 1 && edge.cell == edges[edges.length - 1]) {
                    // Finds the vertical center
                    var states = [];
                    var y: any = 0;
                    console.log('y:', y);
                    for (var i = 0; i < edges.length; i++) {
                        states[i] = this.getState(edges[i]);
                        y += states[i].absolutePoints[0].y;
                    }

                    y /= edges.length;

                    for (var i = 0; i < states.length; i++) {
                        var x = states[i].absolutePoints[1].x;

                        if (states[i].absolutePoints.length < 5) {
                            states[i].absolutePoints.splice(2, 0, new mxPoint(x, y));
                        } else {
                            states[i].absolutePoints[2] = new mxPoint(x, y);
                        }

                        // Must redraw the previous edges with the changed point
                        if (i < states.length - 1) {
                            this.graph.cellRenderer.redraw(states[i]);
                        }
                    }
                }
            }
        };

        // Overrides target perimeter point for connection previews
        mxConnectionHandler.prototype.getTargetPerimeterPoint = function (state: any, me: any) {
            // Determines the y-coordinate of the target perimeter point
            // by using the currentRowNode assigned in updateRow
            var y = me.getY();

            if (this.currentRowNode != null) {
                y = getRowY(state, this.currentRowNode);
            }

            // Checks on which side of the terminal to leave
            var x = state.x;

            if (this.previous.getCenterX() > state.getCenterX()) {
                x += state.width;
            }

            return new mxPoint(x, y);
        };

        // Overrides source perimeter point for connection previews
        mxConnectionHandler.prototype.getSourcePerimeterPoint = function (state: any, next: any, me: any) {
            var y = me.getY();

            if (this.sourceRowNode != null) {
                y = getRowY(state, this.sourceRowNode);
            }

            // Checks on which side of the terminal to leave
            var x = state.x;

            if (next.x > state.getCenterX()) {
                x += state.width;
            }

            return new mxPoint(x, y);
        };

        // Disables connections to invalid rows
        mxConnectionHandler.prototype.isValidTarget = function (cell: any) {
            return this.currentRowNode != null;
        };

        const graph = new mxGraph(container);
        const doc = mxUtils.createXmlDocument();

        this.graph = graph;
        // 允许鼠标右键移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.keepEdgesInBackground = true;
        graph.allowLoops = false;
        graph.centerZoom = false;
        // Enable cell resize
        graph.cellsResizable = false;
        // 启用绘制
        graph.setPanning(true);
        graph.setConnectable(mode !== GRAPH_MODE.READ);
        graph.setCellsDisconnectable(false);
        graph.setTooltips(true);
        graph.setCellsEditable(false);
        // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)
        // 启用/禁止连接
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
        // mxStyleRegistry.putValue('myOrthStyle', myEdgeStyle.OrthConnector);

        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle();
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);
        // 转换value显示的内容
        graph.getLabel = this.customLabelContent;

        // Redefine tooltip
        graph.getTooltip = function (state: any, node: any, x: any, y: any) {
            if (node.getAttribute('isconnectionpoint')) {
                return `${node.textContent}`
            }
        }

        // Implements the connect preview style by default edgeStyle
        graph.connectionHandler.createEdgeState = function (me: any) {
            var edge = graph.createEdge(null, null, null, null, null);
            return new mxCellState(this.graph.view, edge, this.graph.getCellStyle(edge));
        };

        // Adds scrollbars to the outermost div and keeps the
        // DIV position and size the same as the vertex
        var oldRedrawLabel = graph.cellRenderer.redrawLabel;
        graph.cellRenderer.redrawLabel = function (state: any) {
            oldRedrawLabel.apply(this, arguments); // "supercall"
            var graph = state.view.graph;
            var model = graph.model;

            if (model.isVertex(state.cell) && state.text != null) {
                // Scrollbars are on the div
                var s = graph.view.scale;
                state.text.node.style.overflow = 'hidden';
                var div = state.text.node.getElementsByClassName('vertex-content')[0]; // state.text.node.getElementsByTagName('div')[0];

                if (div != null) {
                    // Adds height of the title table cell
                    var oh = 32;
                    var footer = mode !== GRAPH_MODE.READ ? 48 : 0;
                    div.style.display = 'block';
                    div.style.top = oh + 'px';
                    div.style.width = Math.max(1, Math.round(state.width / s)) + 'px';
                    div.style.height = Math.max(1, Math.round((state.height / s) - (oh + footer))) + 'px';
                }
            }
        };

        // Adds a new function to update the currentRow based on the given event
        // and return the DOM node for that row
        graph.connectionHandler.updateRow = function (target: any) {
            while (target != null && target.nodeName != 'TR') {
                target = target.parentNode;
            }

            this.currentRow = null;

            // Checks if we're dealing with a row in the correct table
            if (target != null && target.parentNode.parentNode.className == 'erd') {
                // Stores the current row number in a property so that it can
                // be retrieved to create the preview and final edge
                var rowNumber = 0;
                var current = target.parentNode.firstChild;

                while (target != current && current != null) {
                    current = current.nextSibling;
                    rowNumber++;
                }

                this.currentRow = rowNumber + 1;
            } else {
                target = null;
            }

            return target;
        };

        // Adds placement of the connect icon based on the mouse event target (row)
        graph.connectionHandler.updateIcons = function (state: any, icons: any, me: any) {
            var target = me.getSource();
            target = this.updateRow(target);

            if (target != null && this.currentRow != null) {
                var div = target.parentNode.parentNode.parentNode;
                var s = state.view.scale;

                icons[0].node.style.visibility = 'visible';
                icons[0].bounds.x = state.x + target.offsetLeft + Math.min(state.width, target.offsetWidth * s) - this.icons[0].bounds.width - 2;
                icons[0].bounds.y = state.y - this.icons[0].bounds.height / 2 + (target.offsetTop + target.offsetHeight / 2 - div.scrollTop + div.offsetTop) * s;
                icons[0].redraw();

                this.currentRowNode = target;
            } else {
                icons[0].node.style.visibility = 'hidden';
            }
        };

        // Updates the targetRow in the preview edge State
        var oldMouseMove = graph.connectionHandler.mouseMove;
        graph.connectionHandler.mouseMove = function (sender: any, me: any) {
            if (this.edgeState != null) {
                this.currentRowNode = this.updateRow(me.getSource());

                if (this.currentRow != null) {
                    this.edgeState.cell.value.setAttribute('targetRow', this.currentRow);
                } else {
                    this.edgeState.cell.value.setAttribute('targetRow', '0');
                }

                // Destroys icon to prevent event redirection via image in IE
                this.destroyIcons();
            }

            oldMouseMove.apply(this, arguments);
        };

        // Creates the edge state that may be used for preview
        graph.connectionHandler.createEdgeState = function (me: any) {
            var relation = doc.createElement('Relation');
            relation.setAttribute('sourceRow', this.currentRow || '0');
            relation.setAttribute('targetRow', '0');

            var edge = this.createEdge(relation);
            var style = this.graph.getCellStyle(edge);
            var state = new mxCellState(this.graph.view, edge, style);

            // Stores the source row in the handler
            this.sourceRowNode = this.currentRowNode;

            return state;
        };

        const { registerEvent, registerContextMenu } = this.props;
        if (mode !== GRAPH_MODE.READ) {
            // 事件注册
            if (registerEvent) {
                registerEvent(graph);
            }
            if (registerContextMenu && mode) {
                registerContextMenu(graph);
            }
        }
    }

    // 注册执行布局
    executeLayout (layoutTarget?: any, change?: Function, post?: Function) {
        const graph = this.graph;
        const edgeStyle = this.getDefaultEdgeStyle();
        const parent = layoutTarget || graph.getDefaultParent();
        try {
            if (change != null) { change(); }
            const layout = new mxHierarchicalLayout(graph, 'west');
            layout.disableEdgeStyle = false;
            layout.edgeStyle = edgeStyle;
            layout.interRankCellSpacing = 80;
            layout.intraCellSpacing = 80;
            layout.execute(parent);
        } catch (e) {
            throw e;
        } finally {
            if (post != null) { post(); }
        }
    }

    layoutCenter () {
        this.graph.center(true, true, 0.4, 0.4);
    }

    layout () {
        this.executeLayout(null, null, () => {
            this.layoutCenter();
        });
    }

    zoomIn () {
        this.graph.zoomIn()
    }

    zoomOut () {
        this.graph.zoomOut()
    }

    getDefaultVertexStyle () {
        let style: any = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = 'none'; // '#D1E9FF';
        style[mxConstants.STYLE_FILLCOLOR] = 'none';
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
        let style: any = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#999999';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.OrthConnector; // TopToBottom;
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
        style[mxConstants.STYLE_FONTSIZE] = '10';
        style[mxConstants.STYLE_ROUNDED] = true;
        style[mxConstants.STYLE_CURVED] = true;
        return style
    }
}
export default RelationGraph;
