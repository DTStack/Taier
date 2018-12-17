import React from 'react'
import { Row } from 'antd'

import Editor from 'widgets/code-editor'
import { TASK_STATUS } from '../../../comm/const'
import { createLinkMark, createLogMark } from 'widgets/code-editor/utils'

const editorOptions = {
    mode: 'text',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    lineWrapping: true,
    smartIndent: true
}

const editorStyle = { height: '300px' }

function wrappTitle (title) {
    return `====================${title}====================`
}

function getLogsInfo (title, data, type = 'info') {
    let res = '';
    if (data && data.length > 0) {
        for (let i = 0; i < data.length; ++i) {
            res = `${res} \n${wrappTitle(title)} \n${data[i].id} \n${data[i].value}`
        }
    }
    return createLogMark(res, type)
}
function getLogType (status) {
    switch (status) {
        case TASK_STATUS.RUN_FAILED:
        case TASK_STATUS.SUBMIT_FAILED:
        case TASK_STATUS.PARENT_FAILD: {
            return 'error'
        }
        case TASK_STATUS.FINISHED: {
            return 'success'
        }
        default: {
            return 'info'
        }
    }
}
export function LogInfo (props) {
    window.loggg = props.log;
    /**
     * 这里要多加一些空格后缀，不然codemirror计算滚动的时候会有问题
     */
    const safeSpace = ' ';
    const log = props.log ? JSON.parse(props.log.replace(/\n/g, '\\n').replace(/\r/g, '\\r')) : {};
    const syncJobInfo = props.syncJobInfo;
    const logStyle = Object.assign({}, editorStyle, {
        height: props.height
    });

    const errors = log['all-exceptions'] || ''
    let flinkLog = errors;

    const appLogs = getLogsInfo('appLogs', log.appLog)
    const driverLog = getLogsInfo('driverLog', log.driverLog)
    let logText = ''
    if (props.downloadLog) {
        logText = `完整日志下载地址：${createLinkMark({ href: props.downloadLog, download: '' })}\n`;
    }
    if (log.msg_info) {
        logText = `${logText}${wrappTitle('基本日志')}\n${createLogMark(log.msg_info, getLogType(log.status))} ${safeSpace} \n`
    }

    if (log['perf']) {
        logText = `${logText}\n${wrappTitle('性能指标')}\n${createLogMark(log['perf'], 'warning')}${safeSpace} \n`
    }

    if (flinkLog || log['root-exception']) {
        logText = `${logText}\n\n${wrappTitle('Flink日志')} \n${flinkLog} \n ${createLogMark(log['root-exception'], 'error') || ''}`
    }

    if (appLogs || driverLog) {
        logText = `${logText} \n${appLogs} \n ${driverLog}`
    }

    if (log.msg_info) {
        let logSql = log['sql'];
        if (logSql && typeof logSql == 'object') {
            logSql = JSON.stringify(logSql, null, 2);
        }
        if (logSql) {
            logText = `${logText}${wrappTitle('任务信息')}\n${createLogMark(logSql, 'info')} \n`
        }
    }
    return (
        <div>
            {
                syncJobInfo
                    ? <Row style={{ marginBottom: '14px' }}>
                        <p>运行时长：{syncJobInfo.execTime}秒</p>
                        <p>
                            <span>读取数据：{syncJobInfo.readNum}条</span>&nbsp;&nbsp;
                            <span>写入数据：{syncJobInfo.writeNum}条</span>&nbsp;&nbsp;
                            <span>脏数据：{syncJobInfo.dirtyPercent}%</span>&nbsp;&nbsp;
                            {/* <span><Link to={`/data-manage/dirty-data/table/${syncJobInfo.tableId}`}>查看脏数据</Link></span> */}
                        </p>
                    </Row>
                    : ''
            }
            <Row style={logStyle}>
                <Editor sync value={logText} options={editorOptions} />
            </Row>
        </div>
    )
}
