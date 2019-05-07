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
        const { onRemoveTab, tabOptions, data } = this.props;
        const { activeKey, onChange } = tabOptions;
        if (onRemoveTab) {
            if (activeKey == targetKey && data.length > 1) {
                onChange(data.filter((tab) => {
                    return tab.id != targetKey
                }).slice(-1)[0].id);
            }
            onRemoveTab(targetKey);
        }
    };

    renderTabs (tabs) {
        const { isDisEabledDownload, tabOptions } = this.props;
        const { activeKey } = tabOptions;
        if (tabs && tabs.length > 0) {
            return tabs.map((tab, index) => {
                const { log, data, extData = {} } = tab;
                const tabType = log ? 'log' : 'data';
                const isShow = activeKey == tab.id;
                switch (tabType) {
                    case 'log': {
                        return <TabPane isShow={isShow} tab={extData.name} key={tab.id} closable={!extData.disableClose}>
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
                        const title = <span>{extData.name}</span>;
                        return (
                            <TabPane
                                style={{ minHeight: '100%', position: 'relative' }}
                                tab={title}
                                key={tab.id}
                                closable={!extData.disableClose}
                            >
                                <Result isShow={isShow} data={data} extraView={!isDisEabledDownload && tab.id && data ? (
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
                    forceRender={true}
                    hideAdd
                    type="editable-card"
                    onEdit={this.onEdit}
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
