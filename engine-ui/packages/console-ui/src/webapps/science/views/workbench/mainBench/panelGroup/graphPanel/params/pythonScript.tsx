import * as React from 'react';
import { Collapse, message } from 'antd';
// 编辑器面板组件
import ScriptPanel from 'science/components/scriptPanel';
// 输入源表组件
import InputSource from 'science/components/sourceScript';

import api from 'science/api/experiment';
import { TASK_ENUM, COMPONENT_TYPE } from 'science/consts';

const Panel = Collapse.Panel;
let componentTitle = 'Python脚本';

class PythonScript extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            code: undefined,
            dirty: false
        }
        this.handleSavePython = this.handleSavePython.bind(this)
        this.handlePythonChange = this.handlePythonChange.bind(this)
    }
    static getDerivedStateFromProps (nextProps: any, prevState: any) {
        if (!prevState.dirty) {
            return {
                code: nextProps.data ? nextProps.data.python : ''
            }
        }
        return null
    }
    handleSavePython () {
        const { currentTab, componentId, changeContent, data } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.DATA_TOOLS.PYTHON_SCRIPT];
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            [fieldName]: {
                ...data,
                python: this.state.code
            }
        }
        api.addOrUpdateTask(params).then((res: any) => {
            if (res.code == 1) {
                currentComponentData.data = { ...params, ...res.data };
                changeContent({}, currentTab);
            } else {
                message.warning('保存失败');
            }
        })
    }
    handlePythonChange (value: any) {
        this.setState({
            code: value,
            dirty: true
        })
    }
    render () {
        const { dirty, code } = this.state;
        const { data } = this.props;
        return (
            <div className="params-single-tab">
                <div className="c-panel__siderbar__header">
                    { componentTitle }
                </div>
                <div className="params-single-tab-content">
                    <Collapse
                        bordered={false}
                        defaultActiveKey={['1', '2']}
                        className="params-collapse">
                        <Panel key="1" header="输入源">
                            <InputSource
                                data={data}
                            />
                        </Panel>
                        <Panel
                            key="2"
                            header={componentTitle}
                            {...{ id: 'python-editor' }}
                            style={{ height: 400, background: '#f5f5f5', paddingTop: 10 }}
                        >
                            <ScriptPanel
                                handleSaveScript={this.handleSavePython}
                                handleChange={this.handlePythonChange}
                                dirty={dirty}
                                language="python"
                                target="python-editor"
                                code={code}
                                enable={false}
                            >
                            </ScriptPanel>
                        </Panel>
                    </Collapse>
                </div>
            </div>
        )
    }
}

export default PythonScript
