/* eslint-disable no-unreachable */
import React from 'react';
import { connect } from 'react-redux';
import { debounce, cloneDeep } from 'lodash';
import { message } from 'antd';
import { bindActionCreators } from 'redux';
import utils from 'utils';
import Mx from 'widgets/mxGraph';
import SearchModal from 'widgets/searchModal';

import api from '../../../../../api/experiment';
import GraphEditor from './graphEditor';
import * as experimentActions from '../../../../../actions/experimentActions';
import * as componentActions from '../../../../../actions/componentActions';
import { COMPONENT_TYPE, VertexSize, CONSTRAINT_TEXT } from '../../../../../consts';
import ReqUrls from '../../../../../consts/reqUrls';
import ModelDetailModal from './detailModal';
import RunningLogModal from './runningLog';
import EvaluateReportModal from './evaluateReport';
import DataExploringModal from './evaluateReport/dataExploring';

// const WIDGETS_PREFIX = 'JS_WIDGETS_'; // Prefix for widgets

const SEARCH_MODAL_ID = 'JS_Search_MODAL';

const {
    mxEvent,
    mxPopupMenu,
    mxConstants,
    mxEventObject,
    mxCell,
    mxGeometry,
    mxUtils,
    mxClient
} = Mx;

const applyCellStyle = (cellState, style) => {
    if (cellState) {
        cellState.style = Object.assign(cellState.style, style);
        cellState.shape.apply(cellState);
        cellState.shape.redraw();
    }
}

/* eslint new-cap: ["error", { "newIsCap": false }] */
@connect(state => {
    const { project, user, editor } = state;
    return {
        user,
        editor,
        runTasks: state.component.taskLists, // 即将执行的任务列表
        project: project,
        graph: state.component.graph,
        selectedCell: state.component.selectedCell
    }
}, (dispatch) => {
    return bindActionCreators({ ...experimentActions, ...componentActions }, dispatch);
})
class GraphContainer extends React.Component {
    state = {
        showSearch: false,
        searchResult: null,
        searchText: null,
        detailModalVisible: false,
        detailData: null,
        selectedData: null,
        selectedOutputData: null,
        evaluateReportVisible: false,
        runningLogVisible: false,
        outputDataVisible: false
    }

    _graph = null;

    componentDidMount () {
        console.log('graph did mount', this.props);
    }

    shouldComponentUpdate (nextProps) {
        console.log('graph should update:', nextProps);
        return true;
    }

    initOutputMenuItems = (menu, data) => {
        const ctx = this;
        const menuItemArr = data.outputTypeList || [];
        // 逻辑回归没有【查看数据】功能
        if (data.componentType === COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION) {
            return false;
        }
        if (menuItemArr.length === 1) {
            menu.addItem('查看数据', null, function () {
                data.inputType = menuItemArr[0] || 0;
                ctx.showHideOutputData(true, data)
            }, null, null, true);
        } else if (menuItemArr.length > 1) {
            const parentMenuItem = menu.addItem('查看数据');
            let i = 0;
            while (i < menuItemArr.length) {
                const item = menuItemArr[i];
                const text = CONSTRAINT_TEXT[data.componentType].output.find(o => o.key == item);
                menu.addItem(`${text ? text.value : ('查看数据输出' + i)}`, null, function () {
                    const selectedTarget = { inputType: item, ...data };
                    ctx.showHideOutputData(true, selectedTarget)
                }, parentMenuItem, null, true);
                i++;
            }
        }
    }

    initContextMenu = (graph) => {
        const ctx = this;
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
        mxPopupMenu.prototype.showMenu = function () {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };
        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function (menu, cell, evt) {
            if (!cell) return;
            const currentNode = cell.data || {};

            if (cell.vertex) {
                menu.addItem('重命名', null, function () {
                    ctx.initEditTaskCell(cell, currentNode);
                }, null, null, true);
                menu.addItem('删除', null, function () {
                    ctx.removeCell(cell);
                }, null, null, true);
                menu.addItem('复制', null, function () {
                    ctx.copyCell(cell);
                }, null, null, true);
                menu.addSeparator();
                menu.addItem('从此处开始执行', null, function () {
                    ctx.startHandlerFromHere(cell);
                }, null, null, !ctx.props.isRunning);
                menu.addItem('执行到此节点', null, function () {
                    ctx.handlerToHere(cell)
                }, null, null, !ctx.props.isRunning);
                menu.addItem('执行此节点', null, function () {
                    ctx.handlerThisCell(cell);
                }, null, null, !ctx.props.isRunning);
                menu.addSeparator();
                if (currentNode.componentType === COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION) {
                    const menuModal = menu.addItem('模型选项', null, function () {
                    }, null, null, true);
                    menu.addItem('导出PMML', null, function () {
                        ctx.handleExportPMML(cell);
                    }, menuModal, null, true);
                    menu.addItem('模型描述', null, function () {
                        ctx.handleOpenDescription(cell);
                    }, menuModal, null, true);
                }
                // 初始化输出数据菜单项
                ctx.initOutputMenuItems(menu, currentNode);
                // 查看评估报告
                if (currentNode.componentType === COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION) {
                    menu.addItem('查看评估报告', null, function () {
                        ctx.showHideEvaluateReport(true, currentNode);
                    }, null, null, true);
                }
                menu.addItem('查看日志', null, function () {
                    // 查看日志
                    ctx.showHideRunningLog(true, currentNode);
                }, null, null, true);
            } else {
                menu.addItem('删除依赖关系', null, function () {
                    ctx.removeCell(cell);
                }, null, null, true);
            }
        }
    }
    /* 初始化hover生成的div的效果 */
    mxTitleContent = (state, isVertex) => {
        const data = state.cell.data;
        const div = document.createElement('div');
        div.className = 'titleContent'
        if (isVertex) {
            div.innerHTML = `节点名称：${data.name} <br /> 算法名称：${this.componentType(data.componentType)}`;
            div.style.left = (state.x) + 'px';
            div.style.top = (state.y + VertexSize.height + 10) + 'px';
        }
        state.view.graph.container.appendChild(div);
        return {
            destroy: () => {
                const nodeLists = document.querySelectorAll('.titleContent');
                if (nodeLists.length > 0) {
                    for (let index = 0; index < nodeLists.length; index++) {
                        const node = nodeLists[index];
                        node.parentNode.removeChild(node);
                    }
                }
            }
        }
    }
    componentType = (componentType) => {
        switch (componentType) {
            case 1: return '读数据表';
            case 2: return '写数据表';
            case 3: return 'sql脚本';
            case 4: return '类型转换';
            case 5: return '归一化';
            case 6: return '拆分';
            case 7: return '逻辑二分类';
            case 8: return '数据预测';
            case 9: return '二分类评估';
            default: return '未知';
        }
    }
    /* graph的事件监听 */
    initGraphEvent = (graph) => {
        const ctx = this;
        let selectedCell = null;
        const { saveSelectedCell, changeSiderbar, getTaskDetailData } = this.props;
        this._graph = graph;
        graph.addMouseListener({
            id: 'hoverTitle', // 事件的唯一id，用于update事件
            currentState: null,
            currentTitleContent: null,
            isClick: false, // 是否点击了
            mouseDown: function (sender, me) {
                // Hides on mouse down
                this.isClick = true;
                if (this.currentState != null) {
                    this.dragLeave(me.getEvent(), this.currentState);
                    this.currentState = null;
                }
            },
            mouseMove: function (sender, me) {
                if (this.currentState == null && me.getState() == null) {
                    return;
                }
                const temp = graph.view.getState(me.getCell());
                if (this.currentState == null) {
                    this.dragEnter(me.getEvent(), temp, graph.getModel().isVertex(temp.cell));
                    this.currentState = temp;
                } else if (temp != this.currentState) {
                    this.dragLeave(me.getEvent(), this.currentState);
                    this.currentState = null;
                }
            },
            mouseUp: function (sender, me) {
                this.isClick = false;
            },
            dragEnter: function (evt, state, isVertex) {
                if (!isVertex) {
                    return
                }
                const data = state.cell.data;
                const editTarget = document.getElementById(`JS_cell_${data.id}`);
                if (
                    this.currentTitleContent == null &&
                    !this.isClick &&
                    !graph.popupMenuHandler.isMenuShowing() &&
                    window.getComputedStyle(editTarget).display === 'none'
                    /**
                     * 当前currentTitleContent
                     * 当前没有左键点击
                     * 当前没有右键点击弹出菜单
                     * 当前没有处于重命名状态
                     */
                ) {
                    this.currentTitleContent = new ctx.mxTitleContent(state, isVertex);
                }
            },
            dragLeave: function (evt, state) {
                if (this.currentTitleContent != null) {
                    this.currentTitleContent.destroy();
                    this.currentTitleContent = null;
                }
            }
        }, true);
        graph.addListener(mxEvent.CLICK, function (sender, evt) {
            const cell = evt.getProperty('cell');
            const event = evt.getProperty('event');
            if (event.button === 2) {
                // 鼠标右键
                return false;
            }
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
                selectedCell = cell;
                saveSelectedCell(cell) // 保存已选择的cell
                getTaskDetailData(ctx.props.data, cell.data.id)
                    .then((res) => {
                        changeSiderbar('params', true)
                    })
            } else if (cell === undefined) {
                const cells = graph.getSelectionCells();
                graph.removeSelectionCells(cells);
                changeSiderbar(null, false); // 没有选择cell会关闭侧边栏
                saveSelectedCell({})
            }
        }, true);

        graph.clearSelection = function (evt) {
            if (selectedCell) {
                const cellState = graph.view.getState(selectedCell);
                const style = {}
                style[mxConstants.STYLE_FILLCOLOR] = '#FFFFFF';
                style[mxConstants.STYLE_STROKECOLOR] = '#90D5FF';
                applyCellStyle(cellState, style);
                selectedCell = null;
                changeSiderbar(null, false); // 没有选择cell会关闭侧边栏
            }
        };

        graph.addListener(mxEvent.MOVE_CELLS, function (sender, evt) {
            ctx.handleUpdateTaskData(evt.getName(), evt.getProperty('cells')[0]);
        }, true);
        graph.addListener(mxEvent.CELL_CONNECTED, function (sender, evt) {
            // 一次连接会触发两次该事件，通过判断是否是source来区分
            if (!evt.getProperty('source')) {
                setTimeout(() => {
                    /**
                     * 这里延迟执行是因为这个事件监听的时候edge还只是一个previewState
                     * 而需要的style来保存位置信息的style在真正生成的时候才有值
                     * 故延迟执行，确保edge的style有值了
                     *  */
                    ctx.handleUpdateTaskData(evt.getName(), evt.getProperty('edge'));
                }, 500);
            }
        }, true);
        graph.addListener(mxEvent.PAN, (sender, evt) => {
            ctx._handleListenPan(sender);
        }, true);
        this.listenCopyAndDel(graph);
    }

    listenCopyAndDel = (graph) => {
        const ctx = this;
        if (!graph) return;
        const mockInput = document.createElement('textarea');
        mxUtils.setOpacity(mockInput, 0);
        mockInput.style.width = '1px';
        mockInput.style.height = '1px';
        mockInput.value = '';

        // 处理快捷键事件
        let restoreFocus = false;
        let isCopied = false;

        document.addEventListener('keydown', (evt) => {
            const keyCode = evt.keyCode;
            const source = evt.target;

            if (graph && !graph.isSelectionEmpty() && graph.isEnabled() && !graph.isMouseDown && !graph.isEditing() && source.nodeName != 'INPUT') {
                if (keyCode == 224 /* FF */ ||
                    (!mxClient.IS_MAC && keyCode == 17 /* Control */) ||
                    (mxClient.IS_MAC && keyCode == 91 /* Meta */)) {
                    if (!restoreFocus) {
                        mockInput.style.position = 'absolute';
                        mockInput.style.left = (graph.container.scrollLeft + 10) + 'px';
                        mockInput.style.top = (graph.container.scrollTop + 10) + 'px';
                        graph.container.appendChild(mockInput);
                        restoreFocus = true;
                        isCopied = false;
                        mockInput.focus();
                        mockInput.select();
                    }
                }
            }
        }, true);

        document.addEventListener('keyup', (evt) => {
            const keyCode = evt.keyCode;
            const source = evt.target;

            if (graph && graph.isEnabled() && !graph.isMouseDown && !graph.isEditing() && source.nodeName != 'INPUT') {
                if (restoreFocus && (keyCode == 224 || keyCode == 17 || keyCode == 91)) {
                    restoreFocus = false;
                    if (!graph.isEditing()) {
                        graph.container.focus();
                    }
                    graph.container.removeChild(mockInput);
                }

                if (keyCode == 8 || keyCode == 46) { // Backspace and Del keyPress
                    const cell = graph.getSelectionCell();
                    if (!cell) return;
                    ctx.removeCell(cell);
                }
            }
        }, true);

        mxEvent.addListener(mockInput, 'copy', mxUtils.bind(this, function (evt) {
            if (graph.isEnabled() && !graph.isSelectionEmpty()) {
                isCopied = true;
            }
        }), true);

        mxEvent.addListener(mockInput, 'paste', mxUtils.bind(this, function (evt) {
            mockInput.value = '';
            const cell = graph.getSelectionCell();
            if (isCopied && graph && graph.isEnabled() && cell) {
                this.copyCell(cell);
            }
            mockInput.select();
        }), true);
    }

    /* 复制节点 */
    copyCell = (cell) => {
        const { data } = this.props;
        const rootCell = this._graph.getDefaultParent();
        const graph = this._graph;
        const cellData = this.getCellData(cell);
        this.props.copyCell(data, cellData).then((res) => {
            let cell = new mxCell('', new mxGeometry(cellData.x + 10, cellData.y + 10, VertexSize.width, VertexSize.height));
            cell.data = res;
            cell.vertex = true;
            let cells = graph.importCells([cell], 0, 0, rootCell);
            cell = this.getCellData(cells[0]);
            /**
             * 复制完成后
             * 保存tab
             */
            const tabData = cloneDeep(data);
            tabData.graphData.push(cell);
            this.props.saveExperiment(tabData);
            graph.clearSelection();
        })
    }
    /* 删除节点 */
    removeCell = (cell) => {
        const { data } = this.props;
        let removeCells = [cell];
        const copyData = cloneDeep(data);
        const graphData = copyData.graphData;
        if (cell.edges && cell.edges.length > 0) {
            // 如果删除的是有边的vertex，连带边一起删除
            removeCells = removeCells.concat(cell.edges);
        }
        removeCells.forEach((item) => {
            let index = graphData.findIndex(o => {
                if (item.vertex && o.vertex) {
                    return o.data.id === item.data.id;
                } else if (item.edge && o.edge) {
                    return o.id === item.id;
                }
            });
            if (index > -1) {
                graphData.splice(index, 1);
            }
        })
        if (data.graphData.length !== graphData.length) {
            cell.vertex && api.deleteExperiment({ taskId: cell.data.id });
            cell.edge && this.resetComponentStatus(cell.target, graphData);
            /**
             * 删除之后也要保存
             */
            this.props.saveExperiment(copyData, false).then((res) => {
                if (res.code === 1) {
                    message.success('删除成功');
                } else {
                    message.warning('删除失败');
                }
            });
        } else {
            message.warning('删除失败')
        }
    }
    /* 重置某组件状态 */
    resetComponentStatus = (cell, graphData) => {
        const graphCell = graphData.find(o => o.vertex && o.data.id == cell.data.id);
        if (graphCell) {
            delete graphCell.data.status;
            // graphCell.data.status = 0;
        }
    }
    /* 从这里开始执行 */
    startHandlerFromHere = (cell) => {
        const taskId = cell.data.id;
        const type = 3;
        this.handleRunTask(taskId, type);
    }
    /* 执行到这里 */
    handlerToHere = (cell) => {
        const taskId = cell.data.id;
        const type = 1;
        this.handleRunTask(taskId, type);
    }
    handlerThisCell = (cell) => {
        const taskId = cell.data.id;
        const type = 2;
        this.handleRunTask(taskId, type);
    }
    handleRunTask = (taskId, type) => {
        const { data, currentTab } = this.props;
        this.props.getRunTaskList(data, taskId, type, currentTab);
    }
    /* 导出PMML */
    handleExportPMML = (cell) => {
        console.log(cell);
        window.open(`${ReqUrls.DOWNLOAD_PMML}?taskId=${cell.data.id}`, '_blank');
    }
    /* 模型描述 */
    handleOpenDescription = (cell) => {
        this.setState({
            detailModalVisible: true,
            detailData: cell
        });
    }

    handleListenPan = (graph) => {
        const { data } = this.props;
        const view = graph.view;
        const graphData = data.graphData;
        if (graphData) {
            const index = graphData.findIndex(o => o.graph == true);
            if (index === -1) {
                graphData.push({
                    graph: true,
                    vertex: false,
                    edge: false,
                    scale: view.getScale(),
                    translate: cloneDeep(view.getTranslate())
                })
            } else {
                graphData.splice(index, 1, {
                    graph: true,
                    vertex: false,
                    edge: false,
                    scale: view.getScale(),
                    translate: cloneDeep(view.getTranslate())
                })
            }
        }
        this.props.changeContent(data, {}, false, true)
    }

    _handleListenPan = debounce(this.handleListenPan, 500);
    /**
     *  更新task的data
     *  @param eventName-事件名称
     *  */
    handleUpdateTaskData = (eventName, cell) => {
        const { data } = this.props;
        const graphData = this.getGraphData();
        if (eventName === 'moveCells') {
            cell = this.getCellData(cell);
            const movedCell = data.graphData.find(o => o.vertex && o.data.id === cell.data.id);
            if (movedCell) {
                movedCell.x = cell.x;
                movedCell.y = cell.y;
            }
            this.props.updateTaskData({}, data, false);
        } else if (eventName === 'cellConnected') {
            let length = data.graphData.length;
            if (data.graphData.findIndex(o => o.graph) !== -1) {
                /**
                 * 如果包含layout信息，则长度少算一个
                 * 因为getGraphData里面实际不包含layout信息
                 */
                length -= 1;
            }
            if (graphData.length <= length) {
                /**
                 * 在初始化生成的时候也会触发这个事件，所以要排除掉初始化的情况
                 * 用length来排除初始化的情况是因为没有想到特别好的办法
                 */
                return;
            }
            const cellItem = this.getCellData(cell);
            cellItem.source = this.getCellData(cell.source);
            cellItem.target = this.getCellData(cell.target);
            const valueArr = cell.value.split('_');
            const inputType = CONSTRAINT_TEXT[cellItem.source.data.componentType].output.find(o => o.value === valueArr[0]);
            const outputType = CONSTRAINT_TEXT[cellItem.target.data.componentType].input.find(o => o.value === valueArr[1]);
            cellItem.inputType = inputType ? inputType.key : 0;
            cellItem.outputType = outputType ? outputType.key : 0;
            data.graphData.push(cellItem);
            this.props.saveExperiment(data, false); // 当触发连线的钩子函数的时候实时执行保存操作
        }
        this._graph.clearSelection();
    }
    initEditTaskCell = (cell, task) => {
        const ctx = this;
        const editTarget = document.getElementById(`JS_cell_${task.id}`);
        const { data } = this.props;
        const checkNodeName = function (name) {
            if (name === '') {
                message.error('子节点名称不可为空！')
                return false;
            } else if (name.length > 12) {
                message.error('子节点名称不得超过12个字符！')
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
                    const taskData = Object.assign({}, task, {
                        name: value
                    });
                    const object = data.graphData.find(o => o.vertex && o.data.id === cell.data.id);
                    object.data.name = value
                    data.sqlText = JSON.stringify(data.graphData);
                    // 对整个tab保存一次，再对cellData保存一次
                    Promise.all([
                        new Promise((resolve, reject) => {
                            api.addOrUpdateTask(data).then(res => {
                                resolve(res)
                            })
                        }),
                        new Promise((resolve, reject) => {
                            api.addOrUpdateTask(taskData).then(res => {
                                resolve(res)
                            })
                        })
                    ]).then((res) => {
                        if (res[0].code === 1 && res[1].code === 1) {
                            // 如果两次保存都成功，则更新cellData
                            ctx.updateCellData(cell, taskData, res);
                        }
                    })
                }
                editTarget.removeEventListener('blur', editSucc, false);
                editTarget.removeEventListener('keypress', editSucc, false);
            }
        }

        if (editTarget) {
            editTarget.style.display = 'inline-block';
            editTarget.focus();
            editTarget.addEventListener('blur', editSucc, false);
            editTarget.addEventListener('keypress', editSucc, false)
        }
    }
    updateCellData = (cell, taskData, res) => {
        const { data } = this.props;
        const newContent = { ...cloneDeep(data), ...res[0].data };
        this.props.changeContent(newContent, {}, true, false);
    }

    getCellData = (cell) => {
        return cell && {
            vertex: cell.vertex,
            edge: cell.edge,
            data: cell.data,
            x: cell.geometry.x,
            y: cell.geometry.y,
            value: cell.value,
            id: cell.id,
            style: cell.style
        }
    }

    getGraphData = () => {
        const rootCell = this._graph.getDefaultParent();
        const cells = this._graph.getChildCells(rootCell);
        const cellData = [];
        for (let i = 0; i < cells.length; i++) {
            const cell = cells[i];
            const cellItem = this.getCellData(cell);
            if (cell.edge) {
                cellItem.source = this.getCellData(cell.source);
                cellItem.target = this.getCellData(cell.target);
                const valueArr = cell.value.split('_');
                const inputType = CONSTRAINT_TEXT[cellItem.source.data.componentType].output.find(o => o.value === valueArr[0]);
                const outputType = CONSTRAINT_TEXT[cellItem.target.data.componentType].input.find(o => o.value === valueArr[1]);
                cellItem.inputType = inputType ? inputType.key : 0;
                cellItem.outputType = outputType ? outputType.key : 0;
            }
            cellData.push(cellItem);
        }
        return cellData;
    }

    onSearchChange = (searchText) => {
        if (searchText) {
            const rootCell = this._graph.getDefaultParent();
            const cells = this._graph.getChildCells(rootCell);
            const result = [];
            for (let i = 0; i < cells.length; i++) {
                const cell = cells[i];
                const data = cell.data;
                if (cell.vertex && data) {
                    if (data.name.indexOf(searchText) > -1) {
                        result.push({
                            id: data.id,
                            name: data.name
                        })
                    }
                }
            }
            this.setState({
                searchResult: result
            })
        }
    }

    debounceSearch = debounce(this.onSearchChange, 500, { 'maxWait': 2000 })

    initShowSearch = (e) => {
        this.setState({
            showSearch: true,
            searchText: ''
        }, () => {
            const selectEle = document.getElementById(SEARCH_MODAL_ID);
            if (selectEle) {
                selectEle.focus();
                // fix autoComplete问题
                selectEle.setAttribute('autocomplete', 'off');
            }
        })
    }

    showHideEvaluateReport = (visible, data) => {
        this.setState({
            evaluateReportVisible: visible,
            selectedData: data
        })
    }

    showHideOutputData = (visible, data) => {
        this.setState({
            outputDataVisible: visible,
            selectedOutputData: data
        })
    }

    showHideRunningLog = (visible, data) => {
        this.setState({
            runningLogVisible: visible,
            selectedData: data
        })
    }

    closeSearch = () => {
        this.setState({ showSearch: false, searchResult: '' });
    }

    onSelectResult = (value, option) => {
        const id = option.props.data
        const rootCell = this._graph.getDefaultParent();
        const cells = this._graph.getChildCells(rootCell);
        const cell = cells.find(o => o.vertex && o.data.id == id);
        if (cell) {
            const mxe = new mxEventObject(mxEvent.CLICK, 'cell', cell);
            this._graph.fireEvent(mxe);
            this._graph.scrollCellToVisible(cell, true);
            this.setState({
                showSearch: false,
                searchText: ''
            })
        }
    }

    render () {
        const {
            detailData, selectedData, selectedOutputData,
            showSearch, searchResult, detailModalVisible,
            runningLogVisible, evaluateReportVisible, outputDataVisible
        } = this.state;
        console.log('render:', this.state);
        const { data } = this.props;
        const graphData = cloneDeep(data.graphData);
        return <div className="exp-graph-view" style={{ width: '100%' }}>
            <GraphEditor
                version={data.version}
                data={graphData}
                key={data.id}
                onSearchNode={this.initShowSearch}
                registerContextMenu={this.initContextMenu}
                registerEvent={this.initGraphEvent}
            />
            <SearchModal
                visible={showSearch}
                searchResult={searchResult}
                id={SEARCH_MODAL_ID}
                placeholder="按子节点名称搜索"
                onCancel={this.closeSearch}
                onChange={this.debounceSearch}
                onSelect={this.onSelectResult}
            />
            <ModelDetailModal
                visible={detailModalVisible}
                key={detailData && detailData.id}
                data={detailData}
                onCancel={() => {
                    this.setState({
                        detailModalVisible: false,
                        detailData: null
                    })
                }}
            />
            <RunningLogModal
                visible={runningLogVisible}
                data={selectedData}
                onCancel={() => this.showHideRunningLog(false, null)}
            />
            <EvaluateReportModal
                data={selectedData}
                visible={evaluateReportVisible}
                onCancel={() => this.showHideEvaluateReport(false, null) }
            />
            <DataExploringModal
                data={selectedOutputData}
                visible={outputDataVisible}
                onOk={() => this.showHideOutputData(false, null)}
                onCancel={() => this.showHideOutputData(false, null) }
            />
        </div>
    }
}

export default GraphContainer;
