import React, { Component } from 'react';
import { isEqual } from 'lodash';
import { Tabs, Icon, Tooltip, Button } from 'antd';

import Result from './result';
import CodeEditor from 'widgets/code-editor';
import { defaultEditorOptions } from 'widgets/code-editor/config';

const TabPane = Tabs.TabPane;

const defaultConsoleTab = 'console-log';

class Console extends Component {
    state = {
        activeKey: defaultConsoleTab
    };
    /* eslint-disable */
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const newConsole = nextProps.data;
        const oldConsole = this.props.data;

        if (
            newConsole.showRes &&
            newConsole.results.length > 0 &&
            !isEqual(newConsole.results, oldConsole.results)
        ) {
            // 如果成功获取结果，tab切换到结果界面
            this.setState({ activeKey: `${newConsole.results.length - 1}` });
            this.props.onConsoleTabChange(0);
        } else if (newConsole.results.length === 0) {
            this.setState({ activeKey: defaultConsoleTab }, this.focusEditor);
        }
    }
    /* eslint-disable */

    onEdit = (targetKey, action) => {
        this[action](targetKey);
    };

    onTabChange = activeKey => {
        const { onConsoleTabChange } = this.props;

        this.setState({ activeKey }, () => {
            if (activeKey === defaultConsoleTab) {
                this.focusEditor();
                onConsoleTabChange(1);
            } else {
                onConsoleTabChange(0);
            }
        });
    };

    focusEditor = () => {
        const editor = this.editor.self;
        const doc = editor.doc;

        doc.setCursor(editor.lineCount(), null); // 控制滚动条在底部
    };

    remove = targetKey => {
        const { onRemoveTab } = this.props;

        if (onRemoveTab) onRemoveTab(parseInt(targetKey, 10));
    };

    renderTabs (tabs) {
        const { isDisEabledDownload } = this.props;
        const { activeKey }  = this.state;
        if (tabs && tabs.length > 0) {
            return tabs.map((tab, index) => {
                const title = <span>结果{tab.id ? tab.id : (index + 1)}</span>;

                return (
                    <TabPane
                        style={{ minHeight: '100%', position: 'relative' }}
                        tab={title}
                        key={`${index}`}
                    >
                        <Result isShow={index==activeKey} data={tab.data} extraView={!isDisEabledDownload && tab.jobId && tab.data ? (
                            <a
                                href={`${this.props.downloadUri}?jobId=${
                                    tab.jobId
                                    }`}
                                download
                            >
                                <Button className="btn-download">下载</Button>
                            </a>
                        ) : null} />

                    </TabPane>
                );
            });
        }

        return [];
    }

    render () {
        const {
            data,
            setSplitMax,
            setSplitMin,
            activedTab,
            onConsoleClose
        } = this.props;

        const activeKey = activedTab ? 'console-log' : this.state.activeKey;

        defaultEditorOptions.readOnly = true;
        defaultEditorOptions.lineNumbers = false;

        return (
            <div className="ide-console" style={{ zIndex: 999 }}>
                <Tabs
                    hideAdd
                    type="editable-card"
                    activeKey={activeKey}
                    onChange={this.onTabChange}
                    onEdit={this.onEdit}
                >
                    <TabPane tab="日志" key="console-log">
                        <div style={{ position: 'relative' }}>
                            <CodeEditor
                                style={{ minHeight: 'auto' }}
                                ref={e => {
                                    this.editor = e;
                                }}
                                options={{ ...defaultEditorOptions, mode: 'dtlog' }}
                                key="output-log"
                                sync={true}
                                value={data.log}
                            />
                        </div>
                    </TabPane>
                    {this.renderTabs(data.results)}
                </Tabs>

                <Icon
                    onClick={setSplitMax}
                    className="console-icon up"
                    type="up-square-o"
                />
                <Icon
                    onClick={setSplitMin}
                    className="console-icon down"
                    type="down-square-o"
                />
                <Tooltip placement="top" title="关闭控制台">
                    <Icon
                        className="close-console"
                        type="close"
                        onClick={onConsoleClose}
                    />
                </Tooltip>
            </div>
        );
    }
}

export default Console;
