import * as React from 'react';
import { connect } from 'react-redux';
import { Tabs } from 'antd';

import DiffCodeEditor from 'widgets/editor/diff';
import { TASK_TYPE } from '../../../comm/const';

const TabPane = Tabs.TabPane;

class DiffParams extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            tabKey: 'code'
        }
    }

    callback = (key: any) => {
        this.setState({
            tabKey: key
        })
    }

    codeChange = (old: any, newVal: any) => {
        this.props.setTaskParams(newVal)
    }
    getLanguge (taskType: any) {
        let language: any;
        switch (taskType) {
            case TASK_TYPE.SYNC: {
                language = 'json';
                break;
            }
            case TASK_TYPE.PYTHON_23: {
                language = 'python';
                break;
            }
            case TASK_TYPE.SQL: {
                language = 'dtsql';
                break;
            }
            default: {
                language = 'dtsql';
            }
        }
        return language;
    }
    render () {
        const { editor, versionData, currentData } = this.props;
        const {
            tabKey
        } = this.state;

        return <div className="m-taksdetail diff-params-modal" style={{ marginTop: '5px' }}>
            <Tabs onChange={this.callback} type="card" activeKey={tabKey}>
                <TabPane tab="代码" key="code">
                    <DiffCodeEditor
                        className="merge-text"
                        style={{ height: '500px' }}
                        original={{ value: currentData.sqlText }}
                        modified={{ value: versionData.sqlText }}
                        options={{ readOnly: true }}
                        onChange={this.codeChange}
                        language={this.getLanguge(currentData.taskType)}
                        theme={editor.options.theme}
                    />
                </TabPane>
                <TabPane tab="环境参数" key="params">
                    <DiffCodeEditor
                        language="ini"
                        className="merge-text"
                        style={{ height: '500px' }}
                        options={{ readOnly: true }}
                        sync={true}
                        theme={editor.options.theme}
                        modified={{ value: (versionData && versionData.taskParams) || ' ' }}
                        original={{ value: (currentData && currentData.taskParams) || ' ' }}
                    />
                </TabPane>
            </Tabs>
        </div>
    }
}

const mapState = (state: any) => {
    const { currentPage } = state.realtimeTask;
    return {
        currentRealTabData: currentPage,
        editor: state.editor
    };
};

export default connect(mapState)(DiffParams);
