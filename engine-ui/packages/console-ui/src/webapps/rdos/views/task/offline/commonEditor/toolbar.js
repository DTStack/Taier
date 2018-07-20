import React, { Component } from "react";
import moment from "moment";

import { Button, Modal, Checkbox, Dropdown, Icon, Menu } from "antd";

import utils from "utils";
import { filterComments, splitSql } from "funcs";

import API from "../../../../api";
import CodeEditor from "../../../../components/code-editor";

import { updateUser } from "../../../../store/modules/user";

import {
    execSql,
    stopSql,
    setOutput,
    addLoadingTab
} from "../../../../store/modules/offlineTask/editorAction";

import { workbenchAction } from "../../../../store/modules/offlineTask/actionType";

export default class Toolbar extends Component {
    state = {
        confirmCode: "",
        execConfirmVisible: false
    };

    onNeverWarning = e => {
        const isCheckDDL = e.target.checked ? 1 : 0; // 0表示检查ddl，1表示不检查ddl
        this.props.dispatch(updateUser({ isCheckDDL }));
    };


    execSQL = () => {
        const {
            currentTab,
            editor,
            user,
            currentTabData,
            project,
            dispatch
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
            dispatch(setOutput(currentTab, `正在提交...`));
            dispatch(addLoadingTab(currentTab));
            this.reqExecSQL(currentTabData, params, sqls, i);
        }
    };

    reqExecSQL = (task, params, sqls, index) => {
        const { dispatch, currentTab } = this.props;
        dispatch(execSql(currentTab, task, params, sqls));
    };

    stopSQL = () => {
        const { currentTabData, dispatch, currentTab } = this.props;

        dispatch(stopSql(currentTab, currentTabData));
    };


    sqlFormat = () => {
        const { currentTabData, dispatch } = this.props;
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
                dispatch({
                    type: workbenchAction.SET_TASK_SQL_FIELD_VALUE,
                    payload: data
                });
            }
        });
    };

    getUniqueKey(id) {
        return `${id}_${moment().valueOf()}`;
    }

    editMenu = () => {
        return (
            <Menu>
                <Menu.Item key="find">查找（Cmd/Ctrl）+ F</Menu.Item>
                <Menu.Item key="replace">替换（Cmd/Ctrl）+ F</Menu.Item>
                <Menu.Item key="commandPane">命令面板 (F1)</Menu.Item>
            </Menu>
        )
    }

    viewMenu = () => {
        const { changeEditorTheme } = this.props;
        return (
            <Menu onClick={({key}) => {changeEditorTheme(key)}}>
                <Menu.Item key="vs">默认</Menu.Item>
                <Menu.Item key="vs-dark">黑色</Menu.Item>
                <Menu.Item key="hc-black">高对比黑色</Menu.Item>
            </Menu>
        )
    }

    render() {

        return (
            <div className="ide-toolbar toolbar clear-offset">
                <Button
                    icon="appstore-o"
                    title="格式化"
                    onClick={this.sqlFormat}
                >
                    格式化
                </Button>
                <Dropdown overlay={this.editMenu()} trigger={['click']}>
                    <Button icon="edit" title="编辑">
                        编辑<Icon type="down" />
                    </Button>
                </Dropdown>
                <Dropdown overlay={this.viewMenu()} trigger={['click']}>
                    <Button icon="skin" title="主题">
                        主题<Icon type="down" />
                    </Button>
                </Dropdown>
            </div>
        );
    }
}
