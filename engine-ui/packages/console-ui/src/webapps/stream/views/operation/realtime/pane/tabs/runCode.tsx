import * as React from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Form, Input, Radio, Tabs } from 'antd';
import Editor from 'widgets/editor'
import Address from './runcode/address';

import { formItemLayout, DATA_SOURCE, TASK_TYPE } from '../../../../../comm/const';
import { getTaskTypes as realtimeGetTaskTypes } from '../../../../../store/modules/realtimeTask/comm';
import utils from 'utils';
import ResultTable from './runcode/resultTable';

const { TextArea } = Input;
const FormItem = Form.Item;
const RadioGroup = Radio.Group
const TabPane = Tabs.TabPane;

@(connect((state: any) as any) => {
    return {
        taskTypes: state.realtimeTask.comm.taskTypes
    }
}, (dispatch: any) => {
    return bindActionCreators({
        realtimeGetTaskTypes
    }, dispatch);
})
class RunCode extends React.Component<any, any> {
    state: any = {
        tabKey: 'env'
    }

    componentDidMount () {
        console.log('RunCode')
        this.props.realtimeGetTaskTypes();
    }
    getRunCode () {
        const { data = {}, taskTypes = [] } = this.props;
        const {
            taskType, sqlText,
            taskDesc, name, mainClass, exeArgs, resourceList = []
        } = data;
        switch (taskType) {
            case TASK_TYPE.SQL:
            case TASK_TYPE.DATA_COLLECTION: {
                return (
                    <Editor
                        sync={true}
                        style={{ height: '100%' }}
                        language={this.getEditorLanguage(taskType)}
                        options={{ readOnly: true, minimap: { enabled: false } }}
                        value={sqlText}
                    />
                )
            }
            case TASK_TYPE.MR:
            default: {
                return (
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="任务名称"
                        >
                            <Input disabled value={name} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="任务类型"
                        >
                            <RadioGroup value={taskType} disabled>
                                {taskTypes.map((item: any) =>
                                    <Radio key={item.key} value={item.key}>{item.value}</Radio>
                                )}
                            </RadioGroup>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="资源"
                        >
                            <Input disabled value={resourceList && resourceList.length ? resourceList[0].resourceName : ''} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="mainClass"
                        >
                            <Input disabled value={mainClass} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="参数"
                        >
                            <Input disabled value={exeArgs} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="描述"
                        >
                            <TextArea disabled value={taskDesc} />
                        </FormItem>
                    </Form>
                )
            }
        }
    }
    getEditorLanguage(taskType: any) {
        switch (taskType) {
            case TASK_TYPE.SQL: {
                return 'sql'
            }
            case TASK_TYPE.DATA_COLLECTION: {
                return 'json'
            }
            default: {
                return 'ini'
            }
        }
    }
    changeTab(activeKey: any) {
        this.setState({
            tabKey: activeKey
        })
    }
    getJsonEditor(value: any) {
        value = utils.jsonFormat(value) || '';
        return <Editor
            sync={true}
            style={{ height: '100%' }}
            language="json"
            options={{ readOnly: true, minimap: { enabled: false } }}
            value={value}
        />
    }
    render () {
        const { tabKey } = this.state;
        const { data = {} } = this.props;
        const { taskType, originSourceType, targetSourceType } = data;
        const isShowAddress = taskType == TASK_TYPE.DATA_COLLECTION && originSourceType == DATA_SOURCE.BEATS;
        const isflinkSql = taskType == TASK_TYPE.SQL;
        const isShowResultTable = taskType == TASK_TYPE.DATA_COLLECTION && targetSourceType == DATA_SOURCE.HIVE;

        const editorBoxStyle: any = {
            position: 'absolute',
            top: '20px',
            bottom: '0px',
            left: '0px',
            right: '0px',
            width: '100%'
        }
        return (
            <div className="m-tabs" style={{ height: '100%' }}>
                <Tabs
                    className="nav-border content-border c-runcode"
                    animated={false}
                    tabBarStyle={{ background: 'transparent', borderWidth: '0px' }}
                    onChange={this.changeTab.bind(this)}
                    value={tabKey}
                >
                    <TabPane className="m-panel2" tab="运行代码" key="code">
                        <div style={editorBoxStyle}>
                            {this.getRunCode()}
                        </div>
                    </TabPane>
                    {isflinkSql && [
                        <TabPane className="m-panel2" tab="源表" key="source">
                            <div style={editorBoxStyle}>
                                {this.getJsonEditor(data.sourceParams)}
                            </div>
                        </TabPane>,
                        <TabPane className="m-panel2" tab="结果表" key="sink">
                            <div style={editorBoxStyle}>
                                {this.getJsonEditor(data.sinkParams)}
                            </div>
                        </TabPane>,
                        <TabPane className="m-panel2" tab="维表" key="side">
                            <div style={editorBoxStyle}>
                                {this.getJsonEditor(data.sideParams)}
                            </div>
                        </TabPane>
                    ]}
                    {isShowResultTable && (
                        <TabPane className="m-panel2" tab="结果表" key="resultTable">
                            <ResultTable key={data.id} taskId={data.id} />
                        </TabPane>
                    )}
                    <TabPane className="m-panel2" tab="环境参数" key="env">
                        <div style={editorBoxStyle}>
                            <Editor
                                sync={true}
                                style={{ height: '100%' }}
                                language="ini"
                                options={{ readOnly: true, minimap: { enabled: false } }}
                                value={data.taskParams}
                            />
                        </div>
                    </TabPane>
                    {isShowAddress && (
                        <TabPane className="m-panel2" tab="运行地址" key="address">
                            <Address style={editorBoxStyle} taskId={data.id} />
                        </TabPane>
                    )}
                </Tabs>
            </div>
        )
    }
}

export default RunCode;
