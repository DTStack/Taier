/* eslint-disable no-unused-vars */
import * as React from 'react';

import { Modal, Row, Spin } from 'antd';

import Editor from 'widgets/code-editor';
import { createLinkMark } from 'widgets/code-editor/utils'

import API from '../../../../../../api/experiment';

const logContainerStyle: any = { height: '450px' };

const editorOptions: any = {
    mode: 'text',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    lineWrapping: true,
    smartIndent: true,
    autoRefresh: true
};

// function wrappTitle (title: any) {
//     return `====================${title}====================`
// }

class RunningLogModal extends React.Component<any, any> {
    $editor: any;
    state: any = {
        indexData: null, // 指标数据
        logData: {}, // log数据
        spinning: false
    }
    componentDidUpdate (prevProps: any, prevState: any) {
        if (!prevProps.visible && this.props.visible) {
            this.fetchData();
            return true;
        }
        if (prevProps.visible && !this.props.visible) {
            this.setState({
                logData: {}
            });
            return true
        }
        return false;
    }

    fetchData = async () => {
        const { data } = this.props;
        if (!data) return;
        this.setState({
            spinning: true
        });
        const res = await API.getComponentRunningLog({ taskId: data.id });
        if (res.code === 1) {
            this.setState({
                logData: res.data
            })
        }
        this.setState({
            spinning: false
        });
        this.editorRefresh();
    }
    editorRefresh () {
        if (!this.$editor) {
            return;
        }
        window.setTimeout(() => {
            this.$editor.refresh();
        }, 300);
    }
    editorRef = (editor: any) => {
        this.$editor = editor;
    }

    renderLogContent = () => {
        const { runningIndexData, logData } = this.state;
        const { data } = this.props;
        const logText = prettifyLogText(logData.msg, logData.download);
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
                    <Editor style={{ height: '100%' }} editorRef={ this.editorRef } key={data && data.id} sync value={logText} options={editorOptions} />
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
                <Spin spinning={this.state.spinning}>
                    {this.renderLogContent()}
                </Spin>
            </Modal>
        )
    }
}

export default RunningLogModal;

const prettifyLogText = (message: any, downloadAddress: any) => {
    /**
     * 这里要多加一些空格后缀，不然codemirror计算滚动的时候会有问题
     */
    // const safeSpace = ' ';
    let log = '';
    try {
        log = message ? JSON.parse(message.replace(/\n/g, '\\n').replace(/\r/g, '\\r')) : '';
    } catch (err) {
        log = message;
    }

    let logText = `${log}\n`;
    if (downloadAddress) {
        logText += `完整日志下载地址：${createLinkMark({ href: downloadAddress, download: '' })}\n`;
    }

    return logText;
}
