import React from "react"

import { Collapse } from "antd";

import Editor from "widgets/editor"

const Panel = Collapse.Panel;

class RunCode extends React.Component {

    componentDidMount() {
        console.log("RunCode")
    }

    render() {
        const { data } = this.props;
        return (
            <div>
                <Collapse className="middle-collapse" defaultActiveKey={['code', 'env']}>
                    <Panel header="运行代码" key="code">
                        <Editor
                            sync={true}
                            style={{ height: "400px" }}
                            options={{ readOnly: false }}
                            language="dtsql"
                            options={{ readOnly: true, minimap: { enabled: false } }}
                            value={data.sqlText} 
                        />
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