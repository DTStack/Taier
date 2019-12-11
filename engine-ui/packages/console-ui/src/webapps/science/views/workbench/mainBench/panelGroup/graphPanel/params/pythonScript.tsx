import * as React from 'react';
import { Collapse, Form, message, Tabs } from 'antd';
// 内存设置组件
import { MemorySetting as BaseMemorySetting } from './typeChange';
// 编辑器面板组件
import ScriptPanel from 'science/components/scriptPanel';
// 输入源表组件
import InputSource from 'science/components/sourceScript';

import api from 'science/api/experiment';
import { TASK_ENUM, COMPONENT_TYPE } from 'science/consts';

const Panel = Collapse.Panel;
const TabPane = Tabs.TabPane;
let componentTitle = 'Python脚本';
// 内存设置
class MemorySetting extends BaseMemorySetting {
    constructor (props: any) {
        super(props)
    }
}
// main页面
class PythonScript extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            code: undefined,
            dirty: false
        }
    }
    static getDerivedStateFromProps (nextProps: any, prevState: any) {
        if (!prevState.dirty) {
            return {
                code: (nextProps.data && Object.keys(nextProps.data).length > 0) ? decodeURIComponent((window.atob(nextProps.data.python))) : ''
            }
        }
        return null
    }
    handleSaveComponent = async (field: any, fieldValue: any) => {
        const { currentTab, componentId, changeContent, data } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.DATA_TOOLS.PYTHON_SCRIPT];
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            [fieldName]: {
                ...data,
                [field]: field && fieldValue
            }
        }
        const res = await api.addOrUpdateTask(params);
        if (+res.code !== 1) return message.warning('保存失败');
        currentComponentData.data = { ...params, ...res.data };
        changeContent({}, currentTab);
    }
    handlePythonChange = (value: any) => {
        this.setState({
            code: value,
            dirty: true
        })
    }
    render () {
        const { dirty, code } = this.state;
        const { data } = this.props;
        const WrapMemorySetting = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                let fieldKeys = Object.keys(changedFields);
                fieldKeys.forEach(key => {
                    const ele = changedFields[key];
                    if (!ele.errors && !ele.validating && !ele.dirty) {
                        props.handleSave(key, ele.value)
                    }
                })
            },
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    workerMemory: { value: data.workerMemory },
                    workerCores: { value: data.workerCores }
                }
                return values;
            }
        })(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="Python" key="1">
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
                                        handleSave={() => this.handleSaveComponent('python', window.btoa(encodeURIComponent(code)))}
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
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting
                        data={data}
                        handleSave={this.handleSaveComponent}
                    />
                </TabPane>
            </Tabs>
        )
    }
}

export default PythonScript
