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
import { COMPONENT_TYPE, INPUT_TYPE_ENUM, VertexSize, INPUT_TYPE } from '../../../../../consts';
import { getInputTypeItems } from '../../../../../comm';
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
    mxGeometry
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
        graph: state.component.graph
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
        return true;
    }

    initOutputMenuItems = (menu, data) => {
        const ctx = this;
        const menuItemArr = getInputTypeItems(data.componentType);
        // 逻辑回归没有【查看数据】功能
        if (menuItemArr.length === 1) {
            menu.addItem('查看数据', null, function () {
                data.inputType = INPUT_TYPE.NORMAL;
                ctx.showHideOutputData(true, data)
            }, null, null, true);
        } else if (menuItemArr.length > 1) {
            const parentMenuItem = menu.addItem('查看数据');
            let i = 1;
            while (i < menuItemArr.length) {
                const item = menuItemArr[i];
                menu.addItem(`查看数据输出${i}`, null, function () {
                    const selectedTarget = { inputType: item.inputType, ...data };
                    ctx.showHideOutputData(true, selectedTarget)
                }, parentMenuItem, null, true);
                i++;
            }
        }
    }

    initContextMenu = (graph) => {
        const ctx = this;
        const { isRunning } = this.props;
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
                }, null, null, !isRunning);
                menu.addItem('执行到此节点', null, function () {
                    ctx.handlerToHere(cell)
                }, null, null, !isRunning);
                menu.addItem('执行此节点', null, function () {
                    ctx.handlerThisCell(cell);
                }, null, null, !isRunning);
                menu.addSeparator();
                const menuModal = menu.addItem('模型选项', null, function () {
                }, null, null, true);
                menu.addItem('导出PMML', null, function () {
                    ctx.handleExportPMML(cell);
                }, menuModal, null, true);
                menu.addItem('模型描述', null, function () {
                    ctx.handleOpenDescription(cell);
                }, menuModal, null, true);
                // 初始化输出数据菜单项
                ctx.initOutputMenuItems(menu, currentNode);
                // 查看评估报告
                if (cell.data.taskType === COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION) {
                    menu.addItem('查看评估报告', null, function () {
                        ctx.showHideEvaluateReport(true, cell.data);
                    }, null, null, true);
                }
                menu.addItem('查看日志', null, function () {
                    // 查看日志
                    ctx.showHideRunningLog(true, cell.data);
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
        div.id = 'titleContent'
        if (isVertex) {
            div.innerHTML = `节点名称：${data.name} <br /> 算法名称：${this.componentType(data.componentType)}`;
            div.style.left = (state.x) + 'px';
            div.style.top = (state.y + 32 + 10) + 'px';
        } else {
            const values = state.cell.value.split('_');
            div.innerHTML = `输入参数：${values[0]} <br /> 输出参数: ${values[1]}`;
            div.style.left = (state.getCenterX()) + 'px';
            div.style.top = (state.getCenterY()) + 'px';
        }
        state.view.graph.container.appendChild(div);
        return {
            destroy: () => {
                if (document.getElementById('titleContent')) {
                    div.parentNode.removeChild(div)
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
    initGraphEvent = (graph) => {
        const ctx = this;
        let selectedCell = null;
        // eslint-disable-next-line no-unused-vars
        const { saveSelectedCell, data, changeSiderbar, getTaskDetailData } = this.props;
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
                if (this.currentTitleContent == null && !this.isClick && !graph.popupMenuHandler.isMenuShowing()) {
                    // 当前没有右键点击弹出菜单 并且 没有左键点击 并且 当前没有currentTitleContent
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
                getTaskDetailData(data, cell.data.id);
                changeSiderbar('params', true)
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
            }
        };

        graph.addListener(mxEvent.CELLS_MOVED, function (sender, evt) {
            ctx.handleUpdateTaskData(evt.getName());
        }, true);
        graph.addListener(mxEvent.CELL_CONNECTED, (sender, evt) => {
            // 一次连接会触发两次该事件，通过判断是否是source来区分
            if (!evt.getProperty('source')) {
                setTimeout(() => {
                    /**
                     * 这里延迟执行是因为这个事件监听的时候edge还只是一个previewState
                     * 而需要的style来保存位置信息的style在真正生成的时候才有值
                     * 故延迟执行，确保edge的style有值了
                     *  */
                    ctx.handleUpdateTaskData(evt.getName());
                }, 500);
            }
        }, true);
    }

    /* 复制节点 */
    copyCell = (cell) => {
        const { data, changeContent } = this.props;
        const rootCell = this._graph.getDefaultParent();
        const graph = this._graph;
        const cellData = this.getCellData(cell);
        this.props.copyCell(data, cellData).then((res) => {
            let cell = new mxCell('', new mxGeometry(cellData.x + 10, cellData.y + 10, VertexSize.width, VertexSize.height));
            cell.data = res;
            cell.vertex = true;
            let cells = graph.importCells([cell], cellData.x + 10, cellData.y + 10, rootCell);
            if (data.graphData) {
                data.graphData.push(this.getCellData(cell));
            } else {
                data.graphData = [this.getCellData(cell)]
            }
            changeContent(data, {}, true)
            if (cells != null && cells.length > 0) {
                graph.scrollCellToVisible(cells[0]);
                graph.setSelectionCells(cells);
            }
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
            let index = graphData.findIndex(o => { return o.id === item.id; });
            if (index > -1) {
                graphData.splice(index, 1);
            }
        })
        this.props.changeContent(copyData, data, true)
        this.props.saveExperiment(copyData); // 这里直接执行保存操作
    }
    /* 从这里开始执行 */
    startHandlerFromHere = (cell) => {
        const taskId = cell.data.id;
        const type = 1;
        this.handleRunTask(taskId, type);
    }
    /* 执行到这里 */
    handlerToHere = (cell) => {
        const taskId = cell.data.id;
        const type = 2;
        this.handleRunTask(taskId, type);
    }
    handlerThisCell = (cell) => {
        const taskId = cell.data.id;
        const type = 3;
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
    /**
     *  更新task的data
     *  @param eventName-事件名称
     *  */
    handleUpdateTaskData = (eventName) => {
        const { data } = this.props;
        const graphData = this.getGraphData();
        const oldData = Object.assign({}, data);
        const newData = Object.assign({}, data);
        newData.graphData = graphData;
        if (eventName === 'cellConnected' && newData.graphData.length <= oldData.graphData.length) {
            //  在初始化生成的时候也会触发这个事件，所以要排除掉初始化的情况
            return;
        }
        this.props.updateTaskData(oldData, newData);
        this.props.saveExperiment(newData); // 这里直接执行保存操作
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
                            ctx.updateCellData(cell, taskData);
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
    updateCellData = (cell, taskData) => {
        const { data } = this.props;
        const object = data.graphData.find(o => o.vertex && o.data.id == cell.data.id);
        if (object) {
            object.data = taskData;
            this.props.changeContent(data, {}, true, false);
            this._graph.refresh();
        }
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
                cellItem.inputType = INPUT_TYPE_ENUM[valueArr[0]] || 0;
                cellItem.outputType = INPUT_TYPE_ENUM[valueArr[1]] || 0;
            }
            cellData.push(cellItem);
        }
        return cellData;
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
        const cell = this._cacheCells[id];
        if (cell) {
            const mxe = new mxEventObject(mxEvent.CLICK, 'cell', cell);
            this.graph.fireEvent(mxe);
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
                onCancel={() => this.showHideOutputData(false, null) }
            />
        </div>
    }
}

export default GraphContainer;
