import React, { Component } from 'react'
import moment from 'moment'

import {
    Col, Button, message, Modal, Checkbox,
} from 'antd'

import utils from 'utils'

import API from '../../../../api'
import MyIcon from '../../../../components/icon'
import CodeEditor from '../../../../components/code-editor'
import { matchTaskParams } from '../../../../comm'

import { updateUser } from '../../../../store/modules/user';

import { 
    output, outputRes, setOutput, setSelectionContent
} from '../../../../store/modules/offlineTask/sqlEditor'

import {
    workbenchAction
} from '../../../../store/modules/offlineTask/actionType';

const confirm = Modal.confirm;

export default class Toolbar extends Component {

    state = {
        currentSql: '',
        confirmCode: '',
        running: false,
        disabledStopJob: true,
        execConfirmVisible: false,
    }

    onNeverWarning = (e) => {
        const isCheckDDL = e.target.checked ? 1 : 0; // 0表示检查ddl，1表示不检查ddl
        this.props.dispatch(updateUser({ isCheckDDL }))
    }

    filterComments = (sql) => {
        return sql.replace(/(--)+(.)+(\n|\s)+/g, '')
    }

    filterSql = (sql) => {
        const arr = []
        let sqls = this.filterComments(sql);
        // 如果有有效内容
        if (sqls) { sqls = sqls.split(';') }

        if (sqls && sqls.length > 0) {
            for (let i = 0; i < sqls.length; i++) {
                let sql = sqls[i]
                const trimed = utils.trim(sql)
                if (trimed !== '') { // 过滤语句前后空格
                    arr.push(utils.trimlr(sql))
                }
            }
        }
        return arr
    }

    execSQL = () => {
        const { 
            currentTab, sqlEditor, user,
            currentTabData, project, dispatch,
        } = this.props;

        const params = { 
            projectId: project.id, 
            isCheckDDL: user.isCheckDDL, 
            taskVariables: currentTabData.taskVariables,
        }

        this.setState({ execConfirmVisible: false })

        const code = sqlEditor.selection || currentTabData.sqlText || currentTabData.scriptText

        const sqls = this.filterSql(code)

        if (sqls && sqls.length > 0) {
            let i = 0;
            dispatch(setOutput(currentTab, ` 正在提交... \n waiting...`))
            this.reqExecSQL(currentTabData, params, sqls, i)
        }
    }

    reqExecSQL = (task, params, sqls, index) => {

        const ctx = this
        const key = this.getUniqueKey(task.id)
        const { dispatch, currentTab } = this.props
        params.sql = `${sqls[index]}`
        params.uniqueKey = key
        this.setState({ currentSql: key, disabledStopJob: false, running: true })

        const succCall = (res) => {
            ctx.setState({ disabledStopJob: true, running: false }) // 控制
            if (res.message) dispatch(output(currentTab, `请求结果:\n ${res.message}`))
            if (res.code === 1) {
                dispatch(outputRes(currentTab, res.data))
                dispatch(output(currentTab, '执行成功!'))
                if (index < sqls.length - 1) {
                    ctx.reqExecSQL(task, params, sqls, index+1)
                }
            }
        }

        if (utils.checkExist(task.taskType)) {// 任务执行
            params.taskId = task.id, 
            API.execSQLImmediately(params).then(succCall)
        } else if (utils.checkExist(task.type)) { // 脚本执行
            params.scriptId = task.id, 
            API.execScript(params).then(succCall)
        }
    }

    stopSQL = () => {
        const { currentTabData } = this.props
        const uniqueKey = this.state.currentSql
        if (!uniqueKey) return
        
        const succCall = res => {
            if (res.code === 1) {
                message.success('停止执行成功！')
            } else {
                message.success('停止执行失败！')
            }
        }

        if (utils.checkExist(currentTabData.taskType)) {// 任务执行
            API.stopSQLImmediately({ 
                taskId: currentTabData.id,
                uniqueKey: uniqueKey,
            }).then(succCall)
        } else if (utils.checkExist(currentTabData.type)) { // 脚本执行
            API.stopScript({
                scriptId: currentTabData.id,
                uniqueKey: uniqueKey,
            }).then(succCall)
        }
    }

    // 执行确认
    execConfirm = () => {
        const { currentTab, currentTabData, dispatch, user, sqlEditor } = this.props;
        
        if (user.isCheckDDL === 1) { // 不检测，直接执行
            this.execSQL()
            return;
        } 

        let code = sqlEditor.selection || currentTabData.sqlText || currentTabData.scriptText;
        code = this.filterComments(code);

        // 匹配DDL执行语句，如果符合条件，则提醒
        const regex = /(create|alter|drop|truncate)+\s+(external|temporary)?\s?(table)+\s+([\s\S]*?)/gi;
        const ctx = this;
        
        if (regex.test(code)) {
            this.setState({ execConfirmVisible: true, confirmCode: code });
        } else {
            this.execSQL()
            this.setState({ execConfirmVisible: false })
        }
    }

    sqlFormat = () => {
        const { currentTabData, dispatch } = this.props;
        const params = {
            sql: currentTabData.sqlText || currentTabData.scriptText || '',
        }
        API.sqlFormat(params).then(res => {
            if (res.data) {
                const data = {
                    merged: true,
                }
                if (currentTabData.scriptText) {
                    data.scriptText = res.data
                } else {
                    data.sqlText = res.data
                }
                dispatch({
                    type: workbenchAction.SET_TASK_SQL_FIELD_VALUE,
                    payload: data,
                })
            }
        })
    }

    getUniqueKey(id){
        return `${id}_${moment().valueOf()}`
    }

    render() {
        const { disabledStopJob, running, execConfirmVisible, confirmCode } = this.state
        const { currentTabData } = this.props;
        return (
            <div className="ide-toolbar toolbar">
                <Button
                    onClick={this.execConfirm}
                    loading={running}
                    disabled={!disabledStopJob}
                    title="立即运行"
                    icon="play-circle-o"
                    style={{ marginLeft: '0px' }}> 运行
                </Button>
                <Button
                    onClick={this.stopSQL}
                    icon="pause-circle-o"
                    title="立即停止"
                    disabled={disabledStopJob}
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
                <Modal
                    maskClosable
                    visible={execConfirmVisible}
                    title="执行的语句中包含DDL语句，是否确认执行？"
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    onCancel={() => {this.setState({ execConfirmVisible: false })}}
                    footer={
                        <div>
                            <Checkbox onChange={this.onNeverWarning}>不在提示</Checkbox>
                            <Button onClick={() => {this.setState({ execConfirmVisible: false })}}>取消</Button>
                            <Button type="primary" onClick={this.execSQL}>执行</Button>
                        </div>
                    }
                >
                    <div style={{height: '400px'}}>
                        <CodeEditor value={confirmCode} />
                    </div>
                </Modal>
            </div>
        )
    }
}
