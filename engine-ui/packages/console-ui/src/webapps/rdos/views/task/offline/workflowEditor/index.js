import React, { Component } from 'react';
import { connect } from 'react-redux';
import { debounce } from 'lodash';

import { 
    Tooltip, Icon, message,
    Button, Modal, Select, 
} from 'antd';

import utils from 'utils';
import KeyEventListener from 'widgets/keyCombiner/listener';
import KEY_CODE from 'widgets/keyCombiner/keyCode';

import MyIcon from '../../../../components/icon';
import { taskTypeText } from '../../../../components/display';
import LockPanel from '../../../../components/lockPanel';

import {
    workbenchActions,
} from '../../../../store/modules/offlineTask/offlineAction';
import { TASK_TYPE, MENU_TYPE, PROJECT_TYPE } from '../../../../comm/const';
import { isProjectCouldEdit } from '../../../../comm';

const Mx = require('public/rdos/mxgraph')({
    mxBasePath: 'public/rdos/mxgraph',
    mxImageBasePath: 'public/rdos/mxgraph/images',
    mxLanguage: 'none',
    mxLoadResources: false,
    mxLoadStylesheets: false,
});

const {
    mxGraph,
    mxShape,
    mxPoint,
    mxCell,
    mxText,
    mxGeometry,
    mxUtils,
    mxEvent,
    mxCellHighlight,
    mxPopupMenu,
    mxDragSource,
    mxPolyline,
    mxConstants,
    mxEdgeStyle,
    mxPerimeter,
    mxRubberband,
    mxUndoManager,
    mxGraphView,
    mxGraphHandler,
    mxEventObject,
    mxConstraintHandler,
    mxConnectionConstraint,
    mxHierarchicalLayout,
} = Mx;

const VertexSize = { // vertex大小
    width: 150,
    height: 40,
}

const BASE_COLOR = '#2491F7';
const WIDGETS_PREFIX = 'JS_WIDGETS_'; // Prefix for widgets
const Option = Select.Option;

const applyCellStyle = (cellState, style) => {
    if (cellState) {
        cellState.style = Object.assign(cellState.style, style);
        cellState.shape.apply(cellState);
        cellState.shape.redraw();
    }
}

const getTaskBaseData = (task) => {
    return {
        id: task.id,
        name: task.name,
        type: task.type,
        taskType: task.taskType,
        parentId: task.parentId,
        catalogueType: task.catalogueType,
        notSynced: task.notSynced,
        nodePid: task.nodePid,
        preSave: task.preSave,
        submitStatus: task.submitStatus,
        version: task.version,
        readWriteLockVO: task.readWriteLockVO,
    }
}

@connect(state => {
    const { offlineTask, project, user, editor } = state;
    const { workbench, workflow } = offlineTask;
    const { currentTab, tabs } = workbench;

    return {
        tabs,
        user,
        workflow,
        currentTab,
        editor,
        taskTypes: offlineTask.comm.taskTypes,
        project: project,
    }
}, workbenchActions )
class WorkflowEditor extends Component {

    state = {
        showSearch: false,
        showGuidePic: false,
    }

    componentDidMount() {
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.graph = null;
        const editor = this.Container;
        this._cacheCells = {};
        this._currentNewVertex = null;
        this.initEditor();
        this.loadEditor(editor);
        this.hideMenu();
        const workflowData = this.props.data.sqlText;
        const cells = workflowData ? JSON.parse(workflowData) : [];
        if (cells && cells.length > 0) {
            this.initGraphData(cells);
            this.listenGraphUpdate();
            this.initGraphView();
        } else {
            this.setState({ showGuidePic: true, });
        }
    }

    shouldComponentUpdate (nextProps, nextState) {
        if (nextState.showSearch !== this.state.showSearch) {
            return true;
        }
        if (nextState.showGuidePic !== this.state.showGuidePic) {
            return true;
        }
        if (nextState.searchResult !== this.state.searchResult) {
            return true;
        }
        return false;
    }

    componentWillReceiveProps(nextProps) {
        const old = this.props.workflow;
        const next = nextProps.workflow;

        if (old !== next) {
            if (this._currentNewVertex && next.status === 'cancel') {
                this.graph.removeCells();
                this._currentNewVertex = null;
            } else if (next.status === 'created') {
                this.appendWorkflowNode(next.node);
            }
        }
    }

    componentWillUnmount () {
        this.props.resetWorkflow();
    }

    loadEditor = (container) => {

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

        // Disable default context menu
        mxGraphView.prototype.optimizeVmlReflows = false;
        mxText.prototype.ignoreStringSize = true; //to avoid calling getBBox
        // 启用辅助线
        mxGraphHandler.prototype.guidesEnabled = true;
        mxEvent.disableContextMenu(container);

        const graph = new mxGraph(container)
        this.graph = graph
        // 启用绘制
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.keepEdgesInBackground = false;
        graph.allowLoops = false;
        // // Enable cell resize 
        graph.cellsResizable = false;
        graph.setPanning(true);
        graph.setConnectable(true);
        graph.setTooltips(true);
        // // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)
        // // 启用/禁止连接

        // 禁止Edge对象移动
        graph.isCellsMovable = function(cell) {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge)
        }
        // 禁止cell编辑
        graph.isCellEditable = function() {
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
        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip
        // enables rubberband
        new mxRubberband(graph);

        // Initial draggable elements
        this.listenConnection();
        this.initDraggableToolBar();
        this.initGraphLayout();
        this.initUndoManager();
        this.initContextMenu();
        this.initGraphEvent();
    }

    /**
     * 初始化视图
     */
    initGraphView = () => {
        const graph = this.graph;
        const { data } = this.props;
        if (data.graph) {
            const scale = data.graph.scale;
            const dx = data.graph.translate.x;
            const dy = data.graph.translate.y;
            graph.view.setScale(scale);
            graph.view.setTranslate(dx, dy);
        } else {
            this.layoutCenter();
        }
    }

    getStyles = () => {
        return 'whiteSpace=wrap;fillColor=#F5F5F5;strokeColor=#C5C5C5;'
    }

    formatTooltip = (cell) => {
        if (this.Container) {
            const task = cell.data || '';
            const tips = task ? `${task.name}${task.notSynced ? ' (未保存) ' : ''}` : '';
            return tips
        }
    }

    corvertValueToString = (cell) => {
        if (cell && cell.vertex) {
            const task = cell.data;
            return this.convertTaskToHTML(task);
        }
    }

    convertTaskToHTML = (task) => {
        if (task) {
            let unSave = task.notSynced ? '<span style="color:red;display: inline-block;vertical-align: middle;">*</span>' : '';
            const taskType = taskTypeText(task.taskType);
            return `<div class="vertex"><div class="vertex-title">${unSave} <span style="display: inline-block;max-width: 90%;">${task.name || ''}</span>
            <input class="vertex-input" data-id="${task.id}" id="JS_cell_${task.id}" value="${task.name || ''}" /></div>
            <span class="vertex-desc">${taskType}</span>
            </div>`
        }
        return '';
    }

    initEditTaskCell = (cell, task) => {
        const ctx = this;
        const editTarget = document.getElementById(`JS_cell_${task.id}`);
        const { saveTask, loadTreeNode } = this.props;

        const checkNodeName = function(name) {
            const reg = /^[A-Za-z0-9_]+$/;
            if (name === '') {
                message.error('子节点名称不可为空！')
                return false;
            } else if (name.length > 64) {
                message.error('子节点名称不得超过64个字符！')
                return false;
            } else if (!reg.test(name)) {
                message.error('子节点名称只能由字母、数字、下划线组成!')
                return false;
            }
            return true;
        }
   
        const editSucc = (evt) => {
            const originName = task.name;
            if ((evt.type === 'keypress' && event.keyCode === 13) || evt.type === 'blur') {
                editTarget.style.display = 'none';
                const value = utils.trim(editTarget.value);
                if (checkNodeName(value) && value !== originName) {
                    const taskData = Object.assign({}, getTaskBaseData(task), {
                        name: value,
                    });
                    saveTask(taskData, true).then(res => {
                        const fileStatus = res.data && res.data.readWriteLockVO && res.data.readWriteLockVO.result;
                        if ( res.code === 1 && fileStatus === 0 ) {
                            loadTreeNode(task.nodePid, MENU_TYPE.TASK_DEV);
                            ctx.updateCellData(cell, taskData);
                            ctx.updateGraphData();
                        }
                    });
                } 
                editTarget.removeEventListener('blur', editSucc, false);
                editTarget.removeEventListener('keypress', editSucc, false);
            }
        }

        if (editTarget) {
            editTarget.style.display = 'inline-block';
            editTarget.focus();
            editTarget.addEventListener("blur", editSucc, false);
            editTarget.addEventListener("keypress", editSucc, false)
        }
    }

    onkeyDown = (evt) => {
        const keyCode = evt.keyCode;
        switch(keyCode) {
            case KEY_CODE.BACKUP: {
                break;
            }
            default:
        }
    }

    toggleCreate = (taskType) => {
        const { toggleCreateTask, updateWorkflow, data } = this.props
        const workflow = {
            workflowId: data.id,
            data: data,
            taskType: taskType,
            status: 'create',
        }
        toggleCreateTask();
        updateWorkflow(workflow);
    }

    appendWorkflowNode = (newNode) => {
        const { data, saveTask, loadTreeNode } = this.props;
        this.updateCellData(this._currentNewVertex, newNode);
        const workflow = this.getGraphData();

        data.sqlText = JSON.stringify(workflow);
        saveTask(data, 'noMsg');
        
        loadTreeNode(data.id, MENU_TYPE.TASK_DEV, {
            taskType: TASK_TYPE.WORKFLOW,
            parentId: data.nodePid,
        });

        this.setState({
            showGuidePic: false,
        })
    }

    deleteTask = (cell) => {
        const { delOfflineTask, data } = this.props;
        const ctx = this;
        const taskData = cell.data;
        if (taskData && cell.vertex) {
            Modal.confirm({
                title: '注意',
                okText: '确认',
                cancelText: '取消',
                content: (
                    <div>
                        <p>确定是否要删除节点: <a>{taskData.name}</a></p>
                    </div>
                ),
                onOk() {
                    delOfflineTask({taskId: taskData.id }, data.id).then(res => {
                        if (res.code === 1 || res.code === 250) {
                            ctx.removeCell([cell]);
                        }
                    })
                },
            });
        } else {
            this.removeCell([cell]);
        }
    }

    updateCellData = (cell, cellData) => {
        if (cell) {
            const cellState = this.graph.view.getState(cell);
            if (cellState.cell) {
                cellState.cell.id = cellData.id;
                cellState.cell.data = getTaskBaseData(cellData);
                this.graph.refresh();
            }
        }
    }

    initGraphData = (cells) => {
        const { tabs, updateTabData, data } = this.props;

        const waitUpdateTabs = [];
        if (cells) {
            for (let i = 0; i < cells.length; i++) {
                const cell = cells[i];
                if (cell.vertex && cell.data) {
                    const item = tabs.find(i => i.id === cell.data.id)
                    if (item) {
                        cell.data = getTaskBaseData(item);
                        if (item.notSynced) {
                            waitUpdateTabs.push(item);
                        }
                    }
                }
            }
        }
        this.renderData(cells);

        // 如果节点有需要同步的数据, 则更新当前workflow的状态和待更数据字段
        updateTabData({
            id: data.id,
            toUpdateTasks: waitUpdateTabs,
            notSynced: waitUpdateTabs.length > 0 ? true : (data.notSynced || false)
        });
    }

    saveTask = (cell) => {
        const targetTask = cell.data || {};
        const { saveTask, tabs } = this.props;

        const task = tabs.find(item => {
            return item.id === targetTask.id;
        })

        if (task) {
            task.notSynced = false;
            saveTask(task).then(res => {
                if (res.code === 1) {
                    this.updateCellData(cell, task);
                    this.updateGraphData();
                }
            });
        } else {
            targetTask.notSynced = false;
            this.updateCellData(cell, targetTask);
            this.updateGraphData();
        }
    }

    insertItemVertex = (graph, evt, target, x, y) => {

        const taskType = this._currentSourceType.key;
        const newCell = new mxCell(
            '新节点',
            new mxGeometry(0, 0, VertexSize.width, VertexSize.height)
        );
        newCell.vertex = true;
        newCell.data = {
            taskType: taskType,
            name: '新节点',
        }

        const cells = graph.importCells([newCell], x, y, target);
        if (cells != null && cells.length > 0) {
            this.toggleCreate(taskType);
            this._currentNewVertex = cells[0];
            graph.scrollCellToVisible(cells[0]);
            graph.setSelectionCells(cells);
        }
    }

    getUnderMouseGraph = (evt) => {
        const x = mxEvent.getClientX(evt);
        const y = mxEvent.getClientY(evt);

        const elt = document.elementFromPoint(x, y);
        if (mxUtils.isAncestorNode(this.graph.container, elt)) {
            return this.graph;
        }
        return null;
    }

    removeCell(cells) {
        const ctx = this;
        // 获取选中的Cell
        const cell = cells || this.graph.getSelectionCells() // getSelectionCell
        if (cell && cell.length > 0) {
            ctx.graph.removeCells(cell)
        }
    }

    initUndoManager() {
        const undoManager = new mxUndoManager()
        const graph = this.graph
        this.undoMana = undoManager
        const listener = function(sender, evt) {
            undoManager.undoableEditHappened(evt.getProperty('edit'));
        }
        graph.getModel().addListener(mxEvent.UNDO, listener);
        graph.getView().addListener(mxEvent.UNDO, listener);
    }

    initGraphLayout = () => {
        const graph = this.graph;
        const edgeStyle = this.getDefaultEdgeStyle();
        const model = graph.getModel();
        const layout2 = new mxHierarchicalLayout(graph, 'north');
        layout2.disableEdgeStyle = false;
        layout2.interRankCellSpacing = 40;
        layout2.intraCellSpacing = 20;
        layout2.edgeStyle = edgeStyle;

        this.executeLayout = function (layoutTarget, change, post) {
            const parent = layoutTarget || graph.getDefaultParent();
            model.beginUpdate();
            try {
                console.log('layout:', layout2);
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

    initContextMenu = () => {
        const ctx = this;
        const graph = this.graph;

        const { openTaskInDev, data, project, user } = this.props;
        const couldEdit = isProjectCouldEdit(project, user);
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
        mxPopupMenu.prototype.showMenu = function() {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };

        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function(menu, cell, evt) {

            if (!cell) return;
            const currentNode = cell.data || {};
           
            const isLocked = data.readWriteLockVO && !data.readWriteLockVO.getLock;
            if (isLocked||!couldEdit) return;

            if (cell.vertex) {
                menu.addItem('保存节点', null, function() {
                    ctx.saveTask(cell);
                }, null, null, true) // 正常状态
                menu.addItem('编辑名称', null, function() {
                    ctx.initEditTaskCell(cell, currentNode);
                }, null, null, true) // 正常状态
    
                menu.addItem('查看节点内容', null, function() {
                    openTaskInDev(currentNode.id);
                }, null, null, true) // 正常状态
                menu.addItem('删除节点', null, function() {
                    ctx.deleteTask(cell);
                }, null, null, true) // 正常状态
            } else {
                menu.addItem('删除依赖关系', null, function() {
                    ctx.deleteTask(cell);
                }, null, null, true) // 正常状态
            }
        }
    }

    getPreviewEle = (taskType) => {
        const typeText = taskTypeText(taskType);
        const previewDragTarget = document.createElement('div');
        previewDragTarget.style.width = VertexSize.width + 'px';
        previewDragTarget.style.height = VertexSize.height + 'px';
        previewDragTarget.className = 'preview-drag-vertex';
        previewDragTarget.innerHTML = `<span class="preview-title">新节点</span>
        <span class="preview-desc">${typeText}</span>`;
        return previewDragTarget;
    }

    listenConnection() { // 仅仅限制有效的链接
        const graph = this.graph

        graph.isValidConnection = (source, target) => {

            // 限制，只能vertex可连接
            if (!source.vertex || !target.vertex) return false;

            // 限制连接线条数
            const edges = graph.getEdgesBetween(source, target);
            if (edges.length > 0) return false;

            // 限制循环依赖
            let isLoop = false;
            graph.traverse(target, true, function(vertex, edge) {
                if (source.id === vertex.id) {
                    isLoop = true;
                    return false;
                }
            });
            if (isLoop) return false;

            return true;
        }
    }

    initDraggableToolBar() {

        const ctx = this;
        const graph = this.graph;
        const { taskTypes } = this.props;
        
        for (let i = 0; i < taskTypes.length; i++ ) {
            const type = taskTypes[i];
            const dragTarget = document.getElementById(`${WIDGETS_PREFIX}${type.key}`);
            if (dragTarget) {
                const previewDragTarget = ctx.getPreviewEle(type.key);
                previewDragTarget.setAttribute('data-item', type.key)
                const draggabledEle = mxUtils.makeDraggable(
                    dragTarget,
                    this.getUnderMouseGraph,
                    this.insertItemVertex,
                    previewDragTarget,
                    null,
                    null,
                    graph.autoscroll,
                    true,
                );

                draggabledEle.createPreviewElement = function(graph, event) {
                    ctx._currentSourceType = type;
                    return previewDragTarget;
                }

                draggabledEle.isGuidesEnabled = () => {
                    return graph.graphHandler.guidesEnabled;
                };

                draggabledEle.createDragElement = mxDragSource.prototype.createDragElement;
            }
        }
    }

    updateGraphData = () => {
        const {
            updateTaskField,
         } = this.props;

        const workflow = this.getGraphData();
        const view = this.graph.getView();
        const graph = {
            translate: view.getTranslate(),
            scale: view.getScale(),
        }
        const toUpdateTasks = workflow.filter(item => {
            return item.vertex && item.data && item.data.notSynced === true;
        });
        this.setState({
            showGuidePic: workflow.length > 0 ? false : true,
        })
        updateTaskField({ sqlText: JSON.stringify(workflow), toUpdateTasks, graph });
    }

    initGraphEvent = () => {

        const graph = this.graph;
        let selectedCell = null;
        const { openTaskInDev, } = this.props;
        let highlightEdges = [];

        graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt) {
            const event = evt.getProperty('event');

            if (event.target.className.indexOf('vertex-input') > -1) {
                return;
            }

            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {
                const data = cell.data;
                openTaskInDev(data.id);
            }
        });

        graph.addListener(mxEvent.CLICK, function(sender, evt) {
            const cell = evt.getProperty('cell');
            const event = evt.getProperty('event');

            const activeElement = document.activeElement;
            // 当从编辑对象触发点击事件时，清除activeElement的焦点
            if (
                activeElement && activeElement.className.indexOf('vertex-input') > -1) {
                activeElement.blur();
            }

            if (cell && cell.vertex) {

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
                    const highlight = new mxCellHighlight(graph, '#2491F7', 1);
                    const state = graph.view.getState(edges[i]);
                    highlight.highlight(state);
                    highlightEdges.push(highlight);
                }
                selectedCell = cell;
            } else if (cell === undefined) {
                const cells = graph.getSelectionCells();
                graph.removeSelectionCells(cells);
            }
        });

        graph.clearSelection = function(evt) {
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

    listenGraphUpdate = () => {
        const graph = this.graph;
        graph.addListener(mxEvent.CELLS_MOVED, this.updateGraphData)
        graph.addListener(mxEvent.CELLS_REMOVED, this.updateGraphData)
        graph.addListener(mxEvent.CELL_CONNECTED, this.updateGraphData)
    }

    getGraphData = () => {
        const rootCell = this.graph.getDefaultParent();
        const cells = this.graph.getChildCells(rootCell);
        const cellData = [];
        const getCellData = (cell) => {
            return cell && {
                vertex: cell.vertex,
                edge: cell.edge,
                data: cell.data,
                x: cell.geometry.x,
                y: cell.geometry.y,
                value: cell.value,
                id: cell.id,
            }
        }
        for (let i = 0; i < cells.length; i++) {
            const cell = cells[i];
            const cellItem = getCellData(cell);
            if (cell.edge) {
                cellItem.source = getCellData(cell.source);
                cellItem.target = getCellData(cell.target);
            }
            cellData.push(cellItem);
        }
        return cellData;
    }

    renderData = (data) => {
        const graph = this.graph;
        const rootCell = this.graph.getDefaultParent();
        const cellMap = this._cacheCells;
        const cellStyle = this.getStyles();

        if (data) {
            for (let i = 0; i < data.length; i++) {
                const item = data[i];
                if (item.vertex) {
                    const cell = graph.insertVertex(
                        rootCell, 
                        item.id, 
                        null, 
                        item.x, item.y,
                        VertexSize.width, VertexSize.height, 
                        cellStyle,
                    )
                    cell.data = item.data;
                    cellMap[item.id] = cell;
                } else if (item.edge) {
                    const source = cellMap[item.source.id];
                    const target = cellMap[item.target.id];
                    graph.insertEdge(rootCell, item.id, '', source, target)
                }
            }
        }
    }

    onSearchChange = (searchText) => {
        if (searchText) {
            const rootCell = this.graph.getDefaultParent();
            const cells = this.graph.getChildCells(rootCell);
            const result = [];
            for (let i = 0; i < cells.length; i++) {
                const cell = cells[i];
                const data = cell.data;
                if (cell.vertex && data) {
                    if (data.name.indexOf(searchText) > -1) {
                        result.push({
                            id: data.id,
                            name: data.name,
                        })
                    }
                }
            }
            this.setState({
                searchResult: result,
            })
        }
    }

    debounceSearch = debounce(this.onSearchChange, 500, { 'maxWait': 2000 })

    initShowSearch = (e) => {
        this.setState({
            showSearch: true,
            searchText: '',
        }, () => {
            const selectEle = document.getElementById('JS_Search_Node');
            if (selectEle) {
                selectEle.focus();
                // fix autoComplete问题
                selectEle.setAttribute('autocomplete', 'off');
            }
        })
    }

    onSelectResult = (value, option) => {
        const id = option.props.data
        const cell = this._cacheCells[id];
        if (cell) {
            const mxe = new mxEventObject(mxEvent.CLICK, 'cell', cell);
            this.graph.fireEvent(mxe);
            this.setState({
                showSearch: false,
                searchText: '',
            })
        }
    }

    renderToolBar = () => {
        const { taskTypes, data, user, project } = this.props;
        const isPro = project.projectType == PROJECT_TYPE.PRO;
        const isRoot = user.isRoot;
        const couldEdit = !isPro || isRoot;

        const showTitle = (type, title) => {
            switch(type) {
                case TASK_TYPE.SQL:
                    return '以Spark作为计算引擎，兼容HiveSQL语法';
                case TASK_TYPE.MR:
                    return '基于Java、Scala的Spark节点任务';
                case TASK_TYPE.ML:
                    return '基于Spark MLLib的机器学习节点任务';
                case TASK_TYPE.DEEP_LEARNING:
                return '基于TensorFlow、MXNet的深度学习节点任务';
                default:
                    return title;
            }
        }
        const widgets = taskTypes.map(item => {
                return item.key !== TASK_TYPE.WORKFLOW && 
                <Tooltip 
                    placement="right" 
                    title={showTitle(item.key, item.value)}
                >
                    <Button
                        id={`${WIDGETS_PREFIX}${item.key}`}
                        className="widgets-items"
                        key={item.key}
                        value={item.key}>
                        {item.value}
                    </Button>
                </Tooltip>
            }
        )

        return (
            <div className="graph-widgets">
                <header className="widgets-header bd-bottom">
                    节点组件
                </header>
                <div className="widgets-content">
                    <LockPanel lockTarget={data} couldEdit={couldEdit}/>
                    { widgets }
                </div>
            </div>
        )
    }

    /* eslint-enable */
    render() {

        const { searchResult, showGuidePic } = this.state;
        const options = searchResult && searchResult.map(d => {
            return <Option key={d.id} data={d.id} value={d.name}>{d.name}</Option>
        })

        const { editor } = this.props;
        const themeDark = editor.options.theme !== 'vs' ? true : undefined;

        return (
            <KeyEventListener 
                onKeyDown={this.onkeyDown} 
            >
                <div className="graph-editor" 
                    style={{ 
                        position: 'relative',
                        overflow: 'hidden',
                    }}
                >
                    { this.renderToolBar() }
                    { showGuidePic ? <div 
                        className="absolute-middle" 
                        style={{ 
                            width: '100%', height: '100%', 
                            background: 'url(/public/rdos/img/graph_guide_pic.png)',
                            backgroundPosition: 'center center',
                            backgroundSize: '700px 500px',
                            backgroundRepeat: 'no-repeat',
                        }} /> : null
                    }
                    <div className="editor pointer graph-bg" 
                        style={{
                            height: '100%',
                        }}
                        ref={(e) => { this.Container = e }} 
                    />
                    <div className="graph-toolbar">
                        <Tooltip placement="bottom" title="布局">
                            <MyIcon type="flowchart" onClick={this.layout} themeDark={themeDark}/>
                        </Tooltip>
                        <Tooltip placement="bottom" title="放大">
                            <MyIcon onClick={this.zoomIn} type="zoom-in" themeDark={themeDark}/>
                        </Tooltip>
                        <Tooltip placement="bottom" title="缩小">
                            <MyIcon onClick={this.zoomOut} type="zoom-out" themeDark={themeDark}/>
                        </Tooltip>
                        <Tooltip placement="bottom" title="搜索节点">
                            <Icon 
                                type="search" 
                                onClick={this.initShowSearch} 
                                style={{fontSize: '17px', color: themeDark ? '#ADADAD' :'#333333'}}
                            />
                        </Tooltip>
                    </div>
                    <Modal 
                        closable={false}
                        mask={false}
                        style={{
                            width: '400px',
                            height: '80px',
                            top: '150px',
                            left: '100px',
                        }}
                        bodyStyle={{
                            padding: '10px',
                        }}
                        visible={this.state.showSearch}
                        onCancel={() => this.setState({showSearch: false})}
                        footer={null}
                    >
                         <Select
                            id="JS_Search_Node"
                            mode="combobox"
                            showSearch
                            style={{width: '100%'}}
                            placeholder="按子节点名称搜索"
                            notFoundContent="没有发现相关节点"
                            defaultActiveFirstOption={false}
                            showArrow={false}
                            filterOption={false}
                            autoComplete="off"
                            onChange={this.debounceSearch}
                            onSelect={this.onSelectResult}
                        >
                            {options}
                        </Select>
                    </Modal>
                </div>
            </KeyEventListener>
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

    layoutCenter = () => {
        this.graph.center(true, true, 0.55, 0.4);
    }

    layout = () => {
        this.executeLayout(null, null, () => {
            this.layoutCenter();
            this.updateGraphData();
        });
    }

    undo = () => { // 撤销上一步
        this.undoMana.undo()
    }

    getDefaultVertexStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = '#C5C5C5';
        style[mxConstants.STYLE_FILLCOLOR] = '#F5F5F5';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        return style;
    }

    getDefaultEdgeStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#999';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
        style[mxConstants.STYLE_FONTSIZE] = '10';
        style[mxConstants.STYLE_ROUNDED] = false;
        return style
    }

    /* eslint-disable */
    initEditor() {
        // Overridden to define per-shape connection points
        mxGraph.prototype.getAllConnectionConstraints = function (terminal, source) {
            if (terminal != null && terminal.shape != null) {
                if (terminal.shape.stencil != null) {
                    if (terminal.shape.stencil != null) {
                        return terminal.shape.stencil.constraints;
                    }
                }
                else if (terminal.shape.constraints != null) {
                    return terminal.shape.constraints;
                }
            }
            return null;
        };

        // Defines the default constraints for all shapes
        mxShape.prototype.constraints = [
            new mxConnectionConstraint(new mxPoint(0.5, 0), true),
            new mxConnectionConstraint(new mxPoint(0, 0.5), true),
            new mxConnectionConstraint(new mxPoint(1, 0.5), true),
            new mxConnectionConstraint(new mxPoint(0.5, 1), true),
        ];
        // // Edges have no connection points
        mxPolyline.prototype.constraints = null;
    }
}

export default WorkflowEditor;