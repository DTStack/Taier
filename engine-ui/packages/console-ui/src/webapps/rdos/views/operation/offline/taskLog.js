import React, { Component } from 'react'
import { Link } from 'react-router'
import { Button, Row } from 'antd'
import { isArray } from 'lodash'

import utils from 'utils'

import GoBack from 'widgets/go-back'

import Api from '../../../api'
import Editor from '../../../components/code-editor'
import { TaskType } from '../../../components/status'

const editorOptions = {
    mode: 'text',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true,
}
const titleStyle = { height: '45px', color: '#333' }
const editorStyle = { height: '300px' }

function wrappTitle(title) {
    return `====================${title}====================`
}

function getLogsInfo(title, data) {
    let res = '';
    if (data && data.length > 0) {
        for (let i = 0; i < data.length ; ++i) {
            res = `${res} \n${wrappTitle(title)} \n${data[i].id } \n${data[i].value}`
        }
    }
    return res
}

export function LogInfo(props) {
    const log = props.log;
    const logStyle = Object.assign(editorStyle, {
        height: props.height,
    });
    
    const errors = log['all-exceptions'] || ''
    let flinkLog = isArray(errors) && errors.length > 0 ? errors.map(item => {
        return `${item.exception} \n`
    }) : '';

    const appLogs = getLogsInfo('appLogs', log.appLog)
    const driverLog = getLogsInfo('driverLog', log.driverLog)
    let logText = ''
    if (log.msg_info) {
        logText = `${wrappTitle('基本日志')}\n${log.msg_info}`
    }

    if (log['perf']) {
        logText = `${logText}\n${wrappTitle('性能指标')}\n${log['perf']} \n`
    }

    if (flinkLog || log['root-exception']) {
        logText = `${logText}\n\n${wrappTitle('Flink日志')} \n${flinkLog} \n ${log['root-exception']}`
    }

    if (appLogs || driverLog) {
        logText = `${logText} \n${appLogs} \n ${driverLog}`
    }

    return (
        <div>
            <Row style={logStyle}>
                <Editor sync value={logText} options={editorOptions}/>
            </Row>
        </div>
    )
}

export default class TaskLog extends Component {

    state = {
        taskInfo: '',
    }

    componentDidMount() {
        this.loadMsg()
    }

    loadMsg = () => {
        const ctx = this
        const jobId = this.props.params.jobId
        Api.getOfflineTaskLog({ jobId: jobId }).then((res) => {
            if (res.code === 1) {
                this.setState({ taskInfo: res.data })
            }
        })
    }

    render() {
        const taskInfo = this.state.taskInfo || {}
        const log = taskInfo.logInfo ? JSON.parse(taskInfo.logInfo) : {}
        const tdStyle = {
            width: '110px',
            background: '#fcfcfc',
        }

        return (
            <div className="runtime-page">
                <header className="bd-bottom">
                    <span className="left">运维日志</span>&nbsp;&nbsp;
                    <GoBack className="right" icon="rollback" size="small" />
                </header>
                <div className="runtime-content">
                    <article className="runtime-section">
                        <h1>基本信息</h1>
                        <table className="runtime-info bd">
                            <tbody>
                                <tr>
                                    <td style={tdStyle}>任务名称</td>
                                    <td>{taskInfo.name}</td>
                                    <td style={tdStyle}>任务类型</td>
                                    <td>离线任务-<TaskType value={taskInfo.taskType}/></td>
                                </tr>
                                <tr>
                                    <td style={tdStyle}>运行时间</td>
                                    <td>
                                        {utils.formatDateTime(taskInfo.execStartTime)} 
                                        ~ 
                                        {utils.formatDateTime(taskInfo.execEndTime)} 
                                    </td>
                                    <td style={tdStyle}></td>
                                    <td></td>
                                </tr>
                            </tbody>
                        </table>
                    </article>
                    <article className="runtime-section">
                        <LogInfo log={log} height="auto"/>
                    </article>
                </div>
            </div>
        )
    }
}
