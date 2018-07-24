import React, { Component } from 'react'
import { connect } from 'react-redux'
import { debounce } from 'lodash';
import { bindActionCreators } from 'redux';

import { Button, Modal, Checkbox, } from "antd";

import utils from 'utils';
import { filterComments, splitSql } from 'funcs';
import Editor from 'widgets/editor';
import pureRender from 'utils/pureRender';

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
    }

    componentDidMount() {
        const currentNode = this.props.currentTabData;
        if (currentNode) {
            this.props.getTab(currentNode.id)
        }
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
        execSql(currentTab, task, params, sqls);
    };

    stopSQL = () => {
        const { currentTabData, currentTab, stopSql} = this.props;
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

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })
    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, { 'maxWait': 2000 })

    render() {

        const { editor, currentTabData, value, language } = this.props;

        const currentTab = currentTabData.id;

        const consoleData = editor.console;

        const data = consoleData && consoleData[currentTab] ?
            consoleData[currentTab] : { results: [] }

        const { execConfirmVisible, confirmCode } = this.state;

        const cursorPosition = currentTabData.cursorPosition || undefined;
        const isLocked = currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock;

        const editorOpts = {
            value: value,
            language: '',
            options: {
                readOnly: isLocked,
            },
            cursorPosition: cursorPosition,
            theme: editor.options.theme,
            onChange: this.debounceChange,
            sync: currentTabData.merged || undefined,
            onCursorSelection: this.debounceSelectionChange,
        }

        const toolbarOpts = {
            enable: true,
            enableRun: true,
            enableFormat: true,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.execConfirm,
            onStop: this.stopSQL,
            onFormat: this.sqlFormat,
            onFileEdit: null,
            onThemeChange: (key) => {
                this.props.updateEditorOptions({theme: key})
            },
        }

        const consoleOpts = {
            data: data,
            onConsoleClose: this.closeConsole,
        }

        return (
            <div className="m-editor" style={{height: '100%'}}>
                <IDEEditor 
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
