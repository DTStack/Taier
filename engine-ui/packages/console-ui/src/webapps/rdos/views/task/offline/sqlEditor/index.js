import React, { Component } from 'react'
import { connect } from 'react-redux'
import { debounce } from 'lodash';
import { bindActionCreators } from 'redux';

import { Button, Modal, Checkbox } from 'antd';

import utils from 'utils';
import { filterComments, splitSql, getContainer } from 'funcs';
import Editor from 'widgets/editor';
import { commonFileEditDelegator } from 'widgets/editor/utils';
import { language } from 'widgets/editor/languages/dtsql/dtsql';
import pureRender from 'utils/pureRender';

import reqOfflineUrl from '../../../../api/reqOffline';
import API from '../../../../api';
import IDEEditor from 'main/components/ide';
import TableTipPane from './tableTipPane';
import SyntaxHelpPane from './syntaxTipPane';
import RightTableButton from './tableTipPane/rightButton';
import RightSyntaxButton from './syntaxTipPane/rightButton';

import { matchTaskParams, isProjectCouldEdit, isSparkEngine } from '../../../../comm';

import {
    workbenchActions
} from '../../../../store/modules/offlineTask/offlineAction';

import { updateUser } from '../../../../store/modules/user';

import * as editorActions from '../../../../store/modules/editor/editorAction';
import { editorAction } from '../../../../store/modules/editor/actionTypes';

import { getTableList, getTableListByProject } from '../../../../store/modules/offlineTask/comm';

const isTableTipPane = function (editor) {
    return editor.showRightExtraPane === editorAction.SHOW_TABLE_TIP_PANE;
}

const isSQLSyntaxTipPane = function (editor) {
    return editor.showRightExtraPane === editorAction.SHOW_SYNTAX_HELP_PANE;
}

@pureRender
class EditorContainer extends Component {
    state = {
        confirmCode: '',
        execConfirmVisible: false,
        tableList: [],
        funcList: [],
        funcCompleteItems: [],
        tables: [],
        columns: {}, // 当前sql中表的字段
        partition: {}, // 当前sql中表的分区
        extraPaneLoading: false
    }

    _tableColumns = {}
    _tableLoading = {}
    _projectLoading = {}

    componentDidMount () {
        const currentNode = this.props.currentTabData;
        const projectId = this.props.project.id;
        if (currentNode) {
            this.props.getTab(currentNode.id)// 初始化console所需的数据结构
        }
        this.initTableList(projectId, this.getTaskOrScriptType(currentNode));
        this.initFuncList(this.getTaskOrScriptType(currentNode));
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const current = nextProps.currentTabData
        const old = this.props.currentTabData
        const project = nextProps.project
        const oldProject = this.props.project
        if (project.id != oldProject.id) {
            this.initTableList(project.id, this.getTaskOrScriptType(current));
        }
        if (current && current.id !== old.id) {
            this.props.getTab(current.id)
        }
    }

    getTaskOrScriptType (task) {
        if (task) {
            if (task.hasOwnProperty('taskType')) {
                return {
                    taskType: task.taskType
                }
            } else {
                return {
                    scriptType: task.type
                }
            }
        }
    }
    initTableList (id, type) {
        id = id || this.props.project.id;
        if (!id) {
            console.log('project id 0 remove')
            return;
        }
        this.props.getTableList(id, type);
    }

    initFuncList (type) {
        API.getAllFunction({ ...type })
            .then(
                (res) => {
                    if (res.code == 1) {
                        let { data } = res;
                        this.setState({
                            funcList: data || [],
                            funcCompleteItems: data && data.map(
                                (funcName) => {
                                    return [funcName, '函数', '2000', 'Function']
                                }
                            )
                        })
                    }
                }
            )
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
            const filterComm = filterComments(newVal)
            params.taskVariables = matchTaskParams(taskCustomParams, filterComm)// this.matchTaskParams(newVal)
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

        this.setState({ execConfirmVisible: false });

        const code =
            editor.selection ||
            currentTabData.sqlText ||
            currentTabData.scriptText;

        const sqls = this.filterSql(code);

        if (sqls && sqls.length > 0) {
            let i = 0;
            this.props.setOutput(currentTab, `正在提交...`);
            this.reqExecSQL(currentTabData, params, sqls, i);
        }
    };

    reqExecSQL = (task, params, sqls, index) => {
        const { currentTab, execSql } = this.props;
        execSql(currentTab, task, params, sqls)
            .then((complete) => {
                if (complete) {
                    this._tableColumns = {};
                    this.initTableList(params.projectId, this.getTaskOrScriptType(task));
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

        let filterShowCode = code.replace(/show\s+create/gi, 'show'); // 排除show create;

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
    tableCompleteItems (tableList) {
        return tableList.map(
            (table) => {
                return [table.name, '当前项目表', '1200', 'Field']
            }
        )
    }
    getTableList () {
        const { project, tables } = this.props;
        const projectId = project.id;
        return tables[projectId];
    }

    async completeProvider (completeItems, resolve, customCompletionItemsCreater, status = {}) {
        const { autoComplete = {}, context = {} } = status;
        const { funcCompleteItems } = this.state;
        const tableList = this.getTableList();
        const tableCompleteItems = this.tableCompleteItems(tableList);
        console.log(status)
        // 初始完成项：默认项+所有表+所有函数
        let defaultItems = completeItems
            .concat(customCompletionItemsCreater(tableCompleteItems))
            .concat(customCompletionItemsCreater(funcCompleteItems));
        // 假如触发上下文为点，则去除初始点几个完成项
        if (context.completionContext.triggerCharacter == '.') {
            defaultItems = [];
        }
        if (context.tableContext) {
            let result = await this.getTableListByProject(context.tableContext);
            const projectIdentifier = result[0];
            const _projectTableList = result[1];
            if (_projectTableList) {
                defaultItems = defaultItems.concat(
                    customCompletionItemsCreater(_projectTableList.map(
                        (table) => {
                            return [table.name, `${projectIdentifier}项目表`, '1100', 'Field']
                        }
                    ))
                )
            }
            resolve(defaultItems);
            return;
        }
        // 开始解析具体语境
        if (autoComplete && autoComplete.locations) {
            let promiseList = [];
            // 根据代码中出现的表来获取所有的字段
            for (let location of autoComplete.locations) {
                if (location.type == 'table') {
                    if (location.identifierChain && location.identifierChain.length) {
                        let columns = this.getTableColumns(location.identifierChain.map(item => item.name).join('.'));
                        promiseList.push(columns)
                    }
                }
            }
            try {
                let values = await Promise.all(promiseList);
                let _tmpCache = {}
                // value:[tableName,data]
                for (let value of values) {
                    // 去除未存在的表
                    if (!value || !value[1] || !value[1].length) {
                        continue;
                    }
                    // 防止添加重复的表
                    if (_tmpCache[value[0]]) {
                        continue;
                    }
                    _tmpCache[value[0]] = true;
                    // 在当前语境为column的情况下，提升该表所有column的优先级
                    let priority = '100';
                    if (!context.columnContext || context.columnContext.indexOf(value[0]) == -1) {
                        // 当触发上下文是点，则不显示其余补全项
                        if (context.completionContext.triggerCharacter == '.') {
                            continue;
                        }
                        priority = '1100';
                    }
                    defaultItems = defaultItems.concat(
                        customCompletionItemsCreater(value[1].concat(value[3]).map(
                            (column) => {
                                return [column.columnName, value[0], priority, 'Variable']
                            }
                        ))
                    )
                }
                resolve(defaultItems);
            } catch (e) {
                console.log(e)
                resolve(defaultItems);
            };
        } else {
            resolve(defaultItems)
        }
    }
    /**
     * 获取表的字段
     * @param {表名} tableName
     */
    getTableColumns (tableName) {
        let _tableColumns = this._tableColumns;
        tableName = tableName.toLowerCase();
        if (_tableColumns[tableName]) {
            return Promise.resolve(_tableColumns[tableName])
        }
        // 共用现有请求线.
        if (this._tableLoading[tableName]) {
            return this._tableLoading[tableName]
        }
        let projectAndTableName = tableName.split('.');
        let params = {};
        if (projectAndTableName.length > 1) {
            params = {
                tableName: projectAndTableName[1],
                projectIdentifier: projectAndTableName[0]
            }
        } else {
            params = {
                tableName
            }
        }
        this._tableLoading[tableName] = API.getTableByName(params)
            .then(
                (res) => {
                    this._tableLoading[tableName] = null;
                    if (res.code == 1) {
                        _tableColumns[tableName] = [tableName,
                            res.data ? res.data.column : [],
                            res.data ? res.data.table : {},
                            res.data ? res.data.partition : {}
                        ];
                        return _tableColumns[tableName];
                    } else {
                        console.log('get table columns error')
                    }
                }
            )
        return this._tableLoading[tableName];
    }
    getTableListByProject (projectIdentifier) {
        const { projectTables } = this.props;
        if (projectTables[projectIdentifier]) {
            return Promise.resolve([projectIdentifier, projectTables[projectIdentifier]])
        }
        // 共用现有请求线.
        if (this._projectLoading[projectIdentifier]) {
            return this._projectLoading[projectIdentifier]
        }
        this._projectLoading[projectIdentifier] = this.props.getTableListByProject(projectIdentifier)
            .then(
                (data) => {
                    return data;
                }
            )
        return this._projectLoading[projectIdentifier];
    }
    /**
     * 每一次语法解析完成之后的触发事件
     * @param {*} autoComplete
     * @param {*} syntax
     */
    onSyntaxChange (autoComplete, syntax) {
        const locations = autoComplete.locations;
        let promiseList = [];
        let tables = [];
        let columns = {};
        let partition = {};
        let tmpTables = {};
        for (let location of locations) {
            if (location.type == 'table') {
                if (location.identifierChain && location.identifierChain.length) {
                    const tableName = location.identifierChain.map(item => item.name).join('.');
                    /**
                     * 去除重复表
                     */
                    if (tmpTables[tableName]) {
                        continue;
                    }
                    tmpTables[tableName] = true;
                    /**
                     * 获取sql中存在的table
                     */
                    tables.push(tableName);
                    /**
                     * 获取table的colums
                     */
                    let tmpColumns = this.getTableColumns(tableName);
                    /**
                     * 把获取column的接口都放到promiselist里面统一请求
                     */
                    promiseList.push(tmpColumns)
                }
            }
        }
        this.setState({
            tables: tables,
            extraPaneLoading: true
        })
        /**
         * 开始调用获取column的接口
         */
        Promise.all(promiseList)
            .then(
                (values) => {
                    // value:[tableName,data]
                    for (let value of values) {
                        // 去除未存在的表
                        if (!value || !value[1] || !value[1].length) {
                            continue;
                        }
                        columns[value[0]] = value[1];
                        partition[value[0]] = value[3];
                    }
                    this.setState({
                        columns: columns,
                        partition: partition,
                        extraPaneLoading: false
                    })
                }
            )
    }

    /**
     * 渲染右侧面板
     */
    renderExtraPane = () => {
        const { editor, currentTabData, updateSyntaxPane } = this.props;
        const { columns, partition, extraPaneLoading } = this.state;
        if (isTableTipPane(editor)) {
            return <TableTipPane
                data={columns}
                partition={partition}
                loading={extraPaneLoading}
                tabId={currentTabData.id}
            />;
        } else if (isSQLSyntaxTipPane(editor)) {
            return <SyntaxHelpPane
                loading={extraPaneLoading}
                tabId={currentTabData.id}
                theme={editor.options.theme}
                updateSyntaxPane={updateSyntaxPane}
                syntaxPane={editor.syntaxPane}
            />;
        } else return null;
    }

    renderRightButton = () => {
        const {
            hideRightPane, editor,
            showRightTablePane, showRightSyntaxPane, notShowSyntax
        } = this.props;
        const { options } = editor;
        return (
            <div>
                <RightTableButton
                    theme={options.theme}
                    hideRightPane={hideRightPane}
                    showRightTablePane={showRightTablePane}
                    showTableTooltip={isTableTipPane(editor)}
                />
                <RightSyntaxButton
                    theme={options.theme}
                    notShowSyntax={notShowSyntax}
                    showRightSyntaxPane={showRightSyntaxPane}
                    hideRightPane={hideRightPane}
                    showSyntaxPane={isSQLSyntaxTipPane(editor)}
                />
            </div>
        )
    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })
    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, { 'maxWait': 2000 })
    debounceSyntaxChange = debounce(this.onSyntaxChange.bind(this), 200, { 'maxWait': 2000 })

    render () {
        const { editor, currentTabData, value, project, user } = this.props;

        const currentTab = currentTabData.id;

        const consoleData = editor.console;

        const data = consoleData && consoleData[currentTab]
            ? consoleData[currentTab] : { results: [] }

        const { execConfirmVisible, confirmCode, funcList } = this.state;

        const cursorPosition = currentTabData.cursorPosition || undefined;
        const isLocked = currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock;
        const couldEdit = isProjectCouldEdit(project, user);
        const editorOpts = {
            value: value,
            language: 'dtsql',
            options: {
                readOnly: !couldEdit || isLocked
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
            theme: editor.options.theme,
            onChange: this.debounceChange,
            onSyntaxChange: this.debounceSyntaxChange,
            sync: currentTabData.merged || undefined,
            onCursorSelection: this.debounceSelectionChange
        }

        const toolbarOpts = {
            enable: true,
            enableRun: true,
            enableFormat: couldEdit,
            disableEdit: !couldEdit,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.execConfirm,
            onStop: this.stopSQL,
            onFormat: this.sqlFormat,
            onFileEdit: commonFileEditDelegator(this._editor),
            editorTheme: editor.options.theme,
            onThemeChange: (key) => {
                this.props.updateEditorOptions({ theme: key })
            },
            rightCustomButton: this.renderRightButton()
        }

        const consoleOpts = {
            data: data,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab,
            downloadUri: reqOfflineUrl.DOWNLOAD_SQL_RESULT,
            isDisEabledDownload: project.isAllowDownload == 0
        }

        const extraPne = isSparkEngine(currentTabData.engineType) ? this.renderExtraPane() : null;

        return (
            <div className="m-editor" style={{ height: '100%' }}>
                <IDEEditor
                    editorInstanceRef={(instance) => { this._editor = instance }}
                    editor={editorOpts}
                    toolbar={toolbarOpts}
                    console={consoleOpts}
                    extraPane={extraPne}
                />
                <div id="JS_ddl_confirm_modal">
                    <Modal
                        maskClosable
                        visible={execConfirmVisible}
                        title="执行的语句中包含DDL语句，是否确认执行？"
                        wrapClassName="vertical-center-modal modal-body-nopadding"
                        getContainer={() => getContainer('JS_ddl_confirm_modal')}
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
                        <div style={{ height: '400px' }}>
                            <Editor
                                value={confirmCode}
                                sync={true}
                                language="dtsql"
                                options={{
                                    readOnly: true,
                                    minimap: {
                                        enabled: false
                                    }
                                }}
                            />
                        </div>
                    </Modal>
                </div>
            </div>
        )
    }
}

export default connect(state => {
    return {
        editor: state.editor,
        project: state.project,
        user: state.user,
        tables: state.offlineTask.comm.tables,
        projectTables: state.offlineTask.comm.projectTables,
        showTableTooltip: state.offlineTask.workbench.showTableTooltip
    }
}, dispatch => {
    const taskAc = workbenchActions(dispatch);
    const editorAc = bindActionCreators(editorActions, dispatch);
    const actions = Object.assign(editorAc, {
        updateTaskField: taskAc.updateTaskField,
        updateUser: (user) => {
            dispatch(updateUser(user));
        },
        getTableList: (projectId, type) => {
            dispatch(getTableList(projectId, type));
        },
        getTableListByProject: (projectId) => {
            return dispatch(getTableListByProject(projectId));
        }
    })
    return actions;
})(EditorContainer)
