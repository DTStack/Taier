import React, { Component } from 'react'
import { cloneDeep } from 'lodash'
import moment from 'moment';
import {
    Tooltip, Spin,
    Modal, message, Icon
} from 'antd'

import utils from 'utils'

import { TaskInfo } from './taskInfo'
import { LogInfo } from '../taskLog'
import RestartModal from './restartModal'

import Api from '../../../../api'
import MyIcon from '../../../../components/icon'
import { getVertxtStyle } from '../../../../comm'
import { TASK_STATUS, TASK_TYPE } from '../../../../comm/const'
import { taskTypeText, taskStatusText } from '../../../../components/display'
import {
    getGeoByRelativeNode, getNodeWidth,
    getNodeHeight, getNodeLevelAndCount
} from 'utils/layout';

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
    mxGraphView,
    mxGraphHandler,
    mxRectangle,
    mxCellHighlight,
    mxPoint,
    mxUtils,
    mxText
} = Mx

const VertexSize = { // vertex大小
    width: 150,
    height: 40
}

const defaultGeo = { // 默认几何对象;
    count: 1,
    index: 1,
    level: 0,
    x: 10,
    y: 10,
    width: VertexSize.width,
    height: VertexSize.height,
    margin: 50
}

const replacTreeNodeField = (treeNode, sourceField, targetField, arrField) => {
    if (treeNode) {
        treeNode[targetField] = cloneDeep(treeNode[sourceField]);
        treeNode[sourceField] = undefined;
    }
    const children = treeNode[arrField];
    if (children) {
        for (let i = 0; i < children.length; i++) {
            replacTreeNodeField(children[i], sourceField, targetField, arrField);
        }
    }
}

const applyCellStyle = (cellState, style) => {
    if (cellState) {
        cellState.style = Object.assign(cellState.style, style);
        cellState.shape.apply(cellState);
        cellState.shape.redraw();
    }
}

/**
 * 合并Tree数据
 * @param {*} origin
 * @param {*} target
 */
const mergeTreeNodes = (treeNodeData, mergeSource) => {
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

/* eslint-disable */
class TaskFlowView extends Component {
    state = {
        selectedJob: '', // 选中的Job
        data: {}, // 数据
        loading: 'success',
        lastVertex: '',
        taskLog: {},
        logVisible: false,
        visible: false,
        visibleRestart: false,
        frontPeriodsList: [], // 前周期返回数据
        nextPeriodsList: []
    }

    _view = null; // 存储view信息

    initGraph = (id) => {
        this.Container.innerHTML = ''; // 清理容器内的Dom元素
        this.graph = '';
        this._vertexCells = {}; // 缓存创建的节点
        this._originData = '';

        const editor = this.Container
        this.loadEditor(editor)
        this.hideMenu();
        this.loadTaskChidren({
            jobId: id
        })
    }

    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const currentJob = this.props.taskJob
        const { taskJob, visibleSlidePane } = nextProps
        if (taskJob && visibleSlidePane && (!currentJob || taskJob.id !== currentJob.id)) {
            this.initGraph(taskJob.id);
            this._view = null;
        }
    }

    componentWillUnmount() {
        
    }

    loadTaskChidren = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        Api.getJobChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ selectedJob: data, data })
                ctx.doInsertVertex(res.data)
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadTaskParent = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        Api.getJobParents(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ data, selectedJob: data })
                // 替换 jobVos 字段为 parentNodes
                replacTreeNodeField(res.data, 'jobVOS', 'parentNodes', 'parentNodes')
                ctx.doInsertVertex(res.data);
            }
            ctx.setState({ loading: 'success' })
        })
    }

    /**
     * 加载前后周期数据
     * @param menu 子菜单
     * @param params 请求参数
     * @param periodsType 前后周期类型
     */
    loadPeriodsData = (menu, params, periodsType) => {
        const ctx = this;
        const isNext = params.isAfter;
        Api.getOfflineTaskPeriods(params).then(res => {
            if (res.code === 1) {
                !isNext ? ctx.setState({
                    frontPeriodsList: res.data
                }, () => {
                    ctx.state.frontPeriodsList.map(item => {
                        const times = moment(item.cycTime).format('YYYY-MM-DD HH:mm:ss');
                        const statusText = taskStatusText(item.status);
                        return (
                            menu.addItem(`${times} (${statusText})`, null, function () {
                                ctx.loadTaskChidren({ jobId: item.jobId })
                            }, periodsType)
                        )
                    })
                }) :
                ctx.setState({
                    nextPeriodsList: res.data
                }, () => {
                    ctx.state.nextPeriodsList.map(item => {
                        const times = moment(item.cycTime).format('YYYY-MM-DD HH:mm:ss');
                        const statusText = taskStatusText(item.status);
                        return (
                            menu.addItem(`${times} (${statusText})`, null, function () {
                                ctx.loadTaskChidren({ jobId: item.jobId })
                            }, periodsType)
                        )
                    })
                })
            }
        })
    }

    loadWorkflowNodes = async (workflow, collapsed) => {
        const ctx = this;
        this.setState({ loading: 'loading' })

        // 如果折叠状态，加载
        if (!collapsed) {
            const res = await Api.getWorkflowNodes({ jobId: workflow.id });
            if (res.code === 1) {
                if (res.data) {
                    ctx.doInsertVertex(res.data);
                } else {
                    message.warning('当前工作流没有子节点！');
                }
            }
        } else {
            const data = Object.assign({}, workflow, { subNodes : null });
            ctx.doInsertVertex(data);
        }
        ctx.setState({ loading: 'success' });
    }

    loadEditor = (container) => {
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
        graph.isCellsMovable = function (cell) {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge);
        }
        // 禁止cell编辑
        graph.isCellEditable = function () {
            return false;
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
                cell = graph.getModel().getParent(cell);
                return cell;
            }
            mxGraph.prototype.selectCellForEvent.apply(this, arguments);
        };

        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle)

        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip
        // 转换value显示的内容
        // graph.convertValueToString = this.corvertValueToString

        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';
        mxConstants.STYLE_OVERFLOW = 'hidden';

        // Init container scroll
        this.initContainerScroll(graph);
        // enables rubberband
        new mxRubberband(graph);

        this.graph = graph;
        this.initContextMenu(graph);
        this.initGraphEvent();
    }

    formatTooltip = (cell) => {
        if (cell.vertex) {
            const currentNode = cell.data; // this._vertexCells[cell.id].data;
            return currentNode.batchTask.name;
        }
    }

    /**
     * 该方法渲染cell内容为纯字符串，
     * 而非HTML渲染，可以提高绘制效率
     */
    getShowStr = (data) => {
        const task = data.batchTask;
        if (!task) return '';
        const taskType = taskTypeText(task.taskType);
        const taskStatus = taskStatusText(data.status);
        const taskName = task.name.length > 12 ? `${task.name.substring(0, 10)}...` : task.name;
        const str = `${taskName || ''} \n ${taskType}(${taskStatus})`;
        return str;
    }

    corvertValueToString = (cell) => {
        if (cell.vertex && cell.value) {
            const task = cell.value.batchTask || {};
            const taskType = taskTypeText(task.taskType);
            const taskStatus = taskStatusText(cell.value.status);
            const isDelete = task.isDeleted === 1 ? '（已删除）' : ''; // 已删除

            if (task) {
                return `<div class="vertex"><span class="vertex-title" title="${task.name || ''}">${task.name || ''}${isDelete}</span>
                <span class="vertex-desc">${taskType}(${taskStatus})</span>
                </div>`
            }
        }
        return '';
    }

    preHandGraphTree = (data) => {
        const relationTree = [];
        let level = 0;

        const loop = (treeNodeData, parent, level, parentNode) => {
            if (treeNodeData) {
                const childNodes = treeNodeData.jobVOS; // 子节点
                const parentNodes = treeNodeData.parentNodes; // 父节点

                let currentNodeGeo;

                if (!treeNodeData._geometry) {
                    currentNodeGeo = Object.assign({}, defaultGeo);
                    currentNodeGeo.level = level;
                    currentNodeGeo.y = parentNode.y + defaultGeo.margin;
                } else {
                    currentNodeGeo = treeNodeData._geometry;
                }

                if (treeNodeData.batchTask.taskType === TASK_TYPE.WORKFLOW) {
                    const workflowData = treeNodeData.subNodes;

                    if (workflowData) {
                        let newLevel = 0;
    
                        // 如果是工作流，需要重新计算工作流节点的高和宽
                        const nodeCount = getNodeLevelAndCount(workflowData, 'jobVOS');
                        const tempNode = Object.assign({}, defaultGeo);
                        tempNode.count = nodeCount.count;
                        tempNode.level = nodeCount.level;
                        currentNodeGeo.width = getNodeWidth(tempNode);
                        currentNodeGeo.height = getNodeHeight(tempNode);
    
                        const workflowDefaultRoot = Object.assign({}, defaultGeo);
                        workflowDefaultRoot.x = Math.round((currentNodeGeo.width - VertexSize.width) / 2);
                        workflowDefaultRoot.y = workflowDefaultRoot.height + 25;
                        loop(workflowData, treeNodeData, newLevel, workflowDefaultRoot);
                    }
                }

                currentNodeGeo = getGeoByRelativeNode(parentNode, currentNodeGeo);
                treeNodeData._geometry = currentNodeGeo;

                relationTree.push({
                    parent: parent,
                    source: treeNodeData
                });

                // 处理父亲依赖节点
                if (parentNodes) {
                    for (let i = 0; i < parentNodes.length; i++) {
                        const nodeData = parentNodes[i];
                        const geo = Object.assign({}, defaultGeo);
                        geo.level = level - 1;
                        geo.index = i + 1;
                        geo.count = nodeData.parentNodes && nodeData.parentNodes.length > parentNodes.length
                        ? nodeData.parentNodes.length : parentNodes.length;

                        const existNode = relationTree.find( o => {
                            // return eq ? obj : null;
                            return (o.source && o.source.level === geo.level && o.source.id === nodeData.id) ||
                            (o.target && o.target.level === geo.level && o.target.id === nodeData.id) && o._geometry;
                        })
                        if (existNode) {
                            nodeData._geometry = existNode._geometry;
                        } else {
                            nodeData._geometry = getGeoByRelativeNode(currentNodeGeo, geo);
                        }
                        console.log('parentNodes geo:', nodeData.batchTask.name, nodeData._geometry);

                        relationTree.push({
                            parent: parent,
                            source: nodeData,
                            target: treeNodeData
                        });

                        loop(nodeData, parent, level - 1, currentNodeGeo)
                    }
                }

                if (childNodes) {
                    // 处理被依赖节点
                    for (let i = 0; i < childNodes.length; i++) {
                        const nodeData = childNodes[i];
                        if (!nodeData) continue;

                        const geo = Object.assign({}, defaultGeo);
                        geo.level = level + 1;
                        geo.index = i + 1;
                        geo.count = nodeData.jobVOS && nodeData.jobVOS.lenght > childNodes.length
                        ? nodeData.jobVOS.lenght : childNodes.length;

                        const existNode = relationTree.find( o => {
                            return (o.source && o.source.level === geo.level && o.source.id === nodeData.id) ||
                            (o.target && o.target.level === geo.level && o.target.id === nodeData.id) && o._geometry;
                        })
                        if (existNode) {
                            nodeData._geometry = existNode._geometry;
                        } else {
                            nodeData._geometry = getGeoByRelativeNode(currentNodeGeo, geo);
                        }
                        console.log('childNodes geo:', nodeData.batchTask.name, nodeData._geometry);

                        relationTree.push({
                            parent: parent,
                            source: treeNodeData,
                            target: nodeData
                        });

                        loop(nodeData, parent, ++level, currentNodeGeo)
                    }
                }
            }
        }

        loop(data, null, level, defaultGeo);

        return relationTree;
    }

    renderGraph = (dataArr) => {
        const cellCache = this._vertexCells;
        const graph = this.graph;
        const defaultParent = graph.getDefaultParent();

        const getVertex = (parentCell, data) => {
            if (!data) return null;

            let style = getVertxtStyle(data.status);
            const showText = this.getShowStr(data);

            const isWorkflow = data.batchTask.taskType === TASK_TYPE.WORKFLOW;
            const isWorkflowNode = data.batchTask.flowId && data.batchTask.flowId;
            const noWorkflowSubNodes = !data.subNodes || data.subNodes.lenght === 0;
            if (isWorkflow) {
                style += 'shape=swimlane;swimlaneFillColor=#F7FBFF;fillColor=#D0E8FF;strokeColor=#92C2EF;dashed=1;color:#333333;';
            }

            if (isWorkflowNode && parentCell !== defaultParent) {
                style += 'rounded=1;arcSize=60;'
            }

            const geo = data._geometry || {
                x: 10,
                y: 10,
                width: VertexSize.width,
                height: VertexSize.height
            };

            const cell = graph.insertVertex(
                isWorkflow ? null : parentCell,
                data.id,
                showText,
                geo.x, geo.y,
                geo.width, geo.height,
                style
            )
            if (isWorkflow) {
                if (noWorkflowSubNodes) {
                    cell.collapsed = true;
                    // temp node
                    graph.insertVertex(
                        cell,
                        null,
                        '',
                        0, 50,
                        VertexSize.width, VertexSize.height, // geo.width, geo.height,
                        style
                    )
                }
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
                const isWorkflowNode = source && source.batchTask.flowId && source.batchTask.flowId;

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
                    graph.insertEdge(defaultParent, null, '', sourceCell, targetCell, edgeStyle)
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
        this.initView();
    }

    initContextMenu = (graph) => {
        const ctx = this;
        const { isPro } = ctx.props;
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
        mxPopupMenu.prototype.showMenu = function () {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0 && cells[0].vertex) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };
        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function (menu, cell, evt) {
            if (!cell) return;

            const currentNode = cell.data;
            console.log(currentNode)
            const isWorkflowNode = currentNode.batchTask && currentNode.batchTask.flowId && currentNode.batchTask.flowId !== 0;
            const taskId = currentNode.batchTask && currentNode.batchTask.id;
            const isDelete = currentNode.batchTask && currentNode.batchTask.isDeleted === 1; // 已删除
            if (isDelete) return;
            if (!isWorkflowNode) {
                menu.addItem('展开上游（6层）', null, function () {
                    ctx.loadTaskParent({
                        jobId: currentNode.id,
                        level: 6
                    })
                })
                menu.addItem('展开下游（6层）', null, function () {
                    ctx.loadTaskChidren({
                        jobId: currentNode.id,
                        level: 6
                    })
                })
            }
            menu.addItem('查看任务日志', null, function () {
                ctx.showJobLog(currentNode.jobId)
            })
            menu.addItem('查看任务属性', null, function () {
                ctx.setState({ visible: true })
            })
            const frontPeriods = menu.addItem('转到前一周期实例', null, null);
            const frontParams = {
                jobId: currentNode.id,
                isAfter: false,
                limit: 6
            }
            ctx.loadPeriodsData(menu, frontParams, frontPeriods)
            const nextPeriods = menu.addItem('转到下一周期实例', null, null);
            const nextParams = {
                jobId: currentNode.id,
                isAfter: true,
                limit: 6
            }
            ctx.loadPeriodsData(menu, nextParams, nextPeriods)
            menu.addItem(`${isPro ? '查看' : '修改'}任务`, null, function () {
                ctx.props.goToTaskDev(taskId)
            })
            menu.addItem('终止', null, function () {
                ctx.stopTask({
                    jobId: currentNode.id
                })
            }, null, null,
            // 显示终止操作
            currentNode.status === TASK_STATUS.RUNNING || // 运行中
                currentNode.status === TASK_STATUS.RESTARTING || // 重启中
                currentNode.status === TASK_STATUS.WAIT_SUBMIT || // 等待提交
                currentNode.status === TASK_STATUS.WAIT_RUN
            )

            menu.addItem('刷新任务实例', null, function () {
                ctx.resetGraph(cell)
            })

            menu.addItem('置成功并恢复调度', null, function () {
                ctx.restartAndResume({
                    jobId: currentNode.id,
                    justRunChild: true, // 只跑子节点
                    setSuccess: true
                }, '置成功并恢复调度')
            }, null, null,
            // （运行失败、提交失败）重跑并恢复调度
            currentNode.status === TASK_STATUS.RUN_FAILED ||
                currentNode.status === TASK_STATUS.STOPED ||
                currentNode.status === TASK_STATUS.SUBMIT_FAILED)

            menu.addItem('重跑并恢复调度', null, function () {
                ctx.setState({ visibleRestart: true })
            })
        }
    }

    stopTask = (params) => {
        Api.stopJob(params).then(res => {
            if (res.code === 1) {
                message.success('任务终止运行命令已提交！')
            }
            this.refresh()
        })
    }

    restartAndResume = (params, msg) => { // 重跑并恢复任务
        const { reload } = this.props
        Api.restartAndResume(params).then(res => {
            if (res.code === 1) {
                message.success(`${msg}命令已提交!`)
                if (reload) reload();
            } else {
                message.error(`${msg}提交失败！`)
            }
            this.refresh()
        })
    }

    initGraphEvent = () => {
        const ctx = this;
        const graph = this.graph;
        let highlightEdges = [];
        let selectedCell = null;

        if (graph) {

            // Double event
            graph.addListener(mxEvent.DOUBLE_CLICK, function (sender, evt) {
                const cell = evt.getProperty('cell')
                if (cell && cell.vertex) {
                    const currentNode = cell.data;
                    ctx.showJobLog(currentNode.jobId)
                }
            })
            graph.addListener(mxEvent.CLICK, function (sender, evt) {
                const cell = evt.getProperty('cell');

                const activeElement = document.activeElement;
                // 当从编辑对象触发点击事件时，清除activeElement的焦点
                if (
                    activeElement && activeElement.className.indexOf('vertex-input') > -1) {
                    activeElement.blur();
                }
                if (cell && cell.vertex) {
                    const currentNode = cell.data;
                    ctx.setState({ selectedJob: currentNode });

                    graph.clearSelection();
                    const cellState = graph.view.getState(cell);
                    const style = {}
                    style[mxConstants.STYLE_FILLCOLOR] = '#DEEFFF';
                    style[mxConstants.STYLE_STROKECOLOR] = '#2491F7';
                    applyCellStyle(cellState, style);
    
                    const outEdges = graph.getOutgoingEdges(cell);
                    const inEdges = graph.getIncomingEdges(cell);
                    const edges = outEdges.concat(inEdges);
                    for (let i = 0; i < edges.length; i++) {
                        const highlight = new mxCellHighlight(graph, '#2491F7', 2);
                        const state = graph.view.getState(edges[i]);
                        highlight.highlight(state);
                        highlightEdges.push(highlight);
                    }
                    selectedCell = cell;
                } else if (cell === undefined) {
                    const cells = graph.getSelectionCells();
                    graph.removeSelectionCells(cells);
                }
            })

            graph.clearSelection = function (evt) {
                if (selectedCell) {
                    const cellState = graph.view.getState(selectedCell);
                    const style = {}
                    style[mxConstants.STYLE_FILLCOLOR] = '#F5F5F5';
                    style[mxConstants.STYLE_STROKECOLOR] = '#C5C5C5';
                    applyCellStyle(cellState, style);
    
                    for (let i = 0; i < highlightEdges.length; i++) {
                        highlightEdges[i].hide();
                    }
                    selectedCell = null;
                }
            };
        }
    }

    resetGraph = () => {
        const { taskJob } = this.props
        if (taskJob) {
            this.loadTaskChidren({
                jobId: taskJob.id,
                level: 6
            })
        }
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

    initView = () => {
        const view = this._view;
        const graph = this.graph;
        if (view) {
            const scale = view.scale;
            const dx = view.translate.x;
            const dy = view.translate.y;
            graph.view.setScale(scale);
            graph.view.setTranslate(dx, dy);
            // graph.container.scrollTop = dy;
            // graph.container.scrollLeft = dx;
        } else {
            graph.center();
        }
        // Sets initial scrollbar positions
        // window.setTimeout(function() {
        //     var bounds = graph.getGraphBounds();
        //     var width = Math.max(bounds.width, graph.scrollTileSize.width * graph.view.scale);
        //     var height = Math.max(bounds.height, graph.scrollTileSize.height * graph.view.scale);
        //     graph.container.scrollTop = Math.floor(Math.max(0, bounds.y - Math.max(20, (graph.container.clientHeight - height) / 4)));
        //     graph.container.scrollLeft = Math.floor(Math.max(0, bounds.x - Math.max(0, (graph.container.clientWidth - width) / 2)));
        // }, 0);
    }

    showJobLog = (jobId) => {
        Api.getOfflineTaskLog({ jobId: jobId }).then((res) => {
            if (res.code === 1) {
                this.setState({ taskLog: res.data, logVisible: true, taskLogId: jobId })
            }
        })
    }

    graphEnable () {
        const status = this.graph.isEnabled()
        this.graph.setEnabled(!status)
    }

    refresh = () => {
        this.saveViewInfo();
        setTimeout(() => {
            this.initGraph(this.props.taskJob.id);
        }, 0);
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

    render () {
        const { selectedJob, taskLog } = this.state;
        const { goToTaskDev, project, taskJob, isPro } = this.props;
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
                    <div
                        className="editor pointer"
                        style={{
                            position: 'relative',
                            overflow: 'auto',
                            width: '100%',
                            height: 'calc(100% - 35px)'
                        }}
                        ref={(e) => { this.Container = e }}
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
                        bottom: 0
                    }}
                >
                    <span>{(taskJob && taskJob.batchTask && taskJob.batchTask.name) || '-'}</span>
                    <span style={{ marginLeft: '15px' }}>{(taskJob && taskJob.batchTask && taskJob.batchTask.createUser && taskJob.batchTask.createUser.userName) || '-'}</span>&nbsp;
                    {isPro ? '发布' : '提交'}于&nbsp;
                    <span>{taskJob && taskJob.batchTask && utils.formatDateTime(taskJob.batchTask.gmtModified)}</span>&nbsp;
                    <a title="双击任务可快速查看日志" onClick={() => { this.showJobLog(taskJob && taskJob.jobId) }} style={{ marginRight: '8' }}>查看日志</a>
                    <a onClick={() => { goToTaskDev(taskJob && taskJob.batchTask.id) }}>查看代码</a>
                </div>
                <Modal
                    title="查看属性"
                    width="60%"
                    wrapClassName="vertical-center-modal"
                    visible={this.state.visible}
                    onCancel={() => { this.setState({ visible: false }) }}
                    footer={null}
                >
                    <TaskInfo task={selectedJob} project={project} />
                </Modal>
                <Modal
                    key={taskJob && taskJob.id}
                    width={800}
                    title={(
                        <span>
                            任务日志
                            <Tooltip placement="right" title="刷新">
                                <Icon style={{ cursor: 'pointer', marginLeft: '5px' }} onClick={() => { this.showJobLog(this.state.taskLogId) }} type="reload" />
                            </Tooltip>
                        </span>
                    )}
                    wrapClassName="vertical-center-modal m-log-modal"
                    visible={this.state.logVisible}
                    onCancel={() => { this.setState({ logVisible: false }) }}
                    footer={null}
                    maskClosable={false}
                >
                    <LogInfo
                        log={taskLog.logInfo}
                        syncJobInfo={taskLog.syncJobInfo}
                        downloadLog={taskLog.downloadLog}
                        height="520px"
                    />
                </Modal>
                <RestartModal
                    restartNode={selectedJob}
                    visible={this.state.visibleRestart}
                    onCancel={() => {
                        this.setState({ visibleRestart: false })
                    }}
                />
            </div>
        )
    }

    initContainerScroll (graph) {
        /**
         * Specifies the size of the size for "tiles" to be used for a graph with
         * scrollbars but no visible background page. A good value is large
         * enough to reduce the number of repaints that is caused for auto-
         * translation, which depends on this value, and small enough to give
         * a small empty buffer around the graph. Default is 400x400.
         */
        graph.scrollTileSize = new mxRectangle(0, 0, 400, 400);

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
        let style = [];
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
        style[mxConstants.STYLE_OVERFLOW] = 'hidden';

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
export default TaskFlowView;
