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
    window.loggg=props.log;
    const log = props.log ? JSON.parse(props.log.replace(/\n/g,"\\n").replace(/\r/g,"\\r")) : {};
    const syncJobInfo = props.syncJobInfo;
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
        if(log["sql"]){
            logText=`${wrappTitle('任务信息')}\n${log.sql}\n`+logText
        }
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
            {
                syncJobInfo ? 
                <Row style={{marginBottom: '14px'}}>
                    <p>运行时长：{syncJobInfo.execTime}秒</p>
                    <p>
                        <span>读取数据：{syncJobInfo.readNum}条</span>&nbsp;&nbsp; 
                        <span>写入数据：{syncJobInfo.writeNum}条</span>&nbsp;&nbsp;
                        <span>脏数据：{syncJobInfo.dirtyPercent}%</span>&nbsp;&nbsp;
                        {/* <span><Link to={`/data-manage/dirty-data/table/${syncJobInfo.tableId}`}>查看脏数据</Link></span> */}
                    </p>
                </Row>
                :
                ''
            }
            <Row style={logStyle}>
                <Editor sync value={logText} options={editorOptions}/>
            </Row>
        </div>
    )
}
