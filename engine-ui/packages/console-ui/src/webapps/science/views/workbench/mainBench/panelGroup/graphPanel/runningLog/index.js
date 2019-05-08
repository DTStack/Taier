/* eslint-disable no-unused-vars */
import React, { Component } from 'react';

import { Modal, Row } from 'antd';

import Editor from 'widgets/code-editor';
import { createLinkMark, createLogMark } from 'widgets/code-editor/utils'

import API from '../../../../../../api/experiment';

const logContainerStyle = { height: '450px' };

const editorOptions = {
    mode: 'text',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    lineWrapping: true,
    smartIndent: true
};

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

class RunningLogModal extends Component {
    state = {
        indexData: null, // 指标数据
        logData: null // log数据
    }
    componentDidUpdate (prevProps, prevState) {
        if (!prevProps.visible && this.props.visible) {
            this.fetchData();
        }
        return true;
    }

    fetchData = async () => {
        const { data } = this.props;
        if (!data) return;
        const res = await API.getComponentRunningLog({ taskId: data.id });
        if (res.code === 1) {
            this.setState({
                logData: res.data
            })
        }
    }

    renderLogContent = () => {
        const { runningIndexData, downloadLink, logData } = this.state;
        // const logText = prettifyLogText(logData, downloadLink);

        return (
            <div>
                {
                    runningIndexData
                        ? <Row style={{ marginBottom: '14px' }}>
                            <p>运行时长：{runningIndexData.execTime}秒</p>
                            <p>
                                <span>读取数据：{runningIndexData.readNum}条</span>&nbsp;&nbsp;
                                <span>写入数据：{runningIndexData.writeNum}条</span>&nbsp;&nbsp;
                                <span>脏数据：{runningIndexData.dirtyPercent}%</span>&nbsp;&nbsp;
                            </p>
                        </Row>
                        : ''
                }
                <Row style={logContainerStyle}>
                    <Editor sync value={logData} options={editorOptions} />
                </Row>
            </div>
        )
    }
    render () {
        const { visible, onCancel } = this.props;
        return (
            <Modal
                width={800}
                title="查看日志"
                wrapClassName="vertical-center-modal m-log-modal"
                visible={visible}
                onCancel={onCancel}
                footer={null}
                maskClosable={true}
                bodyStyle={{
                    padding: '0 0 0 0',
                    position: 'relative'
                }}
            >
                {this.renderLogContent()}
            </Modal>
        )
    }
}

export default RunningLogModal;

const prettifyLogText = (message, downloadAddress) => {
    /**
     * 这里要多加一些空格后缀，不然codemirror计算滚动的时候会有问题
     */
    const safeSpace = ' ';
    const log = message ? JSON.parse(message.replace(/\n/g, '\\n').replace(/\r/g, '\\r')) : {};

    const errors = log['all-exceptions'] || ''
    const engineLogErr = log['engineLogErr'];
    let flinkLog = errors;

    const appLogs = engineLogErr ? `${wrappTitle('appLogs')}\n${engineLogErr}\n` : getLogsInfo('appLogs', log.appLog)
    const driverLog = getLogsInfo('driverLog', log.driverLog);

    let logText = ''
    if (downloadAddress) {
        logText = `完整日志下载地址：${createLinkMark({ href: downloadAddress, download: '' })}\n`;
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

    return logText;
}
