import React from "react";
import { Card, Form, Input, Row, Col } from "antd";
import { cloneDeep } from "lodash";

import { longLabelFormLayout,formItemLayout } from "../../../consts"
import GoBack from "main/components/go-back";

const FormItem = Form.Item;
const TextArea = Input.TextArea;

class EditCluster extends React.Component {
    state = {

    }
    componentDidMount() {
        const { location } = this.props;
        const params = location.state;
    }
    render() {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { mode } = this.props.location;

        return (
            <div className="contentBox">
                <p className="box-title" style={{ paddingLeft: "0px" }}><GoBack size="default" type="textButton"></GoBack></p>
                <div className="contentBox">
                    <p className="config-title">集群与租户信息</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FormItem
                            label="集群标识"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('clusterName', {
                                rules: [{
                                    required: true,
                                    message: "请输入集群标识"
                                }]
                            })(
                                <Input />
                            )}
                        </FormItem>
                    </div>
                    <p className="config-title">上传配置文件</p>
                    <div className="config-content" style={{ width: "680px" }}>

                    </div>
                    <p className="config-title">HDFS</p>
                    <div className="config-content" style={{ width: "680px" }}>

                    </div>
                    <p className="config-title">YARN</p>
                    <div className="config-content" style={{ width: "680px" }}>

                    </div>
                    <p className="config-title">Hive JDBC信息</p>
                    <div className="config-content" style={{ width: "680px" }}>

                    </div>
                    <p className="config-title">Spark</p>
                    <div className="config-content" style={{ width: "680px" }}>

                    </div>
                    <p className="config-title">Flink</p>
                    <div className="config-content" style={{ width: "680px" }}>

                    </div>
                </div>
            </div>
        )
    }
}
export default Form.create()(EditCluster);