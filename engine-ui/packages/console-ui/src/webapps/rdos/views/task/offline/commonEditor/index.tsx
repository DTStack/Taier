import * as React from 'react'
import { connect } from 'react-redux'
import { debounce } from 'lodash';
import { bindActionCreators } from 'redux';
import { Menu, Tooltip } from 'antd';

import utils from 'utils';
import { filterComments, splitSql } from 'funcs';
import pureRender from 'utils/pureRender';
import { commonFileEditDelegator } from 'widgets/editor/utils';

import API from '../../../../api';
import IDEEditor from 'main/components/ide';
import reqOfflineUrl from '../../../../api/reqOffline';

import { matchTaskParams, isProjectCouldEdit } from '../../../../comm';
import { TASK_TYPE } from '../../../../comm/const';

import {
    workbenchActions
} from '../../../../store/modules/offlineTask/offlineAction';

import { updateUser } from '../../../../store/modules/user';

import * as editorActions from '../../../../store/modules/editor/editorAction';

@pureRender
class CommonEditorContainer extends React.Component<any, any> {
    state: any = {
    }
    _editor: any;
    componentDidMount () {
        const currentNode = this.props.currentTabData;
        if (currentNode) {
            this.props.getTab(currentNode.id)// 初始化console所需的数据结构
        }
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const current = nextProps.currentTabData
        const old = this.props.currentTabData
        if (current && current.id !== old.id) {
            this.props.getTab(current.id)
        }
    }

    handleEditorTxtChange = (newVal: any, editorInstance: any) => {
        const task = this.props.currentTabData
        const taskCustomParams = this.props.taskCustomParams;
        let params: any = {
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

    onNeverWarning = (e: any) => {
        const isCheckDDL = e.target.checked ? 1 : 0; // 0表示检查ddl，1表示不检查ddl
        this.props.updateUser({ isCheckDDL });
    };

    filterSql = (sql: any, batchSession?: boolean) => {
        const arr: any = [];
        let sqls: any = filterComments(sql);

        // 为 batchSession 时，SQL 无需切割
        if (batchSession) return [utils.trim(sqls)];

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

    /**
     * batchSession 该标记指定所选SQL是否在同一session中执行
     * 支持这一操作需从【高级运行】触发
     */
    execSQL = (batchSession?: any) => {
        const {
            user,
            editor,
            project,
            currentTab,
            singleLineMode,
            currentTabData
        } = this.props;

        const params: any = {
            projectId: project.id,
            isCheckDDL: user.isCheckDDL,
            taskVariables: currentTabData.taskVariables,
            singleSession: !batchSession // 是否为单 session 模式，batchSession 时，则支持批量SQL
        };

        const code =
            editor.selection ||
            currentTabData.sqlText ||
            currentTabData.scriptText;

        if (singleLineMode) {
            let sqls = this.filterSql(code, batchSession);
            if (sqls && sqls.length > 0) {
                let i = 0;
                this.props.setOutput(currentTab, `正在提交...`);
                this.reqExecSQL(currentTabData, params, sqls, i);
            }
        } else {
            if (code && code.length > 0) {
                this.props.setOutput(currentTab, `正在提交...`);
                this.props.addLoadingTab(currentTab);
                this.reqExecSQL(currentTabData, params, [code], 0);
            }
        }
    };

    reqExecSQL = (task: any, params: any, sqls: any, index: any) => {
        const { currentTab, execSql } = this.props;
        execSql(currentTab, task, params, sqls);
    };

    stopSQL = () => {
        const { currentTabData, currentTab, stopSql } = this.props;
        stopSql(currentTab, currentTabData);
    };

    // 执行确认
    execConfirm = () => {
        this.execSQL();
    };

    sqlFormat = () => {
        const { currentTabData, updateTaskField } = this.props;
        const params: any = {
            sql: currentTabData.sqlText || currentTabData.scriptText || ''
        };
        API.sqlFormat(params).then((res: any) => {
            if (res.data) {
                const data: any = {
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

    removeConsoleTab = (targetKey: any) => {
        const { currentTab } = this.props;
        this.props.removeRes(currentTab, parseInt(targetKey, 10))
    }

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetConsole(currentTab)
    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })
    debounceSelectionChange = debounce(this.props.setSelectionContent, 300, { 'maxWait': 2000 })

    render () {
        const { editor = {}, currentTabData, value, mode, toolBarOptions = {}, project, user } = this.props;
        if (toolBarOptions.enableFormat) {
            toolBarOptions.onFormat = toolBarOptions.onFormat || this.sqlFormat;
        }
        const currentTab = currentTabData.id;

        const consoleData = editor.console;

        const data = consoleData && consoleData[currentTab]
            ? consoleData[currentTab] : { results: [] }

        const cursorPosition = currentTabData.cursorPosition || undefined;
        const isLocked = currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock;
        const isScienceTask = currentTabData.taskType == TASK_TYPE.NOTEBOOK;
        const couldEdit = isProjectCouldEdit(project, user) && !isScienceTask;
        toolBarOptions.enableFormat = couldEdit && toolBarOptions.enableFormat;
        const editorOpts: any = {
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

        const toolbarOpts: any = {
            enable: true,
            enableRun: couldEdit,
            disableEdit: !couldEdit,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.execConfirm,
            onStop: this.stopSQL,
            onFileEdit: commonFileEditDelegator(this._editor),
            onThemeChange: (key: any) => {
                this.props.updateEditorOptions({ theme: key })
            },
            ...toolBarOptions
        }

        if (mode === 'sql') {
            toolbarOpts.runningMenu = (
                <Menu
                    onClick={this.execSQL.bind(this, true)}
                >
                    <Menu.Item key="runBatch"><Tooltip title="选中多段SQL代码时，将在一个session中运行">高级运行</Tooltip></Menu.Item>
                </Menu>
            )
        }

        const consoleOpts: any = {
            data: data,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab,
            isDisEabledDownload: project.isAllowDownload == 0,
            downloadUri: reqOfflineUrl.DOWNLOAD_SQL_RESULT
        }

        return (
            <div className="m-editor" style={{ height: '100%' }}>
                <IDEEditor
                    editorInstanceRef={(instance: any) => { this._editor = instance }}
                    editor={editorOpts}
                    toolbar={toolbarOpts}
                    console={consoleOpts}
                />
            </div>
        )
    }
}

export default connect((state: any) => {
    return {
        editor: state.editor,
        project: state.project,
        user: state.user
    }
}, (dispatch: any) => {
    const taskAc = workbenchActions(dispatch);
    const editorAc = bindActionCreators(editorActions, dispatch);
    const actions = Object.assign(editorAc, {
        updateTaskField: taskAc.updateTaskField,
        updateUser: (user: any) => {
            dispatch(updateUser(user));
        }
    })
    return actions;
})(CommonEditorContainer)
