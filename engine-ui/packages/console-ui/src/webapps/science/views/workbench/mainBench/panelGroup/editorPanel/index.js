import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Tabs } from 'antd';
import { debounce, get } from 'lodash';
import { bindActionCreators } from 'redux';
import { commonFileEditDelegator } from 'widgets/editor/utils';

import CommonEditor from '../../../../../components/commonEditor';
import Editor from 'widgets/editor/index'
import SchedulingConfig from '../../../../../components/schedulingConfig';

import API from '../../../../../api';
import reqUrls from '../../../../../consts/reqUrls';
import { siderBarType } from '../../../../../consts';

import workbenchActions from '../../../../../actions/workbenchActions';
import * as runTaskActions from '../../../../../actions/runTaskActions';
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
        const actionsTwo = bindActionCreators(runTaskActions, dispatch);
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
            data
        } = this.props;

        const code = data.sqlText;
        this.reqExecSQL(data, {}, [code]);
    };

    reqExecSQL = (tabData, params, sqls) => {
        const { exec } = this.props;
        exec(tabData, params, sqls);
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
        this.props.removeNotebookRes(currentTab, targetKey);
    };

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetNotebookConsole(currentTab);
    };

    debounceChange = debounce(this.handleEditorTxtChange, 300, {
        maxWait: 2000
    });
    debounceChangeContent = debounce(this.changeContent, 200, {
        maxWait: 2000
    });
    changeContent (key, value) {
        const { data } = this.props;
        this.props.changeContent({
            [key]: value
        }, data);
    }
    renderSiderbarItems () {
        const { data } = this.props;
        return [
            <Tabs.TabPane
                tab='调度参数'
                key='key'
            >
                <SchedulingConfig formData={JSON.parse(data.scheduleConf)} onChange={(newFormData) => {
                    this.debounceChangeContent('scheduleConf', JSON.stringify(newFormData));
                }} />
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
    changeConsoleTab = (avtiveKay) => {
        const { data } = this.props;
        this.props.changeConsoleKey(data.id, avtiveKay);
    }
    render () {
        const { editor, data } = this.props;

        const currentTab = data.id;

        const consoleData = editor.console[siderBarType.notebook];
        const resultData = get(consoleData[currentTab], 'data', []);
        const consoleActivekey = get(consoleData[currentTab], 'activeKey', null);

        const editorOpts = {
            value: data.sqlText,
            language: 'python',
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
            downloadUri: reqUrls.DOWNLOAD_SQL_RESULT,
            tabOptions: {
                activeKey: consoleActivekey,
                onChange: this.changeConsoleTab

            }
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
                        this.forceUpdate();
                    }}
                />
            </CommonEditor>
        );
    }
}

export default EditorPanel;
