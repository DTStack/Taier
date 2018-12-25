import React from 'react'
import { isArray } from 'lodash'
import { Row } from 'antd'

import Editor from 'widgets/code-editor'
import { TASK_STATUS } from '../../../comm/const';
import { createLinkMark, createLogMark } from 'widgets/code-editor/utils'

const editorStyle = { height: '300px' }

const editorOptions = {
    mode: 'text',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true
}

function wrappTitle (title) {
    return `====================${title}====================`
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
export default function LogInfo (props) {
    const log = props.log || {};
    let engineLog = {};
    try {
        engineLog = log.engineLog ? JSON.parse(log.engineLog) : {}
    } catch (e) {
        engineLog = {
            'all-exceptions': [{ exception: log.engineLog }]
        }
        console.log('engineLog is not a json\n', e)
    }
    const logStyle = Object.assign({}, editorStyle, {
        height: props.height
    });

    const errors = engineLog['all-exceptions'] || ''
    let engineLogs = isArray(errors) && errors.length > 0 ? errors.map(item => {
        return `${item.exception} \n`
    }) : errors;

    const baseLog = log.logInfo ? JSON.parse(log.logInfo) : {}

    let logText = ''
    if (log.downLoadLog) {
        logText = `完整日志下载地址：${createLinkMark({ href: log.downLoadLog, download: '' })}\n`;
    }
    if (baseLog.msg_info) {
        logText = `${logText}${wrappTitle('基本日志')}\n${createLogMark(baseLog.msg_info, getLogType(props.status))}`
    }

    if (engineLogs) {
        logText = `${logText} \n${wrappTitle('引擎日志')} \n ${engineLogs}`
    }

    return (
        <div>
            <Row style={logStyle}>
                <Editor sync value={logText} options={editorOptions} />
            </Row>
        </div>
    )
}
