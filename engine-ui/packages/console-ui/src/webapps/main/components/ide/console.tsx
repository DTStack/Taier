import * as React from 'react';
import { isEqual } from 'lodash';
import { Tabs, Icon, Tooltip, Button } from 'antd';

import Result from './result';
import CodeEditor from 'widgets/code-editor';
import { defaultEditorOptions } from 'widgets/code-editor/config';

const TabPane = Tabs.TabPane;

const defaultConsoleTab = 'console-log';

class Console extends React.Component<any, any> {
    public editor: any;
    state = {
        activeKey: defaultConsoleTab
    };
    /* eslint-disable */
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const newConsole = nextProps.data;
        const oldConsole = this.props.data;
        if (!newConsole.showRes && oldConsole.showRes) {
            this.setState({
                activeKey: defaultConsoleTab
            })
        }
        if (
            newConsole.showRes &&
            newConsole.results.length > 0 &&
            !isEqual(newConsole.results, oldConsole.results)
        ) {
            // 如果成功获取结果，tab切换到结果界面
            this.setState({ activeKey: `${newConsole.results.length - 1}` });
            this.props.onConsoleTabChange(0);
        } else if (newConsole.results.length === 0) {
            this.setState({ activeKey: defaultConsoleTab });
        }
    }
    /* eslint-disable */

    onEdit = (targetKey: any, action: any) => {
        this[action as keyof Console](targetKey);
    };

    onTabChange = (activeKey: any) => {
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

    remove = (targetKey: any) => {
        const { onRemoveTab } = this.props;

        if (onRemoveTab) onRemoveTab(parseInt(targetKey, 10));
    };

    renderTabs (tabs: any) {
        const { isDisEabledDownload } = this.props;
        const { activeKey }  = this.state;
        if (tabs && tabs.length > 0) {
            return tabs.map((tab: any, index: number) => {
                const title = <span>结果{tab.id ? tab.id : (index + 1)}</span>;

                return (
                    <TabPane
                        style={{ minHeight: '100%', position: 'relative' }}
                        tab={title}
                        key={`${index}`}
                    >
                        <Result isShow={index+'' == activeKey} data={tab.data} extraView={!isDisEabledDownload && tab.jobId && tab.data ? (
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
            <div className="ide-console" style={{ zIndex: 10 }}>
                <Tabs
                    hideAdd
                    type="editable-card"
                    activeKey={activeKey}
                    onChange={this.onTabChange}
                    onEdit={this.onEdit}
                >
                    <TabPane tab="日志" key="console-log">
                        <div style={{ position: 'relative', height: '100%' }}>
                            <CodeEditor
                                style={{ minHeight: 'auto', height: '100%' }}
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
