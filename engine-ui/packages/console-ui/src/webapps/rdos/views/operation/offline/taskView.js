import React, { Component } from 'react'
import { hashHistory } from 'react-router'

import {
    Tooltip, Spin, message, Icon,
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import MyIcon from '../../../components/icon'
import { taskTypeText } from '../../../components/display'
import { TASK_TYPE, SCHEDULE_STATUS } from '../../../comm/const'

const Mx = require('public/rdos/mxgraph')({
    mxBasePath: 'public/rdos/mxgraph',
    mxImageBasePath: 'public/rdos/mxgraph/images',
    mxLanguage: 'none',
    mxLoadResources: false,
})

const {
    mxGraph,
    mxEvent,
    mxRubberband,
    mxConstants,
    mxEdgeStyle,
    mxPopupMenu,
    mxRectangle,
    mxPerimeter,
    mxGraphHandler,
    mxCompactTreeLayout,
} = Mx

const VertexSize = { // vertex大小
    width: 150,
    height: 40,
}

const getVertexNode = (obj) => {
    return obj;
}

export default class TaskView extends Component {

    state = {
        selectedTask: '', // 选中的Task
        data: {}, // 数据
        loading: 'success',
        lastVertex: '',
        sort: 'children',
        visible: false,
    }

    initGraph = (id) => {
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.graph = "";
        this._vertexCells = {}; // 用于缓存创建的顶点节点
    
        const editor = this.Container
        // this.initEditor()
        this.loadEditor(editor)
        this.hideMenu()
        this.loadTaskChidren({
            taskId: id,
            level: 6,
        })
    }

    componentWillReceiveProps(nextProps) {
        const currentTask = this.props.tabData;
        const { tabData, visibleSlidePane, tabKey } = nextProps;
        if (((!currentTask && tabData) || (tabData && tabData.id !== currentTask.id)) && visibleSlidePane) {
            this.initGraph(tabData.id)
        }
        if (tabKey && this.props.tabKey !== tabKey && tabKey === 'taskFlow') {
            this.refresh();
        }
    }

    loadTaskChidren = (params) => {
        const ctx = this
        params.type = 2;

        this.setState({ loading: 'loading' })
        Api.getTaskChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ selectedTask: data, data })
                ctx.doInsertVertex(data)
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadTaskParent = (params) => {
        const ctx = this

        params.type = 1;

        this.setState({ loading: 'loading' })
        Api.getTaskChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ data, selectedJob: data });
                ctx.doInsertVertex(data);
            }
            ctx.setState({ loading: 'success'})
        })
    }

    preHandGraphTree = (data) => {

        const relationTree = [];

        const loop = (treeNodeData, parent) => {

            if (treeNodeData) {
                const parentNodes = treeNodeData.taskVOS; // 父节点
                const childNodes = treeNodeData.subTaskVOS; // 子节点
                const currentNodeData = getVertexNode(treeNodeData)

                const dataItem = {
                    parent: parent,
                    source: currentNodeData,
                }

                relationTree.push(dataItem);

                // 处理依赖节点
                if (parentNodes && parentNodes.length > 0) {
                    for (let i = 0; i < parentNodes.length; i++) {
                        const nodeData = getVertexNode(parentNodes[i])
                        if (parentNodes[i].taskVOS) {
                            loop(parentNodes[i], parent)
                        }
                        relationTree.push({
                            parent: parent,
                            source: nodeData,
                            target: currentNodeData,
                        });
                    }
                }
    
                // 处理被依赖节点
                if (childNodes && childNodes.length > 0) {
                    for (let i = 0; i < childNodes.length; i++) {
                        const nodeData = getVertexNode(childNodes[i])
                        if (childNodes[i].subTaskVOS) {
                            loop(childNodes[i], parent)
                        }
                        relationTree.push({
                            parent: parent,
                            source: currentNodeData,
                            target: nodeData,
                        });
                    }
                }

                if (treeNodeData.taskType === TASK_TYPE.WORKFLOW) {
                    const workflowData = treeNodeData.subNodes;
                    if (workflowData) {
                        loop(workflowData, currentNodeData)
                    }
                }
            }
        }

        loop(data);

        return relationTree;
    }

    renderGraph = (dataArr) => {
        const cellCache = this._vertexCells;
        const graph = this.graph;
        const defaultParent = graph.getDefaultParent();

        const getVertex = (parentCell, data) => {
            if (!data) return null;

            let style = this.getStyles(data);
            const isWorkflow = data.taskType === TASK_TYPE.WORKFLOW;
            const isWorkflowNode = data.flowId && data.flowId !== 0;

            let width = VertexSize.width;
            let height = VertexSize.height;
            if (isWorkflow) {
                width = width + 20;
                height = height + 100;
                style += 'shape=swimlane;swimlaneFillColor=#F7FBFF;fillColor=#D0E8FF;strokeColor=#92C2EF;dashed=1;';
            }

            if (isWorkflowNode) {
                style += 'rounded=1;arcSize=60;'
            }

            const cell = graph.insertVertex(
                parentCell,
                data.id, 
                data, 
                0, 0,
                width, height, 
                style,
            )
            if (isWorkflow) {
                cell.geometry.alternateBounds = new mxRectangle(0, 0, VertexSize.width, VertexSize.height);
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

                const edges = graph.getEdgesBetween(sourceCell, targetCell)
                const edgeStyle = !isWorkflowNode ? null : 'strokeColor=#B7B7B7;';

                if (edges.length === 0) {
                    graph.insertEdge(defaultParent, null, '', sourceCell, targetCell, edgeStyle)
                }
                this.executeLayout(parentCell);
            }
        }
    }

    doInsertVertex = (data) => {
        const graph = this.graph;
        const arrayData = this.preHandGraphTree(data);
        this.renderGraph(arrayData);
        graph.center();
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
        graph.view.setScale(1)
        // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)
        // 禁止连接
        graph.setConnectable(false)
        // 禁止Edge对象移动
        graph.isCellsMovable = function(cell) {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge)
        }
        // 禁止cell编辑
        graph.isCellEditable = function() {
            return false
        }

        /**
         * Redirects start drag to parent.
        */
        const graphHandlerGetInitialCellForEvent = mxGraphHandler.prototype.getInitialCellForEvent;
        mxGraphHandler.prototype.getInitialCellForEvent = function(me) {
            var cell = graphHandlerGetInitialCellForEvent.apply(this, arguments);
            if (cell.isPart) {
                cell = graph.getModel().getParent(cell)
            }
            return cell;
        };

        // Redirects selection to parent
        graph.selectCellForEvent = function(cell) {
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
        this.initGraphLayout();
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
                return `<div class="vertex"><span class="vertex-title">${task.name || ''}</span>
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
            scheduleStatus: mode  //  1正常调度, 2暂停 NORMAL(1), PAUSE(2),
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
        mxPopupMenu.prototype.showMenu = function() {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0 && cells[0].vertex) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };
        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function(menu, cell, evt) {

            if (!cell) return

            const currentNode = cell.value || {};
            const isWorkflowNode = currentNode.flowId && currentNode.flowId !== 0;

            if (!isWorkflowNode) {
                menu.addItem('展开上游（6层）', null, function() {
                    ctx.loadTaskParent({
                        taskId: currentNode.id,
                        level: 6,
                    })
                })
                menu.addItem('展开下游（6层）', null, function() {
                    ctx.loadTaskChidren({
                        taskId: currentNode.id,
                        level: 6,
                    })
                })
            }
            menu.addItem('补数据', null, function() {
                clickPatchData(currentNode)
            })
            menu.addItem('查看代码', null, function() {
                goToTaskDev(currentNode.id)
            })
            menu.addItem('冻结', null, function() {
                ctx.forzenTasks([currentNode.id], SCHEDULE_STATUS.STOPPED)
            }, null, null, currentNode.scheduleStatus === SCHEDULE_STATUS.NORMAL) // 正常状态

            menu.addItem('解冻', null, function() {
                ctx.forzenTasks([currentNode.id], SCHEDULE_STATUS.NORMAL);
            }, null, null, currentNode.scheduleStatus === SCHEDULE_STATUS.STOPPED) // 冻结状态

            if (!isWorkflowNode) {
                menu.addItem('查看实例', null, function() {
                    hashHistory.push(`/operation/offline-operation?job=${currentNode.name}`)
                })
            }
        }
    }

    initGraphLayout = () => {

        const graph = this.graph;
        const model = graph.getModel();

        const layout = new mxCompactTreeLayout(graph, false);
        layout.horizontal = false;
        layout.useBoundingBox = false;
        layout.edgeRouting = false;
        layout.levelDistance = 30;
        layout.nodeDistance = 10;
        layout.resizeParent = true;

        this.executeLayout = function (layoutNode, change, post) {
            model.beginUpdate();
            try {
                if (change != null) { change(); }
                layout.execute(layoutNode);
            } catch (e) {
                throw e;
            } finally {
                graph.getModel().endUpdate();
                if (post != null) { post(); }
            }
        }
    }

    initGraphEvent = () => {

        const ctx = this;
        const graph = this.graph;

        graph.addListener(mxEvent.onClick, function(sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {
                let data = cell.value;
                ctx.setState({ selectedTask: data })
            }
        })
    }

    refresh = () => {
        this.initGraph(this.props.tabData.id)
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
            const popMenus = document.querySelector('.mxPopupMenu')
            if (popMenus) {
                document.body.removeChild(popMenus)
            }
        })
    }

    /* eslint-enable */
    render() {
        const task = this.state.selectedTask
        const { goToTaskDev } = this.props

        return (
            <div className="graph-editor" 
                style={{
                    position: 'relative',
                    height: '100%',
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
                        <Icon type="reload" onClick={this.refresh} style={{color: '#333333'}} />
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
                    <span style={{marginLeft:"15px"}} >{ (task.createUser && task.createUser.userName) || '-' }</span>&nbsp;
                    发布于&nbsp;
                    <span>{utils.formatDateTime(task.gmtModified)}</span>&nbsp;
                    <a onClick={() => { goToTaskDev(task.id) }}>查看代码</a>
                </div>
            </div>
        )
    }

    getDefaultVertexStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = '#A7CDF0';
        // style[mxConstants.STYLE_ROUNDED] = true; // 设置radius
        style[mxConstants.STYLE_FILLCOLOR] = '#EDF6FF';
        // style[mxConstants.STYLE_GRADIENTCOLOR] = '#e9e9e9';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '14';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        return style;
    }

    getDefaultEdgeStyle() {
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
