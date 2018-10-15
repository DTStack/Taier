import React, { Component } from "react";
import { connect } from "react-redux";
import { debounce } from "lodash";
import { bindActionCreators } from "redux";

import utils from "utils";
import { filterComments, splitSql } from "funcs";
import { commonFileEditDelegator } from "widgets/editor/utils";
import { language } from "widgets/editor/languages/dtsql/dtsql";

import IDEEditor from "main/components/ide";

import API from "../../../../api";
import reqUrls from '../../../../consts/reqUrls';

import * as workbenchActions from "../../../../actions/workbenchActions";
import * as editorActions from "../../../../actions/editorActions";

@connect(
    state => {
        const { workbench, editor } = state;
        return {
            workbench,
            editor
        };
    },
    dispatch => {
        const actionsOne = bindActionCreators(workbenchActions, dispatch);
        const actionsTwo = bindActionCreators(editorActions, dispatch);
        return Object.assign(actionsOne, actionsTwo);
    }
)
class EditorContainer extends Component {

    state = {
        tableList: [],
        funcList: [],
        tableCompleteItems: [],
        funcCompleteItems: [],
        tables: [],
        columns: {}
    };

    _tableColumns = {};
    _tableLoading = {};

    componentDidMount() {
        const currentNode = this.props.data;
        if (currentNode) {
            this.props.getTab(currentNode.id); //初始化console所需的数据结构
        }
        // this.initTableList();
        // this.initFuncList();
    }

    componentWillReceiveProps(nextProps) {
        const current = nextProps.data;
        const old = this.props.data;
        if (current && current.id !== old.id) {
            this.props.getTab(current.id);
        }
    }

    initTableList(id) {
        if (!id) {
            return;
        }
        API.getTableListByName({
            appointProjectId: id
        }).then(res => {
            if (res.code == 1) {
                let { data } = res;
                this.setState({
                    tableList: data.children || [],
                    tableCompleteItems:
                        data.children &&
                        data.children.map(table => {
                            return [table.name, "表名", "1200", "Field"];
                        })
                });
            }
        });
    }

    initFuncList() {
        API.getAllFunction().then(res => {
            if (res.code == 1) {
                let { data } = res;
                this.setState({
                    funcList: data || [],
                    funcCompleteItems:
                        data &&
                        data.map(funcName => {
                            return [funcName, "函数", "2000", "Function"];
                        })
                });
            }
        });
    }

    handleEditorTxtChange = (newVal, editorInstance) => {
        const newData = {
            merged: false,
            sqlText: newVal,
            cursorPosition: editorInstance.getPosition()
        };
        this.props.updateCurrentTab(newData);
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
            data
        } = this.props;

        const params = {
            projectId: project.id,
            isCheckDDL: user.isCheckDDL,
            taskVariables: data.taskVariables
        };

        const code =
            editor.selection ||
            data.sqlText ||
            data.scriptText;

        const sqls = this.filterSql(code);

        if (sqls && sqls.length > 0) {
            let i = 0;
            this.props.setOutput(currentTab, `正在提交...`);
            this.props.addLoadingTab(currentTab);
            this.reqExecSQL(data, params, sqls, i);
        }
    };

    reqExecSQL = (task, params, sqls, index) => {
        const { currentTab, execSql } = this.props;
        execSql(currentTab, task, params, sqls).then(complete => {
            if (complete) {
                this._tableColumns = {};
                this.initTableList();
            }
        });
    };

    stopSQL = () => {
        const { data, currentTab, stopSql } = this.props;
        stopSql(currentTab, data);
    };

    // 执行确认
    execConfirm = () => {
        // 不检测，直接执行
        this.execSQL();
    };

    sqlFormat = () => {
        const { data, updateTab } = this.props;
        const params = {
            sql: data.sqlText,
        };
        API.sqlFormat(params).then(res => {
            if (res.data) {
                const data = {
                    merged: true,
                    value: res.data
                };
                updateTab(data);
            }
        });
    };

    removeConsoleTab = targetKey => {
        const { currentTab } = this.props;
        this.props.removeRes(currentTab, parseInt(targetKey, 10));
    };

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetConsole(currentTab);
    };

    completeProvider(
        completeItems,
        resolve,
        customCompletionItemsCreater,
        status = {}
    ) {
        const {
            autoComplete = {},
            syntax = {},
            context = {},
            word = {}
        } = status;
        const { tableCompleteItems, funcCompleteItems } = this.state;
        console.log(status);
        //初始完成项：默认项+所有表+所有函数
        let defaultItems = completeItems
            .concat(customCompletionItemsCreater(tableCompleteItems))
            .concat(customCompletionItemsCreater(funcCompleteItems));
        //假如触发上下文为点，则去除初始点几个完成项
        if (context.completionContext.triggerCharacter == ".") {
            defaultItems = [];
        }
        //开始解析具体语境
        if (autoComplete && autoComplete.locations) {
            let promiseList = [];
            //根据代码中出现的表来获取所有的字段
            for (let location of autoComplete.locations) {
                if (location.type == "table") {
                    for (let identifierChain of location.identifierChain) {
                        let columns = this.getTableColumns(
                            identifierChain.name
                        );
                        promiseList.push(columns);
                    }
                }
            }
            Promise.all(promiseList)
                .then(values => {
                    let _tmpCache = {};
                    //value:[tableName,data]
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
                        //在当前语境为column的情况下，提升该表所有column的优先级
                        if (
                            context.columnContext &&
                            context.columnContext.indexOf(value[0]) > -1
                        ) {
                            defaultItems = defaultItems.concat(
                                customCompletionItemsCreater(
                                    value[1].map(columnName => {
                                        return [
                                            columnName,
                                            value[0],
                                            "100",
                                            "Variable"
                                        ];
                                    })
                                )
                            );
                        } else {
                            //当触发上下文是点，则不显示其余补全项
                            if (
                                context.completionContext.triggerCharacter ==
                                "."
                            ) {
                                continue;
                            }
                            defaultItems = defaultItems.concat(
                                customCompletionItemsCreater(
                                    value[1].map(columnName => {
                                        return [
                                            columnName,
                                            value[0],
                                            "1100",
                                            "Variable"
                                        ];
                                    })
                                )
                            );
                        }
                    }
                    resolve(defaultItems);
                })
                .catch(e => {
                    console.log(e);
                    resolve(defaultItems);
                });
        } else {
            resolve(defaultItems);
        }
    }

    /**
     * 获取表的字段
     * @param {表名} tableName
     */
    getTableColumns(tableName) {
        let _tableColumns = this._tableColumns;
        if (_tableColumns[tableName]) {
            return Promise.resolve(_tableColumns[tableName]);
        }
        //共用现有请求线程
        if (this._tableLoading[tableName]) {
            return this._tableLoading[tableName];
        }
        this._tableLoading[tableName] = API.getColumnsOfTable({
            tableName
        }).then(res => {
            this._tableLoading[tableName] = null;
            if (res.code == 1) {
                _tableColumns[tableName] = [tableName, res.data];
                return _tableColumns[tableName];
            } else {
                console.log("get table columns error");
            }
        });
        return this._tableLoading[tableName];
    }

    onSyntaxChange(autoComplete, syntax) {
        const locations = autoComplete.locations;
        let promiseList = [];
        let tables = [];
        let columns = {};
        let tmp_tables = {};
        for (let location of locations) {
            if (location.type == "table") {
                for (let identifierChain of location.identifierChain) {
                    if (tmp_tables[identifierChain.name]) {
                        continue;
                    }
                    tmp_tables[identifierChain.name] = true;
                    tables.push(identifierChain.name);
                    let columns = this.getTableColumns(identifierChain.name);
                    promiseList.push(columns);
                }
            }
        }
        this.setState({
            tables: tables
        });
        Promise.all(promiseList).then(values => {
            for (let value of values) {
                //去除未存在的表
                if (!value || !value[1] || !value[1].length) {
                    continue;
                }
                columns[value[0]] = value[1];
            }
            this.setState({
                columns: columns
            });
        });
    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, {
        maxWait: 2000
    });
    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, {
        maxWait: 2000
    });
    debounceSyntaxChange = debounce(this.onSyntaxChange.bind(this), 200, {
        maxWait: 2000
    });

    render() {
        const { editor, data, value } = this.props;

        const currentTab = data.id;

        const consoleData = editor.console;

        const resultData = consoleData && consoleData[currentTab]
                ? consoleData[currentTab]
                : { results: [] };

        const { funcList } = this.state;

        const cursorPosition = data.cursorPosition || undefined;

        const editorOpts = {
            value: value,
            language: "dtsql",

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
            sync: data.merged || undefined,
            onCursorSelection: this.debounceSelectionChange
        };

        const toolbarOpts = {
            enable: true,
            enableRun: true,
            enableFormat: true,
            disAbleEdit: false,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.execConfirm,
            onStop: this.stopSQL,
            onFormat: this.sqlFormat,
            onFileEdit: commonFileEditDelegator(this._editor)
        };

        const consoleOpts = {
            data: resultData,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab,
            downloadUri: reqUrls.DOWNLOAD_SQL_RESULT
        };

        return (
            <div className="m-editor" style={{ height: "100%" }}>
                <IDEEditor
                    editorInstanceRef={instance => {
                        this._editor = instance;
                    }}
                    editor={editorOpts}
                    toolbar={toolbarOpts}
                    console={consoleOpts}
                />
            </div>
        );
    }
}

export default EditorContainer;
