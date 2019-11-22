/* eslint-disable no-unreachable */
import * as React from 'react';
import { connect } from 'react-redux';
import { debounce, cloneDeep, get } from 'lodash';
import { message } from 'antd';
import { bindActionCreators } from 'redux';
import utils from 'utils';
import MxFactory from 'widgets/mxGraph';
import SearchModal from 'widgets/searchModal';

import api from '../../../../../api/experiment';
import GraphEditor from './graphEditor';
import * as experimentActions from '../../../../../actions/experimentActions';
import * as componentActions from '../../../../../actions/componentActions';
import { COMPONENT_TYPE, VertexSize, CONSTRAINT_TEXT, INPUT_TYPE } from '../../../../../consts';
import ReqUrls from '../../../../../consts/reqUrls';
import ModelDetailModal from './detailModal';
import RunningLogModal from './runningLog';
import EvaluateReportModal from './evaluateReport';
import DataExploringModal from './evaluateReport/dataExploring';

const SEARCH_MODAL_ID = 'JS_Search_MODAL';
const Mx = MxFactory.create();
const {
    mxEvent,
    mxPopupMenu,
    mxConstants,
    mxEventObject,
    mxCell,
    mxGeometry,
    mxUtils
} = Mx;

const applyCellStyle = (cellState: any, style: any) => {
    if (cellState) {
        cellState.style = Object.assign(cellState.style, style);
        cellState.shape.apply(cellState);
        cellState.shape.redraw();
    }
}
export interface GraphContainerState {
    selectedData: {
        componentType?: number;
        id?: number;
        [propName: string]: any;
    };
    [propName: string]: any;
}
/* eslint new-cap: ["error", { "newIsCap": false }] */
@(connect((state: any) => {
    const { project, user, editor } = state;
    return {
        user,
        editor,
        runTasks: state.component.taskLists, // 即将执行的任务列表
        project: project,
        graph: state.component.graph,
        selectedCell: state.component.selectedCell
    }
}, (dispatch: any) => {
    return bindActionCreators({ ...experimentActions, ...componentActions }, dispatch);
}) as any)
class GraphContainer extends React.Component<any, GraphContainerState> {
    constructor (props: any) {
        super(props);
        this.state = {
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
    }

    _graph: any = null;
    _activeNode: any;
    _copyLock: boolean;
    _removeLock: number;
    componentDidMount () {
        if (this.props.onRef) {
            this.props.onRef(this);
        }
    }

    shouldComponentUpdate (nextProps: any) {
        console.log('graph should update:', nextProps);
        return true;
    }

    initOutputMenuItems = (menu: any, data: any) => {
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
                if (item == INPUT_TYPE.MODEL) {
                    i++;
                    continue;
                }
                const text = CONSTRAINT_TEXT[data.componentType].output.find((o: any) => o.key == item);
                menu.addItem(`${text ? text.value : ('查看数据输出' + i)}`, null, function () {
                    const selectedTarget: any = { inputType: item, ...data };
                    ctx.showHideOutputData(true, selectedTarget)
                }, parentMenuItem, null, true);
                i++;
            }
        }
    }

    initContextMenu = (graph: any) => {
        const ctx = this;
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
        mxPopupMenu.prototype.showMenu = function () {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };
        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function (menu: any, cell: any, evt: any) {
            if (!cell) return;
            const currentNode = cell.data || {};

            if (cell.vertex) {
                menu.addItem('重命名', null, function () {
                    ctx.initEditTaskCell(cell, currentNode);
                }, null, null, !ctx.props.isRunning);
                menu.addItem('删除', null, function () {
                    ctx.removeCell(cell);
                }, null, null, !ctx.props.isRunning);
                menu.addItem('复制', null, function () {
                    ctx.copyCell(cell);
                }, null, null, !ctx.props.isRunning);
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
                    /* menu.addItem('导出PMML', null, function () {
                        ctx.handleExportPMML(cell);
                    }, menuModal, null, true); */
                    menu.addItem('模型描述', null, function () {
                        ctx.handleOpenDescription(cell);
                    }, menuModal, null, true);
                }
                // 初始化输出数据菜单项
                ctx.initOutputMenuItems(menu, currentNode);
                // 查看评估报告
                const reportList = [
                    COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION,
                    COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION,
                    COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION,
                    COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX
                ]
                if (reportList.includes(currentNode.componentType)) {
                    menu.addItem('查看评估报告', null, function () {
                        ctx.showHideEvaluateReport(true, currentNode);
                    }, null, null, true);
                }
                if (currentNode.componentType !== COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE) {
                    menu.addItem('查看日志', null, function () {
                        // 查看日志
                        ctx.showHideRunningLog(true, currentNode);
                    }, null, null, true);
                }
            } else {
                menu.addItem('删除依赖关系', null, function () {
                    ctx.removeCell(cell);
                }, null, null, true);
            }
        }
    }
    /* 初始化hover生成的div的效果 */
    mxTitleContent = (state: any, isVertex: any) => {
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
    componentType = (componentType: any) => {
        switch (componentType) {
            case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE: return '读数据表';
            case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE: return '写数据表';
            case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT: return 'sql脚本';
            case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE: return '类型转换';
            case COMPONENT_TYPE.DATA_MERGE.NORMALIZE: return '归一化';
            case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT: return '拆分';
            case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION: return '逻辑二分类';
            case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT: return '数据预测';
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION: return '二分类评估';
            case COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION: return 'kmeans聚类';
            case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_REGRESSION: return 'GBDT回归';
            case COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION: return '聚类模型评估';
            case COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION: return '回归模型评估';
            case COMPONENT_TYPE.DATA_MERGE.STANDARD: return '标准化';
            case COMPONENT_TYPE.DATA_MERGE.MISS_VALUE: return '缺失值填充';
            case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_CLASS: return 'GBDT二分类';
            case COMPONENT_TYPE.MACHINE_LEARNING.SVM: return 'SVM';
            case COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX: return '混淆矩阵';
            case COMPONENT_TYPE.FEATURE_ENGINEER.ONE_HOT: return 'one-hot编码';
            default: return '未知';
        }
    }
    /* graph的事件监听 */
    initGraphEvent = (graph: any) => {
        const ctx = this;
        let selectedCell: any = null;

        const { saveSelectedCell, changeSiderbar, getTaskDetailData } = this.props;
        this._graph = graph;

        const attachMouseOverStyle = function (cell: any) {
            const cellState = graph.view.getState(cell);
            const style: any = {}
            style[mxConstants.STYLE_FILLCOLOR] = '#DEEFFF';
            style[mxConstants.STYLE_STROKECOLOR] = '#2491F7';
            applyCellStyle(cellState, style);
        }
        const removeMouseOverStyle = function (cell: any) {
            const cellState = graph.view.getState(cell);
            const style: any = {}
            style[mxConstants.STYLE_FILLCOLOR] = '#FFFFFF';
            style[mxConstants.STYLE_STROKECOLOR] = '#90D5FF';
            applyCellStyle(cellState, style);
        }
        graph.addMouseListener({
            id: 'hoverTitle', // 事件的唯一id，用于update事件
            currentState: null,
            currentTitleContent: null,
            isClick: false, // 是否点击了
            mouseDown: function (sender: any, me: any) {
                // Hides on mouse down
                this.isClick = true;
                if (this.currentState != null) {
                    this.dragLeave(me.getEvent(), this.currentState);
                    this.currentState = null;
                }
            },
            mouseMove: function (sender: any, me: any) {
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
            mouseUp: function (sender: any, me: any) {
                this.isClick = false;
            },
            dragEnter: function (evt: any, state: any, isVertex: any) {
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
                    this.currentTitleContent = ctx.mxTitleContent(state, isVertex);
                    attachMouseOverStyle(state.cell);
                }
            },
            dragLeave: function (evt: any, state: any) {
                if (this.currentTitleContent != null) {
                    this.currentTitleContent.destroy();
                    this.currentTitleContent = null;
                    removeMouseOverStyle(state.cell);
                }
            }
        }, true);
        graph.addListener(mxEvent.CLICK, function (sender: any, evt: any) {
            const cell = evt.getProperty('cell');
            const event = evt.getProperty('event');
            if (event && event.button === 2) {
                // 鼠标右键
                return false;
            }
            const activeElement = document.activeElement;
            // 当从编辑对象触发点击事件时，清除activeElement的焦点
            if (
                activeElement && activeElement.className.indexOf('vertex-input') > -1) {
                (activeElement as any).blur();
            }

            if (cell && cell.vertex) {
                graph.clearSelection();
                attachMouseOverStyle(cell);
                selectedCell = cell;
                saveSelectedCell(cell) // 保存已选择的cell
                getTaskDetailData(ctx.props.data, cell.data.id)
                    .then((res: any) => {
                        changeSiderbar('params', true)
                    })
            } else if (cell === undefined) {
                const cells = graph.getSelectionCells();
                graph.removeSelectionCells(cells);
                changeSiderbar(null, false); // 没有选择cell会关闭侧边栏
                saveSelectedCell({})
            }
            if (ctx._activeNode) ctx._activeNode.focus();
        }, true);

        graph.clearSelection = function (evt: any) {
            if (selectedCell) {
                removeMouseOverStyle(selectedCell);
                selectedCell = null;
                changeSiderbar(null, false); // 没有选择cell会关闭侧边栏
            }
        };

        graph.addListener(mxEvent.MOVE_CELLS, function (sender: any, evt: any) {
            ctx.handleUpdateTaskData(evt.getName(), evt.getProperty('cells')[0]);
        }, true);
        graph.addListener(mxEvent.CELL_CONNECTED, function (sender: any, evt: any) {
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
        graph.addListener(mxEvent.PAN, (sender: any, evt: any) => {
            ctx._handleListenPan(sender);
        }, true);
        this.listenCopyAndDel(graph);
    }

    executeLayout = () => {
        this.handleUpdateTaskData('all');
    }

    listenCopyAndDel = (graph: any) => {
        if (!graph) return;
        let restoreCell: any;
        const ctx = this;
        const mockInput = document.getElementById('mockInput') as HTMLInputElement;
        mxEvent.addListener(graph.container, 'click', mxUtils.bind(this, function (evt: any) {
            if (evt.target.nodeName === 'INPUT' && evt.target.className.indexOf('vertex-input') > -1) {
                // 排除重命名的情况
                return;
            }
            mockInput.value = '';
            mockInput.focus()
        }), true)
        mxEvent.addListener(mockInput, 'copy', mxUtils.bind(this, function (evt: any) {
            if (graph.isEnabled() && !graph.isSelectionEmpty()) {
                const cell = graph.getSelectionCells()[0];
                restoreCell = cell;
            }
        }), true)
        mxEvent.addListener(mockInput, 'paste', mxUtils.bind(this, function (evt: any) {
            if (graph.isEnabled() && !graph.isSelectionEmpty() && mockInput.focus) {
                ctx.copyCell(restoreCell)
            }
        }), true)
        mxEvent.addListener(mockInput, 'keyup', mxUtils.bind(this, function (evt: any) {
            const keyCode = evt.keyCode;
            if (graph.isEnabled() && !graph.isSelectionEmpty() && mockInput.focus && (keyCode == 8 || keyCode == 46)) {
                const cell = graph.getSelectionCells()[0];
                ctx.removeCell(cell);
            }
        }), true)
    }

    /* 复制节点 */
    copyCell = (cell: any) => {
        const { data } = this.props;
        const rootCell = this._graph.getDefaultParent();
        const graph = this._graph;
        const cellData = this.getCellData(cell);
        // 增加一个锁
        if (this._copyLock) {
            return;
        } else {
            this._copyLock = true;
        }
        this.props.copyCell(data, cellData).then((res: any) => {
            this._copyLock = false;
            let cell = new mxCell('', new mxGeometry(cellData.x + 10, cellData.y + 10, VertexSize.width, VertexSize.height));
            cell.data = this.handleSimplify(res);
            cell.vertex = true;
            let cells = graph.importCells([cell], 0, 0, rootCell);
            cell = this.getCellData(cells[0]);
            /**
             * 复制完成后
             * 保存tab
             */
            const tabData = cloneDeep(data);
            tabData.graphData.push(cell);
            this.props.saveExperiment(tabData, false);
            graph.clearSelection();
        }).catch((_err: any) => {
            this._copyLock = false;
        })
    }
    /* 删除节点 */
    removeCell = (cell: any) => {
        const { data } = this.props;
        let removeCells: any = [cell];
        const copyData = cloneDeep(data);
        const graphData = copyData.graphData;
        if (this._removeLock === get(cell, 'data.id', 1)) {
            return;
        } else {
            this._removeLock = get(cell, 'data.id', 1);
        }
        if (cell.edges && cell.edges.length > 0) {
            // 如果删除的是有边的vertex，连带边一起删除
            removeCells = removeCells.concat(cell.edges);
        }
        removeCells.forEach((item: any) => {
            let index = graphData.findIndex((o: any) => {
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
            this.props.saveExperiment(copyData, false).then((res: any) => {
                if (res.code === 1) {
                    message.success('删除成功');
                } else {
                    message.warning('删除失败');
                }
                this._removeLock = null;
            });
        } else {
            message.warning('删除失败')
            this._removeLock = null;
        }
    }
    /* 重置某组件状态 */
    resetComponentStatus = (cell: any, graphData: any) => {
        const graphCell = graphData.find((o: any) => o.vertex && o.data.id == cell.data.id);
        if (graphCell) {
            delete graphCell.data.status;
        }
    }
    /* 从这里开始执行 */
    startHandlerFromHere = (cell: any) => {
        const taskId = cell.data.id;
        const type = 3;
        this.handleRunTask(taskId, type);
    }
    /* 执行到这里 */
    handlerToHere = (cell: any) => {
        const taskId = cell.data.id;
        const type = 1;
        this.handleRunTask(taskId, type);
    }
    handlerThisCell = (cell: any) => {
        const taskId = cell.data.id;
        const type = 2;
        this.handleRunTask(taskId, type);
    }
    handleRunTask = (taskId: any, type: any) => {
        const { data, currentTab } = this.props;
        this.props.getRunTaskList(data, taskId, type, currentTab);
    }
    /* 导出PMML */
    handleExportPMML = (cell: any) => {
        console.log(cell);
        window.open(`${ReqUrls.DOWNLOAD_PMML}?taskId=${cell.data.id}`, '_blank');
    }
    /* 模型描述 */
    handleOpenDescription = (cell: any) => {
        this.setState({
            detailModalVisible: true,
            detailData: cell
        });
    }

    handleListenPan = (graph: any) => {
        const { data } = this.props;
        const view = graph.view;
        const graphData = data.graphData;
        if (graphData) {
            const index = graphData.findIndex((o: any) => o.graph == true);
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
    handleSimplify = (object: any) => {
        if (Object.keys(object).length === 0) {
            return {};
        }
        const copyObject = cloneDeep(object);
        const deleteAttr = (obj: any, attr: any) => {
            delete obj[attr]
        }
        deleteAttr(copyObject, 'isDeleted')
        deleteAttr(copyObject, 'tenantId')
        deleteAttr(copyObject, 'engineType')
        deleteAttr(copyObject, 'taskParams')
        deleteAttr(copyObject, 'exeArgs')
        deleteAttr(copyObject, 'targetId')
        deleteAttr(copyObject, 'componentStatus')
        deleteAttr(copyObject, 'nodePName')
        deleteAttr(copyObject, 'readWriteLockVO')
        deleteAttr(copyObject, 'cron')
        deleteAttr(copyObject, 'scheduleConf')
        return copyObject;
    }
    /**
     *  更新task的data
     *  @param eventName-事件名称
     *  */
    handleUpdateTaskData = (eventName: any, cell?: any) => {
        const { data } = this.props;
        const graphData = this.getGraphData();
        const updateGraphVertex = (arrData: any, target: any) => {
            if (!data) return;
            const movedCell = arrData.find((o: any) => (o.vertex && o.data.id === target.data.id) || o.graph);
            if (movedCell) {
                if (movedCell.graph) {
                    movedCell.translate = target.translate;
                    movedCell.scale = target.scale;
                } else {
                    movedCell.x = target.x;
                    movedCell.y = target.y;
                }
            }
        }
        if (eventName === 'moveCells') {
            cell = this.getCellData(cell);
            updateGraphVertex(data.graphData, cell);
            this.props.updateTaskData({}, data, false);
        } else if (eventName === 'cellConnected') {
            let length = data.graphData.length;
            if (data.graphData.findIndex((o: any) => o.graph) !== -1) {
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
            const cellItem: {
                [propName: string]: any;
            } = this.getCellData(cell);
            cellItem.source = this.getCellData(cell.source);
            cellItem.target = this.getCellData(cell.target);
            const valueArr = cell.value.split('_');
            const inputType = CONSTRAINT_TEXT[cellItem.source.data.componentType].output.find((o: any) => o.value === valueArr[0]);
            const outputType = CONSTRAINT_TEXT[cellItem.target.data.componentType].input.find((o: any) => o.value === valueArr[1]);
            cellItem.inputType = inputType ? inputType.key : 0;
            cellItem.outputType = outputType ? outputType.key : 0;
            data.graphData.push(cellItem);
            this.props.saveExperiment(data, false); // 当触发连线的钩子函数的时候实时执行保存操作
        } else if (eventName === 'all') { // 更新所有节点
            if (graphData && graphData.length > 0) {
                graphData.forEach((o: any) => {
                    if (o.vertex) {
                        // 更新所有节点坐标信息
                        updateGraphVertex(data.graphData, o);
                    }
                })
            }
            this.props.updateTaskData({}, data, false);
        }
        this._graph.clearSelection();
    }
    /*
    ** 设置输入域(input/textarea)光标的位置
    ** @param {HTMLInputElement/HTMLTextAreaElement} elem
    ** @param {Number} index
    **/
    setCursorPosition = function (elem: any, index: any) {
        var val = elem.value
        var len = val.length
        // 超过文本长度直接返回
        if (len < index) return;
        setTimeout(function () {
            elem.focus()
            if (elem.setSelectionRange) { // 标准浏览器
                elem.setSelectionRange(index, index)
            } else { // IE9-
                var range = elem.createTextRange()
                range.moveStart('character', -len)
                range.moveEnd('character', -len)
                range.moveStart('character', index)
                range.moveEnd('character', 0)
                range.select()
            }
        }, 10)
    }
    initEditTaskCell = (cell: any, task: any) => {
        const ctx = this;
        const editTarget = document.getElementById(`JS_cell_${task.id}`) as HTMLInputElement;
        this.setCursorPosition(editTarget, editTarget.value.length);
        const { data } = this.props;
        const checkNodeName = function (name: any) {
            if (name === '') {
                message.error('子节点名称不可为空！')
                return false;
            } else if (name.length > 12) {
                message.error('子节点名称不得超过12个字符！')
                return false;
            }
            return true;
        }

        const editSucc = (evt: any) => {
            console.log(evt)
            const originName = task.name;
            if ((evt.type === 'keypress' && (event as any).keyCode === 13) || evt.type === 'blur') {
                editTarget.style.display = 'none';
                const value = utils.trim(editTarget.value);
                if (checkNodeName(value) && value !== originName) {
                    const taskData = Object.assign({}, task, {
                        name: value
                    });
                    const object = data.graphData.find((o: any) => o.vertex && o.data.id === cell.data.id);
                    object.data.name = value
                    data.sqlText = JSON.stringify(data.graphData);
                    const tab = cloneDeep(data);
                    tab.graphData = tab.graphData.map((item: any) => {
                        if (item.edge) {
                            item.source = { ...item.source, data: { id: item.source.data.id } };
                            item.target = { ...item.target, data: { id: item.target.data.id } };
                        }
                        return item;
                    })
                    // 对整个tab保存一次，再对cellData保存一次
                    Promise.all([
                        new Promise((resolve: any, reject: any) => {
                            api.addOrUpdateTask(tab).then((res: any) => {
                                resolve(res)
                            })
                        }),
                        new Promise((resolve: any, reject: any) => {
                            api.addOrUpdateTask(taskData).then((res: any) => {
                                resolve(res)
                            })
                        })
                    ]).then((res: any) => {
                        if (res[0].code === 1 && res[1].code === 1) {
                            // 如果两次保存都成功，则更新cellData
                            ctx.updateCellData(cell, taskData, res);
                        } else {
                            // 重命名输入框恢复到之前
                            editTarget.value = originName
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
    updateCellData = (cell: any, taskData: any, res: any) => {
        const { data } = this.props;
        const newContent: any = { ...cloneDeep(data), ...res[0].data };
        this.props.changeContent(newContent, {}, true, false);
    }

    getCellData = (cell: any) => {
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
        const cellData: any = [];
        for (let i = 0; i < cells.length; i++) {
            const cell = cells[i];
            const cellItem: {
                [propName: string]: any;
            } = this.getCellData(cell);
            if (cell.edge) {
                cellItem.source = this.getCellData(cell.source);
                cellItem.target = this.getCellData(cell.target);
                const valueArr = cell.value.split('_');
                const inputType = CONSTRAINT_TEXT[cellItem.source.data.componentType].output.find((o: any) => o.value === valueArr[0]);
                const outputType = CONSTRAINT_TEXT[cellItem.target.data.componentType].input.find((o: any) => o.value === valueArr[1]);
                cellItem.inputType = inputType ? inputType.key : 0;
                cellItem.outputType = outputType ? outputType.key : 0;
            }
            cellData.push(cellItem);
        }
        return cellData;
    }

    onSearchChange = (searchText: any) => {
        if (searchText) {
            const rootCell = this._graph.getDefaultParent();
            const cells = this._graph.getChildCells(rootCell);
            const result: any = [];
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

    initShowSearch = (e: any) => {
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

    showHideEvaluateReport = (visible: any, data: any) => {
        this.setState({
            evaluateReportVisible: visible,
            selectedData: data
        })
    }

    showHideOutputData = (visible: any, data: any) => {
        this.setState({
            outputDataVisible: visible,
            selectedOutputData: data
        })
    }

    showHideRunningLog = (visible: any, data: any) => {
        this.setState({
            runningLogVisible: visible,
            selectedData: data
        })
    }

    closeSearch = () => {
        this.setState({ showSearch: false, searchResult: '' });
    }

    onSelectResult = (value: any, option: any) => {
        const id = option.props.data
        const rootCell = this._graph.getDefaultParent();
        const cells = this._graph.getChildCells(rootCell);
        const cell = cells.find((o: any) => o.vertex && o.data.id == id);
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
        const { data, onRefGraph } = this.props;
        const graphData = cloneDeep(data.graphData);
        return (
            <div className="exp-graph-view" style={{ width: '100%', height: '100%' }}>
                <input id="mockInput" readOnly style={{ opacity: 0, width: 1, height: 1, outline: 0, border: 0 }} />
                <GraphEditor
                    version={data.version}
                    data={graphData || []}
                    key={data.id}
                    onSearchNode={this.initShowSearch}
                    registerContextMenu={this.initContextMenu}
                    registerEvent={this.initGraphEvent}
                    executeLayout={this.executeLayout}
                    disableToolbar={true}
                    onRef={onRefGraph}
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
                    onCancel={() => this.showHideEvaluateReport(false, null)}
                    onOk={() => this.showHideEvaluateReport(false, null)}
                />
                <DataExploringModal
                    data={selectedOutputData}
                    visible={outputDataVisible}
                    onOk={() => this.showHideOutputData(false, null)}
                    onCancel={() => this.showHideOutputData(false, null)}
                />
            </div>
        )
    }
}

export default GraphContainer;
