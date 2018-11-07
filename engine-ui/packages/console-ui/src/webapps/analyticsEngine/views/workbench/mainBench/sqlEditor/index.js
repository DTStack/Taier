import React, { Component } from "react";
import { connect } from "react-redux";
import { debounce } from "lodash";
import { bindActionCreators } from "redux";
import { Select } from 'antd';

import utils from "utils";
import { filterComments, splitSql } from "funcs";
import { commonFileEditDelegator } from "widgets/editor/utils";
import { language } from "widgets/editor/languages/dtsql/dtsql";

import IDEEditor from "main/components/ide";

import API from "../../../../api";
import reqUrls from '../../../../consts/reqUrls';

import workbenchActions from "../../../../actions/workbenchActions";
import * as editorActions from "../../../../actions/editorActions";
import commActions from "../../../../actions";

const Option = Select.Option;

@connect(
    state => {
        const { workbench, editor, common } = state;
        const databaseList = workbench.folderTree.children || [];
        return {
            editor,
            workbench,
            databaseList,
            tableList: common.tableList,
            currentTab: workbench.mainBench.currentTab,
        };
    },
    dispatch => {
        const actionsOne = bindActionCreators(workbenchActions, dispatch);
        const actionsTwo = bindActionCreators(editorActions, dispatch);
        const actionsThree = bindActionCreators(commActions, dispatch);
        return Object.assign(actionsOne, actionsTwo, actionsThree);
    }
)
class EditorContainer extends Component {

    state = {
        tableList: [],
        funcList: [],
        tableCompleteItems: [],
        funcCompleteItems: [],
        selectedDatabase: this.props.data ? this.props.data.databaseId : '', // 选中的数据库数据库
        tables: [],
        columns: {}, // 暂时不支持字段AutoComplete
    };

    _tableColumns = {};
    _tableLoading = {};

    componentDidMount() {
        const currentNode = this.props.data;
        if (currentNode) {
            this.props.getTab(currentNode.id); //初始化console所需的数据结构
        }
        this.initTableList();
    }

    componentWillReceiveProps(nextProps) {
        const current = nextProps.data;
        const old = this.props.data;
        if (current && current.id !== old.id) {
            this.props.getTab(current.id);
        }
        if (this.props.tableList !== nextProps.tableList) {
            this.initTableList();
        }
    }

    initTableList() {
        const { tableList } = this.props;
        if (tableList && tableList.length > 0) {
            const items = tableList.map(table => {
                return [table.tableName, "表名", "1200", "Field"];
            })
            this.setState({
                tableList: tableList,
                tableCompleteItems: items,
            });
        }
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
        const data = this.props.data;
        const newData = {
            merged: false,
            id: data.id,
            sqlText: newVal,
            cursorPosition: editorInstance.getPosition()
        };
        this.props.updateTab(newData);
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
            editor,
            currentTab,
            data
        } = this.props;

        const params = {
            databaseId: this.state.selectedDatabase,
        };

        const code =
            editor.selection ||
            data.sqlText;

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
        API.formatSQL(params).then(res => {
            if (res.data) {
                const result = {
                    merged: true,
                    sqlText: res.data,
                    id: data.id,
                };
                updateTab(result);
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

        //初始完成项：默认项+所有表+所有函数
        let defaultItems = completeItems
            .concat(customCompletionItemsCreater(tableCompleteItems))
            .concat(customCompletionItemsCreater(funcCompleteItems));
        resolve(defaultItems);
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
                }
            }
        }
        this.setState({
            tables: tables
        });
    }

    onDatabaseChange = (value) => {
        this.setState({
            selectedDatabase: value,
        })
    }

    customToolbar = () => {
        const { databaseList, data } = this.props;
        const dbOptions = databaseList && databaseList.map(opt => (
            <Option key={`${opt.id}`} value={opt.id}>{opt.name}</Option>
        ))
        
        const defaultValue = data && data.databaseId ? data.databaseId : 
        databaseList.length > 0 ? databaseList[0].id : undefined;

        return (
            <Select
                className="ide-toobar-select"
                placeholder="请选择数据库"
                onChange={this.onDatabaseChange}
                defaultValue={defaultValue}
            >
                { dbOptions }
            </Select>
        )
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
        const { editor, data, databaseList } = this.props;

        const currentTab = data.id;

        const consoleData = editor.console;

        const resultData = consoleData && consoleData[currentTab]
                ? consoleData[currentTab]
                : { results: [] };

        const { funcList } = this.state;

        const cursorPosition = data.cursorPosition || undefined;

        const editorOpts = {
            value: data.sqlText,
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
            onFileEdit: commonFileEditDelegator(this._editor),
            customToobar: this.customToolbar(),
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
