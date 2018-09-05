import React from "react";
import { Modal, Form, Input, Select, Row, Col } from "antd";

import Api from "../../api"
import { formItemLayout } from "../../comm/const";
import { ExtTableCell } from "./extDataSourceMsg"

const FormItem = Form.Item;
const Option = Select.Option;

class LinkModal extends React.Component {
    state = {
        confirmLoading: false,
        targetList: []
    }

    getTargetList(sourceId) {
        const { type } = this.props;
        Api.getLinkSourceList({
            dataSourceId:sourceId
        },type)
        .then(
            (res)=>{
                if(res.code==1){
                    this.setState({
                        targetList:[].concat(res.data.linkProjectSources).concat([res.data.linkSource]).filter(Boolean)
                    })
                }
            }
        )
    }

    componentDidMount() {
        if (this.props.sourceData&&this.props.sourceData.id) {
            this.getTargetList(this.props.sourceData.id);
        }
    }

    componentWillReceiveProps(nextProps) {
        const { visible, sourceData } = nextProps
        const { visible: oldVisible } = this.props;
        if (oldVisible != visible && visible) {
            this.getTargetList(sourceData.id);
        }
    }

    onCancel() {
        this.props.form.resetFields();
        this.setState({
            confirmLoading: false
        })
        this.props.onCancel();
    }
    linkSource() {
        const { sourceData, type } = this.props;
        this.props.form.validateFields(null, (err, values) => {
            if (!err) {
                this.setState({
                    confirmLoading: true
                })
                Api.linkSource({
                    sourceId: sourceData.sourceId,
                    linkSourceId: values.linkSourceId,
                }, type)
                    .then(
                        (res) => {
                            this.setState({
                                confirmLoading: true
                            })
                            if (res.code == 1) {
                                message.success("操作成功")
                                this.props.form.resetFields();
                                this.props.onOk();
                            }
                        }
                    )
            }
        })
    }
    render() {
        const { confirmLoading, targetList } = this.state;
        const { visible, form, sourceData } = this.props;
        const { getFieldDecorator } = form;
        return <Modal
            title={`映射配置(${sourceData.sourceName || ''})`}
            maskClosable={false}
            visible={visible}
            onCancel={this.onCancel.bind(this)}
            onOk={this.linkSource.bind(this)}
            confirmLoading={confirmLoading}
        >
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="本项目"
                >
                    {getFieldDecorator('sourceName', {
                        initialValue: sourceData.dataName
                    })(

                        <Input disabled />

                    )}
                    
                    <ExtTableCell style={{marginTop:"8px"}} sourceData={sourceData} />
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="发布目标"
                >
                    {getFieldDecorator('linkSourceId', {
                        rules: [{
                            required: true,
                            message: "请选择发布目标数据源"
                        }],
                        initialValue: sourceData.linkSourceId
                    })(

                        <Select style={{ width: "100%" }} placeholder="目标数据源">
                            {targetList.map(
                                (target) => {
                                    return <Option key={target.id} value={target.id}>{target.dataName}</Option>
                                }
                            )}
                        </Select>

                    )}
                </FormItem>
            </Form>
        </Modal>
    }
}

const WrapLinkModal = Form.create({})(LinkModal);

export default WrapLinkModal;