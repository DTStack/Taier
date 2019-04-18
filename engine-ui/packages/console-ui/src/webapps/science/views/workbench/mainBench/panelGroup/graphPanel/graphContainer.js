import React from 'react';
import { connect } from 'react-redux';
import { debounce, get } from 'lodash';
import { message } from 'antd';
import { bindActionCreators } from 'redux';
import utils from 'utils';
import Mx from 'widgets/mxGraph';
import SearchModal from 'widgets/searchModal';

import API from '../../../../../api';
import GraphEditor from './graphEditor';
// import { isProjectCouldEdit } from '../../../../../comm';
import * as componentActions from '../../../../../actions/componentActions';

// const WIDGETS_PREFIX = 'JS_WIDGETS_'; // Prefix for widgets
const SEARCH_MODAL_ID = 'JS_Search_MODAL';

const {
    mxEvent,
    // mxCellHighlight,
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
        graphData: state.component.task,
        project: project
    }
}, (dispatch) => {
    return bindActionCreators(componentActions, dispatch);
})
class GraphContainer extends React.Component {
    state = {
        showSearch: false,
        searchResult: null,
        searchText: null
    }

    _graph = null;
    componentDidMount () {
        this.props.getTaskData();
    }
    shouldComponentUpdate (nextProps) {
        return true;
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
                }, null, null, true);
                menu.addItem('复制', null, function () {
                }, null, null, true);
                menu.addSeparator();
                menu.addItem('从此处开始执行', null, function () {
                    ctx.startHandlerFromHere(cell)
                }, null, null, true);
                menu.addItem('执行到此节点', null, function () {
                }, null, null, true);
                menu.addItem('执行此节点', null, function () {
                    ctx.deleteTask(cell);
                }, null, null, true);
                menu.addSeparator();
                const menuLookData = menu.addItem('查看数据', null, function () {
                }, null, null, true);
                menu.addItem('查看数据输出1', null, function () {
                }, menuLookData, null, true);
                menu.addItem('查看数据输出2', null, function () {
                }, menuLookData, null, true);
                const menuModal = menu.addItem('模型选项', null, function () {
                }, null, null, true);
                menu.addItem('导出PMML', null, function () {
                }, menuModal, null, true);
                menu.addItem('模型描述', null, function () {
                }, menuModal, null, true);
                menu.addItem('查看日志', null, function () {
                    ctx.deleteTask(cell);
                }, null, null, true);
            } else {
                menu.addItem('删除依赖关系', null, function () {
                    ctx.deleteTask(cell);
                }, null, null, true);
            }
        }
    }

    initGraphEvent = (graph) => {
        const ctx = this;
        let selectedCell = null;
        const { openTaskInDev, saveSelectedCell } = this.props;
        // let highlightEdges = [];
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
                saveSelectedCell(cell)
                selectedCell = cell;
            } else if (cell === undefined) {
                const cells = graph.getSelectionCells();
                graph.removeSelectionCells(cells);
                saveSelectedCell({})
            }
        });

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
            ctx.props.updateTaskData(graphData);
        });
        graph.addListener(mxEvent.CELLS_REMOVED, this.updateGraphData);
        graph.addListener(mxEvent.CELL_CONNECTED, () => {
            // console.log('CELL_CONNECTED.')
        });
    }
    startHandlerFromHere = (cell) => {
        const taskId = cell.data.id;
        const type = 0;
        this.props.getRunTaskList(taskId, type);
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

    getGraphData = () => {
        const rootCell = this._graph.getDefaultParent();
        const cells = this._graph.getChildCells(rootCell);
        const cellData = [];
        const getCellData = (cell) => {
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
        const { showSearch, searchResult } = this.state;
        const { graphData } = this.props;
        return <div className="exp-graph-view">
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
        </div>
    }
}

export default GraphContainer;
