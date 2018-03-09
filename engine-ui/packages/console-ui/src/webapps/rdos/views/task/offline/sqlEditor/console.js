import React, { Component } from 'react'
import { Table, Tabs, Icon, Tooltip, Button } from 'antd'

import CodeEditor from '../../../../components/code-editor'
import { 
    removeRes, resetConsole 
} from '../../../../store/modules/offlineTask/sqlEditor'

const TabPane = Tabs.TabPane

const editorOptions = {
    mode: 'text/x-sql',
    lineNumbers: false,
    readOnly: true,
    autofocus: true,
    indentWithTabs: true,
    smartIndent: true,
}

const exportStyle = {
    position: 'fixed',
    bottom: '0px',
    height: '30px',
}

export default class Console extends Component {

    state = {
        activeKey: 'console-log',
    }

    componentWillReceiveProps(nextProps) {
        const newConsole = nextProps.data
        if (newConsole.results && newConsole.results.length > 0 ) { // 如果成功获取结果，tab切换到结果界面
            this.setState({ activeKey: `${newConsole.results.length - 1}` })
        }
        this.appendLog(newConsole.log)
    }

    onEdit = (targetKey, action) => {
        this[action](targetKey);
    }

    onChange = (activeKey) => {
        this.setState({activeKey})
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
        currentData.forEach((row, i) => {
            const dataStr = row.join(',')
            csvContent += i < currentData.length ? 
            dataStr + '\n' : dataStr;
        })
        // var encodedUri = encodeURI(csvContent);
        // window.open(encodedUri);
        var blob = new Blob([csvContent]);
        if (window.navigator.msSaveOrOpenBlob)  // IE hack; see http://msdn.microsoft.com/en-us/library/ie/hh779016.aspx
            window.navigator.msSaveBlob(blob, "下载.csv");
        else
        {
            var a = window.document.createElement("a");
            a.href = window.URL.createObjectURL(blob, {type: "text/plain"});
            a.download = "下载.csv";
            document.body.appendChild(a);
            a.click();  // IE: "Access is denied"; see: https://connect.microsoft.com/IE/feedback/details/797361/ie-10-treats-blob-url-as-cross-origin-and-denies-access
            document.body.removeChild(a);
        }
    }



    renderTabs(tabs) {
        if (tabs && tabs.length > 0) {
            return tabs.map((tab, index) => {
                const title = (<span>
                    结果{index+1}
                </span>);
                return (
                    <TabPane
                      style={{ height: '0px' }}
                      tab={title}
                      key={`${index}`}
                    >
                        <Result data={tab} />
                        <Button 
                            style={exportStyle} 
                            onClick={this.exportCsv}
                        >
                            下载
                        </Button>
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
                                ref={(e) => { this.editor = e }}
                                options={editorOptions} 
                                key="output-log" 
                            />
                        </div>
                    </TabPane>
                    { this.renderTabs(data.results) }
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

function generateCols(data) {
    if (data && data.length > 0) {
        const arr = [{
            title: '序号',
            key: 't-id',
            render: (text, item, index) => {
                return index + 1
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

function Result(props) {
    const data = props.data
    const showData = data.slice(1, data.length)
    const columns = generateCols(data[0])
    return (
        <Table
            rowKey="id"
            scroll={{ x: true }} 
            className="console-table"
            bordered 
            dataSource={showData} 
            columns={columns}
        />
    )
}
