import React, { Component } from 'react'
import { connect } from 'react-redux'
import { debounce } from 'lodash';
import { bindActionCreators } from 'redux';

import { Button, Modal, Checkbox, } from "antd";

import utils from 'utils';
import { filterComments, splitSql } from 'funcs';
import Editor from 'widgets/editor';
import { commonFileEditDelegator } from "widgets/editor/utils";
import { language } from "widgets/editor/languages/dtsql/dtsql";
import pureRender from 'utils/pureRender';

import reqOfflineUrl from "../../../../api/reqOffline";
import API from '../../../../api';
import IDEEditor from "../../../../components/editor";

import { matchTaskParams } from '../../../../comm';

import {
    workbenchActions,
} from '../../../../store/modules/offlineTask/offlineAction';

import { updateUser } from "../../../../store/modules/user";

import * as editorActions from '../../../../store/modules/editor/editorAction';

@pureRender
class EditorContainer extends Component {

    state = {
        confirmCode: "",
        execConfirmVisible: false,
        tableList: [],
        funcList: [],
        tableCompleteItems: [],
        funcCompleteItems: []
    }
    _tableColumns = {}
    _tableLoading = {}
    componentDidMount() {

        const currentNode = this.props.currentTabData;
        if (currentNode) {
            this.props.getTab(currentNode.id)//初始化console所需的数据结构
        }
        this.initTableList();
        this.initFuncList();
    }
    initTableList() {
        API.getTableListByName({
            appointProjectId: this.props.project.id
        })
            .then(
                (res) => {
                    if (res.code == 1) {
                        let { data } = res;
                        this.setState({
                            tableList: data.children || [],
                            tableCompleteItems: data.children && data.children.map(
                                (table) => {
                                    return [table.name, '表名', '1200', 'Field']
                                }
                            )
                        })
                    }
                }
            )
    }
    initFuncList() {
        API.getAllFunction()
            .then(
                (res) => {
                    if (res.code == 1) {
                        let { data } = res;
                        this.setState({
                            funcList: data || [],
                            funcCompleteItems: data && data.map(
                                (funcName) => {
                                    return [funcName, '函数', '2000', "Function"]
                                }
                            )
                        })
                    }
                }
            )
    }
    componentWillReceiveProps(nextProps) {
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
            cursorPosition: editorInstance.getPosition(),
        }
        if (utils.checkExist(task.taskType)) {
            params.sqlText = newVal
            // 过滤注释内容
            const filterComm = filterComments(newVal)
            params.taskVariables = matchTaskParams(taskCustomParams, filterComm)//this.matchTaskParams(newVal)
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
                if (trimed !== "") {
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
            currentTabData,
        } = this.props;

        const params = {
            projectId: project.id,
            isCheckDDL: user.isCheckDDL,
            taskVariables: currentTabData.taskVariables
        };

        this.setState({ execConfirmVisible: false });

        const code =
            editor.selection ||
            currentTabData.sqlText ||
            currentTabData.scriptText;

        const sqls = this.filterSql(code);

        if (sqls && sqls.length > 0) {
            let i = 0;
            this.props.setOutput(currentTab, `正在提交...`);
            this.props.addLoadingTab(currentTab);
            this.reqExecSQL(currentTabData, params, sqls, i);
        }
    };

    reqExecSQL = (task, params, sqls, index) => {
        const { currentTab, execSql } = this.props;
        execSql(currentTab, task, params, sqls)
            .then((complete) => {
                if (complete) {
                    this._tableColumns = {};
                    this.initTableList();
                }
            });
    };

    stopSQL = () => {
        const { currentTabData, currentTab, stopSql } = this.props;
        stopSql(currentTab, currentTabData);
    };

    // 执行确认
    execConfirm = () => {
        const { currentTabData, user, editor } = this.props;
        if (user.isCheckDDL === 1) {
            // 不检测，直接执行
            this.execSQL();
            return;
        }

        let code =
            editor.selection ||
            currentTabData.sqlText ||
            currentTabData.scriptText;
        code = filterComments(code);

        let filterShowCode = code.replace(/show\s+create/gi, "show"); //排除show create;

        // 匹配DDL执行语句，如果符合条件，则提醒
        const regex = /(create|alter|drop|truncate)+\s+(external|temporary)?\s?(table)+\s+([\s\S]*?)/gi;

        if (regex.test(filterShowCode)) {
            this.setState({ execConfirmVisible: true, confirmCode: code });
        } else {
            this.execSQL();
            this.setState({ execConfirmVisible: false });
        }
    };

    sqlFormat = () => {
        const { currentTabData, updateTaskField } = this.props;
        const params = {
            sql: currentTabData.sqlText || currentTabData.scriptText || ""
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
    completeProvider(completeItems, resolve, customCompletionItemsCreater, status = {}, ) {
        const { autoComplete = {}, syntax = {}, context = {}, word = {} } = status;
        const { tableCompleteItems, funcCompleteItems } = this.state;
        console.log(status)
        let defaultItems = completeItems
            .concat(customCompletionItemsCreater(tableCompleteItems))
            .concat(customCompletionItemsCreater(funcCompleteItems));
        if (context.completionContext.triggerCharacter == ".") {
            defaultItems = [];
        }
        if (autoComplete && autoComplete.locations) {
            let promiseList = [];
            for (let location of autoComplete.locations) {
                if (location.type == "table") {
                    for (let identifierChain of location.identifierChain) {
                        let columns = this.getTableColumns(identifierChain.name);
                        promiseList.push(columns)
                    }
                }
            }
            Promise.all(promiseList)
                .then(
                    (values) => {
                        let _tmpCache = {}
                        for (let value of values) {
                            //去除未存在的表
                            if (!value || !value[1] || !value[1].length) {
                                continue;
                            }
                            //防止添加重复的表
                            if (_tmpCache[value[0]]) {
                                continue;
                            }
                            _tmpCache[value[0]] = true;
                            if (context.columnContext&&context.columnContext.indexOf(value[0]) > -1) {
                                defaultItems = defaultItems.concat(
                                    customCompletionItemsCreater(value[1].map(
                                        (columnName) => {
                                            return [columnName, value[0], '100', "Variable"]
                                        }
                                    ))
                                )
                            } else {
                                if (context.completionContext.triggerCharacter == ".") {
                                    continue;
                                }
                                defaultItems = defaultItems.concat(
                                    customCompletionItemsCreater(value[1].map(
                                        (columnName) => {
                                            return [columnName, value[0], '1100', "Variable"]
                                        }
                                    ))
                                )
                            }

                        }
                        resolve(defaultItems);
                    }
                ).catch((e) => {
                    console.log(e)
                    resolve(defaultItems);
                })
        } else {
            resolve(defaultItems)
        }
    }
    getTableColumns(tableName) {
        let tableColumns = this._tableColumns;
        if (tableColumns[tableName]) {
            return Promise.resolve(tableColumns[tableName])
        }
        if (this._tableLoading[tableName]) {
            return this._tableLoading[tableName]
        }
        this._tableLoading[tableName] = API.getColumnsOfTable({ tableName })
            .then(
                (res) => {
                    if (res.code == 1) {
                        tableColumns[tableName] = [tableName, res.data];
                        return tableColumns[tableName];
                    } else {
                        console.log("get table columns error")
                    }
                }
            )
        return this._tableLoading[tableName];
    }
    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })
    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, { 'maxWait': 2000 })

    render() {

        const { editor, currentTabData, value } = this.props;

        const currentTab = currentTabData.id;

        const consoleData = editor.console;

        const data = consoleData && consoleData[currentTab] ?
            consoleData[currentTab] : { results: [] }

        const { execConfirmVisible, confirmCode, funcList } = this.state;

        const cursorPosition = currentTabData.cursorPosition || undefined;
        const isLocked = currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock;

        const editorOpts = {
            value: value,
            language: 'dtsql',
            options: {
                readOnly: isLocked,
            },
            customCompleteProvider: this.completeProvider.bind(this),
            languageConfig: {
                ...language,
                builtinFunctions: [],
                windowsFunctions: [],
                innerFunctions: [],
                otherFunctions: [],
                customFunctions: funcList
            },
            cursorPosition: cursorPosition,
            theme: editor.options.theme || "white",
            onChange: this.debounceChange,
            sync: currentTabData.merged || undefined,
            onCursorSelection: this.debounceSelectionChange
        }

        const toolbarOpts = {
            enable: true,
            enableRun: true,
            enableFormat: true,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.execConfirm,
            onStop: this.stopSQL,
            onFormat: this.sqlFormat,
            onFileEdit: commonFileEditDelegator(this._editor),
            onThemeChange: (key) => {
                this.props.updateEditorOptions({ theme: key })
            },
        }

        const consoleOpts = {
            data: data,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab,
            downloadUri: reqOfflineUrl.DOWNLOAD_SQL_RESULT
        }

        return (
            <div className="m-editor" style={{ height: '100%' }}>
                <IDEEditor
                    editorInstanceRef={(instance) => { this._editor = instance }}
                    editor={editorOpts}
                    toolbar={toolbarOpts}
                    console={consoleOpts}
                />
                <Modal
                    maskClosable
                    visible={execConfirmVisible}
                    title="执行的语句中包含DDL语句，是否确认执行？"
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    onCancel={() => {
                        this.setState({ execConfirmVisible: false });
                    }}
                    footer={
                        <div>
                            <Checkbox onChange={this.onNeverWarning}>
                                不再提示
                            </Checkbox>
                            <Button
                                onClick={() => {
                                    this.setState({
                                        execConfirmVisible: false
                                    });
                                }}
                            >
                                取消
                            </Button>
                            <Button type="primary" onClick={this.execSQL}>
                                执行
                            </Button>
                        </div>
                    }
                >
                    <div style={{ height: "400px" }}>
                        <Editor
                            value={confirmCode}
                            sync={true}
                            language="sql"
                            options={{
                                readOnly: true,
                                minimap: {
                                    enabled: false,
                                },
                            }}
                        />
                    </div>
                </Modal>
            </div>
        )
    }
}

export default connect(state => {
    return {
        editor: state.editor,
        project: state.project,
        user: state.user,
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
})(EditorContainer) 
