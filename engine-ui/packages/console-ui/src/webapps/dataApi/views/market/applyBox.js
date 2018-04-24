import React, { Component } from "react";
import { Modal, Form, Input, Spin, message,Button } from "antd"
import { connect } from "react-redux";
import { apiMarketActions } from '../../actions/apiMarket';
const FormItem = Form.Item;
const TextArea = Input.TextArea


const mapDispatchToProps = dispatch => ({
    apiApply(apiId, applyContent) {
        return dispatch(apiMarketActions.apiApply({ apiId: apiId, applyContent: applyContent }));
    },
    getApiExtInfo(apiId) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                apiId: apiId
            })
        )
    }
});

@connect(null, mapDispatchToProps)
class ApplyBox extends Component {
    state = {
        loading: false
    }
    handleSubmit(values) {
        console.log(values)
    }
    handleOk() {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                this.props.apiApply(this.props.apiId, values.applyMsg)
                    .then(
                        (res) => {
                            this.setState({
                                loading: false
                            })
                            this.props.getApiExtInfo(this.props.apiId);
                            if(this.props.getMarketApi){
                                this.props.getMarketApi();
                            }
                            
                            if (res) {
                                message.success('操作成功')
                                this.props.successCallBack();
                            }
                        }
                    )

            }
        });

    }
    handleCancel() {
        this.props.form.resetFields();
        this.props.cancelCallback();
    }
    render() {
        const { getFieldDecorator } = this.props.form;
        return (
            <div>

                <Modal
                    title="申请API"
                    visible={this.props.show}
                    onOk={this.handleOk.bind(this)}
                    onCancel={this.handleCancel.bind(this)}
                    footer={[
                        <Button key="back" size="large" onClick={this.handleCancel.bind(this)}>取消</Button>,
                        <Button key="submit" type="primary" size="large" loading={this.state.loading} onClick={this.handleOk.bind(this)}>
                            提交
                        </Button>,
                    ]}
                >

                    <Form onSubmit={this.handleSubmit.bind(this)}>
                        <FormItem
                            className="text-bottom"
                            label="申请API"
                            hasFeedback
                            labelCol={{
                                sm: 6, xs: 24
                            }}
                            wrapperCol={{
                                sm: 18, xs: 24
                            }}
                        >
                            <span style={{ lineHeight: 1.3, fontSize: 14 }}>
                                {this.props.apiName}
                            </span>
                            <br />
                            <span style={{ lineHeight: 1.2, fontSize: 14 }}>{this.props.desc}</span>

                        </FormItem>

                        <FormItem
                            className="text-bottom"
                            label="申请说明"
                            hasFeedback
                            required
                            labelCol={{
                                sm: 6, xs: 24
                            }}
                            wrapperCol={{
                                sm: 18, xs: 24
                            }}
                        >
                            {getFieldDecorator('applyMsg',
                                {
                                    rules: [{ required: true, message: '请输入申请信息' },
                                    {max:200,message:"最大字符不能超过200"},]
                                }, )(<TextArea style={{ width: 200 }} rows={4} />)}

                        </FormItem>

                    </Form>
                </Modal>

            </div>


        )
    }
}
const WrappedApplyBox = Form.create()(ApplyBox);
export default WrappedApplyBox;