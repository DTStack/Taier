import React, { Component } from 'react'
import { connect } from 'react-redux'
import { debounce } from 'lodash';
import { bindActionCreators } from 'redux';

import utils from 'utils';
import { filterComments, splitSql } from 'funcs';
import pureRender from 'utils/pureRender';
import { commonFileEditDelegator } from 'widgets/editor/utils';

import API from '../../../../api';
import IDEEditor from 'main/components/ide';

import { PROJECT_TYPE } from '../../../../comm/const';
import { matchTaskParams, isProjectCouldEdit } from '../../../../comm';

import {
    workbenchActions
} from '../../../../store/modules/offlineTask/offlineAction';

import { updateUser } from '../../../../store/modules/user';

import * as editorActions from '../../../../store/modules/editor/editorAction';

@pureRender
class CommonEditorContainer extends Component {
    state = {
    }

    componentDidMount () {
        const currentNode = this.props.currentTabData;
        if (currentNode) {
            this.props.getTab(currentNode.id)// 初始化console所需的数据结构
        }
    }

    componentWillReceiveProps (nextProps) {
        const current = nextProps.currentTabData
        const old = this.props.currentTabData
        if (current && current.id !== old.id) {
            this.props.getTab(current.id)
        }
    }

    handleEditorTxtChange = (newVal, editorInstance) => {
        const task = this.props.currentTabData
        const taskCustomParams = this.props.taskCustomParams;
        let params = {
            merged: false,
            cursorPosition: editorInstance.getPosition()
        }
        if (utils.checkExist(task.taskType)) {
            params.sqlText = newVal
            // 过滤注释内容
            params.taskVariables = matchTaskParams(taskCustomParams, newVal)// this.matchTaskParams(newVal)
        } else if (utils.checkExist(task.type)) {
            params.scriptText = newVal
        }
        this.props.updateTaskField(params);
    }

    onNeverWarning = e => {
        const isCheckDDL = e.target.checked ? 1 : 0; // 0表示检查ddl，1表示不检查ddl
        this.props.updateUser({ isCheckDDL });
    };

    filterSql = sql => {
        const arr = [];
        let sqls = filterComments(sql);
        // 如果有有效内容
        if (sqls) {
            sqls = splitSql(sqls);
        }

        if (sqls && sqls.length > 0) {
            for (let i = 0; i < sqls.length; i++) {
                let sql = sqls[i];
                const trimed = utils.trim(sql);
                if (trimed !== '') {
                    // 过滤语句前后空格
                    arr.push(utils.trimlr(sql));
                }
            }
        }
        return arr;
    };

    execSQL = () => {
        const {
            user,
            editor,
            project,
            currentTab,
            currentTabData
        } = this.props;

        const params = {
            projectId: project.id,
            isCheckDDL: user.isCheckDDL,
            taskVariables: currentTabData.taskVariables
        };

        const code =
            editor.selection ||
            currentTabData.sqlText ||
            currentTabData.scriptText;

        if (code && code.length > 0) {
            this.props.setOutput(currentTab, `正在提交...`);
            this.props.addLoadingTab(currentTab);
            this.reqExecSQL(currentTabData, params, [code], 0);
        }
    };

    reqExecSQL = (task, params, sqls, index) => {
        const { currentTab, execSql } = this.props;
        execSql(currentTab, task, params, sqls);
    };

    stopSQL = () => {
        const { currentTabData, currentTab, stopSql } = this.props;
        stopSql(currentTab, currentTabData);
    };

    // 执行确认
    execConfirm = () => {
        const { currentTabData, user, editor } = this.props;

        let code =
            editor.selection ||
            currentTabData.sqlText ||
            currentTabData.scriptText;

        this.execSQL();
    };

    sqlFormat = () => {
        const { currentTabData, updateTaskField } = this.props;
        const params = {
            sql: currentTabData.sqlText || currentTabData.scriptText || ''
        };
        API.sqlFormat(params).then(res => {
            if (res.data) {
                const data = {
                    merged: true
                };
                if (currentTabData.scriptText) {
                    data.scriptText = res.data;
                } else {
                    data.sqlText = res.data;
                }
                updateTaskField(data)
            }
        });
    };

    removeConsoleTab = (targetKey) => {
        const { currentTab } = this.props;
        this.props.removeRes(currentTab, parseInt(targetKey, 10))
    }

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetConsole(currentTab)
    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })
    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, { 'maxWait': 2000 })

    render () {
        const { editor = {}, currentTabData, value, mode, toolBarOptions = {}, project, user } = this.props;

        const currentTab = currentTabData.id;

        const consoleData = editor.console;

        const data = consoleData && consoleData[currentTab]
            ? consoleData[currentTab] : { results: [] }

        const cursorPosition = currentTabData.cursorPosition || undefined;
        const isLocked = currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock;
        const couldEdit = isProjectCouldEdit(project, user);
        const editorOpts = {
            value: value,
            language: mode,
            options: {
                readOnly: isLocked || !couldEdit
            },
            cursorPosition: cursorPosition,
            theme: editor.options.theme,
            onChange: this.debounceChange,
            sync: currentTabData.merged || undefined,
            onCursorSelection: this.debounceSelectionChange
        }

        const toolbarOpts = {
            enable: true,
            enableRun: couldEdit,
            disAbleEdit: !couldEdit,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.execConfirm,
            onStop: this.stopSQL,
            onFileEdit: commonFileEditDelegator(this._editor),
            onThemeChange: (key) => {
                this.props.updateEditorOptions({ theme: key })
            },
            ...toolBarOptions
        }

        const consoleOpts = {
            data: data,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab
        }

        return (
            <div className="m-editor" style={{ height: '100%' }}>
                <IDEEditor
                    editorInstanceRef={(instance) => { this._editor = instance }}
                    editor={editorOpts}
                    toolbar={toolbarOpts}
                    console={consoleOpts}
                />
            </div>
        )
    }
}

export default connect(state => {
    return {
        editor: state.editor,
        project: state.project,
        user: state.user
    }
}, dispatch => {
    const taskAc = workbenchActions(dispatch);
    const editorAc = bindActionCreators(editorActions, dispatch);
    const actions = Object.assign(editorAc, {
        updateTaskField: taskAc.updateTaskField,
        updateUser: (user) => {
            dispatch(updateUser(user));
        }
    })
    return actions;
})(CommonEditorContainer)
