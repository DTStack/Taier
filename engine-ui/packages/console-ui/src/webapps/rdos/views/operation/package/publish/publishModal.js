import React from "react";
import { Card, Tabs, Modal, Checkbox, Row, Col, Form, Input,Table } from "antd";

import utils from "utils";
import { formItemLayout } from "../../../../../console/consts";

const TabPane = Tabs.TabPane;
const FormItem=Form.Item;
const TextArea=Input.TextArea;

class PublishModal extends React.Component {

    state = {
        pagination:{
            current:1,
            pageSize:5,
            total:0
        }
    }
    componentWillReceiveProps(nextProps) {
        const { visible } = nextProps;
        const { visible: old_visible } = this.props;
        if (visible && visible != old_visible) {
            this.reset();
        }
    }
    reset(){
        this.props.form.resetFields();
        this.setState({
           
        })
    }
    onTableChange(pagination){
        this.setState({
            pagination
        })
    }
    initColumns(){
        const {isPublish} = this.props;
        let columns=[{
            title:"对象名称",
            dataIndex:"taskName"
        },{
            title:"类型",
            dataIndex:"taskType"
        },{
            title:"环境参数",
            dataIndex:"env"
        },{
            title:"创建人",
            dataIndex:"createName"
        },{
            title:"修改人",
            dataIndex:"name"
        },{
            title:"修改时间",
            dataIndex:"time",
            width:"150px"
        }]
        if(isPublish){
            columns.splice(2,1);
        }
        return columns
    }
    onOk(){
        const {isPublish} = this.props;
        this.props.onOk();
    }
    render() {
        const { pagination } = this.state;
        const { visible, form, packageList, isPublish } = this.props;
        const {getFieldDecorator} = form;
        return (
            <Modal
                width={800}
                visible={visible}
                title="发布"
                onOk={this.onOk.bind(this)}
                onCancel={this.props.onCancel}
            >
                <FormItem
                    label="发布包名称"
                    {...formItemLayout}
                >
                    {getFieldDecorator('publishName',{
                        rules:[{
                            required:true,
                            message:"请输入发布包名称"
                        }]
                    })(
                        <Input disabled={!isPublish} />  
                    )}
                </FormItem>
                <FormItem
                    label="发布描述"
                    {...formItemLayout}
                >
                    {getFieldDecorator('publishDesc',{
                        rules:[{
                            required:true,
                            message:"请输入发布描述"
                        }]
                    })(
                        <TextArea disabled={!isPublish}  autosize={{minRows:3,maxRows:5}} />  
                    )}
                </FormItem>
                <FormItem
                    label="发布到目标项目"
                    {...formItemLayout}
                >
                    project_name
                </FormItem>
                <Table
                    pagination={pagination}
                    className="m-table"
                    columns={this.initColumns()}
                    dataSource={packageList}
                />
            </Modal>
        )
    }
}
const WrapPublishModal=Form.create()(PublishModal);
export default WrapPublishModal;