import React, { Component } from 'react';
import { Tabs, Icon, Tooltip, Button } from 'antd';

import Result from './result';
import CodeEditor from 'widgets/code-editor';
import { defaultEditorOptions } from 'widgets/code-editor/config';

const TabPane = Tabs.TabPane;

class Console extends Component {
    onEdit = (targetKey, action) => {
        this[action](targetKey);
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
        if (tabs && tabs.length > 0) {
            return tabs.map((tab, index) => {
                const { log, data, extData = {} } = tab;
                const tabType = log ? 'log' : 'data';
                switch (tabType) {
                    case 'log': {
                        return <TabPane tab={extData.name} key={tab.id} closable={!extData.disableClose}>
                            <div style={{ position: 'relative' }}>
                                <CodeEditor
                                    style={{ minHeight: 'auto' }}
                                    ref={e => {
                                        this.editor = e;
                                    }}
                                    options={{ ...defaultEditorOptions, mode: 'dtlog' }}
                                    key="output-log"
                                    sync={true}
                                    value={log}
                                />
                            </div>
                        </TabPane>
                    }
                    default: {
                        const title = <span>结果{tab.id ? tab.id : (index + 1)}</span>;
                        return (
                            <TabPane
                                style={{ minHeight: '100%', position: 'relative' }}
                                tab={title}
                                key={`${index}`}
                                closable={!extData.disableClose}
                            >
                                <Result r={Math.random()} data={data} extraView={!isDisEabledDownload && tab.id && data ? (
                                    <a
                                        href={`${this.props.downloadUri}?jobId=${
                                            tab.id
                                        }`}
                                        download
                                    >
                                        <Button className="btn-download">下载</Button>
                                    </a>
                                ) : null} />

                            </TabPane>
                        );
                    }
                }
            });
        }
        return null;
    }

    render () {
        const {
            data = [],
            setSplitMax,
            setSplitMin,
            onConsoleClose,
            tabOptions
        } = this.props;

        defaultEditorOptions.readOnly = true;
        defaultEditorOptions.lineNumbers = false;

        return (
            <div className="ide-console" style={{ zIndex: 10 }}>
                <Tabs
                    hideAdd
                    type="editable-card"
                    {...tabOptions}
                >
                    {this.renderTabs(data)}
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
