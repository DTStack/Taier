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
// eslint-disable-next-line
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
    /**
     * 这里要多加一些空格后缀，不然codemirror计算滚动的时候会有问题
     */
    const safeSpace = ' ';
    let logText = '';
    let syncJobInfo;
    let logStyle;
    try {
        const log = props.log ? JSON.parse(props.log.replace(/\n/g, '\\n').replace(/\r/g, '\\r')) : {};
        syncJobInfo = props.syncJobInfo;
        logStyle = Object.assign({}, editorStyle, {
            height: props.height
        });

        const errors = log['all-exceptions'] || ''
        const engineLogErr = log['engineLogErr'];
        let flinkLog = errors;

        const appLogs = engineLogErr ? `${wrappTitle('appLogs')}\n${engineLogErr}\n` : getLogsInfo('appLogs', log.appLog)
        const driverLog = getLogsInfo('driverLog', log.driverLog)
        if (props.downloadLog) {
            logText = `完整日志下载地址：${createLinkMark({ href: props.downloadLog, download: '' })}\n`;
        }
        if (props.subNodeDownloadLog) {
            Object.entries(props.subNodeDownloadLog).forEach(([key, value]) => {
                logText = `${logText} ${key}：${createLinkMark({ href: value, download: '' })}`
            });
        }
        if (log.msg_info) {
            logText = `${logText}${wrappTitle('基本日志')}\n${createLogMark(log.msg_info, 'info')} ${safeSpace} \n`
        }

        if (log['perf']) {
            logText = `${logText}\n${wrappTitle('性能指标')}\n${createLogMark(log['perf'], 'warning')}${safeSpace} \n`
        }
        /**
         * 数据增量同步配置信息
         */
        if (log['increInfo']) {
            logText = `${logText}\n${wrappTitle('增量标志信息')}\n${createLogMark(log['increInfo'], 'info')}${safeSpace} \n`
        }

        if (flinkLog || log['root-exception']) {
            logText = `${logText}\n\n${wrappTitle('Flink日志')} \n${createLogMark(flinkLog, 'error')} \n ${createLogMark(log['root-exception'], 'error') || ''}`
        }

        if (appLogs || driverLog) {
            logText = `${logText} \n${createLogMark(appLogs, 'error')} \n ${createLogMark(driverLog, 'error')}`
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
    } catch (e) {
        logText = `${createLogMark('日志解析错误', 'error')}\n${createLogMark(e, 'error')}\n${createLogMark(props.log, 'warning')}`
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
