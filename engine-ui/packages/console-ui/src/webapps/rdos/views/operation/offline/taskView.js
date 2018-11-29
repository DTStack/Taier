import React, { Component } from 'react'
import { hashHistory } from 'react-router'

import {
    Tooltip, Spin, message, Icon
} from 'antd'

import utils from 'utils'
import { getGeoByRelativeNode, getNodeWidth, getNodeHeight, getNodeLevelAndCount } from 'utils/layout';

import Api from '../../../api'
import MyIcon from '../../../components/icon'
import { taskTypeText } from '../../../components/display'
import { TASK_TYPE, SCHEDULE_STATUS } from '../../../comm/const'

const Mx = require('public/rdos/mxgraph')({
    mxBasePath: 'public/rdos/mxgraph',
    mxImageBasePath: 'public/rdos/mxgraph/images',
    mxLanguage: 'none',
    mxLoadResources: false,
    mxLoadStylesheets: false
})

const {
    mxGraph,
    mxEvent,
    mxRubberband,
    mxConstants,
    mxEdgeStyle,
    mxPopupMenu,
    mxPerimeter,
    mxRectangle,
    mxGraphHandler
} = Mx

const VertexSize = { // vertex大小
    width: 150,
    height: 40
}

/**
 * 合并Tree数据
 * @param {*} origin
 * @param {*} target
 */
const mergeTreeNodes = (treeNodeData, mergeSource) => {
    if (treeNodeData) {
        if (treeNodeData.id === mergeSource.id) {
            if (mergeSource.taskVOS) {
                treeNodeData.taskVOS = Object.assign(mergeSource.taskVOS);
            }
            if (mergeSource.subTaskVOS) {
                treeNodeData.subTaskVOS = Object.assign(mergeSource.subTaskVOS);
            }
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

export default class TaskView extends Component {
    state = {
        selectedTask: '', // 选中的Task
        data: {}, // 数据
        loading: 'success',
        lastVertex: '',
        sort: 'children',
        visible: false
    }

    _view = null; // 存储view信息

    initGraph = (id) => {
        this.Container.innerHTML = ''; // 清理容器内的Dom元素
        this.graph = '';
        this._originData = null;
        this._vertexCells = {}; // 用于缓存创建的顶点节点

        const editor = this.Container
        this.loadEditor(editor)
        this.hideMenu()
        this.loadTaskChidren({
            taskId: id
        });
    }

    componentWillReceiveProps (nextProps) {
        const currentTask = this.props.tabData;
        const { tabData, visibleSlidePane, tabKey } = nextProps;
        if (((!currentTask && tabData) || (tabData && tabData.id !== currentTask.id)) && visibleSlidePane) {
            this.initGraph(tabData.id);
            this._view = null;
        }
        if (tabKey && this.props.tabKey !== tabKey && tabKey === 'taskFlow') {
            this.refresh();
        }
    }

    loadTaskChidren = (params) => {
        const ctx = this;
        params.type = 2;

        this.setState({ loading: 'loading' })
        Api.getTaskChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ selectedTask: data, data });
                ctx.doInsertVertex(data);
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadTaskParent = (params) => {
        const ctx = this;

        params.type = 1;

        this.setState({ loading: 'loading' })
        Api.getTaskChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ data, selectedJob: data });
                ctx.doInsertVertex(data);
            }
            ctx.setState({ loading: 'success' })
        })
    }

    preHandGraphTree = (data) => {
        const relationTree = [];

        let level = 0;
        let defaultRoot = { // 层级，默认0;
            count: 1,
            index: 1,
            level: 0,
            x: 10,
            y: 10,
            width: VertexSize.width,
            height: VertexSize.height,
            margin: 50
        }

        const loop = (currentNodeData, parent, level, parentNode) => {
            if (currentNodeData) {
                const parentNodes = currentNodeData.taskVOS; // 父节点
                const childNodes = currentNodeData.subTaskVOS; // 子节点

                let currentNodeGeo;

                if (!currentNodeData._geometry) {
                    currentNodeGeo = Object.assign({}, defaultRoot);
                    currentNodeGeo.level = level;
                    currentNodeGeo.y = parentNode.y + defaultRoot.margin;
                } else {
                    currentNodeGeo = currentNodeData._geometry;
                }

                if (currentNodeData.taskType === TASK_TYPE.WORKFLOW) {
                    const workflowData = currentNodeData.subNodes;
                    let newLevel = 0;

                    // 如果是工作流，需要重新计算工作流节点的高和宽
                    const nodeCount = getNodeLevelAndCount(workflowData);
                    const tempNode = Object.assign({}, defaultRoot);
                    tempNode.count = nodeCount.count;
                    tempNode.level = nodeCount.level;
                    currentNodeGeo.width = getNodeWidth(tempNode);
                    currentNodeGeo.height = getNodeHeight(tempNode);

                    const workflowDefaultRoot = Object.assign({}, defaultRoot);
                    workflowDefaultRoot.x = Math.round((currentNodeGeo.width - VertexSize.width) / 2);
                    workflowDefaultRoot.y = workflowDefaultRoot.height + 25;

                    if (workflowData) {
                        loop(workflowData, currentNodeData, newLevel, workflowDefaultRoot)
                    }
                }

                currentNodeGeo = getGeoByRelativeNode(parentNode, currentNodeGeo);
                currentNodeData._geometry = currentNodeGeo;

                const dataItem = {
                    parent: parent,
                    source: currentNodeData
                };

                relationTree.push(dataItem);

                // 处理依赖节点
                if (parentNodes && parentNodes.length > 0) {
                    for (let i = 0; i < parentNodes.length; i++) {
                        const nodeData = parentNodes[i];
                        const node = Object.assign({}, defaultRoot);
                        node.level = level - 1;
                        node.index = i + 1;
                        node.count = parentNodes.length;
                        nodeData._geometry = getGeoByRelativeNode(currentNodeGeo, node);

                        if (parentNodes[i].taskVOS) {
                            loop(nodeData, parent, level - 1, currentNodeGeo)
                        }

                        relationTree.push({
                            parent: parent,
                            source: nodeData,
                            target: currentNodeData
                        });
                    }
                }

                // 处理被依赖节点
                if (childNodes && childNodes.length > 0) {
                    for (let j = 0; j < childNodes.length; j++) {
                        const nodeData = childNodes[j];

                        const node = Object.assign({}, defaultRoot);
                        node.level = level + 1;
                        node.index = j + 1;
                        node.count = childNodes.length;

                        nodeData._geometry = getGeoByRelativeNode(currentNodeGeo, node);

                        if (childNodes[j].subTaskVOS) {
                            loop(nodeData, parent, ++level, currentNodeGeo)
                        }

                        const treeItem = {
                            parent: parent,
                            source: currentNodeData,
                            target: nodeData
                        };
                        relationTree.push(treeItem);
                    }
                }
            }
        }

        loop(data, null, level, defaultRoot);

        return relationTree;
    }

    renderGraph = (dataArr) => {
        const cellCache = this._vertexCells;
        const graph = this.graph;
        const defaultParent = graph.getDefaultParent();
        this._rootCell = defaultParent;

        const getVertex = (parentCell, data) => {
            if (!data) return null;

            let style = this.getStyles(data);

            const isWorkflow = data.taskType === TASK_TYPE.WORKFLOW;
            const isWorkflowNode = data.flowId && data.flowId !== 0;

            if (isWorkflow) {
                style += 'shape=swimlane;swimlaneFillColor=#F7FBFF;fillColor=#D0E8FF;strokeColor=#92C2EF;dashed=1;';

                if (data.scheduleStatus === SCHEDULE_STATUS.STOPPED) {
                    style += 'swimlaneFillColor=#EFFFFE;fillColor=#cbf8f4;strokeColor=#26DAD1;';
                }
            }

            if (isWorkflowNode) {
                style += 'rounded=1;arcSize=60;'
                data.workflow = parentCell.value;
            }

            const geo = data._geometry || {
                x: 10,
                y: 10,
                width: VertexSize.width,
                height: VertexSize.height
            };

            console.log('geo:', data.name, geo);

            const cell = graph.insertVertex(
                isWorkflow ? null : parentCell,
                data.id,
                data,
                geo.x, geo.y,
                geo.width, geo.height,
                style
            )

            if (isWorkflow) {
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
                const isWorkflowNode = source && source.flowId && source.flowId !== 0;

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

                const edges = graph.getEdgesBetween(sourceCell, targetCell);
                const edgeStyle = !isWorkflowNode ? null : 'strokeColor=#B7B7B7;';

                if (edges.length === 0) {
                    graph.insertEdge(parentCell, null, '', sourceCell, targetCell, edgeStyle)
                }
            }
        }
    }

    doInsertVertex = (data) => {
        const graph = this.graph;

        // clean data;
        graph.getModel().clear();
        this._vertexCells = {};
        const cells = graph.getChildCells(graph.getDefaultParent());
        graph.removeCells(cells);

        // handData
        let originData = this._originData;
        if (originData) {
            mergeTreeNodes(originData, data);
            this._originData = originData;
        } else {
            this._originData = data;
        }

        const arrayData = this.preHandGraphTree(this._originData);
        this.renderGraph(arrayData);
        // 初始化 view
        this.initView();
    }

    loadEditor = (container) => {
        // Disable default context menu
        mxEvent.disableContextMenu(container)
        const graph = new mxGraph(container)
        this.graph = graph
        // 启用绘制
        graph.setPanning(true);
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;

        graph.setConnectable(true)
        graph.setTooltips(true)
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
        graph.isCellEditable = function () {
            return false
        }

        /**
         * Redirects start drag to parent.
        */
        const graphHandlerGetInitialCellForEvent = mxGraphHandler.prototype.getInitialCellForEvent;
        mxGraphHandler.prototype.getInitialCellForEvent = function (me) {
            var cell = graphHandlerGetInitialCellForEvent.apply(this, arguments);
            if (cell.isPart) {
                cell = graph.getModel().getParent(cell)
            }
            return cell;
        };

        // Redirects selection to parent
        graph.selectCellForEvent = function (cell) {
            if (cell.isPart) {
                cell = graph.getModel().getParent(cell)
                return cell;
            }
            mxGraph.prototype.selectCellForEvent.apply(this, arguments);
        };

        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);
        // 转换value显示的内容
        graph.convertValueToString = this.corvertValueToString
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip;
        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';

        // enables rubberband
        new mxRubberband(graph)

        this.initContextMenu(graph);
        this.initGraphEvent();
    }

    formatTooltip = (cell) => {
        const task = cell.value || '';
        return task ? task.name : ''
    }

    corvertValueToString = (cell) => {
        if (cell.vertex && cell.value) {
            const task = cell.value || {};
            const taskType = taskTypeText(task.taskType);
            if (task) {
                return `<div class="vertex"><span class="vertex-title" title="${task.name || ''}">${task.name || ''}</span>
                <span class="vertex-desc">${taskType}</span>
                </div>`
            }
        }
        return '';
    }

    getStyles = (data) => {
        if (data.scheduleStatus === SCHEDULE_STATUS.STOPPED) {
            return 'whiteSpace=wrap;fillColor=#EFFFFE;strokeColor=#26DAD1;'
        }
        return 'whiteSpace=wrap;fillColor=#EDF6FF;strokeColor=#A7CDF0;';
    }

    forzenTasks = (ids, mode) => {
        const ctx = this
        Api.forzenTask({
            taskIdList: ids,
            scheduleStatus: mode //  1正常调度, 2暂停 NORMAL(1), PAUSE(2),
        }).then((res) => {
            if (res.code === 1) {
                message.success('操作成功！');
                ctx.props.reload();
                ctx.refresh();
            }
        })
    }

    initContextMenu = (graph) => {
        const ctx = this
        const { goToTaskDev, clickPatchData } = this.props

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

            const currentNode = cell.value || {};
            const isWorkflowNode = currentNode.flowId && currentNode.flowId !== 0;
            // 如果为工作流节点，且工作流处于冻结状态时，需要禁用子节点的解冻或者调用功能
            const disableRunCtrl = isWorkflowNode && currentNode.workflow.scheduleStatus === SCHEDULE_STATUS.STOPPED;
            if (!isWorkflowNode) {
                menu.addItem('展开上游（6层）', null, function () {
                    ctx.loadTaskParent({
                        taskId: currentNode.id,
                        level: 6
                    })
                })
                menu.addItem('展开下游（6层）', null, function () {
                    ctx.loadTaskChidren({
                        taskId: currentNode.id,
                        level: 6
                    })
                })
            }
            menu.addItem('补数据', null, function () {
                clickPatchData(currentNode)
            })
            menu.addItem('查看代码', null, function () {
                goToTaskDev(currentNode.id)
            })
            menu.addItem('冻结', null, function () {
                ctx.forzenTasks([currentNode.id], SCHEDULE_STATUS.STOPPED)
            }, null, null,
            currentNode.scheduleStatus === SCHEDULE_STATUS.NORMAL && !disableRunCtrl
            ) // 正常状态

            menu.addItem('解冻', null, function () {
                ctx.forzenTasks([currentNode.id], SCHEDULE_STATUS.NORMAL);
            }, null, null,
            currentNode.scheduleStatus === SCHEDULE_STATUS.STOPPED && !disableRunCtrl
            ) // 冻结状态

            if (!isWorkflowNode) {
                menu.addItem('查看实例', null, function () {
                    hashHistory.push(`/operation/offline-operation?job=${currentNode.name}`)
                })
            }
        }
    }

    initGraphEvent = () => {
        const ctx = this;
        const graph = this.graph;

        graph.addListener(mxEvent.onClick, function (sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {
                let data = cell.value;
                ctx.setState({ selectedTask: data })
            }
        });
    }

    saveViewInfo = () => {
        const view = this.graph.getView();
        this._view = {
            translate: view.getTranslate(),
            scale: view.getScale()
        };
    }

    initView = () => {
        const view = this._view;
        const graph = this.graph;
        if (view) {
            const scale = view.scale;
            const dx = view.translate.x;
            const dy = view.translate.y;
            graph.view.setScale(scale);
            graph.view.setTranslate(dx, dy);
        } else {
            graph.center();
        }
    }

    refresh = () => {
        this.saveViewInfo();
        this.initGraph(this.props.tabData.id);
    }

    graphEnable () {
        const status = this.graph.isEnabled()
        this.graph.setEnabled(!status)
    }

    zoomIn = () => {
        this.graph.zoomIn();
    }

    zoomOut = () => {
        this.graph.zoomOut();
    }

    hideMenu = () => {
        document.addEventListener('click', (e) => {
            const popMenus = document.querySelector('.mxPopupMenu')
            if (popMenus) {
                document.body.removeChild(popMenus)
            }
        })
    }

    /* eslint-enable */
    render () {
        const task = this.state.selectedTask
        const { goToTaskDev } = this.props

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
                >
                    <div className="editor pointer" ref={(e) => { this.Container = e }} />
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="刷新">
                        <Icon type="reload" onClick={this.refresh} style={{ color: '#333333' }} />
                    </Tooltip>
                    <Tooltip placement="bottom" title="放大">
                        <MyIcon onClick={this.zoomIn} type="zoom-in"/>
                    </Tooltip>
                    <Tooltip placement="bottom" title="缩小">
                        <MyIcon onClick={this.zoomOut} type="zoom-out"/>
                    </Tooltip>
                </div>
                <div className="box-title graph-info">
                    <span>{task.name || '-'}</span>
                    <span style={{ marginLeft: '15px' }} >{ (task.createUser && task.createUser.userName) || '-' }</span>&nbsp;
                    发布于&nbsp;
                    <span>{utils.formatDateTime(task.gmtModified)}</span>&nbsp;
                    <a onClick={() => { goToTaskDev(task.id) }}>查看代码</a>
                </div>
            </div>
        )
    }

    getDefaultVertexStyle () {
        let style = [];
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

    getDefaultEdgeStyle () {
        let style = [];
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
