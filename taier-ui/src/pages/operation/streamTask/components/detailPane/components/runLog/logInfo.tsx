import * as React from 'react'
import { isArray } from 'lodash'
import Editor from '@/components/codeEditor'
import { TASK_STATUS } from '@/constant';
import { createLinkMark, createLogMark } from '@/components/codeEditor/utils'

interface Props {
    status: TASK_STATUS | undefined;
    log?: {
        engineLog?: string;
        downLoadLog?: string;
        logInfo?: string;
        submitLog?: string;
        'all-exceptions'?: {
            exception?: string
        }[];
    };
    height?: string | number;
}

const editorOptions = {
    mode: 'simpleLog',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true
}

function getLogType (status: TASK_STATUS | undefined) {
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

export default function LogInfo (props: Props) {
    const log = props.log || {};
    let engineLog: Props['log'] = {};

    const wrappTitle = (title: string) => {
        return `====================${title}====================`
    }

    try {
        engineLog = log.engineLog ? JSON.parse(log.engineLog) : {}
    } catch (e) {
        engineLog = {
            'all-exceptions': [{ exception: log.engineLog }]
        }
    }

    const logStyle: React.CSSProperties = {
        height: props.height || '100%'
    }

    const errors = engineLog?.['all-exceptions'] || ''

    let engineLogs = isArray(errors) && errors.length > 0 ? errors.map((item: any) => {
        return `${item.exception} \n`
    }) : errors;

    let logText = ''
    if (log.downLoadLog) {
        logText = `完整日志下载地址：${createLinkMark({ href: `${log.downLoadLog}`, download: '' })}\n`;
    }
    if (log.logInfo) {
        logText = `${logText}${wrappTitle('基本日志')}\n${createLogMark(log.logInfo, getLogType(props.status))}`
    }
    if (log.submitLog) {
        logText = `${logText} \n${wrappTitle('操作日志')}\n${createLogMark(log.submitLog)}`
    }
    if (engineLogs) {
        logText = `${logText} \n${wrappTitle('引擎日志')} \n ${engineLogs}`
    }

    return (
        <div style={logStyle}>
            <Editor style={{ height: '100%' }} sync value={logText} options={editorOptions} />
        </div>
    )
}
