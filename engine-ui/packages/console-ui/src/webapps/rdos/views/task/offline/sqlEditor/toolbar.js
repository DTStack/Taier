import React, { Component } from "react";
import moment from "moment";

import { 
    Button, Modal, Checkbox, 
    Dropdown, Menu, Icon 
} from "antd";

import utils from "utils";
import { filterComments, splitSql } from "funcs";

import API from "../../../../api";
import CodeEditor from "../../../../components/code-editor";

import { updateUser } from "../../../../store/modules/user";

import {
    execSql,
    setOutput,
    addLoadingTab,
    stopSql
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

    // 执行确认
    execConfirm = () => {
        const { currentTabData, user, editor } = this.props;
        this.props.changeTab(1);
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
            <Menu onSelect={this.onEditEditor}>
                <Menu.Item key="find">查找（Cmd/Ctrl）+ F</Menu.Item>
                <Menu.Item key="replace">替换（Cmd/Ctrl）+ F</Menu.Item>
                <Menu.Item key="commandPane">命令面板 (F1)</Menu.Item>
            </Menu>
        )
    }

    viewMenu = () => {
        return (
            <Menu onSelect={this.onViewEditor}>
                <Menu.Item key="vs">默认</Menu.Item>
                <Menu.Item key="vs-dark">黑色</Menu.Item>
                <Menu.Item key="hc-dark">高对比黑色</Menu.Item>
            </Menu>
        )
    }

    render() {
        const { execConfirmVisible, confirmCode } = this.state;
        const { currentTab, editor } = this.props;
        const isRunning = editor.running.indexOf(currentTab) > -1;

        return (
            <div className="ide-toolbar toolbar clear-offset">
                <Button
                    onClick={this.execConfirm}
                    loading={isRunning}
                    disabled={isRunning}
                    title="立即运行"
                    icon="play-circle-o"
                    style={{ marginLeft: "0px" }}
                >
                    {" "}
                    运行
                </Button>
                <Button
                    onClick={this.stopSQL}
                    icon="pause-circle-o"
                    title="立即停止"
                    disabled={!isRunning}
                >
                    停止
                </Button>
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
                        <CodeEditor value={confirmCode} sync={true} />
                    </div>
                </Modal>
            </div>
        );
    }
}
