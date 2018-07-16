import React from "react";
import { Card, Form, Input, Row, Col } from "antd";
import { cloneDeep } from "lodash";

import { longLabelFormLayout } from "../../../consts"
import GoBack from "main/components/go-back";

const FormItem = Form.Item;
const TextArea = Input.TextArea;

class EditCluster extends React.Component {
    state = {
        hdfsNameservices: "ns1",
        hdfsNamenodes: "nn1,nn2",
        hdfsParams: [],
        yarnRmIds:"rm1,rm2"
    }
    componentDidMount() {
        const { location } = this.props;
        const params = location.state;
    }
    addHdfsParams() {
        const { hdfsParams } = this.state;
        this.setState({
            hdfsParams: hdfsParams.concat({
                id: ~~(Math.random() * 1000000),
                name: "",
                value: ""
            })
        })
    }
    removeHdfsParams(value) {
        const { hdfsParams } = this.state;
        hdfsParams.splice(value,1);
        this.setState({
            hdfsParams: hdfsParams
        })
    }
    changeHdfsParam(index,param,event){
        const { hdfsParams } = this.state;
        let newParams=cloneDeep(hdfsParams);
        newParams[index][param]=event.target.value
        this.setState({
            hdfsParams:newParams
        })
    }
    getHdfsParams() {
        const { getFieldDecorator } = this.props.form;
        const { hdfsParams } = this.state;
        return hdfsParams.map(
            (item,index) => {
                return (
                    <div>
                        <Row style={{ marginBottom: "24px" }}>
                            <Col offset={0} span={10}>
                                <Input value={item.name} onChange={this.changeHdfsParam.bind(this,index,"name")} style={{ width: "270px" }} /><span style={{ marginLeft: "2px" }}>:</span>
                            </Col>
                            <Col span={12}>
                                <Input value={item.value} onChange={this.changeHdfsParam.bind(this,index,"value")} />
                            </Col>
                            <a style={{ lineHeight: "32px", marginLeft: "25px" }} onClick={this.removeHdfsParams.bind(this, index)}>删除</a>
                        </Row>
                    </div>
                )
            }
        )
    }
    getSplitFormItem(value, prefix, type) {
        const { getFieldDecorator } = this.props.form;
        const { hdfsNameservices } = this.state;
        const arr = value.split(",");
        const result = [];

        for (let i = 0; i < arr.length; i++) {
            let item = arr[i];
            if (type == "hdfs") {
                result.push(<FormItem
                    {...longLabelFormLayout}
                    label={`${prefix}.${hdfsNameservices}.${item}`}
                >
                    {getFieldDecorator(`${prefix.replace(/\./g, "-")}-${hdfsNameservices}-${item}`, {
                        rules: [{
                            required: true,
                            message: "请填写参数"
                        }],
                        initialValue: "kudu2:9000"
                    })(
                        <Input />
                    )}
                </FormItem>)
            } else {
                const number=item&&item[item.length-1];
                let defaultNumber='';
                if(number||number==0){
                    defaultNumber=1+parseInt(number);
                }
                result.push(<FormItem
                    {...longLabelFormLayout}
                    label={`${prefix}.${item}`}
                >
                    {getFieldDecorator(`${prefix.replace(/\./g, "-")}-${item}`, {
                        rules: [{
                            required: true,
                            message: "请填写参数"
                        }],
                        initialValue:`kudu${defaultNumber}:8032`
                    })(
                        <Input />
                    )}
                </FormItem>)
            }
        }
        return result;
    }
    render() {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { mode } = this.props.location;
        const { hdfsNameservices, hdfsNamenodes, yarnRmIds } = this.state;

        return (
            <div className="contentBox">
                <p className="box-title" style={{ paddingLeft: "0px" }}><GoBack size="default" type="textButton"></GoBack></p>
                <div className="contentBox">
                    <p className="config-title">HDFS</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FormItem
                            {...longLabelFormLayout}
                            label="HDFS nameservices"
                        >
                            {getFieldDecorator('hdfs-nameservices', {
                                rules: [{
                                    required: true,
                                    message: "请填写nameservices"
                                }],
                                initialValue: hdfsNameservices
                            })(
                                <Input onChange={(event) => {
                                    this.setState({
                                        hdfsNameservices: event.target.value
                                    })
                                }} />
                            )}
                        </FormItem>
                        <FormItem
                            {...longLabelFormLayout}
                            label="fs.defaultFS"
                        >
                            {getFieldDecorator('hdfs-fs-defaultFS', {
                                rules: [{
                                    required: true,
                                    message: "请填写 fs.defaultFS"
                                }],
                                initialValue: "hdfs://ns1"
                            })(
                                <Input />
                            )}
                        </FormItem>
                        <FormItem
                            {...longLabelFormLayout}
                            label={`dfs.ha.namenodes.${hdfsNameservices}`}
                        >
                            {getFieldDecorator('hdfs-dfs-ha-namenodes', {
                                rules: [{
                                    required: true,
                                    message: "请填写参数"
                                }],
                                initialValue: hdfsNamenodes
                            })(
                                <Input onChange={(event) => {
                                    this.setState({
                                        hdfsNamenodes: event.target.value
                                    })
                                }} />
                            )}
                        </FormItem>
                        {this.getSplitFormItem(hdfsNamenodes, "dfs.namenode.rpc-address", "hdfs")}
                        <FormItem
                            {...longLabelFormLayout}
                            label={`dfs.client.failover.proxy.provider.${hdfsNameservices}`}
                        >
                            {getFieldDecorator('hdfs-dfs-client-failover-proxy-provider', {
                                initialValue: "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
                            })(
                                <TextArea autosize={{ minRows: 2, maxRows: 4 }} />
                            )}
                        </FormItem>
                        {this.getHdfsParams()}
                        <Row>
                            <Col offset={10}><a onClick={this.addHdfsParams.bind(this)}>添加自定义参数</a></Col>
                        </Row>
                    </div>
                    <p className="config-title">YARN</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FormItem
                            {...longLabelFormLayout}
                            label="yarn.resourcemanager.ha.rm-ids"
                        >
                            {getFieldDecorator('yarn-resourcemanager-ha-rm-ids', {
                                rules: [{
                                    required: true,
                                    message: "请填写参数"
                                }],
                                initialValue: yarnRmIds
                            })(
                                <Input onChange={(event) => {
                                    this.setState({
                                        yarnRmIds: event.target.value
                                    })
                                }} />
                            )}
                        </FormItem>
                        {this.getSplitFormItem(yarnRmIds, "yarn.resourcemanager.address", "yarn")}
                        {this.getSplitFormItem(yarnRmIds, "yarn.resourcemanager.webapp.address", "yarn")}
                      
                        {this.getHdfsParams()}
                        <Row>
                            <Col offset={10}><a onClick={this.addHdfsParams.bind(this)}>添加自定义参数</a></Col>
                        </Row>
                    </div>
                </div>
            </div>
        )
    }
}
export default Form.create()(EditCluster);