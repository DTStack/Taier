import React from "react"

import { Collapse } from "antd";

import Editor from "widgets/editor"
import { TASK_TYPE } from "../../../../../../stream/comm/const";

const Panel = Collapse.Panel;

class RunCode extends React.Component {

    componentDidMount() {
        console.log("RunCode")
    }
    getRunCode() {
        const { data = {} } = this.props;
        const { taskType, sqlText, taskDesc } = data;
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
                    <span>
                        MR:{taskDesc}
                    </span>
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