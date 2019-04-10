import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Tabs } from 'antd';
import { debounce } from 'lodash';
import { bindActionCreators } from 'redux';
import { commonFileEditDelegator } from 'widgets/editor/utils';

import CommonEditor from '../../../../../components/commonEditor';
import Editor from 'widgets/editor/index'

import API from '../../../../../api';
import reqUrls from '../../../../../consts/reqUrls';
import { siderBarType } from '../../../../../consts';

import workbenchActions from '../../../../../actions/workbenchActions';
import * as editorActions from '../../../../../actions/editorActions';
import * as notebookActions from '../../../../../actions/notebookActions';
import commActions from '../../../../../actions';

@connect(
    state => {
        const { workbench, editor } = state;
        return {
            editor,
            workbench
        };
    },
    dispatch => {
        const actionsOne = bindActionCreators(workbenchActions, dispatch);
        const actionsTwo = bindActionCreators(editorActions, dispatch);
        const actionsThree = bindActionCreators(commActions, dispatch);
        const notebookAction = bindActionCreators(notebookActions, dispatch);
        return Object.assign(actionsOne, actionsTwo, actionsThree, notebookAction);
    }
)
class EditorPanel extends Component {
    state = {};

    handleEditorTxtChange = (newVal, editorInstance) => {
        this.props.changeText(newVal, this.props.data);
    };

    execSQL = () => {
        const {
            editor,
            currentTab,
            data
        } = this.props;

        const params = {
            databaseId: this.state.selectedDatabase
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
            sql: data.sqlText
        };
        API.formatSQL(params).then(res => {
            if (res.data) {
                const result = {
                    merged: true,
                    sqlText: res.data,
                    id: data.id
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
        this.props.resetConsole(currentTab, siderBarType.notebook);
    };

    debounceChange = debounce(this.handleEditorTxtChange, 300, {
        maxWait: 2000
    });
    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, {
        maxWait: 2000
    });
    renderSiderbarItems () {
        return [
            <Tabs.TabPane
                tab='调度参数'
                key='key'
            >
                123
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab='任务参数'
                key='key1'
            >
                1232
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab='任务属性'
                key='key2'
            >
                1232
            </Tabs.TabPane>
        ]
    }
    render () {
        const { editor, data } = this.props;

        const currentTab = data.id;

        const consoleData = editor.console[siderBarType.notebook];
        const resultData = consoleData[currentTab] && consoleData[currentTab].data
            ? consoleData[currentTab].data
            : [];

        const cursorPosition = data.cursorPosition || undefined;

        const editorOpts = {
            value: data.sqlText,
            language: 'python',
            disabledSyntaxCheck: true,
            cursorPosition: cursorPosition,
            theme: editor.options.theme,
            onChange: this.debounceChange,
            sync: data.merged || undefined,
            onCursorSelection: this.debounceSelectionChange
        };

        const toolbarOpts = {
            enable: true,
            enableRun: true,
            enableFormat: true,
            disableEdit: false,
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
            <CommonEditor
                console={consoleOpts}
                toolbar={toolbarOpts}
                siderBarItems={this.renderSiderbarItems()}
            >
                <Editor
                    {...editorOpts}
                    editorInstanceRef={instance => {
                        this._editor = instance;
                    }}
                />
            </CommonEditor>
        );
    }
}

export default EditorPanel;
