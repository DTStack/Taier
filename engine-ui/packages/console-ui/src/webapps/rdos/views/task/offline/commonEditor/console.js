import React, { Component } from 'react'
import { isEqual } from 'lodash'
import { Table, Tabs, Icon, Tooltip, Button, Affix } from 'antd'

import reqOffline from '../../../../api/reqOffline';

import CodeEditor from '../../../../components/code-editor'

import {
    removeRes, resetConsole
} from '../../../../store/modules/editor/editorAction'

// import { isEqual } from 'utils/pureRender'

const TabPane = Tabs.TabPane

const editorOptions = {
    mode: 'text/x-sh',
    lineNumbers: false,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true,
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
            this.props.changeTab(0);
        }
        else if (
            (
            // (
            //     !newConsole.showRes &&
            //     newConsole.log !== oldConsole.log
            // ) ||
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
                this.props.changeTab(1);
            }else{
                this.props.changeTab(0);
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

    render() {
        const { data, dispatch, setMax, setMin,changeTabStatus } = this.props
        const activeKey = changeTabStatus ? "console-log" : this.state.activeKey
        return (
            <div className="ide-console">
                <Tabs
                    hideAdd
                    type="editable-card"
                    activeKey={activeKey}
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
                </Tabs>
                
                <Icon onClick={setMax} className="console-icon up" type="up-square-o" />
                <Icon onClick={setMin} className="console-icon down" type="down-square-o" />
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

