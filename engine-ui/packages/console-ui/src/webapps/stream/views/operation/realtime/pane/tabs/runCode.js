import React from "react"
import { connect } from "react-redux"
import { bindActionCreators } from 'redux'

import { Collapse, Form, Input, Radio } from "antd";
import Editor from "widgets/editor"

import { TASK_TYPE } from "../../../../../comm/const";
import { formItemLayout } from "../../../../../comm/const";
import { getTaskTypes as realtimeGetTaskTypes } from '../../../../../store/modules/realtimeTask/comm';

const { TextArea } = Input;
const Panel = Collapse.Panel;
const FormItem = Form.Item;
const RadioGroup = Radio.Group

@connect(state => {
    return {
        taskTypes: state.realtimeTask.comm.taskTypes,
    }
}, dispatch => {
    return bindActionCreators({
        realtimeGetTaskTypes
    }, dispatch);
})
class RunCode extends React.Component {

    componentDidMount() {
        console.log("RunCode")
        this.props.realtimeGetTaskTypes();
    }
    getRunCode() {
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
                        style={{ height: "400px" }}
                        options={{ readOnly: false }}
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
                                {taskTypes.map(item =>
                                    <Radio key={item.key} value={item.key}>{item.value}</Radio>
                                )}
                            </RadioGroup>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="资源"
                        >
                            <Input disabled value={resourceList&&resourceList.length ? resourceList[0].resourceName : ""} />
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
    getEditorLanguage(taskType) {
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
    render() {
        const { data } = this.props;
        return (
            <div>
                <Collapse className="middle-collapse" defaultActiveKey={['code', 'env']}>
                    <Panel header="运行代码" key="code">
                        {this.getRunCode()}
                    </Panel>
                    <Panel header="环境参数" key="env">
                        <Editor
                            sync={true}
                            style={{ height: "400px" }}
                            options={{ readOnly: false }}
                            language="ini"
                            options={{ readOnly: true, minimap: { enabled: false } }}
                            value={data.taskParams}
                        />
                    </Panel>
                </Collapse>
            </div>
        )
    }
}

export default RunCode;