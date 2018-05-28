import React, { Component } from 'react'
import { isEqual } from 'lodash'
import { Table, Tabs, Icon, Tooltip, Button, Affix } from 'antd'

import Api from '../../../../api';

import CodeEditor from '../../../../components/code-editor'
import {
    removeRes, resetConsole
} from '../../../../store/modules/offlineTask/sqlEditor'

// import { isEqual } from 'utils/pureRender'

const TabPane = Tabs.TabPane

const editorOptions = {
    mode: 'text/x-sql',
    lineNumbers: false,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true,
}

const exportStyle = {
    position: 'absolute',
    bottom: '0px',
    height: '30px',
}


class Result extends Component {

    state = {
        currentPage: 1,
    }

    onChange = (page) => {
        this.setState({
            currentPage: page.current,
        })
    }

    generateCols(data) {
        const { currentPage } = this.state
        if (data && data.length > 0) {
            const arr = [{
                title: '序号',
                key: 't-id',
                render: (text, item, index) => {
                    return (currentPage - 1) * 10 + (index + 1)
                },
            }]
            data.forEach((item, index) => {
                arr.push({
                    title: item,
                    key: index + item,
                    render: (text, item) => {
                        return <textarea title={item[index]} value={item[index]} />
                    },
                })
            })
            return arr
        }
        return []
    }

    render() {
        const data = this.props.data
        const showData = data.slice(1, data.length)
        const columns = this.generateCols(data[0])
        return (
            <Table
                rowKey="id"
                scroll={{ x: true }}
                className="console-table"
                bordered
                dataSource={showData}
                onChange={this.onChange}
                columns={columns}
            />
        )
    }
}

class Console extends Component {

    state = {
        activeKey: 'console-log',
    }

    componentWillReceiveProps(nextProps) {
        const newConsole = nextProps.data;
        const oldConsole = this.props.data;
        if (
            newConsole.showRes &&
            newConsole.results.length > 0 &&
            !isEqual(newConsole.results, oldConsole.results)
        ) { // 如果成功获取结果，tab切换到结果界面
            this.setState({ activeKey: `${newConsole.results.length - 1}` })
        } else if (
            ((
                !newConsole.showRes &&
                newConsole.log !== oldConsole.log
            ) ||
                newConsole.results.length === 0)
        ) {
            this.setState({ activeKey: `console-log` }, this.focusEditor)
        }
    }

    onEdit = (targetKey, action) => {
        this[action](targetKey);
    }

    onChange = (activeKey) => {
        this.setState({ activeKey }, () => {
            if (activeKey === 'console-log') {
                this.focusEditor();
            }
        })
    }

    focusEditor = () => {
        const editor = this.editor.self
        // editor.focus();
        const doc = editor.doc
        doc.setCursor(editor.lineCount(), null) // 控制滚动条在底部
    }

    remove = (targetKey) => {
        const { currentTab, dispatch } = this.props
        dispatch(removeRes(currentTab, parseInt(targetKey, 10)))
    }

    closeConsole = () => {
        const { currentTab, dispatch } = this.props
        dispatch(resetConsole(currentTab))
    }

    appendLog = (log) => {
        if (log) {
            const editor = this.editor.self
            const doc = editor.doc
            doc.setValue(log)
            doc.setCursor(doc.lineCount(), null) // 控制滚动条在底部
        }
    }

    exportCsv = () => {
        const { results } = this.props.data
        const index = parseInt(this.state.activeKey, 10)
        const currentData = results[index]
        let csvContent = "";
        let downloadName = `结果${parseInt(index) + 1}.csv`;

        currentData.forEach((row, i) => {
            const dataStr = row.join(',')
            csvContent += i < currentData.length ?
                dataStr + '\n' : dataStr;
        })
        // var encodedUri = encodeURI(csvContent);
        // window.open(encodedUri);
        var blob = new Blob([csvContent]);
        if (window.navigator.msSaveOrOpenBlob)  // IE hack; see http://msdn.microsoft.com/en-us/library/ie/hh779016.aspx
            window.navigator.msSaveBlob(blob, downloadName);
        else {
            var a = window.document.createElement("a");
            a.href = window.URL.createObjectURL(blob, { type: "text/plain" });
            a.download = downloadName;
            document.body.appendChild(a);
            a.click();  // IE: "Access is denied"; see: https://connect.microsoft.com/IE/feedback/details/797361/ie-10-treats-blob-url-as-cross-origin-and-denies-access
            document.body.removeChild(a);
        }
    }

    downloadResult(jobId){
        Api.downloadSqlExeResult({
            jobId:jobId
        });
    }

    renderTabs(tabs) {
        if (tabs && tabs.length > 0) {
            return tabs.map((tab, index) => {
                const title = (<span>
                    结果{index + 1}
                </span>);
                return (
                    <TabPane
                        style={{ minHeight: '100%',"position":"relative" }}
                        tab={title}
                        key={`${index}`}
                    >
                        <Result data={tab.data} />
                        {tab.jobId?<Button
                            style={exportStyle}
                            onClick={this.downloadResult.bind(this,tab.jobId)}
                        >
                            下载
                        </Button>:null}
                    </TabPane>
                );
            });
        }
        return []
    }

    render() {
        const { data, dispatch } = this.props
        return (
            <div className="ide-console">
                <Tabs
                    hideAdd
                    type="editable-card"
                    activeKey={this.state.activeKey}
                    onChange={this.onChange}
                    onEdit={this.onEdit}
                >
                    <TabPane tab="日志" key="console-log">
                        <div style={{ position: 'relative' }}>
                            <CodeEditor
                                cursorAlwaysInEnd
                                style={{ minHeight: "auto" }}
                                ref={(e) => { this.editor = e }}
                                options={editorOptions}
                                key="output-log"
                                sync={true}
                                value={data.log}
                            />
                        </div>
                    </TabPane>
                    {this.renderTabs(data.results)}
                </Tabs>
                <Tooltip
                    placement="top"
                    title="关闭控制台"
                >
                    <Icon
                        className="close-console"
                        type="close"
                        onClick={this.closeConsole}
                    />
                </Tooltip>
            </div>
        )
    }
}

export default Console

