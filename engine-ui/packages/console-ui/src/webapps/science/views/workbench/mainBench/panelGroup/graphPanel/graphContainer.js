/* eslint-disable no-unreachable */
import React from 'react';
import { connect } from 'react-redux';
import { debounce, get, cloneDeep } from 'lodash';
import { message } from 'antd';
import { bindActionCreators } from 'redux';
import utils from 'utils';
import Mx from 'widgets/mxGraph';
import SearchModal from 'widgets/searchModal';

import API from '../../../../../api';
import GraphEditor from './graphEditor';
import * as experimentActions from '../../../../../actions/experimentActions';
import * as componentActions from '../../../../../actions/componentActions';
import { COMPONENT_TYPE } from '../../../../../consts';
import ModelDetailModal from './detailModal';
import RunningLogModal from './runningLog';
import EvaluateReportModal from './evaluateReport';

// const WIDGETS_PREFIX = 'JS_WIDGETS_'; // Prefix for widgets

const SEARCH_MODAL_ID = 'JS_Search_MODAL';

const {
    mxEvent,
    mxPopupMenu,
    mxConstants,
    mxEventObject
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
        project: project
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
        evaluateReportVisible: false,
        runningLogVisible: false
    }

    _graph = null;

    shouldComponentUpdate (nextProps) {
        return true;
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
                const menuLookData = menu.addItem('查看数据', null, function () {
                }, null, null, true);
                menu.addItem('查看数据输出1', null, function () {
                }, menuLookData, null, true);
                menu.addItem('查看数据输出2', null, function () {
                }, menuLookData, null, true);
                if (cell.data.taskType === COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION) {
                    menu.addItem('查看评估报告', null, function () {
                        // 查看评估报告
                        ctx.showHideEvaluateReport(cell.data);
                    }, null, null, true);
                }
                menu.addItem('查看日志', null, function () {
                    // 查看日志
                }, null, null, true);
            } else {
                menu.addItem('删除依赖关系', null, function () {
                    ctx.removeCell(cell);
                }, null, null, true);
            }
        }
    }

    mxTitleContent = (state) => {
        const data = state.cell.data;
        const div = document.createElement('div');
        div.id = 'titleContent'
        div.innerHTML = `节点名称：${data.name} <br /> 算法名称：${data.name}`;
        div.style.padding = '20px 10px';
        div.style.position = 'absolute';
        div.style.cursor = 'default';
        div.style.width = '188px';
        div.style.left = (state.x) + 'px';
        div.style.top = (state.y + 32) + 'px';
        div.style.background = '#FFFFFF';
        div.style.boxShadow = '0 0 6px 0 rgba(0,0,0,0.15)';
        div.style.borderRadius = '2px';
        div.style.color = '#666666';
        div.style.fontSize = '12px';
        state.view.graph.container.appendChild(div);
        return {
            destroy: () => {
                if (document.getElementById('titleContent')) {
                    div.parentNode.removeChild(div)
                }
            }
        }
    }

    initGraphEvent = (graph) => {
        const ctx = this;
        let selectedCell = null;
        const { openTaskInDev, saveSelectedCell, data, changeSiderbar, getTaskDetailData } = this.props;
        this._graph = graph;
        graph.addListener(mxEvent.DOUBLE_CLICK, function (sender, evt) {
            const event = evt.getProperty('event');
            const editInputClassName = get(event, 'target.className', '')
            if (typeof editInputClassName === 'string' && editInputClassName.indexOf('vertex-input') > -1) {
                return;
            }

            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {
                const data = cell.data;
                openTaskInDev(data.id);
            }
        }, true);
        graph.addMouseListener({
            currentState: null,
            currentTitleContent: null,
            mouseDown: function (sender, me) {
                // Hides on mouse down
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
                if (temp != null && !graph.getModel().isVertex(temp.cell)) {
                    // edge
                } else {
                    // vertex
                    if (this.currentState == null) {
                        this.dragEnter(me.getEvent(), temp);
                        this.currentState = temp;
                    } else if (temp != this.currentState) {
                        this.dragLeave(me.getEvent(), this.currentState);
                        this.currentState = null;
                    }
                }
            },
            mouseUp: function (sender, me) { },
            dragEnter: function (evt, state) {
                if (this.currentTitleContent == null) {
                    this.currentTitleContent = new ctx.mxTitleContent(state);
                }
            },
            dragLeave: function (evt, state) {
                if (this.currentTitleContent != null) {
                    this.currentTitleContent.destroy();
                    this.currentTitleContent = null;
                }
            }
        });
        graph.addListener(mxEvent.CLICK, function (sender, evt) {
            const cell = evt.getProperty('cell');
            // const event = evt.getProperty('event');

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
                getTaskDetailData(data, cell.data.id); // 获取已选择的cell的详细数据
                changeSiderbar('params', true); // 选择cell会打开组件参数的侧边栏
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
            const graphData = ctx.getGraphData();
            const oldData = Object.assign({}, data);
            const newData = Object.assign({}, data);
            newData.graphData = graphData;
            ctx.props.updateTaskData(oldData, newData);
        }, true);
        graph.addListener(mxEvent.CELL_CONNECTED, () => {
            // console.log('CELL_CONNECTED.')
        }, true);
    }

    /* 复制节点 */
    copyCell = (cell) => {
        const { data } = this.props;
        const cellData = this.getCellData(cell);
        this.props.copyCell(data, cellData);
    }
    /* 删除节点 */
    removeCell = (cell) => {
        const { data } = this.props;
        const removeCells = [cell];
        const copyData = cloneDeep(data);
        const graphData = copyData.graphData;
        if (cell.edges && cell.edges.length > 0) {
            removeCells.concat(cell.edges);
        }
        removeCells.forEach((item) => {
            let index = graphData.findIndex(o => o.id == item.id);
            if (index > -1) {
                graphData.splice(index, 1);
            }
        })
        this.props.changeContent(copyData, data, true)
    }
    /* 从这里开始执行 */
    startHandlerFromHere = (cell) => {
        const taskId = cell.data.id;
        const type = 0;
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
    }
    /* 模型描述 */
    handleOpenDescription = (cell) => {
        this.setState({
            detailModalVisible: true,
            detailData: cell
        });
    }
    initEditTaskCell = (cell, task) => {
        const ctx = this;
        const editTarget = document.getElementById(`JS_cell_${task.id}`);
        // const { loadTreeNode } = this.props;

        const checkNodeName = function (name) {
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
                    const taskData = Object.assign({}, task, {
                        name: value
                    });
                    API.renameTask({ taskId: task.id, taskName: value }).then(res => {
                        if (res.code === 1) {
                            // const pid = task.nodePid || task.parentId;
                            // loadTreeNode(pid, MENU_TYPE.TASK_DEV);
                            ctx.updateCellData(cell, taskData);
                            ctx.updateGraphData();
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

    updateGraphData = () => {
        // const {
        //     updateTaskField
        // } = this.props;

        // const workflow = this.getGraphData();
        // const view = this._graph.getView();
        // const graph = {
        //     translate: view.getTranslate(),
        //     scale: view.getScale()
        // }
        // const toUpdateTasks = workflow.filter(item => {
        //     return item.vertex && item.data && item.data.notSynced === true;
        // });
        // this.setState({
        //     showGuidePic: !(workflow.length > 0)
        // })
        // updateTaskField({ sqlText: JSON.stringify(workflow), toUpdateTasks, graph });
    }

    getCellData = (cell) => {
        return cell && {
            vertex: cell.vertex,
            edge: cell.edge,
            data: cell.data,
            x: cell.geometry.x,
            y: cell.geometry.y,
            value: cell.value,
            id: cell.id
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

    showHideEvaluateReport = (data) => {
        this.setState({
            evaluateReportVisible: !!data,
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
            detailData, evaluateReportVisible, selectedData,
            showSearch, searchResult, detailModalVisible,
            runningLogVisible
        } = this.state;
        const { data } = this.props;
        const graphData = cloneDeep(data.graphData);
        return <div className="exp-graph-view" style={{ width: '100%' }}>
            <GraphEditor
                data={graphData}
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
                onCancel={() => {
                    this.setState({
                        runningLogVisible: false,
                        runningLog: { logData: null, indexData: null }
                    })
                }}
            />
            <EvaluateReportModal
                data={selectedData}
                visible={evaluateReportVisible}
                onCancel={() => this.showHideEvaluateReport() }
            />
        </div>
    }
}

export default GraphContainer;
